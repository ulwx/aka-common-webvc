<%@ page import="com.github.ulwx.aka.webmvc.WebMvcCbConstants" %>
<%@ page import="com.github.ulwx.aka.webmvc.web.action.CbResult" %>
<%@ page import="com.github.ulwx.aka.webmvc.web.action.ForwardResult" %>
<%@ page language="java" pageEncoding="UTF-8"%>
<%
	CbResult resultJson=(CbResult)request.getAttribute(WebMvcCbConstants.ResultKey);
	ForwardResult result=(ForwardResult)resultJson.getData();
	String forward_url=result.getForwardURL();
	request.getRequestDispatcher(forward_url).forward(request, response);
%>