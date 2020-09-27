package com.future.retronet.request;

import com.future.retronet.RetroNet;
import com.future.retronet.config.Config;
import com.future.retronet.request.adapter.Method;

import okhttp3.MediaType;
import okhttp3.RequestBody;

public class BodyRequestBuilder extends RequestBuilder<BodyRequestBuilder> {

    public BodyRequestBuilder(Class<? extends Config> mConfigClass) {
        super(Method.Body, mConfigClass);
    }

    private RequestBody mBody;

    public BodyRequestBuilder body(RequestBody mBody) {
        this.mBody = mBody;
        return this;
    }

    public BodyRequestBuilder json(String jsonStr) {
        return body(RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonStr));
    }

    public BodyRequestBuilder object(Object object) {
        return json(RetroNet.INSTANCE.getGson().toJson(object));
    }

    public RequestBody getBody() {
        return mBody;
    }
}
