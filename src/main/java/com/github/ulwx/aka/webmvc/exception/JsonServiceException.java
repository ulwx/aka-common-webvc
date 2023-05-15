package com.github.ulwx.aka.webmvc.exception;


public class JsonServiceException extends ServiceException {
    public JsonServiceException(ServiceException se) {
        super(se.getMessage(),se.getCause(),se.getErrorCode());
    }

}
