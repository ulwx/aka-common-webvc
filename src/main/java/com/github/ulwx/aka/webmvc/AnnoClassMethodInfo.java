package com.github.ulwx.aka.webmvc;

import java.lang.reflect.Method;

public class AnnoClassMethodInfo {
    private  String  httpMethods;
    private String requestContentType ;
    private  String responseContentType ;
    private Method method;
    private Class<?> actionClass;

    public String getHttpMethod() {
        return httpMethods;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public Class<?> getActionClass() {
        return actionClass;
    }

    public void setActionClass(Class<?> actionClass) {
        this.actionClass = actionClass;
    }

    public void setHttpMethod(String httpMethod) {
        this.httpMethods = httpMethod;
    }

    public String getRequestContentType() {
        return requestContentType;
    }

    public void setRequestContentType(String requestContentType) {
        this.requestContentType = requestContentType;
    }

    public String getResponseContentType() {
        return responseContentType;
    }

    public void setResponseContentType(String responseContentType) {
        this.responseContentType = responseContentType;
    }
}
