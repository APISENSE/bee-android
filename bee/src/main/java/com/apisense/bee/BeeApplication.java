package com.apisense.bee;

import android.app.Application;

import com.apisense.sdk.APISENSE;
import com.facebook.FacebookSdk;
import com.rollbar.android.Rollbar;

public class BeeApplication extends Application {
    private APISENSE.Sdk sdk;

    public APISENSE.Sdk getSdk() {
        return sdk;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        sdk = new APISENSE(this)
                .enableGCM(getString(R.string.gcm_defaultSenderId))
                .useSdkKey(com.apisense.bee.BuildConfig.SDK_KEY)
                .getSdk();

        Rollbar.init(this,
                com.apisense.bee.BuildConfig.ROLLBAR_KEY,
                com.apisense.bee.BuildConfig.ROLLBAR_ENV
        );

        FacebookSdk.sdkInitialize(getApplicationContext());

    }

}
