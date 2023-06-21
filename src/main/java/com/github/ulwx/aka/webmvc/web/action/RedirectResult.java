package com.github.ulwx.aka.webmvc.web.action;

public class RedirectResult implements Result{
    private String RedirectURL;
    private Object data;
    private String parmName="ret";
    public String getRedirectURL() {
        return RedirectURL;
    }
    public void setRedirectURL(String redirectURL) {
        RedirectURL = redirectURL;
    }

    public Object getData() {
        return data;
    }

    public String getParmName() {
        return parmName;
    }

    public void setParmName(String parmName) {
        this.parmName = parmName;
    }

    public void setData(Object data) {
        this.data = data;
    }

    @Override
    public ResultType getType() {
        return ResultType.redirect;
    }
}
