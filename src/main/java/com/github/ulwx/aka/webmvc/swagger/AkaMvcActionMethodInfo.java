package com.github.ulwx.aka.webmvc.swagger;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class AkaMvcActionMethodInfo {
    private Class actionClass;
    private Method method;
    private String logicActionName;
    private String requestPath;
    private Set<String>  httpMethods;
    private String requestContentType;
    private RequestMapping requestMapping;

    public AkaMvcActionMethodInfo(Class actionClass, Method method, String logicActionName, String requestPath,
                                  Set<String> httpMethods,String requestContentType) {
        this.actionClass = actionClass;
        this.method = method;
        this.logicActionName = logicActionName;
        this.requestPath = requestPath;
        this.httpMethods=httpMethods;
        this.requestContentType=requestContentType;
        init();

    }

    public void init(){
        AkaMvcActionMethodInfo parent=this;
        RequestMapping requestMapping =new RequestMapping(){

            @Override
            public Class<? extends Annotation> annotationType() {
                return RequestMapping.class;
            }
            @Override
            public String name() {
                return "";
            }
            @Override
            public String[] value() {
                return new String[]{parent.requestPath};
            }
            @Override
            public String[] path() {
                return value();
            }
            @Override
            public RequestMethod[] method() {
                List<RequestMethod>  list=new ArrayList<>();
                for(String method:httpMethods){
                    list.add(RequestMethod.valueOf(method.toUpperCase()));
                }
                return list.toArray(new RequestMethod[0]);
            }
            @Override
            public String[] params() {
                return new String[0];
            }
            @Override
            public String[] headers() {
                return new String[0];
            }
            @Override
            public String[] consumes() {
                return new String[0];
            }
            @Override
            public String[] produces() {
                return new String[0];
            }
        };

        this.requestMapping=requestMapping;

    }

    public Class getActionClass() {
        return actionClass;
    }

    public void setActionClass(Class actionClass) {
        this.actionClass = actionClass;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public String getLogicActionName() {
        return logicActionName;
    }

    public void setLogicActionName(String logicActionName) {
        this.logicActionName = logicActionName;
    }

    public String getRequestPath() {
        return requestPath;
    }

    public void setRequestPath(String requestPath) {
        this.requestPath = requestPath;
    }

    public Set<String> getHttpMethods() {
        return httpMethods;
    }

    public void setHttpMethods(Set<String> httpMethods) {
        this.httpMethods = httpMethods;
    }

    public RequestMapping getRequestMapping() {
        return requestMapping;
    }

    public void setRequestMapping(RequestMapping requestMapping) {
        this.requestMapping = requestMapping;
    }
}
