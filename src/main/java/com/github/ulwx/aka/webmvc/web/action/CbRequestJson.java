package com.github.ulwx.aka.webmvc.web.action;

import io.swagger.v3.oas.annotations.media.Schema;

public class CbRequestJson<T> {

	//"状态，200表示成功， -100表示失败
		@Schema(name = "status", description = "状态码,1表示成功， 0表示失败")
		private Integer status= Status.SUC;
		//"错误码，只有status=-100时，error才有意义
		@Schema(name = "error", description = "错误码，只有status=-100时，error才有意义")
		private Integer error= ErrorCode.NO_ERROR;
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

		public static CbRequestJson of(int status , int errorCode, String message, Object data){
			CbRequestJson resultJson=new CbRequestJson();
			resultJson.status=status;
			resultJson.error=errorCode;
			resultJson.message=message;
			resultJson.data=data;
			return resultJson;

		}

}
