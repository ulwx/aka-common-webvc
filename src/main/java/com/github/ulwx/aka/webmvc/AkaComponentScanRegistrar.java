package com.github.ulwx.aka.webmvc;

import com.github.ulwx.aka.webmvc.web.action.ActionSupport;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.boot.context.properties.bind.BindResult;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AkaComponentScanRegistrar implements ImportBeanDefinitionRegistrar, ResourceLoaderAware,
        EnvironmentAware {
    private ResourceLoader resourceLoader;
    private Environment environment;

    public ResourceLoader getResourceLoader() {
        return resourceLoader;
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment=environment;
    }

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader=resourceLoader;
    }
    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata,
                                        BeanDefinitionRegistry registry) {
        // 获取注解上的参数信息，即需要扫描的包路径
        Map<String, Object> annotationAttributes = importingClassMetadata.getAnnotationAttributes(AkaComponentScan.class.getName());

        List<String> basePackageList = new ArrayList<>();

        basePackageList.addAll(Arrays.stream((String[])annotationAttributes.get("basePackages")).filter(StringUtils::hasText)
                .collect(Collectors.toList()));

        basePackageList.addAll(Arrays.stream((Class[])annotationAttributes.get("basePackageClasses")).map(ClassUtils::getPackageName)
                .collect(Collectors.toList()));

        if (basePackageList.isEmpty()) {
            basePackageList.add(getDefaultBasePackage(importingClassMetadata));
        }
        AkaWebMvcProperties akaWebMvcProperties=null;
        BindResult<AkaWebMvcProperties> bindResult=
                Binder.get(environment).bind(AkaConst.AkaWebMvcPropertiesPrefx, Bindable.of(AkaWebMvcProperties.class));
        if(bindResult.isBound()){
            akaWebMvcProperties= bindResult.get();
            List<String> list=akaWebMvcProperties.getNamespaces().values().stream().map(nameSpace ->
                    nameSpace.getPackageName()).collect(Collectors.toList());
            if(list!=null && list.size()>0) {
                basePackageList.addAll(list);
            }
        }

        String[] basePackages =basePackageList.toArray(new String[0]);

        // 扫描包路径下的所有类，并将符合条件的类信息注册为 BeanDefinition
        ClassPathBeanDefinitionScanner scanner = new ClassPathBeanDefinitionScanner(registry,
                false);
        scanner.addIncludeFilter(new ScanServiceAndDaoTypeFilter(resourceLoader));
        scanner.addIncludeFilter(new ScanActionTypeFilter(resourceLoader));
        scanner.setScopeMetadataResolver(new CustomProtoTypeScopeMetadataResolver(
                new Class[]{ActionSupport.class}
        ));
        scanner.setBeanNameGenerator(new UniqueNameGenerator());
        scanner.scan(basePackages);
    }

    private static String getDefaultBasePackage(AnnotationMetadata importingClassMetadata) {
        return ClassUtils.getPackageName(importingClassMetadata.getClassName());
    }
}
