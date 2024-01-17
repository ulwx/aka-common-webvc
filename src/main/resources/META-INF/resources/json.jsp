<%@ page import="com.github.ulwx.aka.webmvc.WebMvcCbConstants" %>
<%@ page import="com.github.ulwx.aka.webmvc.web.action.JsonResult"%>
<%@ page import="com.github.ulwx.aka.webmvc.web.action.CbResult"%><%@ page import="com.ulwx.tool.ObjectUtils"%><%@ page import="com.github.ulwx.aka.webmvc.web.action.ActionContext"%><%@ page import="com.ulwx.tool.StringUtils"%><%@ page import="com.github.ulwx.aka.webmvc.utils.JspLog"%>
<%@ page trimDirectiveWhitespaces="true" %>
<%@ page contentType="application/json; charset=utf-8" language="java"
         errorPage=""%>
<%
	try
	{
        CbResult resultJson=(CbResult)request.getAttribute(WebMvcCbConstants.ResultKey);
        String str=ObjectUtils.toStringUseFastJson(resultJson,true);
        String callback=ActionContext.getContext().getRequestUtils(request).getString("callback");
        if (StringUtils.hasText(callback)) {
            str = callback + "(" + str + ")";

        }
        JspLog.debug(str);
        out.write(str);
        out.flush();
	}
	catch (Exception e)
	{

	}
%>