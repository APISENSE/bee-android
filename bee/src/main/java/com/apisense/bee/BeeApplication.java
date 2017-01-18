package com.apisense.bee;

import android.app.Application;

import com.facebook.FacebookSdk;
import com.rollbar.Rollbar;
import com.rollbar.payload.Payload;
import com.rollbar.sender.Sender;

import io.apisense.sdk.APISENSE;
import io.apisense.sting.network.NetworkStingModule;
import io.apisense.sting.phone.PhoneStingModule;

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
                .addStingsModules(new PhoneStingModule(), new NetworkStingModule())
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
