package com.github.ulwx.aka.webmvc.web.action;

public class ForwardReulst implements Result{
    private String forwardURL;

    public String getForwardURL() {
        return forwardURL;
    }

    public void setForwardURL(String forwardURL) {
        this.forwardURL = forwardURL;
    }
    @Override
    public ResultType getType() {
        return ResultType.forward;
    }
}
