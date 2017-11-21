package com.apisense.bee;

import android.os.AsyncTask;

import com.apisense.bee.utils.accessibilitySting.AccessibilitySting;
import com.facebook.FacebookSdk;
import com.rollbar.Rollbar;
import com.rollbar.payload.Payload;

import java.util.Collections;
import java.util.List;

import io.apisense.sdk.APISENSE;
import io.apisense.sdk.APSApplication;
import io.apisense.sdk.adapter.SimpleAPSCallback;
import io.apisense.sdk.core.sting.InjectedStingPackage;
import io.apisense.sdk.core.sting.StingComponent;
import io.apisense.sdk.core.store.Crop;
import io.apisense.sting.environment.EnvironmentStingModule;
import io.apisense.sting.lib.Sting;
import io.apisense.sting.motion.MotionStingModule;
import io.apisense.sting.network.NetworkStingModule;
import io.apisense.sting.phone.PhoneStingModule;
import io.apisense.sting.visualization.VisualizationStingModule;

public class BeeApplication extends APSApplication {
    private Rollbar rollbar;
    final String sdkKey = "b34d85a0-a942-4abc-98f8-4039bef10b9e";
    final String cropIdentifier = "5WgYZjrP5aSzO1xzVKgx";

    @Override
    public void onCreate() {
        super.onCreate();

        rollbar = new Rollbar(
                BuildConfig.ROLLBAR_KEY,
                BuildConfig.ROLLBAR_ENV
        );

        setupCrop(getSdk());

        FacebookSdk.sdkInitialize(getApplicationContext());
    }

    public void setupCrop(APISENSE.Sdk sdk) {
        final APISENSE.Sdk tempSdk = sdk;
        // Log your Bee user in if no session available.
        if (sdk.getSessionManager().isConnected()) {
            installExperiment(sdk);
        } else {
            sdk.getSessionManager().applicationLogin(new SimpleAPSCallback<Void>() {
                @Override
                public void onDone(Void aVoid) {
                    installExperiment(tempSdk); // You can now install the experiment.
                }
            });
        }
    }

    // Install and start the collect, using your accessKey if the access is private
    private void installExperiment(APISENSE.Sdk sdk) {
        final APISENSE.Sdk tempSdk = sdk;
        sdk.getCropManager().installOrUpdate(cropIdentifier, new SimpleAPSCallback<Crop>() {
            @Override
            public void onDone(Crop crop) {
                // Crop Installed, ready to be started.
                tempSdk.getCropManager().start(crop, new SimpleAPSCallback<Crop>() {
                    @Override
                    public void onDone(Crop crop) {
                        // Crop finally started.
                    }
                });
            }
        });
    }

    @Override
    protected APISENSE.Sdk generateAPISENSESdk() {
        return new APISENSE(this)
                .useSdkKey(sdkKey)
                .bindStingPackage(
                        new VisualizationStingModule(),
                        new InjectedStingPackage() {
                            @Override
                            public List<Sting> getInstances(StingComponent component) {
                                return Collections.<Sting>singletonList(new AccessibilitySting(component.bus()));
                            }
                        })
                .useScriptExecutionService(false)
                .getSdk();
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
