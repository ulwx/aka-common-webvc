package com.github.ulwx.aka.webmvc.swagger;

import com.github.ulwx.aka.webmvc.web.action.ActionSupport;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.condition.RequestCondition;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Predicate;

public class SwaggerRequestMappingHandlerMapping extends RequestMappingHandlerMapping {
    private Map<Class, List<AkaMvcActionMethodInfo>> actionMethodMap = new TreeMap<>();

    public Map<Class, List<AkaMvcActionMethodInfo>> getActionMethodMap() {
        return actionMethodMap;
    }
    public void setActionMethodMap(Map<Class, List<AkaMvcActionMethodInfo>> actionMethodMap) {
        this.actionMethodMap = actionMethodMap;
    }

    public SwaggerRequestMappingHandlerMapping() {
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


}
