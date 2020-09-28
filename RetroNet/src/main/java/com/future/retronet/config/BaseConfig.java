package com.future.retronet.config;


import android.text.TextUtils;

import com.future.retronet.RetroNet;
import com.future.retronet.cache.HttpCache;
import com.future.retronet.interceptor.CacheInterceptor;
import com.future.retronet.interceptor.MainInterceptor;
import com.future.retronet.interceptor.RetroLogInterceptor;
import com.future.retronet.thread.ThreadCallAdapterFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import retrofit2.CallAdapter;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public abstract class BaseConfig implements Config {

    protected String cachePath() {
        return RetroNet.INSTANCE.getContext().getExternalCacheDir() + "/http-cache";
    }

    @Override
    public void build(Retrofit.Builder builder) {
        builder.client(client());//Retrofit 绑定OkHttpClient
        if (!TextUtils.isEmpty(getBaseUrl())) {
            builder.baseUrl(getBaseUrl());
        } else if (getBaseHttpUrl() != null) {
            builder.baseUrl(getBaseHttpUrl());
        } else {
            throw new RuntimeException("没有添加默认getBaseUrl或者getBaseHttpUrl");
        }
        addConverterFactory(builder);//添加解析器
        addCallAdapterFactory(builder);//添加线程切换
    }

    @Override
    public void reset(Retrofit.Builder builder) {
        if (!TextUtils.isEmpty(getBaseUrl())) {
            builder.baseUrl(getBaseUrl());
        } else if (getBaseHttpUrl() != null) {
            builder.baseUrl(getBaseHttpUrl());
        } else {
            throw new RuntimeException("没有添加默认getBaseUrl或者getBaseHttpUrl");
        }

    }

    @Override
    public OkHttpClient client() {
        OkHttpClient.Builder okHttpClientBuilder = new OkHttpClient.Builder();//创建OkHttpClient网络请求
        okHttpClientBuilder.addInterceptor(new MainInterceptor());
        okHttpClientBuilder.addNetworkInterceptor(new RetroLogInterceptor());//添加网络拦截器
        okHttpClientBuilder.addInterceptor(new CacheInterceptor());
        okHttpClientBuilder.cache(HttpCache.getCache(cachePath()));
        okHttpClientBuilder.retryOnConnectionFailure(true);//设置重试，默认重试
        okHttpClientBuilder.connectTimeout(5000, TimeUnit.MILLISECONDS);//设置默认连接长度5秒
        client(okHttpClientBuilder);
        return okHttpClientBuilder.build();
    }

    protected abstract void client(OkHttpClient.Builder clientBuilder);

    protected abstract String getBaseUrl();

    protected HttpUrl getBaseHttpUrl() {
        return HttpUrl.get(getBaseUrl());
    }

    protected List<Converter.Factory> getConverterFactories() {
        List<Converter.Factory> factories = new ArrayList<>();
        factories.add(GsonConverterFactory.create(RetroNet.INSTANCE.getGson()));
        return factories;
    }


    /**
     * 扩展的是对返回的数据类型的自动转换，把一种数据对象转换为另一种数据对象。
     *
     * @param builder
     */
    private void addConverterFactory(Retrofit.Builder builder) {
        List<Converter.Factory> factories = getConverterFactories();
        if (factories != null) {
            for (Converter.Factory factory : factories) {
                builder.addConverterFactory(factory);
            }
        }
    }


    protected List<CallAdapter.Factory> getCallAdapterFactories() {
        List<CallAdapter.Factory> factories = new ArrayList<>();
        factories.add(new ThreadCallAdapterFactory());
        return factories;
    }

    /**
     * addCallAdapterFactory 扩展的是对网络工作对象call的自动转换，把Retrofit中执行网络请求的Call对象，转换为接口中定义的Call对象。
     * @param builder
     */
    private void addCallAdapterFactory(Retrofit.Builder builder) {
        List<CallAdapter.Factory> factories = getCallAdapterFactories();
        if (factories != null) {
            for (CallAdapter.Factory factory : factories) {
                builder.addCallAdapterFactory(factory);
            }
        }
    }
}
