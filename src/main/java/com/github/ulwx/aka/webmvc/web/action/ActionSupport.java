package com.github.ulwx.aka.webmvc.web.action;

import com.github.ulwx.aka.webmvc.BeanGet;
import com.github.ulwx.aka.webmvc.utils.WebMvcCbConstants;
import com.github.ulwx.aka.webmvc.utils.WebMvcCbConstants.SessionKey;
import com.github.ulwx.aka.webmvc.web.action.CbResultJson.STATUS;
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
public  class ActionSupport {
	private static Logger logger = LoggerFactory.getLogger(ActionSupport.class);

	protected BeanGet beanGet;
	private String next;

	//全局预定义逻辑视图名称
	public static final String JSON = "json";
	public  static  final String OK = "ok";
	public  static  final String SUCCESS = "success";
	public  static  final String MAIN = "main";
	public  static  final String NEXT = "next";
	public  static  final String FORWARD = "forward";
	public  static  final String REDIRECT = "redirect";
	public  static  final String DOWNLOAD = "download";
	public  static  final String ERROR = "error";
	public  static  final String MESSAGE = "message";
	public  static  final String LOGIN="login";
	public static final String GATE="gate";

	public HttpServletRequest getRequest() {
		return ServletActionContext.getRequest();
	}

	public ActionContext getContext() {
		return ActionContext.getContext();
	}

	public HttpSession getSession() {
		return this.getRequest().getSession();
	}
	public static void setUserInfo(HttpServletRequest request,Object userInfo){
		request.getSession().setAttribute(WebMvcCbConstants.SessionKey.USER,userInfo);
	}
	public static  Object getUserInfo(HttpServletRequest request) {
		return   request.getSession().getAttribute(WebMvcCbConstants.SessionKey.USER);
	}
	public  static  Object getUserInfo(HttpSession session) {
		return   session.getAttribute(WebMvcCbConstants.SessionKey.USER);
	}
	public  void setUserInfo(Object userInfo) {
		this.getSession().setAttribute(WebMvcCbConstants.SessionKey.USER,userInfo);
	}
	public  Object getUserInfo() {
		return   this.getSession().getAttribute(WebMvcCbConstants.SessionKey.USER);
	}

	public void setNext(String next) {
		this.next = next;
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

	public <T> T getBean(Class<T> t) {
		Map<String, Object[]> rParms = (Map) getRequest().getParameterMap();
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

	public String getNext() {
		return next;
	}

	public String NEXT(String next){
		this.next=next;
		return NEXT;
	}
	public  String JSON_SUC(String message, Object data){
		CbResultJson rj=new CbResultJson();
		rj.setStatus(STATUS.SUC);
		rj.setData(data);
		rj.setError(0);
		rj.setMessage(message);
		ActionContext  ctx = ActionContext.getContext();
		String ret= ObjectUtils.toJsonString(rj);
		ctx.put(SessionKey.JsonKey, JSONP(ret));
		return JSON;

	}

	public String FOWARD_ERROR(String msg,String returnURL){
		ActionContext  ctx = ActionContext.getContext();
		ctx.put(WebMvcCbConstants.SessionKey.MsgKey,msg);
		ctx.put(WebMvcCbConstants.SessionKey.MsgReturnURL,returnURL);
		return ERROR;
	}
	public String FOWARD_MESSAGE(String msg,String returnURL){
		ActionContext  ctx = ActionContext.getContext();
		ctx.put(WebMvcCbConstants.SessionKey.MsgKey,msg);
		ctx.put(WebMvcCbConstants.SessionKey.MsgReturnURL,returnURL);
		return ERROR;
	}
	public  String JSON_SUC(){
		return JSON_SUC("成功",null);
	}

	public  String JSON_SUC(Object data){
		if(data instanceof String){
			return JSON_SUC(data.toString(),data);
		}
		return JSON_SUC("成功",data);
	}

	public  String JSON_ERR(String message){

		CbResultJson rj=new CbResultJson();
		rj.setStatus(CbResultJson.STATUS.ERR);
		rj.setMessage(message);
		rj.setError(CbResultJson.ERROR.COMMON_ERROR);
		ActionContext  ctx = ActionContext.getContext();
		String ret=ObjectUtils.toJsonString2(rj,true,true);
		ctx.put(SessionKey.JsonKey, JSONP(ret));
		//logger.debug("ret="+JSONP(ret));
		return JSON;
	}

	public  String JSON_ERR(Exception e, String message){
		CbResultJson rj=new CbResultJson();
		rj.setStatus(STATUS.ERR);
		rj.setMessage("["+message+"]"+e.toString());
		rj.setError(CbResultJson.ERROR.COMMON_ERROR);
		ActionContext  ctx = ActionContext.getContext();
		String ret= ObjectUtils.toJsonString2(rj,true,true);
		ctx.put(SessionKey.JsonKey, JSONP(ret));
		//logger.debug("ret="+JSONP(ret));
		return JSON;
	}
	public  String JSON_ERR(Integer errorCode, String message){

		CbResultJson rj=new CbResultJson();
		rj.setStatus(CbResultJson.STATUS.ERR);
		rj.setData(null);
		rj.setMessage(message);
		rj.setError(errorCode);
		ActionContext  ctx = ActionContext.getContext();
		String ret=ObjectUtils.toJsonString2(rj,true,true);
		ctx.put(SessionKey.JsonKey, JSONP(ret));
		//logger.debug("ret="+JSONP(ret));
		return JSON;
	}
	public  String JSON_ERR(Integer errorCode){

		CbResultJson rj=new CbResultJson();
		rj.setStatus(CbResultJson.STATUS.ERR);
		rj.setData(null);
		rj.setMessage(CbActionError.errors.get(errorCode));
		rj.setError(errorCode);
		ActionContext  ctx = ActionContext.getContext();
		String ret=ObjectUtils.toJsonString2(rj,true,true);
		ctx.put(SessionKey.JsonKey, JSONP(ret));
		//logger.debug("ret="+JSONP(ret));
		return JSON;
	}
	public String FORWARD(String url){
		ActionContext  ctx = ActionContext.getContext();
		ctx.put(SessionKey.ForwardKey,url);
		return FORWARD;
	}

	public String REDIRECT(String url){
		ActionContext  ctx = ActionContext.getContext();
		ctx.put(SessionKey.RedirectKey,url);
		return REDIRECT;
	}

	public String DOWNLOAD(File file,String fileName){
		ActionContext  ctx = ActionContext.getContext();
		ctx.put(SessionKey.DownloadKey,file);
		ctx.put(SessionKey.DownloadName,fileName);
		return DOWNLOAD;
	}
	public String GATE(String postURL,Map<String,String> gateReqMap){
		ActionContext  ctx = ActionContext.getContext();
		ctx.put(SessionKey.GateKey,postURL);
		ctx.put(SessionKey.GateReqMap,gateReqMap);
		return DOWNLOAD;
	}
	public  String JSON_ERR(Exception e){
		return JSON_ERR(e,e.toString());
	}
	protected String JSONP(String content) {
		RequestUtils ru = this.getRequestUtils();
		String funcName = ru.getString("callback");
		if (StringUtils.hasText(funcName)) {
			String ret = funcName + "(" + content + ")";
			return ret;
		} else {
			return content;
		}
	}

	public BeanGet getBeanGet() {
		return beanGet;
	}
	@Autowired
	public void setBeanGet(BeanGet beanGet) {
		this.beanGet = beanGet;
	}
}
