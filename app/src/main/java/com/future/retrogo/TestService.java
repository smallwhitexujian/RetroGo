package com.future.retrogo;

import io.reactivex.Observable;
import retrofit2.http.GET;

public interface TestService {
    @GET("/iqx/master")
    Observable<Object> gettest();
}
