<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no">
    <meta http-equiv="content-Type" content="text/html; charset=UTF-8" />
    <!-- The above 3 meta tags *must* come first in the head; any other head content must come *after* these tags -->
    <title>${fileName}</title>
    <link href="${ctx}/css/basetypora.css" rel="stylesheet">
    <link href="${ctx}/css/blubook.css" rel="stylesheet">
    <script src="${ctx}/js/showdown.js"></script>

    <link rel="stylesheet" href="${ctx}/css/markdownPad2AutoCatalog.css">
    <script src="${ctx}/js/markdownPad2AutoCatalog.js"></script>
    <script src="${ctx}/js/highlight.js"></script>

<#--    <link rel="stylesheet" href="${ctx}/highlight/styles/default.min.css">-->
<#--    <script src="${ctx}/highlight/js/highlight.min.js"></script>-->

    <style>
        img{
            max-width: 70%;
            height: auto;
        }
        body{
            padding: 20px;
        }
        /* 对整个代码块的样式 */
        /*pre {*/
        /*    background-color: #f4f4f4;*/
        /*    padding: 10px;*/
        /*    position: relative; !* 让伪元素相对于pre定位 *!*/
        /*}*/

        /*!* 对内部的代码标签的样式 *!*/
        /*pre code {*/
        /*    color: #333;*/
        /*}*/

        /*!* 对行号的样式 *!*/
        /*pre::before {*/
        /*    content: "";*/
        /*    counter-reset: line; !* 重置计数器 *!*/
        /*    position: absolute;*/
        /*    left: 0;*/
        /*    top: 0;*/
        /*    bottom: 0;*/
        /*    width: 2em; !* 控制行号宽度 *!*/
        /*    padding: 10px;*/
        /*    text-align: right;*/
        /*    border-right: 1px solid #ccc; !* 添加分隔线 *!*/
        /*}*/

        /*!* 对行号中的文本的样式 *!*/
        /*pre::before {*/
        /*    counter-increment: line; !* 递增计数器 *!*/
        /*    content: counter(line);*/
        /*}*/

    </style>

</head>

<body>


<#-- 这部分代码加载成功后会被覆盖掉 -->
<script>
    const ENTER_REPLACE = "BV,KeOX$e^S[po:^?c3#";
    // console.log(ENTER_REPLACE)
    var converter = new showdown.Converter();
    converter.setOption('tables', 'true');
    converter.setOption('simplifiedAutoLink', 'true');
    converter.setOption('literalMidWordUnderscores', 'true');
    // converter.setOption('splitAdjacentBlockquotes', 'true');
    converter.setOption('openLinksInNewWindow', 'true');
    converter.setOption('moreStyling', 'true');
    converter.setOption('omitExtraWLInCodeBlocks', 'true');
    var text = '${fileContent}';
    // console.log(text.split(ENTER_REPLACE).join("\n"));  // 替换操作
    // console.log(text.replace(ENTER_REPLACE, "\n")); // 这个是正则 不行
    document.body.innerHTML = converter.makeHtml(text.split(ENTER_REPLACE).join("\n"));

    // showdown.js 3822行
    function getAbsolutePath() {
        return '${absolutePicFolderPath}';
    }
    // 补丁2：showdown.js 4580行
    // 补丁3：showdown.js 3022行 & 3056
    // 补丁4：代码返回最后的时候还是有转义字符 所以渲染完毕应该所有符号恢复原状 6045

    // 补丁5：部分图片是以 <img> 标签 写的 并不是 mk语法 所以要 重新搞
    document.querySelectorAll('img').forEach((imgItem) => {
        if (imgItem.src.indexOf("image?path=") === -1) {
            // 证明之前没处理过
            imgItem.src = 'image?path=' + getAbsolutePath() + imgItem.src.split(window.location.origin + "/").join("");
        }
    });


    // 补丁6：自动展开 markdownPad2AutoCatalog.js 622

    // setTimeout(() => {
    //     hljs.highlightAll();
    // }, 3000);
    // document.addEventListener('DOMContentLoaded', (event) => {
    //     document.querySelectorAll('pre code').forEach((block) => {
    //         hljs.highlightBlock(block);
    //     });
    // });
</script>


</body>

</html>