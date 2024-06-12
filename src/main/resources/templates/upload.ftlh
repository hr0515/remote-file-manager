<!DOCTYPE html>
<html lang="en">

<head>
  <meta charset="UTF-8">
  <meta http-equiv="X-UA-Compatible" content="IE=edge">
  <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no">
  <meta http-equiv="content-Type" content="text/html; charset=UTF-8" />

  <title>远程文件上传</title>
  <link rel="stylesheet" href="${ctx}/css/upload.css">
  <link href="${ctx}/bootstrap/css/bootstrap.min.css" rel="stylesheet">
</head>

<body>

<div class="container" role="main">
  <div class="page-header">
    <h3>远程文件上传</h3><a class="btn btn-sm btn-success" href="${explorer}">去查看</a>
    <p> </p>
  </div>

  <div class="dnd-file-uploader">
    <div class="drop-area">
      <ion-icon class="upload-icon" name="cloud-upload"></ion-icon>
      <p class="tip">点击上传</p>
      <p class="tip">支持拖拽</p>
      <p class="tip">支持多文件</p>
      <#--   下面是专门为点击设计的 拖拽的代码都在js里面   -->
      <input style="display: none" type="file" id="clickFilesId" name="file" hidden multiple />
    </div>
    <div class="task-list">
      <#list nameTimeList as item>
            <div>
              <div class="task" style="background: rgb(217, 247, 190);">
                <span class="task-name">${item[1]}</span>
                <span class="task-progress">${item[0]}</span>
              </div>
            </div>
      </#list>
    </div>
  </div>

  <template id="template-task">
    <div class="task">
      <span class="task-name">some file name</span>
<#--      <a class="task-name" target="_blank">some file name</a>-->
      <span class="task-progress">50%</span>
    </div>
  </template>

<#--  <script type="module" src="https://unpkg.com/ionicons@5.5.2/dist/ionicons/ionicons.esm.js"></script>-->
<#--  <script nomodule src="https://unpkg.com/ionicons@5.5.2/dist/ionicons/ionicons.js"></script>-->
  <script src="${ctx}/js/upload.js"></script>


</div>

</body>

</html>