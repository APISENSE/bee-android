package com.apisense.bee;

import android.content.Context;
import android.os.AsyncTask;
import android.support.multidex.MultiDex;

import com.facebook.FacebookSdk;
import com.rollbar.Rollbar;
import com.rollbar.payload.Payload;

import io.apisense.sdk.APISENSE;
import io.apisense.sdk.APSApplication;
import io.apisense.sting.environment.EnvironmentStingModule;
import io.apisense.sting.motion.MotionStingModule;
import io.apisense.sting.network.NetworkStingModule;
import io.apisense.sting.phone.PhoneStingModule;
import io.apisense.sting.visualization.VisualizationStingModule;


public class BeeApplication extends APSApplication {
    private Rollbar rollbar;

    @Override
    protected void attachBaseContext(Context base) {
        // Support for android < 5.0
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        rollbar = new Rollbar(
                BuildConfig.ROLLBAR_KEY,
                BuildConfig.ROLLBAR_ENV
        );
        FacebookSdk.sdkInitialize(getApplicationContext());
    }

    @Override
    protected APISENSE.Sdk generateAPISENSESdk() {
        APISENSE apisense = new APISENSE(this)
                .useSdkKey(com.apisense.bee.BuildConfig.SDK_KEY)
                .bindStingPackage(new PhoneStingModule(),
                        new NetworkStingModule(),
                        new MotionStingModule(),
                        new EnvironmentStingModule(),
                        new VisualizationStingModule()
                )
                .useScriptExecutionService(true);

        configure(apisense);

        return apisense.getSdk();
    }

    protected void configure(APISENSE apisense) {
        // To override
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
