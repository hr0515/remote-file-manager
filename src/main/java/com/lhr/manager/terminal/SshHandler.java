package com.lhr.manager.terminal;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.ssh.ChannelType;
import cn.hutool.extra.ssh.JschUtil;
import com.jcraft.jsch.ChannelShell;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author qingfeng
 * @version 1.0.0
 * @ProjectName SshHandler
 * @Description ssh 处理
 * @createTime 2022/5/2 0002 15:26
 */
@ServerEndpoint(value = "/ws/ssh")
@Component
public class SshHandler {
    private static final ConcurrentHashMap<String, HandlerItem> HANDLER_ITEM_CONCURRENT_HASH_MAP = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() {
        log.info("WebSocket initialized");
    }

    private static Logger log = LoggerFactory.getLogger(SshHandler.class);

    private static final AtomicInteger OnlineCount = new AtomicInteger(0);
    // concurrent包的线程安全Set，用来存放每个客户端对应的Session对象。
    private static CopyOnWriteArraySet<javax.websocket.Session> SessionSet = new CopyOnWriteArraySet<javax.websocket.Session>();


    /**
     * 连接建立成功调用的方法
     */
    @OnOpen
    public void onOpen(javax.websocket.Session session) throws Exception {
        log.info("socket请求");
        SessionSet.add(session);
        SshModel sshItem = new SshModel();
        sshItem.setHost("127.0.0.1");
        sshItem.setPort(22);
        String osName = System.getProperty("os.name").toLowerCase();
        if (osName.contains("win")) {
            sshItem.setUser("administrator");
            sshItem.setPassword("3184020");
        } else {
            sshItem.setUser("root");
            sshItem.setPassword("3184020");
        }
        int cnt = OnlineCount.incrementAndGet(); // 在线数加1
        log.info("有连接加入 当前连接数为：{} sessionId={}", cnt, session.getId());

        Map<String, List<String>> requestParameterMap = session.getRequestParameterMap();
        String dir = null;
        if (requestParameterMap != null && requestParameterMap.size() > 0) dir = requestParameterMap.get("dir").get(0);

        try {
			session.getBasicRemote().sendText("\n连接成功!\n");  // 可能一闪而过了 啥也看不见
		} catch (IOException e) {
			log.error("发送消息出错：{}", e.getMessage());
			e.printStackTrace();
		}

        HandlerItem handlerItem = new HandlerItem(session, sshItem);
        handlerItem.startRead(dir);
        HANDLER_ITEM_CONCURRENT_HASH_MAP.put(session.getId(), handlerItem);
    }

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose(javax.websocket.Session session) {
        SessionSet.remove(session);
        int cnt = OnlineCount.decrementAndGet();
        log.info("有连接关闭 当前连接数为：{}", cnt);
    }

    /**
     * 收到客户端消息后调用的方法
     *
     * @param message 客户端发送过来的消息
     */
    @OnMessage
    public void onMessage(String message, javax.websocket.Session session) throws Exception {
//        log.info("来自客户端的消息：{}", message);  // 这里他会 因为 前端每一个 输入 都会 响应输出
        HandlerItem handlerItem = HANDLER_ITEM_CONCURRENT_HASH_MAP.get(session.getId());
		if (handlerItem.checkInput(message)) {
			handlerItem.outputStream.write(message.getBytes());
		} else {
			handlerItem.outputStream.write("没有执行相关命令权限".getBytes());
			handlerItem.outputStream.flush();
			handlerItem.outputStream.write(new byte[]{3});
		}
		handlerItem.outputStream.flush();
    }

    /**
     * 出现错误
     *
     * @param session
     * @param error
     */
    @OnError
    public void onError(javax.websocket.Session session, Throwable error) {
        log.error("发生错误：{} Session ID： {}", error.getMessage(), session.getId());
        error.printStackTrace();
    }


    private static class HandlerItem implements Runnable {
        private final Logger cmdLogger = LoggerFactory.getLogger("cmd_log");
        private final javax.websocket.Session session;
        private final InputStream inputStream;
        private final OutputStream outputStream;
        private final Session openSession;
        private final ChannelShell channel;
        private final SshModel sshItem;
        private final StringBuilder nowLineInput = new StringBuilder();
        HandlerItem(javax.websocket.Session session, SshModel sshItem) throws IOException {
            this.session = session;
            this.sshItem = sshItem;
            this.openSession = JschUtil.openSession(sshItem.getHost(), sshItem.getPort(), sshItem.getUser(), sshItem.getPassword());
            // 如果这里报错 上句话报错 应该是 电脑本身没 开启shh
            // 本身都是有 ssh的 但是Windows 本身自带的是 client 只能连别人
            // Windows设置 应用 可选应用 添加应用 找到 OpenShh服务器版 安装  —— win10 1903
            // 下面有命令行安装
            this.channel = (ChannelShell) JschUtil.createChannel(openSession, ChannelType.SHELL);
            this.inputStream = channel.getInputStream();
            this.outputStream = channel.getOutputStream();
        }
        void startRead(String dir) throws JSchException, IOException {
            this.channel.connect();
            // 重定向
            if (dir != null && dir.length() != 0 && !"null".equals(dir)) {
                String[] split = dir.split(":");
                if (split.length == 2 && split[0].length() == 1) {
                    outputStream.write((split[0] + ":\r\n").getBytes());
                }
                outputStream.write(("cd " + dir + "\r\n").getBytes());
                outputStream.flush();
            }
            ThreadUtil.execute(this);   // 放到线程池里面 执行
        }
        /**
         * 添加到命令队列
         *
         * @param msg 输入
         * @return 当前待确认待所有命令
         */
        private String append(String msg) {
            char[] x = msg.toCharArray();
            if (x.length == 1 && x[0] == 127) {
                // 退格键
                int length = nowLineInput.length();
                if (length > 0) {
                    nowLineInput.delete(length - 1, length);
                }
            } else {
                nowLineInput.append(msg);
            }
            return nowLineInput.toString();
        }

        public boolean checkInput(String msg) {
            boolean refuse;
            if (StrUtil.equalsAny(msg, StrUtil.CR, StrUtil.TAB)) {
                String join = nowLineInput.toString();
                if (StrUtil.equals(msg, StrUtil.CR)) {
                    nowLineInput.setLength(0);
                }
                refuse = SshModel.checkInputItem(sshItem, join);
            } else {
                // 复制输出
                refuse = SshModel.checkInputItem(sshItem, msg);
            }
            return refuse;
        }

        @Override
        public void run() {
            try {
                byte[] buffer = new byte[4096];
                int i;
                //如果没有数据来，线程会一直阻塞在这个地方等待数据。
                while ((i = inputStream.read(buffer)) != -1) {
                    String msg = new String(Arrays.copyOfRange(buffer, 0, i), sshItem.getCharsetT());
                    cmdLogger.info(msg);
					try {
						session.getBasicRemote().sendText(msg);
					} catch (IOException e) {
						log.error(e.toString());
					}
                }
            } catch (Exception e) {
                if (!this.openSession.isConnected()) {
                    return;
                }
                // 销毁
				HandlerItem handlerItem = HANDLER_ITEM_CONCURRENT_HASH_MAP.get(session.getId());
				if (handlerItem != null) {
					IoUtil.close(handlerItem.inputStream);
					IoUtil.close(handlerItem.outputStream);
					JschUtil.close(handlerItem.channel);
					JschUtil.close(handlerItem.openSession);
				}
				IoUtil.close(session);
				HANDLER_ITEM_CONCURRENT_HASH_MAP.remove(session.getId());
            }
        }
    }
}


/**
 * in:
 * Get-WindowsCapability -Online | Where-Object Name -like 'OpenSSH*'
 * <p>
 * out:
 * Name  : OpenSSH.Client~~~~0.0.1.0
 * State : Installed
 * <p>
 * Name  : OpenSSH.Server~~~~0.0.1.0
 * State : NotPresent
 * <p>
 * <p>
 * NotPresent 则需要安装 复制下面这俩 任一 安装上
 * Install the OpenSSH Client
 * Add-WindowsCapability -Online -Name OpenSSH.Client~~~~0.0.1.0
 * <p>
 * Install the OpenSSH Server
 * Add-WindowsCapability -Online -Name OpenSSH.Server~~~~0.0.1.0
 * <p>
 * <p>
 * Path          :
 * Online        : True
 * RestartNeeded : False
 * <p>
 * 如果出错：
 * Add-WindowsCapability : 无法启动服务，原因可能是已被禁用或与其相关联的设备没有启动。
 * 把 Windows update 服务 启动  并且设置为自动  安装完再禁它
 * <p>
 * Get-WindowsCapability -Online | Where-Object Name -like 'OpenSSH*'
 * Name  : OpenSSH.Client~~~~0.0.1.0
 * State : Installed
 * <p>
 * Name  : OpenSSH.Server~~~~0.0.1.0
 * State : Installed
 * <p>
 * net start sshd
 * <p>
 * 用户名和密码的填法
 * whoami
 * mi10s\administrator
 * 用户名填 administrator 后面这个
 * 密码就是电脑的 pin
 */


/**
 * 用户名和密码的填法
 * whoami
 * mi10s\administrator
 * 用户名填 administrator 后面这个
 * 密码就是电脑的 pin
 *
 *
 */

