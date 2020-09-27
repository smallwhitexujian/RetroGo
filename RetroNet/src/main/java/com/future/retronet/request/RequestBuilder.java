package com.future.retronet.request;

import android.annotation.SuppressLint;
import android.service.carrier.CarrierMessagingService;
import android.util.Log;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.OnLifecycleEvent;

import com.future.retronet.ResultCallback;
import com.future.retronet.RetroBean;
import com.future.retronet.RetroCode;
import com.future.retronet.RetroException;
import com.future.retronet.RetroFun;
import com.future.retronet.RetroNet;
import com.future.retronet.StructType;
import com.future.retronet.Uitls.Utils;
import com.future.retronet.cache.CacheStrategy;
import com.future.retronet.config.Config;
import com.future.retronet.header.HeaderKey;
import com.future.retronet.logger.LogUtil;
import com.future.retronet.request.adapter.DefaultRequestAdapter;
import com.future.retronet.request.adapter.Method;
import com.future.retronet.request.adapter.RequestAdapter;

import java.lang.reflect.Type;
import java.util.LinkedHashMap;
import java.util.Map;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.CheckReturnValue;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.ObservableSource;
import io.reactivex.rxjava3.core.ObservableTransformer;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.functions.Predicate;
import io.reactivex.rxjava3.subjects.PublishSubject;
import okhttp3.ResponseBody;

import static androidx.lifecycle.Lifecycle.*;

public class RequestBuilder<B extends RequestBuilder> implements LifecycleObserver {
    private String mUrl;
    private Map<String, Object> mHeaders;
    private Map<String, Object> mParams;
    private Method mMethod;
    //返回模式
    public StructType mStructType = StructType.Result;
    public int mRetryCount = 0;
    private Class<? extends Config> mConfigClass;

    private LifecycleOwner lifecycleOwner = null;
    private PublishSubject<Event> lifecycleSubject = PublishSubject.create();

    public RequestBuilder(Method method, Class<? extends Config> mConfigClass) {
        mMethod = method;
        this.mConfigClass = mConfigClass;
        mHeaders = new LinkedHashMap<>();
        mParams = new LinkedHashMap<>();
    }

    public B url(String mUrl) {
        this.mUrl = mUrl;
        return (B) this;
    }

    public B params(Map<String, Object> params) {
        if (mParams == null) {
            mParams = new LinkedHashMap<>();
        }
        mParams.putAll(params);
        return (B) this;
    }

    public B addParam(String key, Object val) {
        if (mParams == null) {
            mParams = new LinkedHashMap<>();
        }
        mParams.put(key, val);
        return (B) this;
    }

    public B headers(Map<String, Object> headers) {
        if (mHeaders == null) {
            mHeaders = new LinkedHashMap<>();
        }
        this.mHeaders = headers;
        return (B) this;
    }

    public B addHeader(String key, Object val) {
        if (mHeaders == null) {
            mHeaders = new LinkedHashMap<>();
        }
        mHeaders.put(key, val);
        return (B) this;
    }

    public B structType(StructType structType) {
        this.mStructType = structType;
        return (B) this;
    }

    public B cache(@NonNull CacheStrategy cacheStrategy) {
        return addHeader(HeaderKey.Cache, cacheStrategy.getValue());
    }

    public B readTimeOut(int readTimeOutMils) {
        return addHeader(HeaderKey.ReadTimeOut, readTimeOutMils);
    }

    public B writeTimeOut(int writeTimeOutMils) {
        return addHeader(HeaderKey.WriteTimeOut, writeTimeOutMils);
    }

    public B connectTimeOut(int connectTimeOutMils) {
        return addHeader(HeaderKey.ConnectTimeOut, connectTimeOutMils);
    }

    public B retry(int retryCount) {
        this.mRetryCount = retryCount;
        return (B) this;
    }

    public B bindLifecycle(LifecycleOwner lifecycleOwner) {
        this.lifecycleOwner = lifecycleOwner;
        if (lifecycleOwner != null) {
            lifecycleOwner.getLifecycle().addObserver(this);
        }
        return (B) this;
    }

    @OnLifecycleEvent(Event.ON_DESTROY)
    void onDestroy() {
        LogUtil.e("OnLifecycleEvent  ON_DESTROY");
        if (lifecycleSubject != null) {
            lifecycleSubject.onNext(Event.ON_DESTROY);
        }
        if (lifecycleOwner != null) {
            lifecycleOwner.getLifecycle().removeObserver(this);
        }
        dispose();
    }

    @Deprecated
    private void cancel() {
        dispose();
    }

    @Deprecated
    private void dispose() {

    }

    public Map<String, Object> getHeaders() {
        return mHeaders;
    }

    public Map<String, Object> getParams() {
        return mParams;
    }

    public String getUrl() {
        return mUrl;
    }

    public Method getMethod() {
        return mMethod;
    }

    public Class<? extends Config> getConfigCls() {
        if (mConfigClass == null) {
            throw new RuntimeException("config not be null");
        }
        return mConfigClass;
    }

    private class LifecycleTransformer<T> implements ObservableTransformer<T, T> {
        io.reactivex.rxjava3.core.@NonNull Observable<?> observable;

        LifecycleTransformer(io.reactivex.rxjava3.core.@NonNull Observable<?> observable) {
            this.observable = observable;
        }


        @Override
        public @NonNull ObservableSource<T> apply(io.reactivex.rxjava3.core.@NonNull Observable<T> upstream) {
            return upstream.takeUntil(observable);
        }
    }


    @CheckReturnValue
    private <T> LifecycleTransformer<T> bindUntilEvent(@NonNull final Lifecycle.Event lifeCycleEvent) {

        Observable<Lifecycle.Event> compareLifecycleObservable = lifecycleSubject.filter(new Predicate<Event>() {
            @Override
            public boolean test(Lifecycle.Event event) throws Exception {
                LogUtil.e("bindUntilEvent filter:" + event);
                return lifeCycleEvent.equals(event);   //过滤事件
            }
        });
        return new LifecycleTransformer<>(compareLifecycleObservable);
    }

    public <T> Observable<T> request(@NonNull RequestAdapter adapter) {
        Observable observable = adapter.adapt(this);
        if (mRetryCount > 0) {
            observable = observable.retry(mRetryCount);
        }
        return (Observable<T>) observable.compose(bindUntilEvent(Lifecycle.Event.ON_DESTROY));
    }

    public Observable<ResponseBody> requestBody() {
        return request(new DefaultRequestAdapter());
    }

    public <R> Observable<R> request(Class<R> rspClass) {
        return requestBody().map(new RetroFun<R>(rspClass, mStructType));
    }

    public <R> Observable<R> request(Type rspType) {
        return requestBody().map(new RetroFun<R>(rspType, mStructType));
    }

    public <R> Observable<R> request(Class<? extends RetroBean> rawCls, Class<R> resultCls) {
        return requestBody().map(new RetroFun<R>(Utils.newParameterizedTypeWithOwner(rawCls, resultCls), StructType.Result));
    }

    public <R> Disposable request(final ResultCallback<R> callback) {
        Type type = null;
        if (callback != null) {
            //检测当前活动页面生命周期
            bindLifecycle(callback.getLifecycleOwner());
            type = callback.getType(mStructType);
            if (type == null) {
                type = ResponseBody.class;
                mStructType = StructType.Direct;
            }
        } else {
            type = ResponseBody.class;
            mStructType = StructType.Direct;
        }
        return requestBody()
                .map(new RetroFun<R>(type, mStructType))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Object>() {
                               @Override
                               public void accept(Object result) throws Throwable {
                                   if (callback != null) {
                                       if (result != null) {
                                           if (result instanceof RetroBean) {
                                               RetroBean temp = (RetroBean) result;
                                               if (temp.isSuccess()) {
                                                   callback.onSuccess((R) result);
                                               } else {
                                                   callback.onError(temp.getCode(), new RetroException(temp.getCode(), temp.getMessage()));
                                               }
                                           } else {
                                               callback.onSuccess((R) result);
                                           }
                                       } else {
                                           callback.onError(RetroCode.CODE_PARSE_ERR, new RetroException(RetroCode.CODE_PARSE_ERR, "parse error"));
                                       }
                                   }
                               }
                           }, new Consumer<Throwable>() {
                               @Override
                               public void accept(Throwable throwable) throws Throwable {
                                   if (callback != null) {
                                       callback.onError(RetroCode.CODE_ERR_IO, new RetroException(RetroCode.CODE_ERR_IO, throwable.getMessage()));
                                   }
                               }
                           }
                );
    }
}
