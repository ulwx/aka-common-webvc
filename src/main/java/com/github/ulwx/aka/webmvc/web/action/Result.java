package com.github.ulwx.aka.webmvc.web.action;

public interface Result {

    public ResultType getType();

    public default CbResult<? extends Result> getResult(int status, int errorCode, String message){
        CbResult rj=new CbResult();
        rj.setStatus(status);
        rj.setData(this);
        rj.setError(errorCode);
        rj.setMessage(message);
        return rj;
    };
}
