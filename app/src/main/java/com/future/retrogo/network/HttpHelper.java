package com.future.retrogo.network;

import android.content.Context;

import com.future.retronet.RetroNet;

public class HttpHelper {
    static String mBaseUrl;
    static boolean mDebug = false;

    public static void init(Context context, boolean isDebug, String baseUrl) {
        mBaseUrl = baseUrl;
        mDebug = isDebug;
        RetroNet.INSTANCE.init(context, DefaultHttpConfig.class, isDebug);
    }
}
