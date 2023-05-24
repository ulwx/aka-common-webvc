<%@ page import="com.github.ulwx.aka.webmvc.WebMvcCbConstants" %>
<%@ page import="com.github.ulwx.aka.webmvc.web.action.JsonResult"%>
<%@ page import="com.github.ulwx.aka.webmvc.web.action.CbResultJson"%>
<%@ page trimDirectiveWhitespaces="true" %>
<%@ page contentType="application/json; charset=utf-8" language="java"
         errorPage=""%>
<%
	try
	{
        CbResultJson resultJson=(CbResultJson)request.getAttribute(WebMvcCbConstants.ResultKey);
		JsonResult result = (JsonResult)resultJson.getData();
        out.write(result.getContent());
	}
	catch (Exception e)
	{
		//JspLog.error("",e);

	}
%>