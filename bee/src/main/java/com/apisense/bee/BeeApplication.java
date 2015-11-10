package com.apisense.bee;

import android.app.Application;

import com.apisense.sdk.APISENSE;
import com.rollbar.android.Rollbar;

public class BeeApplication extends Application {
    private APISENSE.Sdk sdk;

    public APISENSE.Sdk getSdk() {
        return sdk;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        sdk = new APISENSE(this).enableGCM(getString(R.string.gcm_defaultSenderId))
                .getSdk();

        Rollbar.init(this, "61805ed6db4d4a12832bc706019eeb1e", "production");
    }

}
