package com.github.ulwx.aka.webmvc.web.action;

public class ForwardResult implements Result{
    // 格式为 /<jspname>
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
