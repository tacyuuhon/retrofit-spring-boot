package com.github.tacyuuhon.retrofit.boot.annotation;

import com.github.tacyuuhon.retrofit.boot.autoconfigure.ApiClientScanBeanDefinitionRegistrar;
import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import({ApiClientScanBeanDefinitionRegistrar.class})
public @interface ApiClientScan {

    @AliasFor("basePackages")
    String[] value() default {};

    @AliasFor("value")
    String[] basePackages() default {};
}
