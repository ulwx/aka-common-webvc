package com.github.ulwx.aka.webmvc.web.action;

import com.ulwx.tool.ObjectUtils;
import com.ulwx.tool.StringUtils;

public class JsonResult implements Result{
    private String content;

    public String getContent() {
        return content;
    }


    @Override
    public ResultType getType() {
        return ResultType.json;
    }

    public void setContent(Object obj, String callBack){
        String ret="";
        if(obj instanceof String){
            ret=obj.toString();
        }else {
            ret = ObjectUtils.toJsonString2(obj, true, true);
        }
        ret=JSONP(ret,callBack);
        this.content=ret;
    }

    private String JSONP(String content,String callBack) {
        if (StringUtils.hasText(callBack)) {
            String ret = callBack + "(" + content + ")";
            return ret;
        } else {
            return content;
        }
    }

}
