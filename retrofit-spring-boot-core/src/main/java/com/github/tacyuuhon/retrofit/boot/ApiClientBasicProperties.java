package com.github.tacyuuhon.retrofit.boot;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;
import okhttp3.logging.HttpLoggingInterceptor.Level;

import java.util.concurrent.TimeUnit;

@Setter
@Getter
@ToString
public abstract class ApiClientBasicProperties {

    @NonNull
    private String baseUrl;
    private boolean validateEagerly = true;
    private boolean ignoreHostnameVerify = false;
    private boolean enableRequestTraceLog = false;
    private Level requestTraceLogFormat = Level.BODY;
    private boolean enableNetworkTraceLog = false;
    private Level networkTraceLogFormat = Level.BODY;
    private boolean followSslRedirects = true;
    private boolean followRedirects = true;
    private boolean retryOnConnectionFailure = true;
    private int connectTimeout = 10_000;
    private int readTimeout = 10_000;
    private int writeTimeout = 10_000;
    private int pingInterval = 0;
    private TimeUnit connectTimeoutTimeUnit = TimeUnit.MILLISECONDS;
    private TimeUnit readTimeoutTimeUnit = TimeUnit.MILLISECONDS;
    private TimeUnit writeTimeoutTimeUnit = TimeUnit.MILLISECONDS;
    private TimeUnit pingIntervalTimeUnit = TimeUnit.MILLISECONDS;
}
