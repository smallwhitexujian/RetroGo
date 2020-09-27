package com.future.retronet.request;

import com.future.retronet.config.Config;
import com.future.retronet.request.adapter.Method;

public class PutRequestBuilder extends RequestBuilder<PutRequestBuilder> {
    public PutRequestBuilder(Class<? extends Config> mConfigClass) {
        super(Method.Put, mConfigClass);
    }
}
