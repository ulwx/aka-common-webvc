package com.github.ulwx.aka.webmvc.swagger;


import com.github.ulwx.aka.webmvc.web.action.ActionSupport;
import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.OpenAPI;
import org.apache.commons.collections4.map.HashedMap;
import org.springdoc.core.OpenAPIService;
import org.springdoc.core.PropertyResolverUtils;
import org.springdoc.core.SecurityService;
import org.springdoc.core.SpringDocConfigProperties;
import org.springdoc.core.customizers.OpenApiBuilderCustomizer;
import org.springdoc.core.customizers.ServerBaseUrlCustomizer;
import org.springdoc.core.providers.JavadocProvider;
import org.springdoc.webmvc.core.SpringWebMvcProvider;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Swagger3",
                version = "1.0",
                description = "Swagger3使用演示",
                contact = @Contact(name = "TOM")
        ),

        security = @SecurityRequirement(name = "Authorization"),
        externalDocs = @ExternalDocumentation(description = "参考文档",
                url = "https://github.com/swagger-api/swagger-core/wiki/Swagger-2.X---Annotations"
        )
)

@SecurityScheme(type = SecuritySchemeType.APIKEY, name = "Authorization", in = SecuritySchemeIn.HEADER)
//@Profile({"dev", "test"})
public class Swagger3Configuration implements ApplicationContextAware {

    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Bean
    public OpenAPIService openAPIBuilder(Optional<OpenAPI> openAPI,
                                         SecurityService securityParser,
                                         SpringDocConfigProperties springDocConfigProperties,
                                         PropertyResolverUtils propertyResolverUtils,
                                         Optional<List<OpenApiBuilderCustomizer>> openApiBuilderCustomisers,
                                         Optional<List<ServerBaseUrlCustomizer>> serverBaseUrlCustomisers,
                                         Optional<JavadocProvider> javadocProvider) {
        OpenAPIService openAPIService = new OpenAPIService(openAPI, securityParser, springDocConfigProperties, propertyResolverUtils,
                openApiBuilderCustomisers, serverBaseUrlCustomisers, javadocProvider);

        Map<String, ActionSupport> beansOfTypeRequestMappingHandlerMapping =
                applicationContext.getBeansOfType(ActionSupport.class);
        Map<String, Object> map = new HashedMap<>();
        for (String key : beansOfTypeRequestMappingHandlerMapping.keySet()) {
            if (key.endsWith("Action")) {
                map.put(key, beansOfTypeRequestMappingHandlerMapping.get(key));

            }
        }
        openAPIService.addMappings(map);
        return openAPIService;

    }

    @Bean
    public SpringWebMvcProvider springWebMvcProvider() {
        return new MySpringWebMvcProvider();
    }


}