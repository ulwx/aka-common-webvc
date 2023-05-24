package com.github.ulwx.aka.webmvc;

import com.github.ulwx.aka.webmvc.exception.JsonServiceException;
import com.github.ulwx.aka.webmvc.exception.JspServiceException;
import com.github.ulwx.aka.webmvc.web.action.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.*;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.handler.SimpleMappingExceptionResolver;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Properties;

@Configuration(AkaConst.WebContextConfigName)
@EnableTransactionManagement(proxyTargetClass = true)
@ServletComponentScan(AkaConst.WebMvcComponetPackage)
@ConfigurationPropertiesScan({AkaConst.WebMvcComponetPackage})
@EnableAspectJAutoProxy(exposeProxy = true,proxyTargetClass=true)
@AkaComponentScan(AkaConst.WebMvcComponetPackage)
public class WebContextConfiguration implements WebMvcConfigurer {
    private  static Logger logger = LoggerFactory.getLogger(WebContextConfiguration.class);
    private AkaWebMvcProperties akaWebMvcProperties;

    @Autowired
    public void setAkaWebMvcProperties(AkaWebMvcProperties akaWebMvcProperties) {
        this.akaWebMvcProperties = akaWebMvcProperties;
    }
    @Bean
    public ViewResolver internalResourceViewResolver() {
        InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
        viewResolver.setPrefix("/");
        viewResolver.setSuffix(".jsp");
        return viewResolver;
    }


    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        registry.addResourceHandler("/**")
                .addResourceLocations("/","classpath:/META-INF/resources/","classpath:/static/",
                        "classpath:/resources","classpath:/public");
        registry.addResourceHandler("/webjars/**").
                addResourceLocations("classpath:/META-INF/resources/webjars/");;

    }


    @Override
    public void extendHandlerExceptionResolvers(List<HandlerExceptionResolver> resolvers) {
        SimpleMappingExceptionResolver simpleMappingExceptionResolver=new SimpleMappingExceptionResolver(){
            @Override
            protected ModelAndView doResolveException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {

                ModelAndView modelAndView= super.doResolveException(request, response, handler, ex);
                if(modelAndView!=null){
                    CbResultJson content=CbResultJson.of(Status.ERR,0,
                            ex.getMessage(), null);
                    if(ex instanceof JspServiceException){
                        MsgResult msgResult=new MsgResult();
                        msgResult.setMsg( ex.getMessage());
                        msgResult.setReturnURL("");
                        CbResultJson ret=CbResultJson.of(Status.ERR,0, ex.getMessage(), msgResult);
                        modelAndView.addObject(WebMvcCbConstants.ResultKey, ret);
                    }else if(ex instanceof JsonServiceException){
                        //modelAndView.addObject(WebMvcCbConstants.SessionKey.JsonViewKey, CbResultJson.ERR(ex, "异常"));
                        JsonResult jsonResult= new JsonResult();
                        String callBack=request.getParameter("callback");
                        jsonResult.setContent(content,callBack);
                        CbResultJson ret=CbResultJson.of(Status.ERR,0, ex.getMessage(),jsonResult);
                        modelAndView.addObject(WebMvcCbConstants.ResultKey, ret);
                    }
                }
                return modelAndView;
            }
        };
        Properties p=new Properties();
        String messageView = "forward:"+ akaWebMvcProperties.getGlobalViews().get(ActionSupport.MESSAGE);
        String jsonView = "forward:"+ akaWebMvcProperties.getGlobalViews().get( ActionSupport.JSON);
        p.put(JspServiceException.class.getName(),messageView);
        p.put(JsonServiceException.class.getName(),jsonView);
        simpleMappingExceptionResolver.setExceptionMappings(p);
        //simpleMappingExceptionResolver.setExceptionAttribute(WebMvcCbConstants.SessionKey.ExceptionKey);
        simpleMappingExceptionResolver.setDefaultStatusCode(200);
        resolvers.add(0,simpleMappingExceptionResolver);

    }
}
