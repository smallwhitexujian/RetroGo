package com.future.retronet.request;

import com.future.retronet.config.Config;
import com.future.retronet.request.adapter.Method;

public class GetRequestBuilder extends RequestBuilder<GetRequestBuilder> {

    public GetRequestBuilder(Class<? extends Config> mConfigClass) {
        super(Method.Get, mConfigClass);
    }
}
