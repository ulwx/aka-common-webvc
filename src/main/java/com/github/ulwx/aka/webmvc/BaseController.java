package com.github.ulwx.aka.webmvc;

import com.github.ulwx.aka.webmvc.AkaWebMvcProperties.NameSpace;
import com.github.ulwx.aka.webmvc.annotation.AkaMvcAction;
import com.github.ulwx.aka.webmvc.annotation.AkaMvcActionMethod;
import com.github.ulwx.aka.webmvc.exception.JsonServiceException;
import com.github.ulwx.aka.webmvc.exception.JspServiceException;
import com.github.ulwx.aka.webmvc.exception.ServiceException;
import com.github.ulwx.aka.webmvc.utils.WebMvcUtils;
import com.github.ulwx.aka.webmvc.web.action.*;
import com.ulwx.tool.ArrayUtils;
import com.ulwx.tool.ObjectUtils;
import com.ulwx.tool.RequestUtils;
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

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
public class BaseController implements ApplicationContextAware {
    private static Logger log = Logger.getLogger(BaseController.class);
    private ApplicationContext applicationContext;
    private static final AntPathMatcher pathMatcher = new AntPathMatcher();
    private AkaWebMvcProperties akaWebMvcProperties;
    private List<RequestProcessor> requestProcessors = new ArrayList<>();
    private List<LogicActionExeInfoBuilder> logicActionMethodNameBuilders = new ArrayList<>();

    @RequestMapping("/")
    public String hello() {
        return akaWebMvcProperties.getIndexUrl();
    }

    @Autowired
    public void setAkaWebMvcProperties(AkaWebMvcProperties akaWebMvcProperties) {
        this.akaWebMvcProperties = akaWebMvcProperties;
    }


    public List<RequestProcessor> getRequestProcessors() {
        return requestProcessors;
    }

    public List<LogicActionExeInfoBuilder> getLogicActionMethodNameBuilders() {
        return logicActionMethodNameBuilders;
    }

    @Autowired(required = false)
    public void setLogicActionMethodNameBuilders(List<LogicActionExeInfoBuilder> logicActionMethodNameBuilders) {
        this.logicActionMethodNameBuilders = logicActionMethodNameBuilders;
    }

    @Autowired(required = false)
    public void setRequestProcessors(List<RequestProcessor> requestProcessors) {
        this.requestProcessors = requestProcessors;
    }

    public final static class Properites {
        public static String ModClassMethodName = "mod-class-method-name";
        public static String AllowMethods = "allow-methods";


    }

    public static Map<String, String> getViewMap(NameSpace nameSpace, String logicActionName) {
        Map<String, Map<String, String>> map = nameSpace.getUrlMaps();
        Map<String, String> vewMap = map.get(logicActionName);
        if (vewMap != null) {
            return vewMap;
        }
        logicActionName = logicActionName.replace("_", "-");
        for (String key : map.keySet()) {
            if (pathMatcher.match(key.replace("_", "-"), logicActionName)) {
                return map.get(key);
            }
        }
        return null;
    }

    private void checkHttpMethods(String allowMethods, HttpServletRequest request) {
        if (!allowMethods.isEmpty()) {
            String[] strs = ArrayUtils.trim(allowMethods.split(","));
            if (!ArrayUtils.containsIgnoreCase(strs, request.getMethod())) {
                throw new ServiceException("不支持http " + request.getMethod() + "请求！");
            }
        }
    }

    private Object onBefore(HttpServletRequest request, ActionMethodInfo actionMethodInfo, RequestUtils context) {
        for (int i = 0; i < requestProcessors.size(); i++) {
            RequestProcessor requestProcessor = requestProcessors.get(i);
            Object ret = requestProcessor.onBefore(request, actionMethodInfo, context);
            if (ret != null) return ret;

        }
        return null;
    }

    private Object onAfter(HttpServletRequest request, ActionMethodInfo actionMethodInfo, RequestUtils context, Object result) {
        for (int i = 0; i < requestProcessors.size(); i++) {
            RequestProcessor requestProcessor = requestProcessors.get(i);
            Object ret = requestProcessor.onAfter(request, actionMethodInfo, context, result);
            if (ret != null) return ret;

        }
        return null;
    }

    private Object onExcepton(HttpServletRequest request, ActionMethodInfo actionMethodInfo, RequestUtils context) {
        for (int i = 0; i < requestProcessors.size(); i++) {
            RequestProcessor requestProcessor = requestProcessors.get(i);
            Object ret = requestProcessor.onException(request, actionMethodInfo, context);
            if (ret != null) return ret;

        }
        return null;
    }

    private Object onFinished(HttpServletRequest request, ActionMethodInfo actionMethodInfo, RequestUtils context, Object result) {
        for (int i = 0; i < requestProcessors.size(); i++) {
            RequestProcessor requestProcessor = requestProcessors.get(i);
            Object ret = requestProcessor.onFinished(request, actionMethodInfo, context, result);
            if (ret != null) return ret;

        }
        return null;
    }

    public static boolean checkMethod(Method method){
        if(method.getParameterTypes()!=null && method.getParameterTypes().length>1){
            return false;
        }
        if(method.getParameterTypes().length==1){
            Class pType=method.getParameterTypes()[0];
            if(!CbRequestJson.class.isAssignableFrom(pType)){
                return false;
            }
        }
        if(method.getReturnType()!=String.class
                &&  !CbResultJson.class.isAssignableFrom(method.getReturnType())){
            return false;
        }
        return true;
    }
    private Object run(ActionMethodInfo actionMethodInfo,
                       HttpServletRequest request) throws Exception {
        RequestUtils requestUtils = ActionContext.getContext().getRequestUtils(request);
        ActionContext.getContext().put(WebMvcActiionContextConst.ActionMethodInfo,actionMethodInfo);
        ActionSupport cba = actionMethodInfo.getActionObj();
        Object ret = null;
        int exeStatus = 0;
        try {
            Object beforeRet = this.onBefore(request, actionMethodInfo, requestUtils);
            exeStatus = 1;
            if (beforeRet == null) {
                Method method=actionMethodInfo.getAnnoClassMethodInfo().getMethod();
                boolean checked=checkMethod(method);
                if(!checked){
                    throw new ServiceException(method+"方法不满足执行条件!");
                }
                Type returnType=method.getGenericReturnType();
                Type[] parmaterTypes=method.getGenericParameterTypes();
                Object bodyObj=null;
                boolean hasParam=false;
                if(parmaterTypes !=null){
                    if(parmaterTypes.length>1) throw new ServiceException("请求的参数的个数不能大于1个！");
                    if(parmaterTypes.length==1){
                        ParameterizedType pType=(ParameterizedType)parmaterTypes[0];
                        bodyObj=requestUtils.getBody(pType);
                        hasParam=true;
                    }

                }
                if(hasParam) {
                    ret = method.invoke(cba,bodyObj);
                }else{
                    ret = method.invoke(cba);
                }
                if(ret!=null){
                    if(ret instanceof CbResultJson){
                        Result dataResult=(Result)((CbResultJson<?>) ret).getData();
                        request.setAttribute(WebMvcCbConstants.ResultKey,
                                ret );
                        ret=dataResult.getType().toString();

                    }
                }
                exeStatus = 2;
                Object afterRet = this.onAfter(request, actionMethodInfo, requestUtils, ret);
                exeStatus = 3;
                if (afterRet != null) {
                    ret = afterRet;
                }
            } else {
                ret = beforeRet;
            }

        } catch (Exception e) {
            log.error(e+",exeStatus="+exeStatus+",ret="+ret,e);
            if (exeStatus == 1) {
                Object exceptonRet = this.onExcepton(request, actionMethodInfo, requestUtils);
                exeStatus = 4;
                if (exceptonRet != null) {
                    ret = exceptonRet;
                }
            }
            throw e;
        } finally {
            if (exeStatus == 3 || exeStatus == 4) {
                Object finishedRet = this.onFinished(request, actionMethodInfo, requestUtils, ret);
                if (finishedRet != null) {
                    ret = finishedRet;
                }
            }

        }
        return ret;
    }

    private ActionMethodInfo parseRequest(NameSpace nameSpace,
                                          String logicActionMethodName,
                                          HttpServletRequest request) {
        String mod = null;
        String actionName = null;
        String actionClassName = null;
        String actionMethodName = null;
        String packagePrefix = nameSpace.getPackageName();
        String fullActionClassName = null;
        if (packagePrefix == null || packagePrefix.trim().isEmpty()) {
            throw new ServiceException("命名空间定义出错！");
        }

        if (logicActionMethodName.endsWith("JSON")) {
            logicActionMethodName = StringUtils.trimTailString(logicActionMethodName, "JSON");
        }
        Map<String, String> viewsMap =
                getViewMap(nameSpace, logicActionMethodName);// nameSpace.getUrlMaps().get(logicActionName);
        if (viewsMap != null && !viewsMap.isEmpty()) {
            String temp = StringUtils.trim(viewsMap.get(Properites.ModClassMethodName));
            if (!temp.isEmpty()){
                logicActionMethodName=temp;
            }
            String allowMethods = StringUtils.trim(viewsMap.get(Properites.AllowMethods));
            checkHttpMethods(allowMethods, request);
        }
        if (logicActionMethodName == null || logicActionMethodName.isEmpty()) {
            //////////////////////////////////////
            for (int i = 0; i < this.logicActionMethodNameBuilders.size(); i++) {
                LogicActionExeInfoBuilder logicActionMethodNameBuilder = logicActionMethodNameBuilders.get(i);
                LogicActionExeInfo result = logicActionMethodNameBuilder.build(nameSpace, logicActionMethodName, request);
                if (result != null) {
                    logicActionMethodName = result.toString();
                    break;
                }
            }
        }

        String[] strs = logicActionMethodName.split("_|\\-");
        if (strs.length != 3) {
            throw new ServiceException(logicActionMethodName + "请求非法！");
        } else {
            mod = strs[0];
            actionName = strs[1];
            actionClassName = strs[1] + "Action";
            actionMethodName = strs[2];

        }
        fullActionClassName = packagePrefix + "." + mod + "." + actionClassName;
        if (log.isDebugEnabled()) {
            log.debug("action=" + fullActionClassName + ",actionMethod=" + actionMethodName);
        }
        ActionSupport cba = null;
        try {
            cba = (ActionSupport) applicationContext.getBean(fullActionClassName);
        } catch (NoSuchBeanDefinitionException ext) {
            throw new ServiceException(fullActionClassName + "没有定义！");
        }
        ActionMethodInfo actionMethodInfo = new ActionMethodInfo();
        actionMethodInfo.setNamespace(nameSpace);
        actionMethodInfo.setLogicActionMethodName(logicActionMethodName);
        actionMethodInfo.setViewsMap(viewsMap);
        actionMethodInfo.setMethodName(actionMethodName);
        actionMethodInfo.setActionLogicName(actionName);
        actionMethodInfo.setMod(mod);
        actionMethodInfo.setActionClassFullName(fullActionClassName);
        actionMethodInfo.setActionObj(cba);
        return actionMethodInfo;
    }

   private AnnoClassMethodInfo fetchAnnoClassMethodInfo(Class<?> actionClass,String methodName) throws Exception{
        AnnoClassMethodInfo annoClassMethodInfo=new AnnoClassMethodInfo();
        String httpMethod="";
        String requestContentType="";
        String responseContentType="";
        AkaMvcAction akaMvcActionAnno = actionClass.getAnnotation(AkaMvcAction.class);
        if (akaMvcActionAnno != null) {
            httpMethod = StringUtils.trim(akaMvcActionAnno.httpMethod());
            requestContentType=StringUtils.trim(akaMvcActionAnno.requestContentType());
            responseContentType=StringUtils.trim(akaMvcActionAnno.responseContentType());
        }
        Method[] methods=actionClass.getMethods();
        Method method=null;
        for(int i=0; i<methods.length; i++){
            if(methods[i].getName().equals(methodName)){
                method=methods[i];
                break;
            }
        }
        if(method==null){
            throw new ServiceException(actionClass+"无法找到"+methodName+"方法！");
        }
        //method = actionClass.getMethod(methodName);
        annoClassMethodInfo.setMethod(method);
        annoClassMethodInfo.setActionClass(actionClass);
        AkaMvcActionMethod akaMvcActionMethod = method.getAnnotation(AkaMvcActionMethod.class);
        if (akaMvcActionMethod != null) {
            String httpMethod2 = StringUtils.trim(akaMvcActionMethod.httpMethod());
            String requestContentType2 =StringUtils.trim(akaMvcActionMethod.requestContentType());
            String responseContentType2 =StringUtils.trim(akaMvcActionMethod.responseContentType());
            if(!httpMethod2.isEmpty()){
                httpMethod=httpMethod2;
            }
            if(!requestContentType2.isEmpty()){
                requestContentType=requestContentType2;
            }
            if(!responseContentType2.isEmpty()){
                responseContentType=responseContentType2;
            }
        }
        annoClassMethodInfo.setHttpMethod(httpMethod);
        annoClassMethodInfo.setRequestContentType(requestContentType);
        annoClassMethodInfo.setResponseContentType(responseContentType);

        return annoClassMethodInfo;

    }

    private String getView(String viewURL, String LogicActionMethodName) {
        String returnView = "";
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
                    throw new ServiceException(LogicActionMethodName + "请求非法！[" + viewURL + "配置非法]");
                }
            } else {
                returnView = "forward:" + viewURL;
                return returnView;
            }
        }
        return returnView;

    }

    @RequestMapping(path = "/{namespace}/{logicActionMethodName}.action")
    public String HandleRequest(HttpServletRequest request, @PathVariable String namespace,
                                @PathVariable String logicActionMethodName) throws Exception {

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
        boolean isJSONResponse = false;
        try {
            if (WebMvcUtils.isAjax(request)) {
                isJSONResponse = true;
            }
            NameSpace nameSpace = akaWebMvcProperties.getNamespaces().get(namespace);
            ActionMethodInfo actionMethodInfo = this.parseRequest(nameSpace,
                    logicActionMethodName, request);

            AnnoClassMethodInfo annoClassMethodInfo =
                    fetchAnnoClassMethodInfo(actionMethodInfo.getActionObj().getClass(),
                    actionMethodInfo.getMethodName());

            String responseType=StringUtils.trim(annoClassMethodInfo.getResponseContentType());
            if(!responseType.isEmpty()){
                if(responseType.toLowerCase().contains("json")){
                    isJSONResponse=true;
                }
            }

            checkHttpMethods(annoClassMethodInfo.getHttpMethod(), request);
            actionMethodInfo.setAnnoClassMethodInfo(annoClassMethodInfo);
            actionMethodInfo.setJSONResponse(isJSONResponse);
            //运行处理方法
            Object ret = this.run(actionMethodInfo, request);
            ActionSupport cba = actionMethodInfo.getActionObj();
            if (ret != null && ret.equals("json")) {
                isJSONResponse = true;
                if (log.isDebugEnabled()) {
                    log.debug("ret=" + ObjectUtils.toString(cba.getContext().get(WebMvcCbConstants.ResultKey)));
                }
            } else {
                if (log.isDebugEnabled()) {
                    log.debug("ret=" + ret);
                }
            }
            if (ret != null && !(ret instanceof String)) {
                throw new ServiceException("方法返回的逻辑视图名称必须为字符串！" + actionMethodInfo.getActionClassFullName()
                        + "#" + actionMethodInfo.getMethodName() + "()");
            }
            String returnView = "";

            String viewURL = null;
            ret = StringUtils.trim(ret);
            Map<String, String> viewsMap = actionMethodInfo.getViewsMap();
            if (viewsMap != null) {
                viewURL = StringUtils.trim(viewsMap.get(ret));
                returnView = StringUtils.trim(this.getView(viewURL, actionMethodInfo.getLogicActionMethodName()));
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
            if (returnView.isEmpty() && StringUtils.hasText(viewURL)) {
                viewURL = viewURL.replace("{namespace}", namespace);
                viewURL = viewURL.replace("{mod}", actionMethodInfo.getMod());
                viewURL = viewURL.replace("{action-name}", actionMethodInfo.getActionLogicName());
                viewURL = viewURL.replace("{action-method}", actionMethodInfo.getMethodName());
                if (ret.toString().equals("next")) {
                    viewURL = viewURL.replace("{next}", actionMethodInfo.getActionObj().getNext());
                }
                returnView = this.getView(viewURL, actionMethodInfo.getLogicActionMethodName());

            }
            returnView=StringUtils.trim(returnView);
            if(StringUtils.isEmpty(returnView)) {
                throw new ServiceException(logicActionMethodName + "没有找到相应的视图！");
            }
            if (log.isDebugEnabled()) {
                log.debug("returnView=" + returnView);
            }
            return returnView;

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
            if (isJSONResponse) {
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
