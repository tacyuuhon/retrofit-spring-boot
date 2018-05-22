package com.github.tacyuuhon.retrofit.boot.binder.provider;

import org.springframework.boot.bind.PropertySourcesPropertyValues;
import org.springframework.boot.bind.RelaxedDataBinder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;

import com.github.tacyuuhon.retrofit.boot.binder.PropertiesBinderProvider;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SpringBoot1PropertiesBinderProvider implements PropertiesBinderProvider {

	@Override
	public <T> T getProperties(Class<T> clazz, Environment environment) {
		T properties = null;
		
        try {
			properties = clazz.newInstance();
			String namePrefix = AnnotationUtils.findAnnotation(clazz, ConfigurationProperties.class).prefix();
		    RelaxedDataBinder binder = new RelaxedDataBinder(properties, namePrefix);
		    binder.bind(new PropertySourcesPropertyValues(((ConfigurableEnvironment) environment).getPropertySources()));
		} catch (InstantiationException | IllegalAccessException e) {
			log.error(e.getMessage(), e);
		}
       
		return properties;
	}

}
