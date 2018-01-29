package com.github.tacyuuhon.retrofit.boot.interceptor;

import lombok.Builder;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

@Builder
public class ApiClientRequestInterceptor implements Interceptor {

    private ApiClientInterceptProcessor processor;

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();

        processor.beforeProcess(request);
        Response response = chain.proceed(request);
        processor.afterProcess(request, response);

        return response;
    }

}
