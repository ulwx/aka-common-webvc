package com.github.ulwx.aka.webmvc.web.action;

import java.util.Map;

public class GateResult implements Result{

    private  String gateURL;
    private Map<String,String> reqMap;

    public String getGateURL() {
        return gateURL;
    }

    public void setGateURL(String gateURL) {
        this.gateURL = gateURL;
    }

    public Map<String, String> getReqMap() {
        return reqMap;
    }

    public void setReqMap(Map<String, String> reqMap) {
        this.reqMap = reqMap;
    }

    @Override
    public ResultType getType() {
        return ResultType.gate;
    }
}
