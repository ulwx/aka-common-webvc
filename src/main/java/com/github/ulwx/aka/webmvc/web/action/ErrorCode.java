package com.github.ulwx.aka.webmvc.web.action;

import java.util.HashMap;
import java.util.Map;

public class ErrorCode {
    public static final int NO_ERROR = 0;
    public static final int COMMON_ERROR = 999;
    public static Integer TWO_INPUT_PASS_NOT_MATCH=1000;
    public static Map<Integer,String> errors=new HashMap<Integer,String>();

    static{
        errors.put(TWO_INPUT_PASS_NOT_MATCH, "两次输入密码不匹配！");
        errors.put(COMMON_ERROR, "");
    }

    public Map<Integer, String> getError() {
        // TODO Auto-generated method stub
        return errors;
    }

    public static void  put(Map<Integer,String> map){
        errors.putAll(map);
    }
}
