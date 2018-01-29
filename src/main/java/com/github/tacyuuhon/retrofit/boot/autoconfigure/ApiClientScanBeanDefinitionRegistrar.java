package com.github.tacyuuhon.retrofit.boot.autoconfigure;

import com.github.tacyuuhon.retrofit.boot.ApiClientBasicProperties;
import com.github.tacyuuhon.retrofit.boot.ApiClientBuilderFactory;
import com.github.tacyuuhon.retrofit.boot.ApiClientProxyFactoryBean;
import com.github.tacyuuhon.retrofit.boot.annotation.ApiClient;
import com.github.tacyuuhon.retrofit.boot.annotation.ApiClientScan;
import com.github.tacyuuhon.retrofit.boot.interceptor.ApiClientInterceptProcessor;
import com.google.common.collect.Lists;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.boot.bind.PropertySourcesPropertyValues;
import org.springframework.boot.bind.RelaxedDataBinder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.StandardAnnotationMetadata;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.ObjectUtils;
import retrofit2.Retrofit;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Slf4j
public class ApiClientScanBeanDefinitionRegistrar implements ImportBeanDefinitionRegistrar, EnvironmentAware, BeanFactoryAware {

    @Setter
    private Environment environment;
    @Setter
    private BeanFactory beanFactory;

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {

        List<String> basePackages = findBasePackages(importingClassMetadata);
        ClassPathBeanDefinitionScanner scanner = createClassPathBeanDefinitionScanner(registry);
        registerBeanDefinition(scanner, registry, basePackages);
    }

    private String[] getIntrospectedPackage(AnnotationMetadata importingClassMetadata) {
        return new String[]{((StandardAnnotationMetadata) importingClassMetadata).getIntrospectedClass().getPackage().getName()};
    }

    private <T> T getProperties(Class<T> clazz) {
        T properties = null;

        Assert.isAssignable(ApiClientBasicProperties.class, clazz);

        try {

            properties = clazz.newInstance();
            String namePrefix = AnnotationUtils.findAnnotation(clazz, ConfigurationProperties.class).prefix();
            RelaxedDataBinder binder = new RelaxedDataBinder(properties, namePrefix);
            binder.bind(new PropertySourcesPropertyValues(((ConfigurableEnvironment) environment).getPropertySources()));

        } catch (InstantiationException | IllegalAccessException ex) {
            log.error(ex.getMessage(), ex);
        }

        log.info("Properties [{}] values: {}", clazz.getName(), properties);

        return properties;
    }

    private List<String> findBasePackages(AnnotationMetadata importingClassMetadata) {
        List<String> basePackageList = Lists.newArrayList();

        Map<String, Object> annotationAttributes = importingClassMetadata.getAnnotationAttributes(ApiClientScan.class.getCanonicalName());
        Assert.notNull(annotationAttributes, "Not found @ApiClientScan annotation");

        String[] basePackages = (String[]) annotationAttributes.get("basePackages");
        basePackages = ObjectUtils.isEmpty(basePackages) ? getIntrospectedPackage(importingClassMetadata) : basePackages;
        Assert.notEmpty(basePackages, "The basePackages is null");

        basePackageList.addAll(Lists.newArrayList(basePackages));
        log.info("Find the basePackages : {}", basePackageList);

        return basePackageList;
    }

    private ClassPathBeanDefinitionScanner createClassPathBeanDefinitionScanner(BeanDefinitionRegistry registry) {

        ClassPathBeanDefinitionScanner scanner = new ClassPathBeanDefinitionScanner(registry, false) {

            @Override
            protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
                AnnotationMetadata metadata = beanDefinition.getMetadata();
                return metadata.isIndependent() && metadata.isInterface();
            }
        };
        scanner.addIncludeFilter(new AnnotationTypeFilter(ApiClient.class));

        return scanner;
    }

    private void registerBeanDefinition(ClassPathBeanDefinitionScanner scanner, BeanDefinitionRegistry registry, List<String> basePackages) {

        basePackages.forEach(basePackage -> {
            log.info("Scanning @ApiClient annotation in [{}] package", basePackage);
            Set<BeanDefinition> beanDefinitionSet = scanner.findCandidateComponents(basePackage);

            beanDefinitionSet.forEach(beanDefinition -> {

                try {

                    String beanClassName = beanDefinition.getBeanClassName();
                    Class<?> beanClass = ClassUtils.forName(beanClassName, ClassUtils.getDefaultClassLoader());

                    ApiClient apiClientAnnotation = AnnotationUtils.findAnnotation(beanClass, ApiClient.class);
                    Class<? extends ApiClientBasicProperties> propertiesClass = apiClientAnnotation.properties();
                    Class<? extends ApiClientInterceptProcessor>[] interceptProcessors = apiClientAnnotation.interceptProcessors();

                    ApiClientBasicProperties properties = getProperties(propertiesClass);

                    Retrofit retrofit = ApiClientBuilderFactory.builder()
                            .properties(properties)
                            .interceptProcessors(interceptProcessors)
                            .beanFactory(beanFactory)
                            .build()
                            .createRetrofit();

                    beanDefinition.setBeanClassName(ApiClientProxyFactoryBean.class.getName());
                    beanDefinition.getPropertyValues().add("interfaceClass", beanClass);
                    beanDefinition.getPropertyValues().add("retrofit", retrofit);

                    registry.registerBeanDefinition(BeanDefinitionReaderUtils.generateBeanName(beanDefinition, registry), beanDefinition);
                    log.info("Registered @ApiClient proxy at [{}] interface", beanClassName);

                } catch (ClassNotFoundException | LinkageError ex) {
                    log.error(ex.getMessage(), ex);
                }

            });

            log.info("Finished scanning @ApiClient annotation in [{}] package", basePackage);
        });

    }

}
