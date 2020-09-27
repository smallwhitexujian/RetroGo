package com.future.retronet.request.adapter;

import com.future.retronet.BaseApiService;
import com.future.retronet.RetroNet;
import com.future.retronet.request.BodyRequestBuilder;
import com.future.retronet.request.RequestBuilder;

import io.reactivex.rxjava3.core.Observable;
import okhttp3.ResponseBody;

public class DefaultRequestAdapter implements RequestAdapter {

    @Override
    public Observable<ResponseBody> adapt(RequestBuilder builder) {
        if (builder != null) {
            switch (builder.getMethod()) {
                case Get:
                    return RetroNet.create(builder.getConfigCls(), BaseApiService.class)
                            .get(builder.getHeaders(), builder.getUrl(), builder.getParams());
                case Post:
                    return RetroNet.create(builder.getConfigCls(), BaseApiService.class)
                            .post(builder.getHeaders(), builder.getUrl(), builder.getParams());
                case Put:
                    return RetroNet.create(builder.getConfigCls(), BaseApiService.class)
                            .put(builder.getHeaders(), builder.getUrl(), builder.getParams());
                case Delete:
                    return RetroNet.create(builder.getConfigCls(), BaseApiService.class)
                            .delete(builder.getHeaders(), builder.getUrl(), builder.getParams());
                case Body:
                    return RetroNet.create(builder.getConfigCls(), BaseApiService.class)
                            .body(builder.getHeaders(), builder.getUrl(), ((BodyRequestBuilder) builder).getBody());
                default:
                    return Observable.never();
            }
        }
        return Observable.never();
    }
}
