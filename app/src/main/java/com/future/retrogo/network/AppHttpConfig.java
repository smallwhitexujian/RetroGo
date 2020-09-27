package com.future.retrogo.network;

import com.future.retrogo.App;

public class AppHttpConfig extends DefaultHttpConfig {
    @Override
    protected String getBaseUrl() {
        return App.url;
    }

}
