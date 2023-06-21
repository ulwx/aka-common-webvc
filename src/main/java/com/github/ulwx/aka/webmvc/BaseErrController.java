package com.github.ulwx.aka.webmvc;

import com.github.ulwx.aka.dbutils.tool.support.StringUtils;
import com.github.ulwx.aka.webmvc.utils.WebMvcUtils;
import com.github.ulwx.aka.webmvc.web.action.*;
import com.ulwx.tool.NetUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;

@Controller("com.github.ulwx.aka.webmvc.BaseErrController")
@RequestMapping("${server.error.path:${error.path:/error}}")
public class BaseErrController implements ErrorController {
    private static Logger log = Logger.getLogger(BaseErrController.class);
    private AkaWebMvcProperties akaWebMvcProperties;

    @Autowired
    public void setAkaWebMvcProperties(AkaWebMvcProperties akaWebMvcProperties) {
        this.akaWebMvcProperties = akaWebMvcProperties;

    }

    @RequestMapping
    //针对找不到指定资源的错误，这里会拦截
    public ModelAndView handle(HttpServletRequest request) {
        //WebMvcAutoConfiguration
        String statusCode = StringUtils.trim(request.getAttribute("javax.servlet.error.status_code"));
        String message = StringUtils.trim(request.getAttribute("javax.servlet.error.message"));
        Exception exception=(Exception)request.getAttribute("javax.servlet.error.exception");
        String exceptionStr="";
        message=message+";";
        ByteArrayOutputStream byteArrayOutputStream=new ByteArrayOutputStream();
        PrintWriter printWriter=new PrintWriter(byteArrayOutputStream,true);
        if(exception!=null) {
            exception.printStackTrace(printWriter);
        }
        try {
            exceptionStr=byteArrayOutputStream.toString("utf-8");
        }catch (Exception e){
            log.error(""+e,e);
        }
        exceptionStr=StringUtils.trim(exceptionStr);
        if(exceptionStr.isEmpty()){
            exceptionStr=StringUtils.trim(exception);
        }
        message=message+";"+exceptionStr;
        message=StringUtils.trimLeadingString(message,";");
        message=StringUtils.trimTailString(message,";");
        String errerSourceUrl = StringUtils.trim(request.getAttribute("javax.servlet.error.request_uri"));
        if(statusCode.equals("404")){
            if(StringUtils.hasText(message)){
                message="【"+message+"】";
            }
            message= message+"【请求地址："+errerSourceUrl+"不存在】";
        }else{

        }
        String messageView = "forward:" + akaWebMvcProperties.getGlobalViews().get(ActionSupport.MESSAGE);
        String jsonView = "forward:" + akaWebMvcProperties.getGlobalViews().get(ActionSupport.JSON);
        ModelAndView modelAndView = new ModelAndView();

        if (WebMvcUtils.isAjax(errerSourceUrl)) {
            String querStr=NetUtils.getQueryStrFromURL(errerSourceUrl);
            String [] strs=NetUtils.urlQueryStrToMap(querStr,"utf-8").get("callback");
            String callBack="";
            if(strs!=null && strs.length==1){
                callBack=strs[0];
            }
            ActionContext.getContext().getRequestUtils(request).setString("callback",callBack);
            CbResult ret= CbResult.of(Status.ERR,0,
                    message + "【" + statusCode + "】", null);
            modelAndView.addObject(WebMvcCbConstants.ResultKey, ret);
            modelAndView.setViewName(jsonView);
        } else {
            MsgResult msgResult=new MsgResult();
            msgResult.setMsg(message + "【" + statusCode + "】");
            //msgResult.setReturnURL(errerSourceUrl);
            CbResult<MsgResult> ret= CbResult.of(Status.ERR,0,
                    message + "【" + statusCode + "】", msgResult);
            modelAndView.addObject(WebMvcCbConstants.ResultKey, ret);
            modelAndView.setViewName(messageView);
        }

        return modelAndView;
    }

}
