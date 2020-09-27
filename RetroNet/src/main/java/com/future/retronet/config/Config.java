package com.future.retronet.config;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;

public interface Config {

    void build(Retrofit.Builder builder);

    void reset(Retrofit.Builder builder);

    OkHttpClient client();
}
