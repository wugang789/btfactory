<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
 <head>
  <title> ${name} </title>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
 </head>

 <body>
	<h4>种子网络下载地址：<a href="${linkUrl}" target="_blank">${linkUrl}</a></h4> 
	<h4>种子文件名：${name}.${type}&nbsp;&nbsp;&nbsp;&nbsp;<a id="localFilePath" href="">打开所在文件夹</a> </h4>
	<h4>影片名称：${title}</h4>
	<br/><br/>
	${context}
 </body>
</html>
<script type="text/javascript">
	var local = document.location.href;
	document.getElementById("localFilePath").href=local.substring(0,local.lastIndexOf("/"));
</script>