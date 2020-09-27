package com.future.retronet.request.adapter;

import com.future.retronet.request.RequestBuilder;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;

public interface RequestAdapter {
    <T> Observable<T> adapt(@NonNull RequestBuilder builder);
}
