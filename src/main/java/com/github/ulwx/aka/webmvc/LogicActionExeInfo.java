package com.github.ulwx.aka.webmvc;

public class LogicActionExeInfo {
    private String mod;
    private String actionLogicName;//去掉Action后缀的action名称
    private String methodName;

    public String getMod() {
        return mod;
    }

    public void setMod(String mod) {
        this.mod = mod;
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
    @Override
    public String toString(){
        return mod+"-"+actionLogicName+"-"+methodName;
    }
}
