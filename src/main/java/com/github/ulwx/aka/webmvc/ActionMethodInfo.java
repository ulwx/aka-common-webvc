package com.github.ulwx.aka.webmvc;

import com.github.ulwx.aka.webmvc.web.action.ActionSupport;


import java.util.Map;

public class ActionMethodInfo {
    private String logicActionMethodName;
    private String mod;
    private AkaWebMvcProperties.NameSpace namespace;
    private String actionLogicName;
    private String methodName;
    private String actionClassFullName;
    private ActionSupport actionObj;
    private Map<String, String> viewsMap;
    private AnnoClassMethodInfo annoClassMethodInfo;

    private Boolean isJSONResponse;

    public Boolean getJSONResponse() {
        return isJSONResponse;
    }

    public void setJSONResponse(Boolean JSONResponse) {
        isJSONResponse = JSONResponse;
    }

    public ActionMethodInfo(){

    }

    public AnnoClassMethodInfo getAnnoClassMethodInfo() {
        return annoClassMethodInfo;
    }

    public void setAnnoClassMethodInfo(AnnoClassMethodInfo annoClassMethodInfo) {
        this.annoClassMethodInfo = annoClassMethodInfo;
    }

    public String getLogicActionMethodName() {
        return logicActionMethodName;
    }

    public void setLogicActionMethodName(String logicActionMethodName) {
        this.logicActionMethodName = logicActionMethodName;
    }

    public String getMod() {
        return mod;
    }

    public void setMod(String mod) {
        this.mod = mod;
    }

    public AkaWebMvcProperties.NameSpace getNamespace() {
        return namespace;
    }

    public void setNamespace(AkaWebMvcProperties.NameSpace namespace) {
        this.namespace = namespace;
    }

    public String getActionLogicName() {
        return actionLogicName;
    }

    public void setActionLogicName(String actionLogicName) {
        this.actionLogicName = actionLogicName;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public String getActionClassFullName() {
        return actionClassFullName;
    }

    public void setActionClassFullName(String actionClassFullName) {
        this.actionClassFullName = actionClassFullName;
    }

    public ActionSupport getActionObj() {
        return actionObj;
    }

    public void setActionObj(ActionSupport actionObj) {
        this.actionObj = actionObj;
    }

    public Map<String, String> getViewsMap() {
        return viewsMap;
    }

    public void setViewsMap(Map<String, String> viewsMap) {
        this.viewsMap = viewsMap;
    }
}
