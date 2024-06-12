<!DOCTYPE html>
<html lang="en">
<head>
    <meta name="viewport" content="initial-scale=1.0, maximum-scale=1.0">
    <link rel="stylesheet" href="${ctx}/css/xterm.css">
    <script type="text/javascript" src="${ctx}/js/xterm.js"></script>
    <script type="text/javascript" src="${ctx}/js/xterm-addon-attach.js"></script>
    <script type="text/javascript" src="${ctx}/js/xterm-addon-fit.js"></script>
    <title>远程终端访问</title>

    <link href="${ctx}/bootstrap/css/bootstrap.min.css" rel="stylesheet">

</head>
<body>
<div class="container" role="main">
    <div class="page-header">
        <h3>远程终端访问</h3><a class="btn btn-sm btn-success" href="${explorer}">去查看</a>
        <p></p>
    </div>
    <div class="page-header" style="margin: 20px">
        <a class="btn btn-sm btn-success" href="Javascript:(0)" onclick="UTF8ClickBtn()">UTF-8 win</a>
    </div>

    <div id="xterm"></div>
    <script type="text/javascript">
        var socket = new WebSocket('${webSocketAddr}');
        //连接打开事件
        socket.onopen = function () {
            console.log("WebSocket 已打开");
        };
        //收到消息事件
        socket.onmessage = function (msg) {
            // console.log(msg);  message Event
            term.write(msg.data); //把接收的数据写到这个插件的屏幕上
        };
        //连接关闭事件
        socket.onclose = function () {
            console.log("WebSocket 已关闭");
        };
        //发生了错误事件
        socket.onerror = function () {
            alert("WebSocket 发生了错误");
        };

        const term = new Terminal({
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
        term.onData(function (key) {
            socket.send(key); //转换为字符串
        });
        // 初始化 fit 插件
        const fitAddon = new FitAddon.FitAddon();
        term.loadAddon(fitAddon);
        // 调用 fit 方法来自动调整终端大小以适应其容器
        term.open(document.getElementById("xterm"));
        fitAddon.fit();

        // 监听窗口大小变化事件，并调用 fit 方法  z新版本不用他 用它报错
        // window.addEventListener('resize', fitAddon.fit.bind(fitAddon));


        function runFakeTerminal() {
            if (term._initialized) {
                return;
            }
            term._initialized = true;
            term.writeln('正在连接服务器...');
        }

        runFakeTerminal();

        function UTF8ClickBtn() {
            socket.send("chcp 65001 \r\n");
            term.focus()
        }

    </script>
</div>
</body>
</html>