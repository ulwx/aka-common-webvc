package com.github.ulwx.aka.webmvc.web.action;

import com.ulwx.tool.StringUtils;

public class JsonResult implements Result{


    @Override
    public ResultType getType() {
        return ResultType.json;
    }



    public String JSONP(String content,String callBack) {

        if (StringUtils.hasText(callBack)) {
            String ret = callBack + "(" + content + ")";
            return ret;
        } else {
            return content;
        }
    }

}
