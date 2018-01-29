package com.github.tacyuuhon.retrofit.boot.interceptor.aware;

public interface ApiClientPropertiesAware<T> {

    public void setProperties(T T);
}
