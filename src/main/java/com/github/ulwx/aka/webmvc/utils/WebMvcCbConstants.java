package com.github.ulwx.aka.webmvc.utils;

import com.github.ulwx.aka.dbutils.tool.support.StringUtils;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletRequest;

public class WebMvcCbConstants {
	private static Logger log = Logger.getLogger(WebMvcCbConstants.class);
	public static class SessionKey {
		public final static String USER = "USER";
		public final static String MsgReturnURL = "ReturnURL";
		public final static String MsgKey = "Msg";
		public final static String JsonKey="json";
		public final static String ExceptionKey="ExceptionKey";
		public final static String RedirectKey = "redirect"; //forward
		public final static String ForwardKey = "forward"; //DOWNLOAD
		public final static String DownloadKey = "download";
		public final static String DownloadName="download-name";
		public final static String GateKey="gate";//
		public final static String GateReqMap="gate-req-map";
	}
	public static class OperType {
		// 操作类型：1：登陆 2：退出 3：查看 4：编辑 5：删除 6：添加
		public static Integer LOGIN = 1;
		public static Integer LOGOUT = 2;
		public static Integer LOOK = 3;
		public static Integer EDIT = 4;
		public static Integer DEL = 5;
		public static Integer ADD = 6;
	}
	public static String AjaxURLSTR="JSON.action";

	public static boolean isAjax(HttpServletRequest hreq) {
		String AjaxURLSTR= WebMvcCbConstants.AjaxURLSTR;
		String ruri = hreq.getRequestURI();
		if ((StringUtils.hasText(AjaxURLSTR) && ruri.contains(AjaxURLSTR))
				|| (hreq.getHeader("x-requested-with") != null
				&& hreq.getHeader("x-requested-with").equalsIgnoreCase("XMLHttpRequest"))
		) {
			return true;
		}
		return false;
	}
	public static boolean isAjax(String url) {
		String AjaxURLSTR= WebMvcCbConstants.AjaxURLSTR;
		if ((StringUtils.hasText(AjaxURLSTR) && url.contains(AjaxURLSTR))) {
			return true;
		}
		return false;
	}


}
