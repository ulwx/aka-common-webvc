<%@ page import="com.github.ulwx.aka.webmvc.utils.WebMvcCbConstants" %>
<%@ page import="com.github.ulwx.aka.webmvc.utils.WebMvcCbConstants.SessionKey" %>
<%@ page language="java" pageEncoding="UTF-8"%>
<%
	String redirect_url=(String)request.getAttribute(SessionKey.RedirectKey);
	response.sendRedirect(request.getContextPath()+redirect_url);
%>