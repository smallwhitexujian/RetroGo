package com.future.retrogo;

import android.app.Application;

import com.future.retrogo.network.HttpHelper;
import com.future.retronet.logger.TimberUtil;

public class App extends Application {
    public static String url = "https://dqx.shengshow.com/";

    @Override
    public void onCreate() {
        super.onCreate();
        TimberUtil.setLogAuto();
        HttpHelper.init(this, true, url);
    }
}
