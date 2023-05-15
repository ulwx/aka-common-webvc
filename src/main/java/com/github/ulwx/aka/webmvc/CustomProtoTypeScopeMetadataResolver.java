package com.github.ulwx.aka.webmvc;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ScannedGenericBeanDefinition;
import org.springframework.context.annotation.ScopeMetadata;
import org.springframework.context.annotation.ScopeMetadataResolver;

public class CustomProtoTypeScopeMetadataResolver implements ScopeMetadataResolver {
    private static Logger logger = Logger.getLogger(CustomProtoTypeScopeMetadataResolver.class);
    private static final String SCOPE_SINGLETON = "singleton";
    private static final String SCOPE_PROTOTYPE = "prototype";
    private Class[] assginedTypes;

    public CustomProtoTypeScopeMetadataResolver(Class[] assginedTypes) {
        this.assginedTypes = assginedTypes;
    }

    @Override
    public ScopeMetadata resolveScopeMetadata(BeanDefinition definition) {
        logger.debug("beanClassName="+definition.getBeanClassName());
        ScopeMetadata scopeMetadata = new ScopeMetadata();
        if(definition instanceof ScannedGenericBeanDefinition){
            ScannedGenericBeanDefinition scannedGenericBeanDefinition = (ScannedGenericBeanDefinition)definition;
            try {
                Class beanClass=Class.forName(scannedGenericBeanDefinition.getBeanClassName());
                for(Class clazz:assginedTypes) {
                    if (clazz.isAssignableFrom(beanClass)) {
                        scopeMetadata.setScopeName(SCOPE_PROTOTYPE);
                        return scopeMetadata;
                    }
                }
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
        scopeMetadata.setScopeName(SCOPE_SINGLETON);
        return scopeMetadata;

    }
}