package com.github.ulwx.aka.webmvc;

import com.github.ulwx.aka.webmvc.web.action.ActionContext;

public class WebMvcActiionContextConst {
    public final static String ActionMethodInfo="Aka.ActionMethodInfo";

    public static  ActionMethodInfo getActionMethodInfo(){
        ActionMethodInfo actionMethodInfo=(ActionMethodInfo)
                ActionContext.getContext().get(WebMvcActiionContextConst.ActionMethodInfo);
        return actionMethodInfo;
    }
}
