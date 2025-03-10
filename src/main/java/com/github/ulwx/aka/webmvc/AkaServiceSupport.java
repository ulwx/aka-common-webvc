package com.github.ulwx.aka.webmvc;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

public class AkaServiceSupport {
    private final static Logger logger = Logger.getLogger(AkaServiceSupport.class);
    protected BeanGet beanGet;

    public BeanGet getBeanGet() {
        return beanGet;
    }
    protected   <T> T beanGet(Class<T> beanClass) {
        return this.beanGet.bean(beanClass);
    }
    protected  <T>  T beanGet(Class<T> beanClass, HttpServletRequest hreq){
        return beanGet.bean(beanClass,hreq);
    }
    protected   <T> Map<String, T> beanManyGet(Class<T> beanClass){
        return beanGet.beans(beanClass);
    }
    protected  <T> T beanGet(Class<T> beanClass,String name){
        return beanGet.bean(beanClass,name);
    }

    @Autowired
    public void setBeanGet(BeanGet beanGet) {
        this.beanGet = beanGet;
    }



}
