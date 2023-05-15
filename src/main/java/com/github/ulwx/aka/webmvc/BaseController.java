package com.github.ulwx.aka.webmvc;

import com.github.ulwx.aka.webmvc.AkaWebMvcProperties.NameSpace;
import com.github.ulwx.aka.webmvc.annotation.AkaMvcAction;
import com.github.ulwx.aka.webmvc.annotation.AkaMvcActionMethod;
import com.github.ulwx.aka.webmvc.exception.JsonServiceException;
import com.github.ulwx.aka.webmvc.exception.JspServiceException;
import com.github.ulwx.aka.webmvc.exception.ServiceException;
import com.github.ulwx.aka.webmvc.utils.WebMvcCbConstants;
import com.github.ulwx.aka.webmvc.web.action.ActionContext;
import com.github.ulwx.aka.webmvc.web.action.ActionSupport;
import com.ulwx.tool.ArrayUtils;
import com.ulwx.tool.ObjectUtils;
import com.ulwx.tool.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Controller;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

@Controller
public class BaseController implements ApplicationContextAware {
    private static Logger log = Logger.getLogger(BaseController.class);
    private ApplicationContext applicationContext;
    private  static final AntPathMatcher pathMatcher = new AntPathMatcher();
    private AkaWebMvcProperties akaWebMvcProperties;

    @Autowired
    public void setAkaWebMvcProperties(AkaWebMvcProperties akaWebMvcProperties) {
        this.akaWebMvcProperties = akaWebMvcProperties;
    }

    @RequestMapping("/")
    public String hello() {
        int i = 0;
        return akaWebMvcProperties.getIndexUrl();

    }

    public final static class Properites{
        public static String  ModClassMethodName="mod-class-method-name";
        public static String  AllowMethods="allow-methods";


    }
    public static Map<String, String>  getViewMap(NameSpace nameSpace,String logicActionName){
        Map<String, Map<String, String>> map=nameSpace.getUrlMaps();
        Map<String ,String> vewMap=map.get(logicActionName);
        if(vewMap!=null) {
            return vewMap;
        }
        logicActionName=logicActionName.replace("_","-");
        for(String key: map.keySet()){
            if(pathMatcher.match(key.replace("_","-"),logicActionName)){
                return map.get(key);
            }
        }
       return null;
    }
    private void checkHttpMethods(String allowMethods,HttpServletRequest request){
        if(!allowMethods.isEmpty()){
            String[] strs= ArrayUtils.trim(allowMethods.split(","));
            if(!ArrayUtils.containsIgnoreCase(strs,request.getMethod())){
                throw new ServiceException("不支持http "+request.getMethod()+"请求！");
            }
        }
    }

    @RequestMapping(path = "/{namespace}/{logicActionName}.action")
    public String HandleRequest(HttpServletRequest request, @PathVariable String namespace,
                                @PathVariable String logicActionName) throws Exception {

        if (log.isDebugEnabled()) {
            log.debug("request URL:" + request.getRequestURL());
            log.debug("request parmaters:" + ObjectUtils.toString(ActionContext.getContext()
                    .getRequestUtils(request)));
        }

        if (namespace == null || namespace.isEmpty()) {
            throw new ServiceException("url里没有命名空间！");
        }
        String url = request.getRequestURL().toString();
        namespace = namespace.trim();
        boolean isJSONRequest = false;
        try {
            if (WebMvcCbConstants.isAjax(request)) {
                isJSONRequest = true;
            }
            String mod = null;
            String actionName = null;
            String actionClassName = null;
            String actionMethod = null;
            String actionMethodRealName = null;
            NameSpace nameSpace = akaWebMvcProperties.getNamespaces().get(namespace);
            String packagePrefix = nameSpace.getPackageName();
            String fullActionClassName = null;
            if (packagePrefix == null || packagePrefix.trim().isEmpty()) {
                throw new ServiceException("命名空间定义出错！");
            }

            if (logicActionName.endsWith("JSON")) {
                logicActionName = StringUtils.trimTailString(logicActionName, "JSON");
            }
            Map<String, String> viewsMap =
                    getViewMap(nameSpace,logicActionName);// nameSpace.getUrlMaps().get(logicActionName);
            if (viewsMap != null) {
                actionMethodRealName = StringUtils.trim(viewsMap.get(Properites.ModClassMethodName));
                String allowMethods=StringUtils.trim(viewsMap.get(Properites.AllowMethods));
                checkHttpMethods(allowMethods,request);

            }
            if (actionMethodRealName == null || actionMethodRealName.isEmpty()) {
                actionMethodRealName = logicActionName;
            }
            String[] strs = actionMethodRealName.split("_|\\-");
            if (strs.length != 3) {
                throw new ServiceException(actionMethodRealName + "请求非法！");
            } else {
                mod = strs[0];
                actionName = strs[1];
                actionClassName = strs[1] + "Action";
                actionMethod = strs[2];

            }
            fullActionClassName = packagePrefix + "." + mod + "." + actionClassName;
            if (log.isDebugEnabled()) {
                log.debug("action=" + fullActionClassName + ",actionMethod=" + actionMethod);
            }
            ActionSupport cba = null;
            String httpMethod="";
            try {
                cba=(ActionSupport) applicationContext.getBean(fullActionClassName);
               //判断@AkaMvcAction注解和 @AkaMvcActionMethod注解
                AkaMvcAction akaMvcActionAnno=cba.getClass().getAnnotation(AkaMvcAction.class);
                if(akaMvcActionAnno!=null){
                    httpMethod=StringUtils.trim(akaMvcActionAnno.httpMethod());
                }
            } catch (NoSuchBeanDefinitionException ext) {
                throw new ServiceException(fullActionClassName+"没有定义！");
            }

            Method m = cba.getClass().getMethod(actionMethod);
            AkaMvcActionMethod akaMvcActionMethod=m.getAnnotation(AkaMvcActionMethod.class);
            if(akaMvcActionMethod!=null){
                httpMethod=StringUtils.trim(akaMvcActionMethod.httpMethod());
            }
            checkHttpMethods(httpMethod,request);

            Object ret = m.invoke(cba);
            if (ret != null && ret.equals("json")) {
                if (log.isDebugEnabled()) {
                    log.debug("ret=" + ObjectUtils.toString(cba.getContext().get("json")));
                }
            } else {
                if (log.isDebugEnabled()) {
                    log.debug("ret=" + ret);
                }
            }
            String returnView = "";
            try {
                if (ret != null && ret instanceof String && !((String) ret).trim().isEmpty()) {
                    String viewURL = null;
                    ret = StringUtils.trim(ret);
                    if (viewsMap != null) {
                        viewURL = StringUtils.trim(viewsMap.get(ret));
                        if (StringUtils.hasText(viewURL)) {
                            String[] mstrs = viewURL.split(":");
                            if (mstrs.length == 2) {
                                String vtype = StringUtils.trim(mstrs[0]);
                                String vvalue = StringUtils.trim(mstrs[1]);
                                if (vtype.equals("redirect")) {
                                    returnView = "redirect:" + vvalue;
                                    return returnView;
                                } else if (vtype.equals("forward")) {
                                    returnView = "forward:" + vvalue;
                                    return returnView;
                                } else {
                                    throw new ServiceException(actionMethodRealName + "请求非法！[" + viewURL + "配置非法]");
                                }
                            } else {
                                returnView = "forward:" + viewURL;
                                return returnView;
                            }
                        }
                    }

                    //查找全局逻辑视图
                    /**
                     *  namespace.global-views.ok=/jsp/{namespace}/{action-name}/{action-name}.jsp
                     *  {namespace}: 当前请求action的所在的命名空间
                     *  {mod} : 当前请求action的模块名
                     *  {action-name} : 为action类名去掉Action后缀后的字符串，即action名
                     *  {action-method} : 处理当前请求的action处理请求的方法名
                     *  {next}  : 当前请求action对象的next属性，
                     */
                    viewURL = akaWebMvcProperties.getGlobalViews().get(ret);
                    if (StringUtils.hasText(viewURL)) {
                        viewURL = viewURL.replace("{namespace}", namespace);
                        viewURL = viewURL.replace("{mod}", mod);
                        viewURL = viewURL.replace("{action-name}", actionName);
                        viewURL = viewURL.replace("{action-method}", actionMethod);
                        if (ret.toString().equals("next")) {
                            viewURL = viewURL.replace("{next}", cba.getNext());
                        }

                        String[] mstrs = viewURL.split(":");
                        if (mstrs.length == 2) {
                            String vtype = StringUtils.trim(mstrs[0]);
                            String vvalue = StringUtils.trim(mstrs[1]);
                            if (vtype.equals("redirect")) {
                                returnView = "redirect:" + vvalue;
                                return returnView;
                            } else {
                                returnView = "forward:" + vvalue;
                                return returnView;
                            }
                        } else {
                            returnView = "forward:" + viewURL;
                            return returnView;
                        }
                    } else {
                        throw new ServiceException(logicActionName + "没有找到相应的视图！");
                    }

                } else {
                    throw new ServiceException("方法返回的逻辑视图为空！" + fullActionClassName + "#" + m + "()");
                }
            } finally {
                if (log.isDebugEnabled()) {
                    log.debug("returnView=" + returnView);
                }
            }

        } catch (Exception ex) {
            log.error(ex + "", ex);
            Exception targetException = null;
            if (ex instanceof InvocationTargetException) {
                InvocationTargetException itException = (InvocationTargetException) ex;
                targetException = (Exception) itException.getCause();
            } else {
                targetException = ex;
            }
            ServiceException sex = null;
            if (targetException instanceof ServiceException) {
                sex = (ServiceException) targetException;
            } else {
                sex = new ServiceException(targetException);
            }
            if (isJSONRequest) {
                throw new JsonServiceException(sex);
            } else {
                throw new JspServiceException(sex);
            }
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
