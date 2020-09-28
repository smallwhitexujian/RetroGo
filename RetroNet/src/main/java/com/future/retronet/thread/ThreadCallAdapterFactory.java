package com.future.retronet.thread;


import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import io.reactivex.Observable;
import retrofit2.Call;
import retrofit2.CallAdapter;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

/**
 * 扩展的是对网络工作对象callWorker的自动转换，把Retrofit中执行网络请求的Call对象，转换为接口中定义的Call对象
 */
public class ThreadCallAdapterFactory extends CallAdapter.Factory {

    private RxJava2CallAdapterFactory rxFactory = RxJava2CallAdapterFactory.create();

    public ThreadCallAdapterFactory() {
    }

    @Override
    public CallAdapter<?, ?> get(Type returnType, Annotation[] annotations, Retrofit retrofit) {
        CallAdapter callAdapter = rxFactory.get(returnType, annotations, retrofit);
        return callAdapter != null ? new ThreadCallAdapter(callAdapter, annotations) : null;
    }

    final class ThreadCallAdapter<R> implements CallAdapter<R, Observable<?>> {
        CallAdapter<R, Observable<?>> delegateAdapter;
        private ThreadStrategy subscribeScheduler = ThreadStrategy.IO;
        private ThreadStrategy observerScheduler = null;

        ThreadCallAdapter(CallAdapter<R, Observable<?>> delegateAdapter, Annotation[] annotations) {
            this.delegateAdapter = delegateAdapter;
            for (Annotation annotation : annotations) {
                if (annotation instanceof RetroThread) {
                    RetroThread retroThread = (RetroThread) annotation;
                    subscribeScheduler = retroThread.subscribeThread();
                    observerScheduler = retroThread.observeThread();
                    return;
                }
            }
        }

        @Override
        public Type responseType() {
            return delegateAdapter.responseType();
        }

        @Override
        public Observable<?> adapt(Call<R> call) {
            Observable<?> observable = delegateAdapter.adapt(call);
            if (subscribeScheduler != null) {
                observable = observable.subscribeOn(subscribeScheduler.cheduler());
            }
            if (observerScheduler != null) {
                if (observerScheduler == subscribeScheduler) {
                    //如果返回的线程和注解设置的线程一致则不做处理
                } else {
                    //切换线程
                    observable = observable.observeOn(observerScheduler.cheduler());
                }
            }
            return observable;
        }
    }
}