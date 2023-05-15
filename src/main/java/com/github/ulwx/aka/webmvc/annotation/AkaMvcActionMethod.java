package com.github.ulwx.aka.webmvc.annotation;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
public @interface AkaMvcActionMethod {
    String  httpMethod() default "";
    String responseContentType() default "application/json";
    String requestContentType() default "";

}
