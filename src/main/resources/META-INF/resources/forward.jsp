<%@ page import="com.github.ulwx.aka.webmvc.utils.WebMvcCbConstants" %>
<%@ page import="com.github.ulwx.aka.webmvc.utils.WebMvcCbConstants.SessionKey" %>
<%@ page language="java" pageEncoding="UTF-8"%>
<%
	String forward_url=(String)request.getAttribute(SessionKey.ForwardKey);
	request.getRequestDispatcher(forward_url).forward(request, response);
%>