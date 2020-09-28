package com.future.retrogo;

import com.future.retronet.RetroBean;

import io.reactivex.Observable;
import retrofit2.http.GET;

public interface TestService {
    @GET("/iqx/master")
    Observable<RetroBean<Object>> gettest();
}
