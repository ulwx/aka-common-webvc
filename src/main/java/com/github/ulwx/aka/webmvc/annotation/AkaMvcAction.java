package com.github.ulwx.aka.webmvc.annotation;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
public @interface AkaMvcAction {
    String  httpMethod() default "";
    String requestContentType() default "";
    String responseContentType() default "application/json";

}
