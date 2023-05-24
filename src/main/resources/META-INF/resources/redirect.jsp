<%@ page import="com.github.ulwx.aka.webmvc.WebMvcCbConstants" %>
<%@ page import="com.github.ulwx.aka.webmvc.web.action.CbResultJson" %>
<%@ page import="com.github.ulwx.aka.webmvc.web.action.RedirectResult" %>
<%@ page import="com.ulwx.tool.StringUtils" %>
<%@ page language="java" pageEncoding="UTF-8"%>
<%
	CbResultJson resultJson=(CbResultJson)request.getAttribute(WebMvcCbConstants.ResultKey);
	RedirectResult redirectResult=(RedirectResult)resultJson.getData()
	String redirectURL= StringUtils.trim(redirectResult.getRedirectURL());
	if(redirectURL.startsWith("http")){
		response.sendRedirect(redirectResult.getRedirectURL());
	}else if(redirectURL.startsWith("/")){
		response.sendRedirect(request.getContextPath()+redirectURL);
	}else{
		response.sendRedirect(request.getContextPath()+"/"+redirectURL);
	}

%>