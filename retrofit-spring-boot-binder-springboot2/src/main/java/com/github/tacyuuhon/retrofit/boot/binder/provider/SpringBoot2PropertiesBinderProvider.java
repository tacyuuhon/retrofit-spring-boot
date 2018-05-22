package com.github.tacyuuhon.retrofit.boot.binder.provider;


import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.env.Environment;

import com.github.tacyuuhon.retrofit.boot.binder.PropertiesBinderProvider;

public class SpringBoot2PropertiesBinderProvider implements PropertiesBinderProvider {

	@Override
	public <T> T getProperties(Class<T> clazz, Environment environment) {
		T properties = null;
		  
		String namePrefix = AnnotationUtils.findAnnotation(clazz, ConfigurationProperties.class).prefix();
		Binder binder = Binder.get(environment);
		properties = binder.bind(namePrefix, Bindable.of(clazz)).get();
		  
		return properties;
	}

}
