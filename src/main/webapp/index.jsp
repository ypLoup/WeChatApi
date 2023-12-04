<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <meta http-equiv="X-UA-Compatible" content="ie=edge">
  <title>js 图片转base64方式</title>
</head>
<body>
  <p id="container1"></p>
  <script>
    getBase64("http://101.68.222.195:8081/home/xiuyan/Projects/platform_6900/app/image/access_face_image/20200703/362202198311030674_SKUO-AC-dc:0d:30:4f:0c:5e_1593778841.0746317.jpg")
    function getBase64(imgUrl) {
      window.URL = window.URL || window.webkitURL;
      var xhr = new XMLHttpRequest();
      xhr.open("GET", imgUrl, true);
      xhr.setRequestHeader('Content-Type', 'image/jpeg');
      // 至关重要
      xhr.responseType = "blob";
      alert(111);
      xhr.onload = function () {
        if (this.status == 200) {
          alert(111);
          var blob = this.response;
          console.log("blob", blob)
          // 至关重要
          let oFileReader = new FileReader();
          oFileReader.onloadend = function (e) {
            let base64 = e.target.result;
            console.log("方式一》》》》》》》》》", base64)
          };
          oFileReader.readAsDataURL(blob);
          //====为了在页面显示图片，可以删除====
          var img = document.createElement("img");
          img.onload = function (e) {
            window.URL.revokeObjectURL(img.src); // 清除释放
          };
          let src = window.URL.createObjectURL(blob);
          img.src = src
          document.getElementById("container1").appendChild(img);
          //====为了在页面显示图片，可以删除====
 
        }
      }
      xhr.send();
    }
  </script>
</body>
</html>