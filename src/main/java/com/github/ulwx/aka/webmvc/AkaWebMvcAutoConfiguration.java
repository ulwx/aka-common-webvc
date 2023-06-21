package com.github.ulwx.aka.webmvc;

import org.springdoc.core.SpringDocConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigurationExcludeFilter;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.context.TypeExcludeFilter;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.PropertySource;


@PropertySource(value = {"classpath*:aka-application*.yml"},
        factory = MyPropertySourceFactory.class)
@ComponentScan(
        basePackages = {AkaConst.WebMvcComponetPackage},
        nameGenerator = UniqueNameGenerator.class,
        excludeFilters = {
                @Filter(type = FilterType.CUSTOM, classes = TypeExcludeFilter.class),
                 @Filter(type = FilterType.CUSTOM, classes = AutoConfigurationExcludeFilter.class)
        })

//@Configuration("com.github.ulwx.aka.webmvc.AkaWebMvcAutoConfiguration")
public class AkaWebMvcAutoConfiguration {

}

