package com.github.ulwx.aka.webmvc;

import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.io.Resource;

import java.util.LinkedHashMap;
import java.util.Map;


public class CustomEnvironmentPostProcessor implements EnvironmentPostProcessor {

    //classpath*:aka-application-wb-admin-base.yml
    //classpath*:aka-application-wb-webvc.yml
    //classpath*:aka-application-wb-service-frame.yml
    private final static String resourceName="classpath*:aka-application-wb-*.yml";
    public MapPropertySource findAkaYmlSource() throws Exception{
        Map<String, Object> sourceMap=new LinkedHashMap<>();
        Resource[]  resources= ScanSupport.instance.getResources(resourceName);
        for(int i=0; i<resources.length; i++) {
            if (!resources[i].exists()) {
                throw new IllegalArgumentException("Resource " + resourceName + " does not exist");
            }
            Resource resource=resources[i];
            PropertiesPropertySource propertySource = null;
            if (resourceName.endsWith(".yml") || resourceName.endsWith(".yaml")) {
                YamlPropertiesFactoryBean factory = new YamlPropertiesFactoryBean();
                factory.setResources(resource);
                factory.afterPropertiesSet();
                propertySource = new PropertiesPropertySource(resourceName, factory.getObject());

            }
            sourceMap.putAll(propertySource.getSource());

        }
        MapPropertySource finalPropertiesPropertySource =
                new MapPropertySource(resourceName,sourceMap);
        return finalPropertiesPropertySource;
    }
    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        mergeProperties(environment);

    }

    private void mergeProperties(ConfigurableEnvironment environment) {
        try {
            MapPropertySource  mapPropertySource=findAkaYmlSource();
            addOrReplacePropertySource(environment.getPropertySources(), mapPropertySource);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addOrReplacePropertySource(MutablePropertySources propertySources,  MapPropertySource mapPropertySource) {
        if (propertySources.contains(resourceName)) {
            propertySources.replace(resourceName, mapPropertySource);
        } else {
            propertySources.addLast(mapPropertySource);
        }

    }
}
