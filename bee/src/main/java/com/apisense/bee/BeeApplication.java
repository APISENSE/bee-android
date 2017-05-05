package com.apisense.bee;

import android.app.Application;
import android.os.AsyncTask;

import com.facebook.FacebookSdk;
import com.rollbar.Rollbar;
import com.rollbar.payload.Payload;

import io.apisense.sdk.APISENSE;
import io.apisense.sting.environment.EnvironmentStingModule;
import io.apisense.sting.motion.MotionStingModule;
import io.apisense.sting.network.NetworkStingModule;
import io.apisense.sting.phone.PhoneStingModule;
import io.apisense.sting.visualization.VisualizationStingModule;

public class BeeApplication extends Application {
    private APISENSE.Sdk sdk;
    private Rollbar rollbar;

    public APISENSE.Sdk getSdk() {
        return sdk;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        sdk = new APISENSE(this)
                .useSdkKey(com.apisense.bee.BuildConfig.SDK_KEY)
                .addStingsModules(new PhoneStingModule(), new NetworkStingModule(),
                        new MotionStingModule(), new EnvironmentStingModule(),
                        new VisualizationStingModule())
                .getSdk();

        rollbar = new Rollbar(
                BuildConfig.ROLLBAR_KEY,
                BuildConfig.ROLLBAR_ENV
        );

        FacebookSdk.sdkInitialize(getApplicationContext());
    }

    public void reportException(final Throwable throwable) {
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                Payload rollbarPayload = Payload.fromError(
                        BuildConfig.ROLLBAR_KEY, BuildConfig.ROLLBAR_ENV,
                        throwable, null
                );
                rollbar.getSender().send(rollbarPayload);
                return null;
            }
        }.execute();

    }
}
