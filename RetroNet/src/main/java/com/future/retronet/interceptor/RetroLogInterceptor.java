package com.future.retronet.interceptor;

import com.future.retronet.BuildConfig;
import com.future.retronet.logger.LogUtil;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;

public class RetroLogInterceptor implements Interceptor {

    private HttpLoggingInterceptor httpLoggingInterceptor;

    public RetroLogInterceptor() {
        httpLoggingInterceptor = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
            @Override
            public void log(String message) {
                LogUtil.d(message);
            }
        });
        httpLoggingInterceptor.setLevel(BuildConfig.DEBUG ? HttpLoggingInterceptor.Level.BODY : HttpLoggingInterceptor.Level.NONE);
    }

    @Override
    public Response intercept(Interceptor.Chain chain) throws IOException {
        return httpLoggingInterceptor.intercept(chain);
    }
}
