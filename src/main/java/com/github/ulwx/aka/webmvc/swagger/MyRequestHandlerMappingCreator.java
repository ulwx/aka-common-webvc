package com.github.ulwx.aka.webmvc.swagger;

import com.github.ulwx.aka.webmvc.AkaWebMvcProperties;
import com.github.ulwx.aka.webmvc.BaseController;
import com.github.ulwx.aka.webmvc.ScanSupport;
import com.github.ulwx.aka.webmvc.annotation.AkaMvcAction;
import com.github.ulwx.aka.webmvc.annotation.AkaMvcActionMethod;
import com.github.ulwx.aka.webmvc.web.action.ActionSupport;
import com.ulwx.tool.ArrayUtils;
import com.ulwx.tool.StringUtils;
import com.ulwx.tool.reflect.ReflectionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.*;

@Component
public class MyRequestHandlerMappingCreator extends RequestHandlerMappingCreator {
    private  AkaWebMvcProperties akaWebMvcProperties;

    public AkaWebMvcProperties getAkaWebMvcProperties() {
        return akaWebMvcProperties;
    }
    @Autowired
    public void setAkaWebMvcProperties(AkaWebMvcProperties akaWebMvcProperties) {
        this.akaWebMvcProperties = akaWebMvcProperties;
    }

    @Override
    public  Map<Class, List<AkaMvcActionMethodInfo>> findActionMethods() {

        Map<Class, List<AkaMvcActionMethodInfo>> resultMap = new LinkedHashMap<>();
        Collection<AkaWebMvcProperties.NameSpace> namespaces = akaWebMvcProperties.getNamespaces().values();
        for (AkaWebMvcProperties.NameSpace nameSpace : namespaces) {
            try {
                Set<Class> classSet = new LinkedHashSet<>();
                classSet.addAll(ScanSupport.instance.doScan(nameSpace.getPackageName(), "",
                        "Action", ActionSupport.class));
                for (Class clazz : classSet) {
                    List<AkaMvcActionMethodInfo> methodList = new ArrayList<>();
                    String httpMethod = "";
                    String requestContentType="";
                    String responseContentType="application/json";
                    AkaMvcAction akaMvcActionAnno = (AkaMvcAction) clazz.getAnnotation(AkaMvcAction.class);
                    if (akaMvcActionAnno != null) {
                        httpMethod = StringUtils.trim(akaMvcActionAnno.httpMethod());
                        responseContentType=StringUtils.trim(akaMvcActionAnno.responseContentType());
                        requestContentType=StringUtils.trim(akaMvcActionAnno.requestContentType());
                    }
                    String[] strs = clazz.getName().split("\\.");
                    String actionName = StringUtils.trimTailString(strs[strs.length - 1], "Action");
                    String mod = strs[strs.length - 2];
                    String path = nameSpace.getName() + "/" + mod + "-" + actionName;
                    Method[] methods = ReflectionUtil.getClassPublicMethods(clazz,ActionSupport.class);// clazz.getMethods();
                    for (Method method : methods) {
                        boolean checked=BaseController.checkMethod(method);
                        if(!checked) continue;
                        String actionUrlPath = path + "-" + method.getName() ;
                        String logicActionName = mod + "-" + actionName + "-" + method.getName();
                        AkaMvcActionMethod akaMvcActionMethod = method.getAnnotation(AkaMvcActionMethod.class);
                        if (akaMvcActionMethod != null) {
                            httpMethod = StringUtils.trim(akaMvcActionMethod.httpMethod());
                            responseContentType=StringUtils.trim(akaMvcActionMethod.responseContentType());
                            requestContentType=StringUtils.trim(akaMvcActionMethod.requestContentType());
                        }
                        if(responseContentType.toLowerCase().contains("json")){
                            actionUrlPath=actionUrlPath+"JSON";
                        }
                        actionUrlPath=actionUrlPath+ ".action";
                        String[] allowMethods = null;
                        if (!httpMethod.isEmpty()) {
                            allowMethods = ArrayUtils.trim(httpMethod.split(","));

                        }
                        String[] configAllowMethods = null;
                        Map<String, String> viewsMap =
                                BaseController.getViewMap(nameSpace, logicActionName);
                        if (viewsMap != null) {
                            String configAllowMethodStr = StringUtils.trim(viewsMap.get(BaseController.Properites.AllowMethods));
                            if (!configAllowMethodStr.isEmpty()) {
                                configAllowMethods = configAllowMethodStr.split(",");
                            }

                        }
                        Set<String> finalHttpMethods = ArrayUtils.Intersection(configAllowMethods, allowMethods);

                        AkaMvcActionMethodInfo akaMvcActionMethodInfo = new AkaMvcActionMethodInfo(clazz, method,
                                logicActionName, actionUrlPath,finalHttpMethods,requestContentType);

                        methodList.add(akaMvcActionMethodInfo);

                    }
                    resultMap.put(clazz, methodList);
                }

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return resultMap;

    }

}
