
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@page import="com.ulwx.tool.EscapeUtil"%>
<%@page import="com.ulwx.tool.ObjectUtils"%>
<%@page import="com.ulwx.tool.StringUtils"%>

<%@page import="java.util.Set"%>
<%@page import="java.util.Map"%>
<%@ page import="com.github.ulwx.aka.webmvc.utils.WebMvcCbConstants" %>
<%@ page import="com.github.ulwx.aka.webmvc.utils.WebMvcCbConstants.SessionKey" %>
<!DOCTYPE html>
<%
%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title></title>
</head>
<%
   
	String postURL=(String)request.getAttribute(SessionKey.GateKey);

%>
<body >
	<div style="position:absolute;left:0; right:0; top:0; bottom:0;margin:auto;width:140px;height:40px">
	正在进行跳转.....<img src="<%=request.getContextPath()%>/aka-common-webvc/images/loading-bars.gif">
	</div>
	<form id="form" method="post" action="<%=postURL%>"
		style="width: 100%;visibility: hidden;">
		<%
		 	Map<String,String> parm = (Map) request.getAttribute(SessionKey.GateReqMap);
			Set<String> keys = parm.keySet();
			for (String key : keys) {
				String val = (String) parm.get(key);
		%>

			<div style="margin: 20px">
				<%=key%>：<input type="text" id="<%=key%>" name="<%=key%>"
					value="<%=val%>" style="width: 200px" />
			</div>
		<%
			}
		%>
		<div style="margin: 20px">
			<button type="submit">Submit</button>
		</div>
	</form>
	<script type="text/javascript">
		document.getElementById("form").submit();
	</script>
</body>
</html>








