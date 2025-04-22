<%@ page import="com.github.ulwx.aka.webmvc.WebMvcCbConstants" %>
<%@ page import="com.github.ulwx.aka.webmvc.web.action.CbResult" %>
<%@ page import="com.github.ulwx.aka.webmvc.web.action.RedirectResult" %>
<%@ page import="com.ulwx.tool.StringUtils" %>
<%@ page import="com.ulwx.tool.ObjectUtils" %>
<%@ page import="com.ulwx.tool.EscapeUtil" %>
<%@ page import="com.github.ulwx.aka.webmvc.utils.JspLog" %>
<%@ page language="java" pageEncoding="UTF-8"%>
<%
	CbResult resultJson=(CbResult)request.getAttribute(WebMvcCbConstants.ResultKey);
	RedirectResult redirectResult=(RedirectResult)resultJson.getData();
	String redirectURL= StringUtils.trim(redirectResult.getRedirectURL());
	if(redirectResult.getData()!=null) {
		String argName=redirectResult.getParmName();
		if (redirectURL.lastIndexOf("?") > 0) {
			redirectURL=redirectURL+"&";

		} else {
			redirectURL=redirectURL+"?";
		}
		redirectURL=redirectURL+argName+"="+
				EscapeUtil.escapeUrl(ObjectUtils.toStringUseFastJson(
						redirectResult.getData()),"utf-8");
	}
	if(redirectURL.startsWith("http")){

	}else if(redirectURL.startsWith("/")){
		redirectURL=request.getContextPath()+redirectURL;
	}else{
		response.sendRedirect(request.getContextPath()+"/"+redirectURL);
	}
	//JspLog.debug(redirectURL);
	response.sendRedirect(redirectURL);

%>