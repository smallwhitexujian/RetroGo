package com.future.retronet

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
object RetroNet {
    private lateinit var mContext: Context
    private var mDefaultConfig: Class<out Config?>? = null
    private var mGson: Gson? = null
    private var respCodeInterceptorList: ArrayList<RetroCodeInterceptor>? = null

    public fun getContext(): Context {
        return mContext
    }

    public fun setGson(gson: Gson) {
        mGson = gson
    }

    public fun getGson(): Gson {
        if (mGson == null) {
            mGson = Gson()
        }
        return mGson as Gson
    }

    public fun init(context: Context, debug: Boolean) {
        init(context, null, debug)
    }

    fun init(context: Context, clientConfig: Class<out Config?>?, debug: Boolean) {
        mContext = context.applicationContext
        respCodeInterceptorList = ArrayList<RetroCodeInterceptor>()
        setDefaultConfig(clientConfig)
        LogUtil.setIsDebug(debug)
        if (debug){
            TimberUtil.setLogAuto()
        }
        RxJavaPlugins.setErrorHandler(RetroErrorHandler())
    }

    /**
     * 获取OkHttpClient
     * @param defaultConfig
     * @return
     */
    fun getClient(defaultConfig: Class<out Config?>?): OkHttpClient? {
        return Api.getOkHttpClient(defaultConfig)
    }

    /**
     * reset config, maybe you want change the retrofit with the config
     * 充值配置，如果你想修改Retrofit的配置
     *
     * @param clientConfig
     */
    fun resetConfig(clientConfig: Class<out Config?>?) {
        Api.resetConfig(clientConfig)
    }

    fun setDefaultConfig(defaultConfig: Class<out Config?>?) {
        mDefaultConfig = defaultConfig
    }

    fun getDefaultConfig(): Class<out Config?>? {
        return mDefaultConfig
    }

    @Synchronized
    fun getRespCodeinteceptorList(): List<RetroCodeInterceptor?>? {
        return respCodeInterceptorList
    }

    fun addRespCodeInteceptor(inteceptor: RetroCodeInterceptor?) {
        inteceptor?.let { respCodeInterceptorList?.add(it) }
    }

    fun removeRespCodeInteceptor(inteceptor: RetroCodeInterceptor?) {
        respCodeInterceptorList?.remove(inteceptor)
    }

    @JvmStatic
    fun <T> create(service: Class<T>): T {
        LogUtil.d(" create service 1: " + service.simpleName + "    " +
                " myPid : " + Process.myPid() + "  mDefaultConfig == null" +
                (mDefaultConfig == null).toString())
        if (mDefaultConfig == null) {
            throw java.lang.RuntimeException("mDefaultConfig == null, you must set a default config before!")
        }
        return Api.provide(mDefaultConfig).create(service)
    }


    @JvmStatic
    fun <T> create(clientConfig: Class<out Config?>?, service: Class<T>): T {
        LogUtil.d(" create service 2: " + service.simpleName + "     " +
                "myPid : " + Process.myPid() + "  mDefaultConfig == null"
                + (mDefaultConfig == null).toString()
        )
        return Api.provide(clientConfig).create(service)
    }

    fun <T> get(): GetRequestBuilder? {
        return get<T>(mDefaultConfig)
    }

    operator fun <T> get(clientConfig: Class<out Config?>?): GetRequestBuilder? {
        if (clientConfig == null) {
            throw RuntimeException("clientConfig == null")
        }
        return GetRequestBuilder(clientConfig)
    }

    fun <T> post(): PostRequestBuilder? {
        return post<T>(mDefaultConfig)
    }

    fun <T> post(clientConfig: Class<out Config?>?): PostRequestBuilder? {
        if (clientConfig == null) {
            throw RuntimeException("clientConfig == null")
        }
        return PostRequestBuilder(clientConfig)
    }

    fun put(): PutRequestBuilder? {
        return put(mDefaultConfig)
    }

    fun put(clientConfig: Class<out Config?>?): PutRequestBuilder? {
        if (clientConfig == null) {
            throw RuntimeException("clientConfig == null")
        }
        return PutRequestBuilder(clientConfig)
    }

    fun delete(): DeleteRequestBuilder? {
        return delete(mDefaultConfig)
    }

    fun delete(clientConfig: Class<out Config?>?): DeleteRequestBuilder? {
        if (clientConfig == null) {
            throw RuntimeException("clientConfig == null")
        }
        return DeleteRequestBuilder(clientConfig)
    }

    fun body(): BodyRequestBuilder? {
        return body(mDefaultConfig)
    }

    fun body(clientConfig: Class<out Config?>?): BodyRequestBuilder? {
        if (clientConfig == null) {
            throw RuntimeException("clientConfig == null")
        }
        return BodyRequestBuilder(clientConfig)
    }


}