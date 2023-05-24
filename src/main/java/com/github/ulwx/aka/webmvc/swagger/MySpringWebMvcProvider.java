package com.github.ulwx.aka.webmvc.swagger;

import com.github.ulwx.aka.webmvc.AkaWebMvcProperties;
import org.springdoc.webmvc.core.SpringWebMvcProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.handler.AbstractHandlerMethodMapping;
import org.springframework.web.servlet.mvc.method.RequestMappingInfoHandlerMapping;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import javax.servlet.ServletContext;
import java.util.*;
import java.util.stream.Collectors;


public class MySpringWebMvcProvider extends SpringWebMvcProvider {

    private Optional<ServletContext> servletContext;
    private List<RequestHandlerMappingCreator> requestHandlerMappingCreators;

    public List<RequestHandlerMappingCreator> getRequestHandlerMappingCreators() {
        return requestHandlerMappingCreators;
    }
    @Autowired
    public void setRequestHandlerMappingCreators(List<RequestHandlerMappingCreator> requestHandlerMappingCreators) {
        this.requestHandlerMappingCreators = requestHandlerMappingCreators;
    }

    public Optional<ServletContext> getServletContext() {
        return servletContext;
    }

    @Autowired
    public void setServletContext(Optional<ServletContext> servletContext) {
        this.servletContext = servletContext;
    }

    @Override
    public Map getHandlerMethods() {
        if (this.handlerMethods == null) {
            Map<String, RequestMappingHandlerMapping> beansOfTypeRequestMappingHandlerMapping =
                    applicationContext.getBeansOfType(RequestMappingHandlerMapping.class);

            for (String key: beansOfTypeRequestMappingHandlerMapping.keySet()) {
                RequestMappingInfoHandlerMapping rim = beansOfTypeRequestMappingHandlerMapping.get(key);
                if (rim instanceof RequestMappingHandlerMapping) {
                    RequestMappingHandlerMapping rmm = (RequestMappingHandlerMapping) rim;
                    try {
                        for (RequestHandlerMappingCreator creator:requestHandlerMappingCreators) {
                            RequestMappingHandlerMapping requestMappingHandlerMapping =
                                    creator.createRequestMappingHandlerMapping(rmm, servletContext);
                            beansOfTypeRequestMappingHandlerMapping.put(
                                    requestMappingHandlerMapping.getClass().getSimpleName(),
                                    requestMappingHandlerMapping);
                        }
                        break;
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            }
            this.handlerMethods = beansOfTypeRequestMappingHandlerMapping.values().stream()
                    .map(AbstractHandlerMethodMapping::getHandlerMethods)
                    .map(Map::entrySet)
                    .flatMap(Collection::stream)
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (a1, a2) -> a1, LinkedHashMap::new));
        }


        return this.handlerMethods;
    }
}
