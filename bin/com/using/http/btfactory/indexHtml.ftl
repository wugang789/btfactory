<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
 <head>
  <title> 首页 </title>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
  <style type="text/css">
	a{TEXT-DECORATION:none}
  </style>
  <script type="text/javascript">
	function showContext(context) {
		document.getElementById("frmContext").src = context.replace(/\/\//gi,"/");
	}
  </script>
 </head>

 <body>
	<table width="100%">
		<tr>
			<td width="12%" valign="top">
				<div id="contectDiv" style="height:500px; width:210px; overflow:auto">
					<#list listLinkBeanIndex as listLinkBeanIndex>
					${listLinkBeanIndex_index + 1}.<a href="javascript:showContext('${listLinkBeanIndex.indexLinkUrl}');">${listLinkBeanIndex.title}</a>
					<br /><br />
					</#list>
				</div>
			</td>
			<td width="88%" valign="top">
				<iframe id="frmContext" height="600px" width="100%" src=""></iframe>
			</td>
		</tr>
	
	</table>
 </body>
</html>
<script type="text/javascript">
	var contextHeight = document.documentElement.clientHeight - 40;
	document.getElementById("contectDiv").style.height = contextHeight + "px";
	document.getElementById("frmContext").height = contextHeight + "px";
</script>
