package com.github.ulwx.aka.webmvc.utils;

import org.apache.log4j.Logger;

public class JspLog {
    private  static Logger logger = Logger.getLogger(JspLog.class);
    public static void debug(String str){

        logger.debug( str);
    }
}
