package com.github.ulwx.aka.webmvc;

import org.springframework.boot.autoconfigure.AutoConfigurationExcludeFilter;
import org.springframework.boot.context.TypeExcludeFilter;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.PropertySource;


@PropertySource(name="classpath*:aka-application-webvc.yml"
        , value = {"classpath*:aka-application-webvc.yml"},
        factory = MyPropertySourceFactory.class)
@ComponentScan(
        basePackages = {AkaConst.WebMvcComponetPackage},
        nameGenerator = UniqueNameGenerator.class,
        excludeFilters = {
                @Filter(type = FilterType.CUSTOM, classes = TypeExcludeFilter.class),
                 @Filter(type = FilterType.CUSTOM, classes = AutoConfigurationExcludeFilter.class)
        })

public class AkaWebMvcAutoConfiguration {

}

