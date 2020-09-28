package com.future.retronet.interceptor;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * 这个其实是数据解压缩模块
 */
public class GzipInterceptor implements Interceptor {
    @Override
    public Response intercept(Interceptor.Chain chain) throws IOException {
        Request request = chain.request();
        Response response = chain.proceed(request);

        if ("gzip".equals(response.header("Content-Encoding"))) {
            ResponseBody body = response.body();
            ResponseBody outBody = ResponseBody.create(body.contentType(), ungzip(body.byteStream()));

            return response.newBuilder()
                    .headers(response.headers())
                    .removeHeader("Content-Encoding")
                    .body(outBody)
                    .build();
        } else {
            return response;
        }
    }

    public static byte[] ungzip(InputStream in) throws IOException {
        GZIPInputStream pIn = new GZIPInputStream(in);
        byte[] ret = IOUtils.toByteArray(pIn);
        pIn.close();
        return ret;
    }
}
