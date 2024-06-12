////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////
// 代码存档，真正调用在 html里面写了

var socket = new WebSocket("ws://localhost:9999/ws/ssh?dir=${explorer}");
//连接打开事件
socket.onopen = function() {
    console.log("WebSocket 已打开");
};
//收到消息事件
socket.onmessage = function(msg) {
    // console.log(msg);  message Event
    term.write(msg.data); //把接收的数据写到这个插件的屏幕上
};
//连接关闭事件
socket.onclose = function() {
    console.log("WebSocket 已关闭");
};
//发生了错误事件
socket.onerror = function() {
    alert("WebSocket 发生了错误");
};

var term = new Terminal({
    rendererType: "canvas", //渲染类型
    rows: parseInt(24), //行数
    cols: parseInt(100), // 不指定行数，自动回车后光标从下一行开始
    convertEol: true, //启用时，光标将设置为下一行的开头
    // scrollback: 2000, //终端中的回滚量
    disableStdin: false, //是否应禁用输入
    // cursorStyle: "underline", //光标样式
    cursorBlink: true, //光标闪烁
    theme: {
        foreground: "white", //字体
        background: "#060101", //背景色
        cursor: "help" //设置光标
    }
});
// const attachAddon = new AttachAddon(socket);
// term.loadAddon(attachAddon);
term.open(document.getElementById("xterm"));
// 支持输入与粘贴方法

//szbuf = "";
term.onData(function(key) {

    socket.send(key); //转换为字符串

    // 下面这种方式 虽然 可以 方便日志记录, 但是并不能支持上下 箭头查看历是的功能
    // 归根结底在于 它这个 是监控回车 \r 的
    // szbuf += key;
    // if (key.indexOf("\r") !== -1 || key.indexOf("\n") !== -1 || key.indexOf("\r\n") !== -1) {
    //     console.log("现在才可以执行");
    //     socket.send(szbuf); //转换为字符串
    //     szbuf = "";
    // }
    // term.write(key)
});

// term.onLineFeed(function(){
//     // 有换行 产生的回调
//     console.log("执行换行"+JSON.stringify(commandKey))
// });

// 标题更换回调
// term.onTitleChange(function(key){
//     console.log("onTitleChange:"+key);
// });

function runFakeTerminal() {
    if (term._initialized) {
        return;
    }
    term._initialized = true;
    term.writeln('正在连接服务器...');

    // 按键监听 下面所有代码都注释掉了 没起作用
    // term.onKey(e => {
    //     const printable = !e.domEvent.altKey && !e.domEvent.altGraphKey && !e.domEvent.ctrlKey && !e.domEvent.metaKey;
    //         if (e.domEvent.keyCode === 13) {
    //         // e.domEvent.key === 'Enter';
    //         // 按下回车键的操作
    //     } else if (e.domEvent.keyCode === 8) {
    //         // 代表 删除 Backspace 键按下
    //         if (term._core.buffer.x > 2) {
    //             // term.write('\b \b')
    //         }
    //     } else if (printable) {
    //         // term.write(e.key);
    //     }
    //     // console.log(commandKey);
    //     // console.log("key::"+e.domEvent.keyCode);
    // });
}
runFakeTerminal();