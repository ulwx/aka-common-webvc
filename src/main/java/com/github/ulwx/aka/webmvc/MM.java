package com.github.ulwx.aka.webmvc;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
public class MM {
    private static volatile MessageSource messageSource;
    /**
     * 设置默认语言环境为中文
     */
    public static final Locale DEF_LOCALE = Locale.CHINA;
    public MM(MessageSource messageSource) {
        MM.messageSource = messageSource;
    }


    /**
     * @param code key值，通过这个来获取不同语言下的value
     * @param args 这是传入的占位符，可以为空
     * @return {@link String}
     */
    public static String M(String code, Object... args) {
        // 获得当前的语言环境
        Locale locale=LocaleContextHolder.getLocale();
        if (locale == null) {
            locale = DEF_LOCALE;
        }
        try {
            return messageSource.getMessage(code, args, locale);
        }catch (Exception e) {
            int i=code.indexOf(".");
            if(i>0){
                code=code.substring(i+1);
            }
            return code;
        }
    }

    public static String M(String code, Object[] args,Locale locale) {
        try {
            return messageSource.getMessage(code, args, locale);
        }catch (Exception e) {
            int i=code.indexOf(".");
            if(i>0){
                code=code.substring(i+1);
            }
            return code;
        }
    }

    public static String getLanguage(){
        Locale locale=LocaleContextHolder.getLocale();
        return locale.getLanguage();
    }
}
