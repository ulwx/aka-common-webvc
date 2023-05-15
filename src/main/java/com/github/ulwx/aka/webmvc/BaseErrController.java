package com.github.ulwx.aka.webmvc;

import com.github.ulwx.aka.dbutils.tool.support.StringUtils;
import com.github.ulwx.aka.webmvc.utils.WebMvcCbConstants;
import com.github.ulwx.aka.webmvc.web.action.ActionSupport;
import com.github.ulwx.aka.webmvc.web.action.CbResultJson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
@Controller
@RequestMapping("${server.error.path:${error.path:/error}}")
public class BaseErrController implements ErrorController {

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
        String exception=StringUtils.trim(request.getAttribute("javax.servlet.error.exception"));
        message=message+";"+exception;
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
        String jsonView = "forward:" + akaWebMvcProperties.getGlobalViews().get( ActionSupport.JSON);
        ModelAndView modelAndView = new ModelAndView();
        if (WebMvcCbConstants.isAjax(errerSourceUrl)) {
            modelAndView.addObject(WebMvcCbConstants.SessionKey.JsonKey, CbResultJson.ERR(message + "【" + statusCode + "】"));
            modelAndView.setViewName(jsonView);
        } else {
            modelAndView.setViewName(messageView);
            modelAndView.addObject(WebMvcCbConstants.SessionKey.MsgKey, message + "【" + statusCode + "】");
            modelAndView.addObject(WebMvcCbConstants.SessionKey.MsgReturnURL, errerSourceUrl);
        }

        return modelAndView;
    }

}
