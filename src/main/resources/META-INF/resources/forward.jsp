<%@ page import="com.github.ulwx.aka.webmvc.WebMvcCbConstants" %>
<%@ page import="com.github.ulwx.aka.webmvc.web.action.CbResultJson" %>
<%@ page import="com.github.ulwx.aka.webmvc.web.action.ForwardReulst" %>
<%@ page language="java" pageEncoding="UTF-8"%>
<%
	CbResultJson resultJson=(CbResultJson)request.getAttribute(WebMvcCbConstants.ResultKey);
	ForwardReulst result=(ForwardReulst)resultJson.getData();
	String forward_url=result.getForwardURL();
	request.getRequestDispatcher(forward_url).forward(request, response);
%>