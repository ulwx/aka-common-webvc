package com.github.ulwx.aka.webmvc.web.action;

import com.github.ulwx.aka.webmvc.*;
import com.ulwx.tool.ObjectUtils;
import com.ulwx.tool.RequestUtils;
import com.ulwx.tool.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.util.Map;

import static com.github.ulwx.aka.webmvc.WebMvcCbConstants.TRANSLATE;

public  class ActionSupport {
	private static Logger logger = LoggerFactory.getLogger(ActionSupport.class);

	protected BeanGet beanGet;
	//全局预定义逻辑视图名称
	public static final String JSON = "json";
	public  static  final String FORWARD = "forward";
	public  static  final String REDIRECT = "redirect";
	public  static  final String DOWNLOAD = "download";
	public  static  final String ERROR = "error";
	public  static  final String MESSAGE = "message";
	public static final String GATE="gate";

	public  static  final String _OK = "ok";
	public  static  final String _SUCCESS = "success";
	public  static  final String _MAIN = "main";

	public  static  final String LOGIN="login";


	public HttpServletRequest getRequest() {
		return ServletActionContext.getRequest();
	}

	public ActionContext getContext() {
		return ActionContext.getContext();
	}

	public HttpSession getSession() {
		return this.getRequest().getSession();
	}
	public static void setUserInfo(HttpServletRequest request, SessionUser userInfo){
		request.getSession().setAttribute(WebMvcCbConstants.USER,userInfo);
	}
	public static  SessionUser getUserInfo(HttpServletRequest request) {
		return   (SessionUser)request.getSession().getAttribute(WebMvcCbConstants.USER);
	}
	public  static  SessionUser getUserInfo(HttpSession session) {
		return   (SessionUser)session.getAttribute(WebMvcCbConstants.USER);
	}
	public  void setUserInfo(SessionUser userInfo) {
		this.getSession().setAttribute(WebMvcCbConstants.USER,userInfo);
	}
	public  SessionUser getUserInfo() {
		return   (SessionUser)this.getSession().getAttribute(WebMvcCbConstants.USER);
	}


	/**
	 * 获取HttpServletResponse对象
	 *
	 * @return
	 */
	public HttpServletResponse getHttpResponse() {
		HttpServletResponse response = ServletActionContext.getResponse();

		return response;
	}
	@SuppressWarnings("rawtypes")
	public RequestUtils getRequestUtils() {
		RequestUtils requestUtils= ActionContext.getContext().getRequestUtils(this.getRequest());
		return requestUtils;

	}

	public ActionMethodInfo getActionMethodInfo(){
		ActionMethodInfo actionMethodInfo=(ActionMethodInfo)
				ActionContext.getContext().get(WebMvcActiionContextConst.ActionMethodInfo);
		return actionMethodInfo;
	}
	public <T> T getBean(Class<T> t) {
		Map<String, Object[]> rParms = getRequestUtils().getrParms();
		try {
			logger.debug(ObjectUtils.toString(rParms));
			return ObjectUtils.fromMapToJavaBean(t.getConstructor().newInstance(), rParms);
		} catch (Exception e) {
			return null;
		}
	}

	public String getRemoteAddr() {
		String ip = "";
		try {
			HttpServletRequest request =ServletActionContext.getRequest();
			ip = StringUtils.trim(request.getHeader("X-Real-IP"));
			if (StringUtils.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
				ip = StringUtils.trim(request.getRemoteAddr());
			}

			// 可是，如果通过了多级反向代理的话，X-Forwarded-For的值并不止一个，而是一串Ｉｐ值
			// 是取X-Forwarded-For中第一个非unknown的有效IP字符串
			String[] str = ip.split(",");
			if (str != null && str.length > 1) {
				ip = str[0];
			}
		} catch (Exception e) {
			logger.error("", e);
		}

		return ip;

	}


	private CbResult buildResult(int status, int errorCode, Object result, String message){
		CbResult rj=null;
		if(result instanceof Result) {
			rj = ((Result)result).getResult(status, errorCode, message);
		}else{
			rj=new CbResult();
			rj.setStatus(status);
			rj.setData(result);
			rj.setError(errorCode);
			rj.setMessage(message);
		}
		ActionContext ctx = ActionContext.getContext();
		ctx.put(WebMvcCbConstants.ResultKey, rj);
		return rj;

	}
	public String errorView(int errorCode,String msg, String returnURL){
		MsgResult msgResult=new MsgResult();
		msgResult.setMsg(msg);
		msgResult.setReturnURL(returnURL);
		this.buildResult(Status.ERR,errorCode,msgResult,msg);
		return ERROR;
	}
	public String msgView(String msg,String returnURL){
		MsgResult msgResult=new MsgResult();
		msgResult.setMsg(msg);
		msgResult.setReturnURL(returnURL);
		ActionContext ctx = ActionContext.getContext();
		this.buildResult(Status.SUC,0,msgResult,msg);
		return MESSAGE;
	}
	public  String JsonViewSuc(String message, Object data){

		this.buildResult(Status.SUC,0,data,message);
		return JSON;

	}
	public void needTranslate(Translate translate){
		ActionContext ctx = ActionContext.getContext();
		ctx.put(TRANSLATE, translate );
	}
	public  String JsonViewSuc(){
		return JsonViewSuc("成功",null);
	}
	public  String JsonViewSuc(Object data){
		if(data instanceof String){
			return JsonViewSuc(data.toString(),data);
		}
		return JsonViewSuc("成功",data);
	}
	public  String JsonView(boolean suc,Object data,String message){
		if(suc){
			this.buildResult(Status.SUC,0,data,message);
		}else{
			this.buildResult(Status.ERR,0,null,message);
		}

		return JSON;
	}
	public  String JsonViewError(String message){
		this.buildResult(Status.ERR,0,null,message);
		return JSON;
	}
	public  String JsonViewError(String message,Object data){
		this.buildResult(Status.ERR,0,data,message);
		return JSON;
	}
	public CbResult getResult(String view){
		if(view==null) return null;
		ActionContext  ctx = ActionContext.getContext();
		return (CbResult)ctx.get(WebMvcCbConstants.ResultKey);

	}
	public CbResult result(String view){
		return getResult(view);

	}
	public  String JsonViewError(Integer errorCode, String message){

		this.buildResult(Status.ERR,errorCode,null,message);
		return JSON;
	}
	public  String JsonViewError(Integer errorCode, String message,Object data){

		this.buildResult(Status.ERR,errorCode,data,message);
		return JSON;
	}
	public  String JsonViewError(Integer errorCode){
		this.buildResult(Status.ERR,errorCode,null,ErrorCode.errors.get(errorCode));
		return JSON;

	}
	public String ForwardView(String url){
		ForwardResult forwardReulst=new ForwardResult();
		forwardReulst.setForwardURL(url);
		this.buildResult(Status.SUC,0,forwardReulst,"成功！");
		return FORWARD;
	}

	public String RedirectView(String url, Object Data){

		RedirectResult redirectResult=new RedirectResult();
		redirectResult.setRedirectURL(url);
		redirectResult.setData(Data);
		this.buildResult(Status.SUC,0,redirectResult,"成功！");
		return REDIRECT;
	}
	public String RedirectView(String url){

		RedirectResult redirectResult=new RedirectResult();
		redirectResult.setRedirectURL(url);
		this.buildResult(Status.SUC,0,redirectResult,"成功！");
		return REDIRECT;
	}
	public String DownloadView(File file,String fileName){

		DownLoadResult downLoadReulst=new DownLoadResult();
		downLoadReulst.setFile(file);
		downLoadReulst.setFileName(fileName);
		this.buildResult(Status.SUC,0,downLoadReulst,"成功！");
		return DOWNLOAD;
	}
	public String GateView(String postURL,Map<String,String> gateReqMap){
		GateResult gateResult=new GateResult();
		gateResult.setGateURL(postURL);
		gateResult.setReqMap(gateReqMap);
		this.buildResult(Status.SUC,0,gateResult, "");
		return GATE;
	}

	public BeanGet getBeanGet() {
		return beanGet;
	}
	@Autowired
	public void setBeanGet(BeanGet beanGet) {
		this.beanGet = beanGet;
	}
}
