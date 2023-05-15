package com.github.ulwx.aka.webmvc.exception;

public class ServiceException extends RuntimeException {

	private static final long serialVersionUID = 8164708610922822091L;
	private  String errorCode="0000";

	public ServiceException(Throwable cause) {
		super(cause);
	}
	public ServiceException(Throwable cause,String errorCode) {
		super(cause);
		this.errorCode=errorCode;
	}
	public ServiceException(String msg,Throwable cause) {
		super(msg,cause);
	}
	public ServiceException(String msg,Throwable cause,String errorCode) {
		super(msg,cause);
		this.errorCode=errorCode;

	}
	public ServiceException(String msg){
		super(msg);
	}
	public ServiceException(String msg,String errorCode) {
		super(msg);
		this.errorCode=errorCode;
	}

	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}
}
