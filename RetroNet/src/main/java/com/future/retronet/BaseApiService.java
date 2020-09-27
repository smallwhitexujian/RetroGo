package com.future.retronet;

import com.future.retronet.header.HeaderKey;

import java.util.Map;

import io.reactivex.rxjava3.core.Observable;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.QueryMap;

public interface BaseApiService {
    /**
     * get 方法
     *
     * @param header
     * @param url
     * @param maps
     * @return
     */
    @GET("{url}")
    Observable<ResponseBody> get(@Header(HeaderKey.Header) Map<String, Object> header,
                                 @Path(value = "url", encoded = true) String url,
                                 @QueryMap Map<String, Object> maps);

    /**
     * post 方法
     *
     * @param header
     * @param url
     * @param maps
     * @return
     */
    @FormUrlEncoded
    @POST("{url}")
    Observable<ResponseBody> post(@Header(HeaderKey.Header) Map<String, Object> header,
                                  @Path(value = "url", encoded = true) String url,
                                  @FieldMap Map<String, Object> maps);

    /**
     * delete 方法
     *
     * @param header
     * @param url
     * @param maps
     * @return
     */
    @DELETE("{url}")
    Observable<ResponseBody> delete(@Header(HeaderKey.Header) Map<String, Object> header,
                                    @Path("url") String url,
                                    @QueryMap Map<String, Object> maps);

    /**
     * put 方法
     *
     * @param header
     * @param url
     * @param maps
     * @return
     */
    @PUT("{url}")
    Observable<ResponseBody> put(@Header(HeaderKey.Header) Map<String, Object> header,
                                 @Path("url") String url,
                                 @QueryMap Map<String, Object> maps);

    /**
     * body 方法
     *
     * @param header
     * @param url
     * @param body
     * @return
     */
    @POST("{url}")
    Observable<ResponseBody> body(@Header(HeaderKey.Header) Map<String, Object> header,
                                  @Path(value = "url", encoded = true) String url,
                                  @Body RequestBody body);

}
