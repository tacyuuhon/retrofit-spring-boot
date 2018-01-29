package com.github.tacyuuhon.retrofit.boot;

import com.github.tacyuuhon.retrofit.boot.interceptor.ApiClientInterceptProcessor;
import com.github.tacyuuhon.retrofit.boot.interceptor.ApiClientRequestInterceptor;
import com.github.tacyuuhon.retrofit.boot.interceptor.aware.ApiClientPropertiesAware;
import com.github.tacyuuhon.retrofit.boot.interceptor.aware.ApiClientSpringBeanFactoryAware;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import okhttp3.logging.HttpLoggingInterceptor.Level;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.util.Assert;
import retrofit2.Retrofit;

import java.util.Arrays;

@Slf4j
@Builder
public class ApiClientBuilderFactory {

    private BeanFactory beanFactory;
    private ApiClientBasicProperties properties;
    private Class<? extends ApiClientInterceptProcessor>[] interceptProcessors;

    public Retrofit createRetrofit() {

        OkHttpClient.Builder okHttpClientBuilder = new OkHttpClient.Builder()
                .connectTimeout(properties.getConnectTimeout(), properties.getConnectTimeoutTimeUnit())
                .followRedirects(properties.isFollowRedirects())
                .followSslRedirects(properties.isFollowSslRedirects())
                .pingInterval(properties.getPingInterval(), properties.getPingIntervalTimeUnit())
                .readTimeout(properties.getReadTimeout(), properties.getReadTimeoutTimeUnit())
                .retryOnConnectionFailure(properties.isRetryOnConnectionFailure())
                .writeTimeout(properties.getWriteTimeout(), properties.getWriteTimeoutTimeUnit());


        if (properties.isIgnoreHostnameVerify()) {
            okHttpClientBuilder.hostnameVerifier((hostname, session) -> {
                return true;
            });
        }

        if (properties.isEnableRequestTraceLog()) {
            okHttpClientBuilder.addInterceptor(createHttpLoggingInterceptor(properties.getRequestTraceLogFormat()));
        }

        if (properties.isEnableNetworkTraceLog()) {
            okHttpClientBuilder.addInterceptor(createHttpLoggingInterceptor(properties.getNetworkTraceLogFormat()));
        }

        if (ArrayUtils.isNotEmpty(interceptProcessors)) {
            Arrays.stream(interceptProcessors).forEach(processor -> {
                okHttpClientBuilder.addInterceptor(createInterceptProcessor(processor));
            });
        }


        OkHttpClient client = okHttpClientBuilder.build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(properties.getBaseUrl())
                .validateEagerly(properties.isValidateEagerly())
                .client(client)
                .build();

        return retrofit;
    }

    private Interceptor createHttpLoggingInterceptor(Level level) {
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
        httpLoggingInterceptor.setLevel(level);
        return httpLoggingInterceptor;
    }


    @SuppressWarnings({"unchecked", "rawtypes"})
    private Interceptor createInterceptProcessor(Class<? extends ApiClientInterceptProcessor> processorClass) {

        Assert.isAssignable(ApiClientInterceptProcessor.class, processorClass);

        ApiClientInterceptProcessor processor = null;
        try {

            processor = processorClass.newInstance();

            if (processor instanceof ApiClientPropertiesAware) {
                ((ApiClientPropertiesAware) processor).setProperties(properties);
            }

            if (processor instanceof ApiClientSpringBeanFactoryAware) {
                ((ApiClientSpringBeanFactoryAware) processor).setBeanFactory(beanFactory);
            }


        } catch (InstantiationException | IllegalAccessException ex) {
            log.error(ex.getMessage(), ex);
        }

        Assert.notNull(processor, "ApiClientInterceptProcessor: [" + processorClass.getName() + "] has not been correctly instantiated");

        return ApiClientRequestInterceptor.builder().processor(processor).build();
    }
}
