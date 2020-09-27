package com.future.retronet;

import com.future.retronet.interceptor.RetroCodeInterceptor;
import com.future.retronet.logger.LogUtil;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import io.reactivex.functions.Function;
import okhttp3.ResponseBody;

public class RetroFun<T> implements Function<ResponseBody, T> {

    StructType mStructType = StructType.Result;
    Type type;

    public RetroFun(Type type, StructType mStructType) {
        this.mStructType = mStructType == null ? StructType.Result : mStructType;
        this.type = type;
    }

    @Override
    public T apply(ResponseBody requestBody) {
        return parse(requestBody);
    }


    protected T parse(ResponseBody responseBody) {
        if (type instanceof ParameterizedType) {
            Class<T> cls = (Class) ((ParameterizedType) type).getRawType();
            if (RetroBean.class.isAssignableFrom(cls)) {
                return parseRetroBean(responseBody);
            } else {
                mStructType = StructType.Direct;
                return parseBasicTypes(responseBody, cls);
            }
        } else {
            if (RetroBean.class.isAssignableFrom((Class<T>) type)) {
                return parseRetroBean(responseBody);
            } else {
                mStructType = StructType.Direct;
                return parseBasicTypes(responseBody, (Class<T>) type);
            }
        }
    }


    private T parseRetroBean(ResponseBody responseBody) {
        RetroBean<T> bean = null;
        try {
            bean = RetroNet.INSTANCE.getGson().fromJson(responseBody.toString(), type);
        } catch (Exception e) {
            throw createParseException(e);
        }
        if (bean == null) {
            throw new RetroException(RetroCode.CODE_PARSE_ERR, "Json Parse error:" + type);
        }
        LogUtil.i("parse succed --> type: " + type);
        switch (mStructType) {
            case Result:
                if (!bean.isSuccess()) {
                    interceptorResp(bean.getCode(), bean.getExtra());
                    throw new RetroException(bean.getCode(), bean.getMessage());
                }
                T result = bean.getData();
                if (result != null) {
                    return result;
                } else {
                    return (T) new Object();
                }
            case Bean:
                if (!bean.isSuccess()) {
                    interceptorResp(bean.getCode(), bean.getExtra());
                    throw new RetroException(bean.getCode(), bean.getMessage());
                }
                return (T) bean;
            case Direct:
                return (T) bean;
            default:
                throw new RetroException(bean.getCode(), bean.getMessage());
        }
    }

    /**
     * parse the basic type
     *
     * @param cls juge if the type is basic type or not
     * @see CharSequence
     * @see Character
     * @see Boolean
     * @see Double
     * @see Long
     * @see Short
     * @see Float
     * @see Integer
     * @see Byte
     * @see InputStream
     * @see ResponseBody
     */
    private T parseBasicTypes(ResponseBody responseBody, Class<T> cls) {
        if (CharSequence.class.isAssignableFrom(cls)) {
            try {
                return (T) responseBody.string();
            } catch (IOException e) {
                throw createParseException(e);
            }
        } else if (Boolean.class.isAssignableFrom(cls)) {
            try {
                return (T) Boolean.valueOf(responseBody.string());
            } catch (IOException e) {
                throw createParseException(e);
            }
        } else if (Double.class.isAssignableFrom(cls)) {
            try {
                return (T) Double.valueOf(responseBody.string());
            } catch (IOException e) {
                throw createParseException(e);
            }
        } else if (Long.class.isAssignableFrom(cls)) {
            try {
                return (T) Long.valueOf(responseBody.string());
            } catch (IOException e) {
                throw createParseException(e);
            }
        } else if (Short.class.isAssignableFrom(cls)) {
            try {
                return (T) Short.valueOf(responseBody.string());
            } catch (IOException e) {
                throw createParseException(e);
            }
        } else if (Float.class.isAssignableFrom(cls)) {
            try {
                return (T) Float.valueOf(responseBody.string());
            } catch (IOException e) {
                throw createParseException(e);
            }
        } else if (Integer.class.isAssignableFrom(cls)) {
            try {
                return (T) Integer.valueOf(responseBody.string());
            } catch (IOException e) {
                throw createParseException(e);
            }
        } else if (Byte.class.isAssignableFrom(cls)) {
            try {
                return (T) Byte.valueOf(responseBody.string());
            } catch (IOException e) {
                throw createParseException(e);
            }
        } else if (InputStream.class.isAssignableFrom(cls)) {
            return (T) responseBody.byteStream();
        } else if (ResponseBody.class.isAssignableFrom(cls)) {
            return (T) responseBody;
        } else if (RetroBean.class.isAssignableFrom(cls)) {
            return parseRetroBean(responseBody);
        } else {
            try {
                //the parse data type use type not cls
                return RetroNet.INSTANCE.getGson().fromJson(responseBody.string(), type);
            } catch (Exception e) {
                throw createParseException(e);
            }
        }
    }

    //解析失败拦截
    private boolean interceptorResp(int code, String extra) {
        if (RetroNet.INSTANCE.getRespCodeinteceptorList() != null && !RetroNet.INSTANCE.getRespCodeinteceptorList().isEmpty()) {
            for (RetroCodeInterceptor interceptor : RetroNet.INSTANCE.getRespCodeinteceptorList()) {
                return interceptor.interceptorRespCode(code, extra);
            }
        }
        return false;
    }


    private RetroException createParseException(Exception e) {
        return new RetroException(RetroCode.CODE_PARSE_ERR, "Json Parse error:" + type + "\n" + e.getMessage());
    }
}
