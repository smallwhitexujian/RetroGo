package com.future.retronet.interceptor;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 网盘重试拦截器
 */
public class RetryInterceptor implements Interceptor {
    public int maxRetry;//最大重连次数
    private int retryNum = 0;//假如设置为1次重试的话，则最大可能请求次数为2次（默认1次+重连1次）

    public RetryInterceptor(int maxRetry) {
        this.maxRetry = maxRetry;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        Response response = chain.proceed(request);
        while (!response.isSuccessful() && retryNum < maxRetry) {
            retryNum++;
            response = chain.proceed(request);//重试
        }
        return response;
    }
}
