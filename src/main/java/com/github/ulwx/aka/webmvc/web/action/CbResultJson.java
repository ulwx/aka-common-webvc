package com.github.ulwx.aka.webmvc.web.action;

import com.ulwx.tool.ObjectUtils;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "CommonResult", description = "通用返回对象")
public class CbResultJson<T> {

		public static class STATUS{
			public  static final int SUC=1;
			public static final int ERR=0;
		}
		public static class ERROR{
			public static final int NO_ERROR=0;
			public static final int COMMON_ERROR=999;
		}
		//"状态，200表示成功， -100表示失败
		@Schema(name = "code", description = "状态码,200表示成功， -100表示失败")
		private Integer status= STATUS.SUC;
		//"错误码，只有status=-100时，error才有意义
		@Schema(name = "error", description = "错误码，只有status=-100时，error才有意义")
		private Integer error= ERROR.NO_ERROR;
		//承载的数据
		//"提示性信息"
		@Schema(name = "message", description = "提示性信息")
		private String message="成功";

		@Schema(name = "data", description = "承载的数据")
		private T data;

		public Integer getStatus() {
			return status;
		}
		public void setStatus(Integer status) {
			this.status = status;
		}
		public Integer getError() {
			return error;
		}
		public void setError(Integer error) {
			this.error = error;
		}
		public T getData() {
			return data;
		}
		public void setData(T data) {
			this.data = data;
		}
		public String getMessage() {
			return message;
		}

		public void setMessage(String message) {
			this.message = message;
		}

		
		public static  String SUC(String message,Object data){
			CbResultJson rj=new CbResultJson();
			rj.setStatus(STATUS.SUC);
			rj.setData(data);
			rj.setError(0);
			rj.setMessage(message);

			String ret= ObjectUtils.toJsonString2(rj,true,true);

			return ret;
		
		}
		
		public  static String SUC(Object data){
			CbResultJson rj=new CbResultJson();
			rj.setStatus(STATUS.SUC);
			rj.setError(ERROR.NO_ERROR);
			rj.setData(data);
			rj.setMessage("成功");
		
			String ret= ObjectUtils.toJsonString2(rj,true,true);
			return ret;
		}
		
		public static String ERR(String message){
			
		
			CbResultJson rj=new CbResultJson();
			rj.setStatus(STATUS.ERR);
			rj.setData(null);
			rj.setMessage(message);
			rj.setError(ERROR.COMMON_ERROR);
		
			String ret=ObjectUtils.toJsonString2(rj,true,true);
			return ret;
		}
		
		public static String ERR(Exception e,String message){
			CbResultJson rj=new CbResultJson();
			rj.setStatus(STATUS.ERR);
			rj.setMessage("["+message+"]"+e.toString());
			rj.setError(ERROR.COMMON_ERROR);

			String ret= ObjectUtils.toJsonString2(rj,true,true);
			return ret;
		}
		
		public static String ERR(Exception e){
			CbResultJson rj=new CbResultJson();
			rj.setStatus(STATUS.ERR);
			rj.setMessage(e.toString()+"");
			rj.setError(ERROR.COMMON_ERROR);
			String ret= ObjectUtils.toJsonString2(rj,true,true);
			return ret;
		}


	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
