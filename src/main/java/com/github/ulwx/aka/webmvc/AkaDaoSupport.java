package com.github.ulwx.aka.webmvc;

import com.github.ulwx.aka.dbutils.database.spring.MDataBaseTemplate;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

 public abstract class AkaDaoSupport {
    private final static Logger logger = Logger.getLogger(AkaDaoSupport.class);
    protected BeanGet beanGet;
    protected MDataBaseTemplate template;

    public BeanGet getBeanGet() {
        return beanGet;
    }
    @Autowired
    public void setBeanGet(BeanGet beanGet) {
        this.beanGet = beanGet;
    }

    public MDataBaseTemplate getTemplate() throws Exception{
        return template;
    }
    @Autowired
    public void setTemplate(MDataBaseTemplate template) {
        this.template = template;
    }

     /**
      * 如果子类覆盖此方法，并返回指定的数据源名称，
      * 则getTemplate()返回的MDataBaseTemplate实例会使用此数据源。
      * 当然也可以在Dao类上通过@AkaDS注解指定数据源。
      * @return
      */
    public 	 String getDS(){ return "" ;}
}
