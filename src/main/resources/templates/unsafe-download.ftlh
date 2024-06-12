<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta http-equiv="content-Type" content="text/html; charset=UTF-8" />
    <style>
        .d {
            width: 993px;
            margin-right: auto;
            margin-left: auto;
            height: auto;
            padding-bottom: 100px;
        }
        .d2 {
            line-height: 43px;
            border-top: 1px solid #EEEEEE;
            padding: 28px 10px 16px 10px;
        }
        .load {
            text-align: center;
        }
        .load a {
            margin-top: 6px;
            cursor: default;
            display: inline-block;
            line-height: initial;
            text-decoration: none;
            height: 32px;
            line-height: 32px;
        }
        .load a:focus {
            outline: 0;
        }
        .txt {
            cursor: pointer;
            color: #fff;
            background-color: #FF6452;
            padding: 6px 35px;
            box-shadow: 0 1px 1px rgba(0,0,0,.17);
            transition: .2s;
            border: 1px solid #ff6452;
        }
        .txt:hover {
            background-image: linear-gradient(#ff7a6b, #fd6452);
            box-shadow: 0 1px 2px rgba(0,0,0,.26);
            border: 1px solid #ff5e4c;
        }
        .txt:active {
            box-shadow: inset 0 1px 2px rgba(0, 0, 0, 0.17);
            background: #b94d41;
            transition: .0s;
            border: 1px solid #a54035;
        }
        .txt:focus {
            box-shadow: inset 0 1px 2px rgba(0, 0, 0, 0.17);
            background: #b94d41;
            transition: .0s;
            border: 1px solid #a54035;
        }
        .txtc {
            margin: 0 26px;
        }
    </style>
    <title>远程文件分享</title>
</head>
<body>
<div class="d">
    <div style="font-size: 30px;text-align: center;padding: 56px 0px 20px 0px;">${fileName}</div>
    <div class="d2">
        <div>
            <table width="99%" border="0" align="center" cellspacing="0">
                <tbody>
                    <tr>
                        <td width="330" valign="top">
                            <span class="p7">文件大小：</span>${size}<br>
                            <span class="p7">上传时间：</span>${time}<br>
                            <span class="p7">分享用户：</span>${name}<br>
                            <span class="p7">文件描述：</span>${note}<br>
                        </td>
                    </tr>
                </tbody>
            </table>
        </div>
        <div class="load">
            <#if id != 0>
            <a href="/unsafe/down?id=${id}" target="_blank" rel="noreferrer">
                <span class="txt">电信下载</span>
                <span class="txt txtc">联通下载</span>
                <span class="txt">普通下载</span>
            </a>
            </#if>
        </div>
    </div>
</div>
</body>
</html>
