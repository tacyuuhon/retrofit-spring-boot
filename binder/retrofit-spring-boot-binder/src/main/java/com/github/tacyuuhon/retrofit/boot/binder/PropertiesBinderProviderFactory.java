package com.github.tacyuuhon.retrofit.boot.binder;

import java.util.Iterator;
import java.util.ServiceLoader;

import org.springframework.util.Assert;

public class PropertiesBinderProviderFactory {

    private PropertiesBinderProviderFactory() {}

    public static PropertiesBinderProvider getPropertiesBinderProvider() {
        PropertiesBinderProvider provider = null;

        ServiceLoader<PropertiesBinderProvider> serviceLoader = ServiceLoader.load(PropertiesBinderProvider.class);    
        Iterator<PropertiesBinderProvider> providers = serviceLoader.iterator();    
        if (providers.hasNext()) {    
        	provider = providers.next();    
        }
        
        Assert.notNull(provider, "PropertiesBinderProvider is null");

        return provider;
    }
}