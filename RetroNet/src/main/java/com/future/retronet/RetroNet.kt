package com.future.retronet

import android.annotation.SuppressLint
import android.content.Context
import android.os.Process
import com.future.retronet.config.Config
import com.future.retronet.interceptor.RetroCodeInterceptor
import com.future.retronet.logger.LogUtil
import com.future.retronet.logger.TimberUtil
import com.future.retronet.request.*
import com.google.gson.Gson
import io.reactivex.plugins.RxJavaPlugins
import okhttp3.OkHttpClient

/**
 * 网络请求
 */
@SuppressLint("StaticFieldLeak")
class RetroNet {
    companion object {
        @JvmField
        var mContext: Context? = null

        @JvmField
        var mDefaultConfig: Class<out Config?>? = null

        @JvmField
        var mGson: Gson? = null

        @JvmField
        var respCodeInterceptorList: ArrayList<RetroCodeInterceptor>? = null

        @JvmStatic
        fun getContext(): Context {
            return mContext!!.applicationContext
        }

        @JvmStatic
        fun setGson(gson: Gson) {
            mGson = gson
        }

        @JvmStatic
        fun getGson(): Gson {
            if (mGson == null) {
                mGson = Gson()
            }
            return mGson as Gson
        }

        @JvmStatic
        fun init(context: Context, debug: Boolean) {
            init(context, null, debug)
        }

        @JvmStatic
        fun init(context: Context, clientConfig: Class<out Config?>?, debug: Boolean) {
            mContext = context.applicationContext
            respCodeInterceptorList = ArrayList<RetroCodeInterceptor>()
            setDefaultConfig(clientConfig)
            LogUtil.setIsDebug(debug)
            if (debug) {
                TimberUtil.setLogAuto()
            }
            RxJavaPlugins.setErrorHandler(RetroErrorHandler())
        }

        /**
         * 获取OkHttpClient
         * @param defaultConfig
         * @return
         */
        @JvmStatic
        fun getClient(defaultConfig: Class<out Config?>?): OkHttpClient? {
            return Api.getOkHttpClient(defaultConfig)
        }

        /**
         * reset config, maybe you want change the retrofit with the config
         * 充值配置，如果你想修改Retrofit的配置
         *
         * @param clientConfig
         */
        @JvmStatic
        fun resetConfig(clientConfig: Class<out Config?>?) {
            Api.resetConfig(clientConfig)
        }

        fun setDefaultConfig(defaultConfig: Class<out Config?>?) {
            mDefaultConfig = defaultConfig
        }

        @JvmStatic
        fun getDefaultConfig(): Class<out Config?>? {
            return mDefaultConfig
        }

        @JvmStatic
        @Synchronized
        fun getRespCodeinteceptorList(): List<RetroCodeInterceptor?>? {
            return respCodeInterceptorList
        }

        @JvmStatic
        fun addRespCodeInteceptor(inteceptor: RetroCodeInterceptor?) {
            inteceptor?.let { respCodeInterceptorList?.add(it) }
        }

        @JvmStatic
        fun removeRespCodeInteceptor(inteceptor: RetroCodeInterceptor?) {
            respCodeInterceptorList?.remove(inteceptor)
        }

        @JvmStatic
        fun <T> create(service: Class<T>): T {
            LogUtil.d(
                " create service 1: " + service.simpleName + "    " +
                        " myPid : " + Process.myPid() + "  mDefaultConfig == null" +
                        (mDefaultConfig == null).toString()
            )
            if (mDefaultConfig == null) {
                throw java.lang.RuntimeException("mDefaultConfig == null, you must set a default config before!")
            }
            return Api.provide(mDefaultConfig).create(service)
        }


        @JvmStatic
        fun <T> create(clientConfig: Class<out Config?>?, service: Class<T>): T {
            LogUtil.d(
                " create service 2: " + service.simpleName + "     " +
                        "myPid : " + Process.myPid() + "  mDefaultConfig == null"
                        + (mDefaultConfig == null).toString()
            )
            return Api.provide(clientConfig).create(service)
        }

        @JvmStatic
        fun <T> get(): GetRequestBuilder? {
            return get<T>(mDefaultConfig)
        }

        @JvmStatic
        operator fun <T> get(clientConfig: Class<out Config?>?): GetRequestBuilder? {
            if (clientConfig == null) {
                throw RuntimeException("clientConfig == null")
            }
            return GetRequestBuilder(clientConfig)
        }

        @JvmStatic
        fun <T> post(): PostRequestBuilder? {
            return post<T>(mDefaultConfig)
        }

        @JvmStatic
        fun <T> post(clientConfig: Class<out Config?>?): PostRequestBuilder? {
            if (clientConfig == null) {
                throw RuntimeException("clientConfig == null")
            }
            return PostRequestBuilder(clientConfig)
        }

        fun put(): PutRequestBuilder? {
            return put(mDefaultConfig)
        }

        @JvmStatic
        fun put(clientConfig: Class<out Config?>?): PutRequestBuilder? {
            if (clientConfig == null) {
                throw RuntimeException("clientConfig == null")
            }
            return PutRequestBuilder(clientConfig)
        }

        @JvmStatic
        fun delete(): DeleteRequestBuilder? {
            return delete(mDefaultConfig)
        }

        @JvmStatic
        fun delete(clientConfig: Class<out Config?>?): DeleteRequestBuilder? {
            if (clientConfig == null) {
                throw RuntimeException("clientConfig == null")
            }
            return DeleteRequestBuilder(clientConfig)
        }

        @JvmStatic
        fun body(): BodyRequestBuilder? {
            return body(mDefaultConfig)
        }

        @JvmStatic
        fun body(clientConfig: Class<out Config?>?): BodyRequestBuilder? {
            if (clientConfig == null) {
                throw RuntimeException("clientConfig == null")
            }
            return BodyRequestBuilder(clientConfig)
        }
    }
}