package com.github.tacyuuhon.retrofit.boot.annotation;

import com.github.tacyuuhon.retrofit.boot.ApiClientBasicProperties;
import com.github.tacyuuhon.retrofit.boot.interceptor.ApiClientInterceptProcessor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ApiClient {

    Class<? extends ApiClientBasicProperties> properties();

    Class<? extends ApiClientInterceptProcessor>[] interceptProcessors() default {};

}
