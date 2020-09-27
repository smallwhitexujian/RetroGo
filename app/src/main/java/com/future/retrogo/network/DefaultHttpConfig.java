package com.future.retrogo.network;

import com.future.retronet.config.BaseConfig;
import com.future.retronet.interceptor.RetroLogInterceptor;

import okhttp3.OkHttpClient;

public class DefaultHttpConfig extends BaseConfig {
    @Override
    protected void client(OkHttpClient.Builder clientBuilder) {
        if (HttpHelper.mDebug) {
            clientBuilder.addNetworkInterceptor(new RetroLogInterceptor());
        }
    }

    @Override
    protected String getBaseUrl() {
        return HttpHelper.mBaseUrl;
    }
}
