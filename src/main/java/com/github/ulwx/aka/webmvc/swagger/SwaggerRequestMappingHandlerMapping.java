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
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.condition.RequestCondition;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import javax.servlet.ServletContext;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Predicate;

public class SwaggerRequestMappingHandlerMapping extends RequestMappingHandlerMapping {
    private Map<Class, List<AkaMvcActionMethodInfo>> actionMethodMap = new TreeMap<>();

    public Map<Class, List<AkaMvcActionMethodInfo>> getActionMethodMap() {
        return actionMethodMap;
    }

    public void setActionMethodMap(Map<Class, List<AkaMvcActionMethodInfo>> actionMethodMap) {
        this.actionMethodMap = actionMethodMap;
    }

    private AkaWebMvcProperties akaWebMvcProperties;

    public SwaggerRequestMappingHandlerMapping(AkaWebMvcProperties akaWebMvcProperties) {
        this.akaWebMvcProperties = akaWebMvcProperties;
    }

    @Override
    protected boolean isHandler(Class<?> beanType) {

        if (beanType.getName().endsWith("Action") &&
                ActionSupport.class.isAssignableFrom(beanType)) {
            if (actionMethodMap.get(beanType) != null) {
                return true;
            }

        }
        return false;

    }

    @Override
    @Nullable
    protected RequestMappingInfo getMappingForMethod(Method method, Class<?> handlerType) {
        RequestMappingInfo info = createRequestMappingInfo(method, handlerType);
        return info;
    }

    String getPathPrefix(Class<?> handlerType) {
        for (Map.Entry<String, Predicate<Class<?>>> entry :

                this.getPathPrefixes()
                        .entrySet()) {
            if (entry.getValue().test(handlerType)) {
                String prefix = entry.getKey();
                return prefix;
            }
        }
        return null;
    }

    @Nullable
    private RequestMappingInfo createRequestMappingInfo(Method element, Class<?> handlerType) {
        RequestMapping requestMapping = findRequestMapping(element, handlerType);
        if(requestMapping==null){
            return null;
        }
        RequestCondition<?> condition = getCustomMethodCondition((Method) element);
        return createRequestMappingInfo(requestMapping, condition);
    }

    private RequestMapping findRequestMapping(Method element, Class<?> handlerType) {
        List<AkaMvcActionMethodInfo> list = actionMethodMap.get(handlerType);
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getMethod().equals(element)) {
                return list.get(i).getRequestMapping();
            }
        }
        return null;

    }

    public static RequestMappingHandlerMapping createRequestMappingHandlerMapping(RequestMappingHandlerMapping src,
                                                                                  AkaWebMvcProperties akaWebMvcProperties,
                                                                                  Optional<ServletContext> srcServletContext) throws Exception {
        SwaggerRequestMappingHandlerMapping mapping = new SwaggerRequestMappingHandlerMapping(akaWebMvcProperties);
        mapping.setApplicationContext(src.getApplicationContext());
        ServletContext servletContext =srcServletContext.orElse(null);
        mapping.setServletContext(servletContext);
        mapping.setOrder(0);
        mapping.setContentNegotiationManager(src.getContentNegotiationManager());
       // mapping.setCorsConfigurationSource(src.getCorsConfigurationSource());
        mapping.setPatternParser(src.getPatternParser());
        mapping.setUrlPathHelper(src.getUrlPathHelper());
        mapping.setPathMatcher(src.getPathMatcher());
        mapping.setUseSuffixPatternMatch(src.useSuffixPatternMatch());
        mapping.setUseRegisteredSuffixPatternMatch(src.useRegisteredSuffixPatternMatch());
        mapping.setUseTrailingSlashMatch(src.useTrailingSlashMatch());
        mapping.setPathPrefixes(src.getPathPrefixes());
        Map<Class, List<AkaMvcActionMethodInfo>> actionMethodMap = findActionMethods(akaWebMvcProperties);
        mapping.setActionMethodMap(actionMethodMap);
        mapping.afterPropertiesSet();
        return mapping;
    }

    public static Map<Class, List<AkaMvcActionMethodInfo>> findActionMethods(AkaWebMvcProperties akaWebMvcProperties) {

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
                        if(method.getParameterTypes()!=null && method.getParameterTypes().length>0){
                            continue;
                        }
                        if(method.getReturnType()!=String.class){
                            continue;
                        }

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
