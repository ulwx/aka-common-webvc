package com.github.ulwx.aka.webmvc.exception;

public class JspServiceException extends ServiceException {
    private ServiceException se=null;
    public JspServiceException(ServiceException se) {
        super(se.getMessage(),se.getCause(),se.getErrorCode());
        this.se=se;

    }
}
