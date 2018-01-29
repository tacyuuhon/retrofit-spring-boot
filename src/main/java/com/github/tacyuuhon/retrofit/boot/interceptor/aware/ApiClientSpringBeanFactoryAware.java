package com.github.tacyuuhon.retrofit.boot.interceptor.aware;

import org.springframework.beans.factory.BeanFactory;

public interface ApiClientSpringBeanFactoryAware {

    public void setBeanFactory(BeanFactory beanFactory);
}
