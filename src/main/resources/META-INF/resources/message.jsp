<%@ page import="com.github.ulwx.aka.webmvc.web.action.CbResultJson" %>
<%@ page import="com.github.ulwx.aka.webmvc.web.action.MsgResult" %>
<%@ page import="com.ulwx.tool.EscapeUtil" %>
<%@ page import="com.github.ulwx.aka.webmvc.WebMvcCbConstants" %>

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
CbResultJson<MsgResult> resultJson=(CbResultJson)request.getAttribute(WebMvcCbConstants.ResultKey);
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
    <div  style="text-align: center;margin-top:10px">
        <a  href="javascript:void(0)"  onclick="url()">确定</a>
    </div>
    <%}%>
</div>
</body>
</html>