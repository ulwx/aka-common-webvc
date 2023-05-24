package com.github.ulwx.aka.webmvc.utils;

import com.github.ulwx.aka.dbutils.tool.support.StringUtils;
import com.github.ulwx.aka.webmvc.WebMvcCbConstants;

import javax.servlet.http.HttpServletRequest;

public class WebMvcUtils {
    public static boolean isAjax(HttpServletRequest hreq) {
        String AjaxURLSTR= WebMvcCbConstants.AjaxURLSTR;
        String ruri = hreq.getRequestURI();
        if ((StringUtils.hasText(AjaxURLSTR) && ruri.contains(AjaxURLSTR))
                || (hreq.getHeader("x-requested-with") != null
                && hreq.getHeader("x-requested-with").equalsIgnoreCase("XMLHttpRequest"))
        ) {
            return true;
        }
        return false;
    }
    public static boolean isAjax(String url) {
        String AjaxURLSTR= WebMvcCbConstants.AjaxURLSTR;
        if ((StringUtils.hasText(AjaxURLSTR) && url.contains(AjaxURLSTR))) {
            return true;
        }
        return false;
    }
}
