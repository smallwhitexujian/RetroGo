package com.future.retronet.interceptor;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;
import android.util.Log;

import com.future.retronet.RetroException;
import com.future.retronet.RetroNet;
import com.future.retronet.cache.CacheStrategy;
import com.future.retronet.cache.HttpCache;
import com.future.retronet.header.HeaderKey;
import com.future.retronet.logger.LogUtil;

import java.io.IOException;
import java.net.HttpURLConnection;

import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class CacheInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        CacheStrategy cacheStrategy = checkSetCache(request);
        switch (cacheStrategy) {
            case CacheFirst:
                return doCacheFirst(chain, request);
            case NetWorkFirst:
                return doNetWorkFirst(chain, request);
            case None:
            default:
                request = request.newBuilder()
                        .cacheControl(new CacheControl.Builder().noCache().build())
                        .build();
                return chain.proceed(request);
        }
    }

    private CacheStrategy checkSetCache(Request request) {
        String cacheHeader = request.header(HeaderKey.Cache);
        if (TextUtils.isEmpty(cacheHeader)) {
            return CacheStrategy.None;
        }
        return CacheStrategy.valueOf(cacheHeader);
    }

    /**
     * check NetworkAvailable
     *
     * @param context
     * @return
     */
    private static boolean isNetworkAvailable(Context context) {
        ConnectivityManager manager = (ConnectivityManager) context.getApplicationContext().getSystemService(
                Context.CONNECTIVITY_SERVICE);
        if (null == manager)
            return false;
        NetworkInfo info = manager.getActiveNetworkInfo();
        if (null == info || !info.isAvailable())
            return false;
        return true;
    }

    /**
     * if network is available and the request during max-age time(default 60s), so get cache first<br>
     * then if  network is unavailable, get data from cache.
     */
    private Response doCacheFirst(Chain chain, Request request) throws IOException {
        if (isNetworkAvailable(RetroNet.INSTANCE.getContext())) {
            Response response = chain.proceed(request);
            return response.newBuilder()
                    .removeHeader(HeaderKey.Cache)
                    // clear head info,because if server unsupport,it will return some useless info
                    // if you donot clear,anything behind will donot action.
                    .removeHeader("Pragma")
                    .header("Cache-Control", "public, max-age=" + HttpCache.maxAge)
                    .build();
        } else {
            LogUtil.e("no network load cahe");
            request = request.newBuilder()
                    .removeHeader(HeaderKey.Cache)
                    .removeHeader("Pragma")
                    //set cache life cycle is HttpCache.maxStale second
                    .header("Cache-Control", "public, only-if-cached, max-stale=" + HttpCache.maxStale)
                    .build();
            Response response = chain.proceed(request);
            if (response.code() == HttpURLConnection.HTTP_GATEWAY_TIMEOUT) {
                throw new RetroException(response.code(), "Connot connect server");
            }
            return response;
        }
    }

    /**
     * if network is available, so get data from network whatever<br>
     * then if  network is unavailable, get data from cache.
     */
    private Response doNetWorkFirst(Chain chain, Request request) throws IOException {
        if (isNetworkAvailable(RetroNet.INSTANCE.getContext())) {
            Response response = chain.proceed(request);
            return response.newBuilder()
                    .removeHeader(HeaderKey.Cache)
                    .removeHeader("Pragma")
                    //when net is available, set cache max-age time is zero, asy get data from network
                    .header("Cache-Control", "public, max-age=" + 0)
                    .build();
        } else {
            LogUtil.e("no network load cahe");
            request = request.newBuilder()
                    .removeHeader(HeaderKey.Cache)
                    .removeHeader("Pragma")
                    //set cache life cycle is HttpCache.maxStale second
                    .header("Cache-Control", "public, only-if-cached, max-stale=" + HttpCache.maxStale)
                    .build();
            Response response = chain.proceed(request);
            if (response.code() == HttpURLConnection.HTTP_GATEWAY_TIMEOUT) {
                throw new RetroException(response.code(), "Connot connect server");
            }
            return response;
        }
    }
}
