<%@ page import="com.ulwx.tool.ObjectUtils" %>
<%@ page import="com.github.ulwx.aka.webmvc.utils.WebMvcCbConstants.SessionKey" %>
<%@ page trimDirectiveWhitespaces="true" %>
<%@ page contentType="application/json; charset=utf-8" language="java"
         errorPage=""%>
<%
	try
	{
		Object result = request.getAttribute(SessionKey.JsonKey);
		result = result instanceof String ? result : ObjectUtils.toJsonString(result);
		out.write((String) result);

	}
	catch (Exception e)
	{
		//JspLog.error("",e);

	}
%>