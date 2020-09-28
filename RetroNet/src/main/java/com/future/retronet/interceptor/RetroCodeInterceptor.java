package com.future.retronet.interceptor;
/**
 * errCode拦截接口
 */
public interface RetroCodeInterceptor {
    boolean interceptorRespCode(int code, String extra);
}