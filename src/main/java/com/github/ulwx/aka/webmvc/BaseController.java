package com.github.ulwx.aka.webmvc;

import com.github.ulwx.aka.webmvc.AkaWebMvcProperties.NameSpace;
import com.github.ulwx.aka.webmvc.annotation.AkaMvcAction;
import com.github.ulwx.aka.webmvc.annotation.AkaMvcActionMethod;
import com.github.ulwx.aka.webmvc.exception.ServiceException;
import com.github.ulwx.aka.webmvc.utils.WebMvcUtils;
import com.github.ulwx.aka.webmvc.web.action.*;
import com.ulwx.tool.*;
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
import java.util.*;

@Controller("com.github.ulwx.aka.webmvc.BaseController")
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

    private String onBefore(HttpServletRequest request, ActionMethodInfo actionMethodInfo, RequestUtils context) {
        for (int i = 0; i < requestProcessors.size(); i++) {
            RequestProcessor requestProcessor = requestProcessors.get(i);
            String view=requestProcessor.onBefore(request, actionMethodInfo, context);
            if(view!=null) return view;

        }
        return null;

    }

    private void onAfter(HttpServletRequest request, ActionMethodInfo actionMethodInfo,
                           RequestUtils context, String result) {
        for (int i = 0; i < requestProcessors.size(); i++) {
            RequestProcessor requestProcessor = requestProcessors.get(i);
             requestProcessor.onAfter(request, actionMethodInfo, context, result);
        }
    }

    private void onExcepton(HttpServletRequest request, ActionMethodInfo actionMethodInfo,
                              RequestUtils context,
                              Exception e,ProcessorStatus status) {
        for (int i = 0; i < requestProcessors.size(); i++) {
            RequestProcessor requestProcessor = requestProcessors.get(i);
            requestProcessor.onException(request, actionMethodInfo, context,e,status);
        }
    }

    private void onFinished(HttpServletRequest request, ActionMethodInfo actionMethodInfo,
                              RequestUtils context, String result,Exception e,ProcessorStatus status) {
        for (int i = 0; i < requestProcessors.size(); i++) {
            RequestProcessor requestProcessor = requestProcessors.get(i);
            requestProcessor.onFinished(request, actionMethodInfo, context, result,e,status);

        }
    }

    public static boolean checkMethod(Method method){
        if(method.getParameterTypes()!=null && method.getParameterTypes().length>1){
            return false;
        }
        if(method.getReturnType()!=String.class
                &&  !CbResult.class.isAssignableFrom(method.getReturnType())){
            return false;
        }
        return true;
    }
    private String run(boolean isJSONResponse,ActionMethodInfo actionMethodInfo,
                       HttpServletRequest request) throws Exception {
        RequestUtils requestUtils = ActionContext.getContext().getRequestUtils(request);
        ActionContext.getContext().put(WebMvcActiionContextConst.ActionMethodInfo,actionMethodInfo);
        ActionSupport cba = actionMethodInfo.getActionObj();
        Object ret = null;
        String viewName="";
        Exception exception=null;
        ProcessorStatus processorStatus=null;
        Object argObj=null;
        try {
            processorStatus = ProcessorStatus.Start;
            String beforeView=this.onBefore(request, actionMethodInfo, requestUtils);
            if(beforeView==null) {
                processorStatus = ProcessorStatus.OnBeforeComplete;
                Method method = actionMethodInfo.getAnnoClassMethodInfo().getMethod();
                boolean checked = checkMethod(method);
                if (!checked) {
                    throw new ServiceException(method + "方法不满足执行条件!");
                }
                Type[] parmaterTypes = method.getGenericParameterTypes();

                boolean hasParam = false;
                if (parmaterTypes != null) {
                    if (parmaterTypes.length > 1) throw new ServiceException("请求的参数的个数不能大于1个！");
                    if (parmaterTypes.length == 1) {
                        if (parmaterTypes[0] instanceof ParameterizedType) {
                            ParameterizedType pType = (ParameterizedType) parmaterTypes[0];
                            Class<?> clazz=(Class<?>)pType.getRawType();
                            argObj = requestUtils.getBody(pType);
                            if(argObj instanceof CbRequest){
                                CbRequest cbRequest= (CbRequest)argObj;
                                if(cbRequest.getRequestId()==null ||
                                        cbRequest.getRequestId().trim().isEmpty()){
                                    throw new RuntimeException("请求里requestId必须设置！");
                                }
                            }
                        } else {
                            if (parmaterTypes[0] instanceof Class) {
                                Class clazz = (Class) parmaterTypes[0];
                                argObj = ObjectUtils.fromMapToJavaBean(clazz.newInstance(), requestUtils.getrParms());
                            } else {
                                throw new RuntimeException("不支持请求参数为" + parmaterTypes[0] + "类型！");
                            }
                        }
                        hasParam = true;
                    }

                }
                if (hasParam) {
                    ret = method.invoke(cba, argObj);
                } else {
                    ret = method.invoke(cba);
                }
                if (ret == null) {
                    viewName = ResultType.json.toString();
                    CbResult cbResult = new CbResult();
                    cbResult.setError(ErrorCode.VIEW_ERROR);
                    cbResult.setStatus(Status.ERR);
                    request.setAttribute(WebMvcCbConstants.ResultKey, cbResult);
                } else {
                    if (ret instanceof CbResult) {
                        Object dataResult = ((CbResult<?>) ret).getData();
                        if(argObj instanceof CbRequest){
                            CbRequest cbRequest= (CbRequest)argObj;
                            if(cbRequest.getRequestId()!=null &&
                                   !cbRequest.getRequestId().trim().isEmpty()){
                                ((CbResult<?>) ret).setRequestId(cbRequest.getRequestId());
                            }
                        }
                        request.setAttribute(WebMvcCbConstants.ResultKey, ret);
                        if (dataResult instanceof Result) {
                            viewName = ((Result) dataResult).getType().toString();
                        } else { //json
                            viewName = ResultType.json.toString();
                        }

                    } else if (ret instanceof String) {
                        viewName = ret.toString();
                    } else {
                        viewName = ResultType.json.toString();
                        CbResult cbResult = new CbResult();
                        cbResult.setData(ret);
                        request.setAttribute(WebMvcCbConstants.ResultKey, cbResult);
                    }
                }

            }else{
                viewName=beforeView;
            }

            //添加requestid，时间戳，path等
            if (argObj != null && argObj instanceof CbRequest) {
                CbRequest cbRequest = (CbRequest) argObj;
                CbResult resultTemp = (CbResult) request.getAttribute(WebMvcCbConstants.ResultKey);
                resultTemp.setRequestId(cbRequest.getRequestId());
                resultTemp.setTimestamp(CTime.formatLocalDateTime());
                resultTemp.setPath(request.getRequestURI().toString());
            }

            processorStatus = ProcessorStatus.onActionComplete;
            this.onAfter(request, actionMethodInfo, requestUtils, viewName);
            processorStatus = ProcessorStatus.OnAfterComplete;

        } catch (Exception e) {
            log.error(e+",exeStatus="+processorStatus+",ret="+ret,e);
            exception=e;
            try {
                this.onExcepton(request, actionMethodInfo, requestUtils, exception,processorStatus);
                processorStatus = ProcessorStatus.OnExceptionComplete;

            }catch (Exception ex){
                exception.addSuppressed(ex);
                log.error(ex+"",exception);
                processorStatus = ProcessorStatus.OnExceptionHasError;
            }finally {
                if(StringUtils.isEmpty(viewName)) {
                    viewName= this.handException(isJSONResponse,request,exception);
                }
            }

        } finally {
            this.onFinished(request, actionMethodInfo, requestUtils, viewName,exception,processorStatus);

        }
        return viewName;
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
        List<Method> methodList=new ArrayList<>();
        for(int i=0; i<methods.length; i++){
            if(methods[i].getName().equals(methodName)){
                Method method=methods[i];
                if(method.getParameterTypes()!=null && method.getParameterTypes().length>1){
                    continue;
                }
                if(method.getReturnType()!=String.class
                        &&  !CbResult.class.isAssignableFrom(method.getReturnType())){
                    continue;
                }
                methodList.add(method);
            }
        }
       Method method=null;
       if(methodList.size()!=1){
           //throw new ServiceException(actionClass+"不能存在多个"+methodName+"名称方法！");
           //查找无参的
           for(Method m: methodList){
               if(m.getParameterTypes()==null || m.getParameterTypes().length==0){
                   method=m;
                   break;
               }
           }
       }else{
           method=methodList.get(0);
       }
       if(method==null){
           throw new ServiceException(actionClass+"无法找到符合"+methodName+"条件的方法！方法必须只能" +
                   "有一个参数或无参数，返回类型必须为String或CbResult类型！");
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

        ActionContext.getContext()
                .getRequestUtils(request);
        if (log.isDebugEnabled()) {
            log.debug("request URL:" + request.getRequestURL());
            log.debug("request parmaters:" + ObjectUtils.toString(ActionContext.getContext()
                    .getRequestUtils(request)));
        }

        if (namespace == null || namespace.isEmpty()) {
            throw new ServiceException("url里没有命名空间！");
        }
        //String url = request.getRequestURL().toString();
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
            String viewName = this.run(isJSONResponse,actionMethodInfo, request);
            ActionSupport cba = actionMethodInfo.getActionObj();
            if (viewName != null ) {
                if (viewName.equals("json")) {
                    isJSONResponse = true;
                }
            }
            if (log.isDebugEnabled()) {
               CbResult cbResult = (CbResult)cba.getContext().get(WebMvcCbConstants.ResultKey);
               if(cbResult !=null) {
                   log.debug("ret=" + ObjectUtils.toString(cbResult));
               }
            }
            String finalViewURL = "";
            String viewURL = null;
            viewName = StringUtils.trim(viewName);
            Map<String, String> viewsMap = actionMethodInfo.getViewsMap();
            if (viewsMap != null) {
                viewURL = StringUtils.trim(viewsMap.get(viewName));
                finalViewURL = StringUtils.trim(this.getView(viewURL, actionMethodInfo.getLogicActionMethodName()));
            }

            //查找全局逻辑视图
            /**
             *  namespace.global-views.ok=/jsp/{namespace}/{action-name}/{action-name}.jsp
             *  {namespace}: 当前请求action的所在的命名空间
             *  {mod} : 当前请求action的模块名
             *  {action-name} : 为action类名去掉Action后缀后的字符串，即action名
             *  {action-method} : 处理当前请求的action处理请求的方法名
             */
            viewURL = akaWebMvcProperties.getGlobalViews().get(viewName);
            if (finalViewURL.isEmpty() && StringUtils.hasText(viewURL)) {
                viewURL = viewURL.replace("{namespace}", namespace);
                viewURL = viewURL.replace("{mod}", actionMethodInfo.getMod());
                viewURL = viewURL.replace("{action-name}", actionMethodInfo.getActionLogicName());
                viewURL = viewURL.replace("{action-method}", actionMethodInfo.getMethodName());

                finalViewURL = this.getView(viewURL, actionMethodInfo.getLogicActionMethodName());

            }
            finalViewURL=StringUtils.trim(finalViewURL);
            if(StringUtils.isEmpty(finalViewURL)) {
                throw new ServiceException(logicActionMethodName + "没有找到相应的视图！");
            }
            if (log.isDebugEnabled()) {
                log.debug("returnView=" + finalViewURL);
            }
            return finalViewURL;

        } catch (Exception ex) {
            return this.handException(isJSONResponse,request,ex);

        }
    }

    private String handException(boolean isJSONResponse,HttpServletRequest request,Exception ex){
        log.error(ex + "", ex);
        Exception targetException = null;
        if (ex instanceof InvocationTargetException) {
            InvocationTargetException itException = (InvocationTargetException) ex;
            targetException = (Exception) itException.getCause();
        } else {
            targetException = ex;
        }
        String message=targetException+"";
        if (isJSONResponse) {
            String callBack=request.getParameter("callback");
            ActionContext.getContext().getRequestUtils(request).setString("callback",callBack);
            CbResult ret= CbResult.of(Status.ERR,0, "服务器异常，请联系管理员！【"+message+"】",null);
            ActionContext ctx = ActionContext.getContext();
            ctx.put(WebMvcCbConstants.ResultKey, ret);
            return ActionSupport.JSON;
        } else {
            MsgResult msgResult=new MsgResult();
            msgResult.setMsg( ex.getMessage());
            msgResult.setReturnURL("");
            CbResult ret= CbResult.of(Status.ERR,0, "服务器异常，请联系管理员！【"+message+"】", msgResult);
            ActionContext ctx = ActionContext.getContext();
            ctx.put(WebMvcCbConstants.ResultKey, ret);
            return ActionSupport.MESSAGE;
        }
    }
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
