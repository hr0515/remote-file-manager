<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no">
    <meta http-equiv="content-Type" content="text/html; charset=UTF-8" />
    <!-- The above 3 meta tags *must* come first in the head; any other head content must come *after* these tags -->
    <title>${meta.title}</title>

    <!-- Bootstrap -->
    <link href="${ctx}/bootstrap/css/bootstrap.min.css" rel="stylesheet">
    <!-- jQuery Data Table -->
    <link href="${ctx}/css/dataTables.bootstrap.min.css" rel="stylesheet">

    <!-- HTML5 shim and Respond.js for IE8 support of HTML5 elements and media queries -->
    <!-- WARNING: Respond.js doesn't work if you view the page via file:// -->
<#--    <!--[if lt IE 9]>-->
<#--    <script src="https://oss.maxcdn.com/html5shiv/3.7.3/html5shiv.min.js"></script>-->
<#--    <script src="https://oss.maxcdn.com/respond/1.4.2/respond.min.js"></script>-->
<#--    <![endif]&ndash;&gt;-->
    <script src="${ctx}/js/notify_stand.js"></script>

</head>

<body>

<div class="container" role="main">
    <div class="page-header">
<#--        <span class="pull-right">-->
<#--            <ul class="list-inline">-->
<#--            <#list meta.quickLinks as title, dir>-->
<#--                <li>-->
<#--                    <a href="?dir=${dir}" data-toggle="tooltip" data-placement="top" title="Jump to ${dir}"-->
<#--                       data-original-title="Jump to ${dir}">${title}</a>-->
<#--                </li>-->
<#--            </#list>-->
<#--            </ul>-->
<#--        </span>-->
        <h3>${meta.title}</h3><a class="btn btn-sm btn-success" href="${upload}">去上传</a>&nbsp;&nbsp;&nbsp;&nbsp;
        <a class="btn btn-sm btn-success" href="${terminal}">去终端</a>&nbsp;&nbsp;&nbsp;&nbsp;
        <a class="btn btn-sm btn-success" href="${logPath}">去日志</a>&nbsp;&nbsp;&nbsp;&nbsp;
        <a class="btn btn-sm btn-success" href="${share}">分享链接列表</a>&nbsp;&nbsp;&nbsp;&nbsp;
        <a class="btn btn-sm btn-success" href="${version}">版本控制列表</a>&nbsp;&nbsp;&nbsp;&nbsp;
        <a class="btn btn-sm btn-success" onclick="clearClickBtn(this)">清除列表数据</a>

<#--        <p>${meta.description}</p>-->
        <p> </p>
    </div>

    <#assign currentDirectoryFullName = fileTree.currentDirectory.fullName >
    <ol class="breadcrumb">
        $
        <#if currentDirectoryFullName == '/'>
            /
        <#else>
            <#list currentDirectoryFullName?split("/") as x>
                <#if x!=''>
                    <#assign currentFullFolder = currentDirectoryFullName?substring(0, currentDirectoryFullName?index_of(x)) + x>
                    <#if currentDirectoryFullName?index_of(x) != 0>/</#if>
                    <a href="?dir=${currentFullFolder}"
                       data-toggle="tooltip" data-placement="top" title="浏览 '${currentFullFolder}' 文件夹">${x}</a>
                </#if>
            </#list>
        </#if>
    </ol>

    <div class="row">
        <div class="col-md-12">
            <table id="files" class="table table-hover table-striped table-bordered" style="width:100%">
                <thead>
                <tr>
                    <th class="col-md-6">文件名</th>
                    <th>权限</th>
                    <th>大小&nbsp;</th>
                    <th>修改日期</th>
                    <th>功能</th>
                </tr>
                </thead>

                <#if fileTree.parentDirectory??>
                    <#assign parentDirectory = fileTree.parentDirectory >
                    <tr>
                        <td>
                            <span class="glyphicon glyphicon-arrow-up"></span>
                            <#if parentDirectory.readable>
                                <a href="?dir=${parentDirectory.fullName}"
                                   data-toggle="tooltip" data-placement="right" title="返回上一层"># 返回上一层</a>
                            <#else>
                                返回上一层
                            </#if>
                        </td>
                        <td>${parentDirectory.accessAttributes}</td>
                        <td></td>
                        <td></td>
                        <td></td>
                    </tr>
                </#if>
                <#assign childDirectories = fileTree.childDirectories >
                <#list childDirectories as childDirectory>
                    <tr>
                        <td>
                            <span class="glyphicon glyphicon-folder-close"></span>
                            <#if childDirectory.readable>
                                <a href="?dir=${childDirectory.fullName}"
                                   data-toggle="tooltip" data-placement="right"
                                   title="浏览 '${childDirectory.name}' 文件夹">${childDirectory.name}</a>
                            <#else>
                                ${childDirectory.name}
                            </#if>
                        </td>
                        <td>${childDirectory.accessAttributes}</td>
                        <td></td>
                        <td>${childDirectory.dateTime}</td>
                        <td></td>
                    </tr>
                </#list>

                <#assign files = fileTree.files >
                <#list files as file>
                    <tr>
                        <td>
                            <span class="glyphicon glyphicon-file"></span>
                            <#if file.readable && (file.name?ends_with(".txt") || file.name?ends_with(".log") || file.name?ends_with(".html") || file.name?ends_with(".md") || file.name?ends_with(".htm"))>
                                <a onclick="fileClickBtn(this)" href="JavaScript:(0)" value="${ctx}/file-viewer?file=${file.fullName}"
                                   data-toggle="tooltip" data-placement="right"
                                   title="浏览 '${file.name}' 文件">${file.name}</a>

                            <#elseif file.readable && (file.name?ends_with(".mp4") || file.name?ends_with(".avi") || file.name?ends_with(".flv") || file.name?ends_with(".rmvb") || file.name?ends_with(".mov"))>
                                <a onclick="fileClickBtn(this)" href="JavaScript:(0)" value="${ctx}/file-play?file=${file.fullName}"
                                   data-toggle="tooltip" data-placement="right"
                                   title="浏览 '${file.name}' 视频">${file.name}</a>
                            <#elseif file.readable && (file.name?ends_with(".jpg") || file.name?ends_with(".png") || file.name?ends_with(".jpeg") || file.name?ends_with(".gif") || file.name?ends_with(".bmp"))>
                                <a onclick="picClickBtn(this)" href="JavaScript:(0)" value="${ctx}/image?path=${file.fullName}"
                                   data-toggle="tooltip" data-placement="right">${file.name}</a>
                            <#else>
                                ${file.name}
                            </#if>
                        </td>
                        <td>${file.accessAttributes}</td>
                        <td>${file.size}</td>
                        <td>${file.dateTime}</td>
                        <td>
                            <#if file.readable>
                                <a class="btn btn-xs btn-info" href="${ctx}/file-downloader?file=${file.fullName}"
                                   target="_blank"
                                   title="下载 '${file.name}'" data-toggle="tooltip" data-placement="right"
                                   data-original-title="下载 '${file.name}'">下载</a>

                                <a onclick="shareFileClickBtn(this)" class="btn btn-xs btn-info" href="JavaScript:(0)" value="${file.fullName}"
                                   title="分享 '${file.name}'" data-toggle="tooltip" data-placement="right"
                                   data-original-title="分享 '${file.name}'">分享</a>
                            </#if>
                        </td>
                    </tr>
                </#list>
            </table>
        </div>
    </div>

    <footer class="footer">
        <p>Powered By HR!</p>
    </footer>

</div> <!-- /container -->




<!-- jQuery (necessary for Bootstrap's JavaScript plugins) -->
<script src="${ctx}/js/jquery-3.7.0.js"></script>
<!-- Include all compiled plugins (below), or include individual files as needed -->
<script src="${ctx}/bootstrap/js/bootstrap.min.js"></script>
<!-- Jquery Data Table -->
<script src="${ctx}/js/jquery.dataTables.min.js"></script>
<script src="${ctx}/js/dataTables.bootstrap.min.js"></script>

<script type="text/javascript">

    // 分享功能 start
    async function copyTextToClipboard(text) {
        try {
            await navigator.clipboard.writeText(text);
        } catch (err) {
            console.error('Failed to copy: ', err);
        }
    }
    function shareFailed(code) {
        notify.destroyAll();
        notify.error("分享失败! code: " + code);
    }
    // 使用方法
    function shareFileClickBtn(ths) {
        console.log(ths);
        notify.loading("创建分享链接...");
        $.get("/share", {file: ths.getAttribute("value")})
            .done(function(response) {
                if (response === '') {
                    shareFailed(1);
                }else if (response.indexOf("html") !== -1) {
                    // 重新加载整个页面
                    location.reload();
                }else {
                    copyTextToClipboard(window.location.origin + "/unsafe/download/" + response);
                    notify.destroyAll();
                    notify.success("创建成功!(自动写入剪贴板) id: " + response);
                }
            })
            .fail(function(xhr, status, error) {
                shareFailed(2);
            });
    }
    // 分享功能 end

    function clearClickBtn(ths) {
        $.get("/share/clear", {})
            .done(function(response) {
                if (response === 1) {
                    notify.success("清除成功");
                }else {
                    notify.success("清除失败 1");
                }
            })
            .fail(function(xhr, status, error) {
                notify.success("清除失败 2 网络连接失败");
            });
    }

    function fileClickBtn(ths) {
        // window.location.href = ths.getAttribute('value');
        window.open(ths.getAttribute('value'), '_blank'); // 在新窗口中打开当前页面的 URL
    }
    function picClickBtn(ths) {
        // console.log(ths.getAttribute('value'))
        imgShow(ths.getAttribute('value'));
    }

    $(document).ready(function () {
        $('[data-toggle="tooltip"]').tooltip();

        $('#files').DataTable( {
            paging: false
        } );
    });
</script>
<!-- Global site tag (gtag.js) - Google Analytics -->
<#--<script async src="https://www.googletagmanager.com/gtag/js?id=G-FWEV2G1WG5"></script>-->
<#--<script>-->
<#--    window.dataLayer = window.dataLayer || [];-->
<#--    function gtag(){dataLayer.push(arguments);}-->
<#--    gtag('js', new Date());-->

<#--    gtag('config', 'G-FWEV2G1WG5');-->
<#--</script>-->



<style>
    .maskweap{width: 100%;height: 100%;position: fixed;top: 0;left: 0;background-color: rgba(0,0,0,0.8);display: flex;display: -webkit-flex;-webkit-justify-content: center;justify-content: center;-webkit-align-items: center;align-items: center;display: none;}
    .maskweap .imgshow{width: 100%;box-sizing: border-box;height: 100%;display: flex;display: -webkit-flex;-webkit-align-items: center;align-items: center;-webkit-justify-content: center;justify-content: center;}
    .maskweap .imgshow img{display: block;width:auto;max-width: 70%;box-sizing: border-box;padding: 10px;background-color: #FFFFFF;}
</style>
<div class="maskweap">
    <div class="imgshow">
        <img src="" class="bigimg" />
    </div>
</div>
<script>
    //图片放大显示
    function imgShow(src) {
        $(".bigimg").attr("src",src);
        $(".maskweap").fadeIn();
    }
    $(document).mousedown(function(e) {
        if ($(e.target).parent(".imgshow").length === 0) {
            $(".maskweap").fadeOut(); //弹窗关闭
        }
    });
</script>

</body>
</html>