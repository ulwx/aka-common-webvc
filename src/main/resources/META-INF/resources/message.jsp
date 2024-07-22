<%@ page import="com.github.ulwx.aka.webmvc.web.action.CbResult" %>
<%@ page import="com.github.ulwx.aka.webmvc.web.action.MsgResult" %>
<%@ page import="com.ulwx.tool.EscapeUtil" %>
<%@ page import="com.github.ulwx.aka.webmvc.WebMvcCbConstants" %>
<%@ page import="com.ulwx.tool.StringUtils" %>

<%@ page language="java" contentType="text/html; charset=utf-8"
         pageEncoding="utf-8"%>
<!DOCTYPE html>
<html> 
<head>
<title>消息</title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge" />

<style type="text/css">



</style>


<script  type="text/javascript" language="javascript">
<%
CbResult<MsgResult> resultJson=(CbResult)request.getAttribute(WebMvcCbConstants.ResultKey);
MsgResult msgResult=(MsgResult)resultJson.getData();
String msg=msgResult.getMsg();
String returnUrl=msgResult.getReturnURL();

%>
function url(){
    if(window.top!=null){
        window.top.location='<%=returnUrl%>';
    }
}

</script>
</head>
<body >
<div >
    <div class="message_tip">
        <xmp>
        <%=msg%>
        </xmp>

    </div>
    <%
      if(!returnUrl.equals("")){
    %>
    <div  style="margin-left: 40px;margin-top:10px">
        <%if(StringUtils.hasText(returnUrl)){%>
        <a  href="javascript:void(0)"  onclick="url()">确定</a>
        <%}%>
    </div>
    <%}%>
</div>
</body>
</html>