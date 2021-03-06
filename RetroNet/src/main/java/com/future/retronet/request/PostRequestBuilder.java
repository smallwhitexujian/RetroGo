package com.future.retronet.request;

import com.future.retronet.config.Config;
import com.future.retronet.request.adapter.Method;

public class PostRequestBuilder extends RequestBuilder<PostRequestBuilder> {

    public PostRequestBuilder(Class<? extends Config> mConfigClass) {
        super(Method.Post, mConfigClass);
    }
}
