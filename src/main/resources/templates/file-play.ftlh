<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>${fileName}</title>
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no">
    <meta http-equiv="content-Type" content="text/html; charset=UTF-8" />
    <style>

        body {
            margin: 0;
            padding: 0;
        }

    </style>
</head>
<body>

<video id="myVideo" style="width: 100%; height: 100%; object-fit: fill" autoplay controls>
    <source src="${absolutePath}" type="video/mp4">
    Your browser does not support the video tag.
</video>

<script>
    var video = document.getElementById("myVideo");
    video.play();
</script>
</body>
</html>