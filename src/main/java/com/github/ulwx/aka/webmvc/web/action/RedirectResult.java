package com.github.ulwx.aka.webmvc.web.action;

public class RedirectResult implements Result{
    private String RedirectURL;

    public String getRedirectURL() {
        return RedirectURL;
    }
    public void setRedirectURL(String redirectURL) {
        RedirectURL = redirectURL;
    }

    @Override
    public ResultType getType() {
        return ResultType.redirect;
    }
}
