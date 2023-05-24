package com.github.ulwx.aka.webmvc.swagger;

import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import javax.servlet.ServletContext;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public  abstract class RequestHandlerMappingCreator {

   abstract  public  Map<Class, List<AkaMvcActionMethodInfo>> findActionMethods();

    public  RequestMappingHandlerMapping createRequestMappingHandlerMapping(RequestMappingHandlerMapping src,
                                                                            Optional<ServletContext> srcServletContext) throws Exception
    {
        SwaggerRequestMappingHandlerMapping mapping = new SwaggerRequestMappingHandlerMapping();
        mapping.setApplicationContext(src.getApplicationContext());
        ServletContext servletContext =srcServletContext.orElse(null);
        mapping.setServletContext(servletContext);
        mapping.setOrder(0);
        mapping.setContentNegotiationManager(src.getContentNegotiationManager());
        mapping.setPatternParser(src.getPatternParser());
        mapping.setUrlPathHelper(src.getUrlPathHelper());
        mapping.setPathMatcher(src.getPathMatcher());
        mapping.setUseSuffixPatternMatch(src.useSuffixPatternMatch());
        mapping.setUseRegisteredSuffixPatternMatch(src.useRegisteredSuffixPatternMatch());
        mapping.setUseTrailingSlashMatch(src.useTrailingSlashMatch());
        mapping.setPathPrefixes(src.getPathPrefixes());
        Map<Class, List<AkaMvcActionMethodInfo>> actionMethodMap = findActionMethods();
        mapping.setActionMethodMap(actionMethodMap);
        mapping.afterPropertiesSet();
        return mapping;
    }
}
