package com.github.ulwx.aka.webmvc.web.action;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

public  class ServletActionContext {
    public static  ServletRequestAttributes getRequestAttributes() {
        ServletRequestAttributes requestAttributes =(ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return requestAttributes;
    }

    public static  HttpServletRequest getRequest(){
        ServletRequestAttributes requestAttributes = getRequestAttributes();
        if(requestAttributes == null){return null;}
        return requestAttributes.getRequest();
    }

    public static HttpServletResponse getResponse(){
        ServletRequestAttributes requestAttributes = getRequestAttributes();
        if(requestAttributes == null){return null;}
        return requestAttributes.getResponse();
    }
    public static HttpSession getSession(){
        HttpServletRequest request = getRequest();
        if(request == null){return null;}
        return  request.getSession();
    }
    public static HttpSession getSession(boolean create){
        HttpServletRequest request = getRequest();
        if(request == null){return null;}
        return  getRequest().getSession(create);
    }
}
