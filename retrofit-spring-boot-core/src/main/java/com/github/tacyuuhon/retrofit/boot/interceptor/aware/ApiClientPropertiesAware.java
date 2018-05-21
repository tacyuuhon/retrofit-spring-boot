package com.github.tacyuuhon.retrofit.boot.interceptor.aware;

public interface ApiClientPropertiesAware<T> {

    void setProperties(T T);
}
