package com.future.retronet.interceptor;
/**
 * errcode拦截接口
 */
public interface RetroCodeInterceptor {
    boolean interceptorRespCode(int code, String extra);
}