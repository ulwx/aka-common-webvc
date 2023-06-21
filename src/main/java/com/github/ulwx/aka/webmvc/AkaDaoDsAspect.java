package com.github.ulwx.aka.webmvc;

import com.github.ulwx.aka.dbutils.spring.multids.AkaAbsctractDataSourceAspect;
import com.github.ulwx.aka.dbutils.spring.multids.DataSourceAspectInfo;
import com.github.ulwx.aka.webmvc.AkaWebMvcProperties.DS.DeciderStrategy;
import com.ulwx.tool.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Aspect
@Order(-1)
@Component("com.github.ulwx.aka.webmvc.AkaDaoDsAspect")
@ConditionalOnProperty(prefix="aka.webvc.ds-config",
        name = "decider-strategy" ,havingValue = "PARENT_DIR_NAME")
public class AkaDaoDsAspect extends AkaAbsctractDataSourceAspect {

    private AkaWebMvcProperties properties;

    public AkaWebMvcProperties getProperties() {
        return properties;
    }

    @Autowired
    public void setProperties(AkaWebMvcProperties properties) {
        this.properties = properties;
    }

    private static final Logger log = LoggerFactory.getLogger(AkaDaoDsAspect.class);

    @Pointcut("target(com.github.ulwx.aka.webmvc.AkaDaoSupport)" +
            "&& !@annotation(com.github.ulwx.aka.dbutils.spring.multids.AkaDS) && " +
            " !within(com.github.ulwx.aka.webmvc.AkaDaoSupport) " +
            "&& !@within(com.github.ulwx.aka.dbutils.spring.multids.AkaDS)")
    @Override
    public void dsPointCut() {

    }

    @Override
    public DataSourceAspectInfo getDataSourceAspectInfo(ProceedingJoinPoint point) {
        MethodSignature signature = (MethodSignature) point.getSignature();
        AkaDaoSupport akaDaoSupport=(AkaDaoSupport)point.getTarget();
        String dynamicDataSourceBeanName=properties.getDsConfig().getAkaDynamicDataSourceName()
                ==null? "":properties.getDsConfig().getAkaDynamicDataSourceName().trim();
        String dsName=StringUtils.trim(akaDaoSupport.getDS());;
        if(dsName.isEmpty()) {
            String deciderStrategy = StringUtils.trim(properties.getDsConfig().getDeciderStrategy());
            if (deciderStrategy.equals(DeciderStrategy.PARENT_DIR_NAME) &&
                    StringUtils.isEmpty(dsName)) {
                String belongTypeName = point.getSignature().getDeclaringTypeName();
                String[] strs = belongTypeName.split("\\.");
                dsName = strs[strs.length - 2];
            }
        }
        if(dsName==null || dsName.isEmpty()){
            throw new RuntimeException("无法确定数据源名称！");
        }
        String laodBalancer=properties.getDsConfig().getGroupLoadBalancer()==null?"":
                properties.getDsConfig().getGroupLoadBalancer().trim();
        DataSourceAspectInfo dataSourceAspectInfo=new DataSourceAspectInfo(dsName,dynamicDataSourceBeanName,laodBalancer);
        return dataSourceAspectInfo;

    }



}

