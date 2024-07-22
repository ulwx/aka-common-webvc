package com.github.ulwx.aka.webmvc.web.action;

public class MsgResult implements Result{

    private String msg="";
    private String returnURL="";
    private Boolean showButton=true;

    public Boolean getShowButton() {
        return showButton;
    }

    public void setShowButton(Boolean showButton) {
        this.showButton = showButton;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getReturnURL() {
        return returnURL;
    }

    public void setReturnURL(String returnURL) {
        this.returnURL = returnURL;
    }

    @Override
    public ResultType getType() {
        return ResultType.msg;
    }

}
