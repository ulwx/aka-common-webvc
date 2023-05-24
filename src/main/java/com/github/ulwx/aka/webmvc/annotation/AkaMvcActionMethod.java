package com.github.ulwx.aka.webmvc.annotation;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
public @interface AkaMvcActionMethod {
    /**
     * 允许的http请求方法，以英文逗号分割
     * @return
     */
    String  httpMethod() default "";

    /**
     * 响应包体的MIME类型
     * @return
     */
    String responseContentType() default "application/json";

    /**
     * 请求包体的MIME类型
     * @return
     */
    String requestContentType() default "";

}
