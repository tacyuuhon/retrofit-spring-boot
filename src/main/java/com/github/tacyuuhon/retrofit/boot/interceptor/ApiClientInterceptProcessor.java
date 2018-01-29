package com.github.tacyuuhon.retrofit.boot.interceptor;

import okhttp3.Request;
import okhttp3.Response;

public interface ApiClientInterceptProcessor {

    void beforeProcess(Request request);

    void afterProcess(Request request, Response response);


}
