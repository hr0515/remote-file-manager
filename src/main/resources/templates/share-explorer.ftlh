<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no">
    <meta http-equiv="content-Type" content="text/html; charset=UTF-8" />
    <!-- The above 3 meta tags *must* come first in the head; any other head content must come *after* these tags -->
    <title>分享链接列表</title>

    <!-- Bootstrap -->
    <link href="${ctx}/bootstrap/css/bootstrap.min.css" rel="stylesheet">
    <!-- jQuery Data Table -->
    <link href="${ctx}/css/dataTables.bootstrap.min.css" rel="stylesheet">

</head>

<body>

<div class="container" role="main">
    <div class="page-header">

        <h3>分享链接列表</h3><a class="btn btn-sm btn-success" href="/">回主页</a>
        <p id="linkShareShowId" style="font-weight: bold;margin-top: 15px">&nbsp;</p>
    </div>

    <div class="row">
        <div class="col-md-12">
            <table id="files" class="table table-hover table-striped table-bordered" style="width:100%">
                <thead>
                <tr>
                    <th>id</th>
                    <th>文件名</th>
                    <th>文件大小&nbsp;</th>
                    <th>日期</th>
                    <th>分享用户</th>
                    <th>路径</th>
                    <th>备注</th>
                    <th>功能</th>
                </tr>
                </thead>

                <#list shares as share>
                    <tr>
                        <td>${share.id}</td>
                        <td>${share.fileName}</td>
                        <td>${calcFile(share.size)}</td>
                        <td>${share.time?string('yyyy.MM.dd HH:mm:ss')}</td>
                        <td>${share.name}</td>
                        <td>${share.path}</td>
                        <td>${share.note}</td>
                        <td>
                            <a onclick="copyShareLink(this)" class="btn btn-xs btn-info" href="JavaScript:(0)" value="${share.id}"
                               title="复制链接 '${share.fileName}'" data-toggle="tooltip" data-placement="right"
                               data-original-title="复制链接 '${share.fileName}'">复制链接</a>
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


<#-- 定义自定义函数 calcFile -->
<#function calcFile bytes>
    <#assign KB = 1024>
    <#assign MB = KB * 1024>
    <#assign GB = MB * 1024>
    <#assign TB = GB * 1024>
    <#if bytes < KB>
        <#return bytes + " B">
    <#elseif bytes < MB>
        <#return (bytes / KB)?int + " KB">
    <#elseif bytes < GB>
        <#return (bytes / MB)?int + " MB">
    <#elseif bytes < TB>
        <#return (bytes / GB)?int + " GB">
    <#else>
        <#return (bytes / TB)?int + " TB">
    </#if>
</#function>


<script type="text/javascript">

    // 后端处理的话 得遍历 list 干脆前端也搞一个函数吧
    function calcFile_js(bytes) {
        var KB = 1024;
        var MB = KB * 1024;
        var GB = MB * 1024;
        var TB = GB * 1024;
        if (bytes < KB) {
            return bytes + " B";
        } else if (bytes < MB) {
            return bytes / KB + " KB";
        } else if (bytes < GB) {
            return bytes / MB + " MB";
        } else if (bytes < TB) {
            return bytes / GB + " GB";
        } else {
            return bytes / TB + " TB";
        }
    }

    async function copyTextToClipboard(text) {
        try {
            await navigator.clipboard.writeText(text);
        } catch (err) {
            console.error('Failed to copy: ', err);
        }
    }

    function copyShareLink(ths) {
        var url = window.location.origin + "/unsafe/download/" + ths.getAttribute("value");
        var share = document.getElementById('linkShareShowId');
        share.innerHTML = url;
        copyTextToClipboard(url);
    }

    $(document).ready(function () {
        $('[data-toggle="tooltip"]').tooltip();

        $('#files').DataTable( {
            paging: false
        } );
    });
</script>

</body>
</html>