package com.future.retronet.request.adapter;

import com.future.retronet.request.RequestBuilder;

import io.reactivex.Observable;
import io.reactivex.annotations.NonNull;


public interface RequestAdapter {
    <T> Observable<T> adapt(@NonNull RequestBuilder builder);
}
