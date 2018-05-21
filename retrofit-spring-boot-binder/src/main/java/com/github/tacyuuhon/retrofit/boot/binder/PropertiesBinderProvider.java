package com.github.tacyuuhon.retrofit.boot.binder;

import org.springframework.core.env.Environment;

public interface PropertiesBinderProvider {

    public <T> T getProperties(Class<T> clazz, Environment environment);
    
}