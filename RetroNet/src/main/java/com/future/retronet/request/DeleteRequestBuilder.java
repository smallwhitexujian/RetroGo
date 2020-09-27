package com.future.retronet.request;

import com.future.retronet.config.Config;
import com.future.retronet.request.adapter.Method;

public class DeleteRequestBuilder extends RequestBuilder<DeleteRequestBuilder> {
    public DeleteRequestBuilder(Class<? extends Config> mConfigClass) {
        super(Method.Delete, mConfigClass);
    }
}
