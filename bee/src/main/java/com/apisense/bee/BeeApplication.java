package com.apisense.bee;

import android.app.Application;

import io.apisense.sdk.APISENSE;
import com.facebook.FacebookSdk;
import com.rollbar.Rollbar;
import com.rollbar.payload.Payload;
import com.rollbar.sender.Sender;

public class BeeApplication extends Application {
    private APISENSE.Sdk sdk;
    private Sender rollbarSender;

    public APISENSE.Sdk getSdk() {
        return sdk;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        sdk = new APISENSE(this)
                .useSdkKey(com.apisense.bee.BuildConfig.SDK_KEY)
                .getSdk();

        Rollbar rollbar = new Rollbar(
                BuildConfig.ROLLBAR_KEY,
                BuildConfig.ROLLBAR_ENV
        );
        rollbar.handleUncaughtErrors();
        this.rollbarSender = rollbar.getSender();

        FacebookSdk.sdkInitialize(getApplicationContext());
    }

    public void reportException(Throwable throwable) {
        Payload rollbarPayload = Payload.fromError(
                BuildConfig.ROLLBAR_KEY, BuildConfig.ROLLBAR_ENV,
                throwable, null
        );
        rollbarSender.send(rollbarPayload);
    }
}
