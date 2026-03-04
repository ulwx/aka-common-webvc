package com.github.ulwx.aka.webmvc;


import jakarta.servlet.http.HttpServletRequest;

public interface LogicActionExeInfoBuilder {
    /**
     *
     * @param nameSpace
     * @param urlPartAfterNameSpace
     * @param request
     * @return 返回的格式为： [mod]-[actionName]-[methodName]，其中mod为
     */
    LogicActionExeInfo build(AkaWebMvcProperties.NameSpace nameSpace,
                             String  urlPartAfterNameSpace,
                             HttpServletRequest request);
}
