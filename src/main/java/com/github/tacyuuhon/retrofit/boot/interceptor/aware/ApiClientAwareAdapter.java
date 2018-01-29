package com.github.tacyuuhon.retrofit.boot.interceptor.aware;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.BeanFactory;

public class ApiClientAwareAdapter<T> implements ApiClientPropertiesAware<T>, ApiClientSpringBeanFactoryAware {

    @Getter
    @Setter
    private T properties;

    @Getter
    @Setter
    private BeanFactory beanFactory;

}
