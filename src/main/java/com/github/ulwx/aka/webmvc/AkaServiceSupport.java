package com.github.ulwx.aka.webmvc;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

 public class AkaServiceSupport {
    private final static Logger logger = Logger.getLogger(AkaServiceSupport.class);
    protected BeanGet beanGet;

    public BeanGet getBeanGet() {
        return beanGet;
    }
    @Autowired
    public void setBeanGet(BeanGet beanGet) {
        this.beanGet = beanGet;
    }



}
