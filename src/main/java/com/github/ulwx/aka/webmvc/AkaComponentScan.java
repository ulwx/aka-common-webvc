package com.github.ulwx.aka.webmvc;

import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Inherited
@Import(AkaComponentScanRegistrar.class)
public @interface AkaComponentScan {
    @AliasFor("basePackages")
    String[] value() default {};
     @AliasFor("value")
    String[] basePackages() default {};
    Class<?>[] basePackageClasses() default {};
}
