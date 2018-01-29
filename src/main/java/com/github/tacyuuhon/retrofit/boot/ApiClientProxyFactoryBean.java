package com.github.tacyuuhon.retrofit.boot;

import lombok.Setter;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.stereotype.Service;
import retrofit2.Retrofit;

@Service
public class ApiClientProxyFactoryBean implements FactoryBean<Object> {

    private @Setter
    Class<?> interfaceClass = null;
    private @Setter
    Retrofit retrofit;

    @Override
    public Object getObject() {
        return retrofit.create(interfaceClass);
    }

    @Override
    public Class<?> getObjectType() {
        return interfaceClass;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

}
