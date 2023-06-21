package com.github.ulwx.aka.webmvc;

import com.github.ulwx.aka.webmvc.exception.ServiceException;
import org.apache.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Component("com.github.ulwx.aka.webmvc.BeanGet")
public class BeanGet implements ApplicationContextAware {
    private  static Logger logger = Logger.getLogger(BeanGet.class);
    private  static volatile ApplicationContext applicationContext;
    private static Object lock=new Object();
    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException {
        if(applicationContext==null) {
            synchronized (lock) {
                if(applicationContext==null) {
                    applicationContext = context;
                    lock.notifyAll();
                }
            }
        }

    }

    public static ApplicationContext getApplicationContext() {
        if(applicationContext==null){
            synchronized (lock){
                if(applicationContext==null){
                    try {
                        lock.wait();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
        return applicationContext;
    }


    public   <T> T bean(Class<T> beanClass){
        try {
            return (T)getApplicationContext().getBean(beanClass);
        } catch (NoSuchBeanDefinitionException e) {
            logger.error(e+"",e);
            return null;
        }catch(Exception ex){
            logger.error(ex+"",ex);
            throw new ServiceException(ex);
        }
    }

    public   <T> Map<String, T> beans(Class<T> beanClass){
        try {
            return getApplicationContext().getBeansOfType(beanClass);
        } catch (NoSuchBeanDefinitionException e) {
            logger.debug(beanClass+" type of bean not exists in context! return null");
            return null;
        }catch(Exception ex){
            throw new ServiceException(ex);
        }
    }
    public  <T>  T bean(Class<T> beanClass, HttpServletRequest hreq){
        return bean(beanClass,hreq.getServletContext());
    }
    public  <T> T bean(Class<T> beanClass,String name){
        try {
            return (T)getApplicationContext().getBean(name,beanClass);
        } catch (NoSuchBeanDefinitionException e) {
            logger.debug(name+" bean not exists in context! return null");
            return null;
        }catch(Exception ex){
            throw new ServiceException(ex);

        }
    }
    public  <T>  T bean(Class<T> beanClass, ServletContext context){
        ApplicationContext ac = WebApplicationContextUtils.getWebApplicationContext(context);
        T userService= (T)ac.getBean(beanClass);
        return userService;

    }
    public static  <T> T getBean(Class<T> beanClass,String name){
        try {
            return (T)getApplicationContext().getBean(name,beanClass);
        } catch (NoSuchBeanDefinitionException e) {
            logger.debug(name+" bean not exists in context! return null");
            return null;
        }catch(Exception ex){
            throw new ServiceException(ex);

        }
    }

    public static  <T>  T getBean(Class<T> beanClass, HttpServletRequest hreq){
        return getBean(beanClass,hreq.getServletContext());
    }
    public static   <T>  T getBean(Class<T> beanClass, ServletContext context){
        ApplicationContext ac = WebApplicationContextUtils.getWebApplicationContext(context);
        T userService= (T)ac.getBean(beanClass);
        return userService;

    }


    public static   <T> T getBean(Class<T> beanClass){
        try {
            return (T)getApplicationContext().getBean(beanClass);
        } catch (NoSuchBeanDefinitionException e) {
            logger.debug(beanClass+" type of bean not exists in context! return null");
            return null;
        }catch(Exception ex){
            throw new ServiceException(ex);
        }
    }
    public static   <T> Map<String, T> getBeans(Class<T> beanClass){
        try {
            return getApplicationContext().getBeansOfType(beanClass);
        } catch (NoSuchBeanDefinitionException e) {
            logger.debug(beanClass+" type of bean not exists in context! return null");
            return null;
        }catch(Exception ex){
            throw new ServiceException(ex);
        }
    }
    public static   <T> Map<String, T> getBeans(Class<T> beanClass, ServletContext context){
        try {
            ApplicationContext ac = WebApplicationContextUtils.getWebApplicationContext(context);
            return ac.getBeansOfType(beanClass);
        } catch (NoSuchBeanDefinitionException e) {
            logger.debug(beanClass+" type of bean not exists in context! return null");
            return null;
        }catch(Exception ex){
            throw new ServiceException(ex);
        }
    }

    public static   <T> Map<String, T> getBeans(Class<T> beanClass, HttpServletRequest hreq){
        try {
            ApplicationContext ac = WebApplicationContextUtils.getWebApplicationContext(hreq.getServletContext());
            return ac.getBeansOfType(beanClass);
        } catch (NoSuchBeanDefinitionException e) {
            logger.debug(beanClass+" type of bean not exists in context! return null");
            return null;
        }catch(Exception ex){
            throw new ServiceException(ex);
        }
    }
}
