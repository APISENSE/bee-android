package com.apisense.bee;

import android.os.Build;

import com.facebook.FacebookSdk;
import com.rollbar.notifier.Rollbar;
import com.rollbar.notifier.config.ConfigBuilder;
import com.rollbar.notifier.provider.Provider;
import com.rollbar.notifier.sender.BufferedSender;
import com.rollbar.notifier.sender.Sender;
import com.rollbar.notifier.sender.SyncSender;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import io.apisense.sdk.APISENSE;
import io.apisense.sdk.APSApplication;
import io.apisense.sting.environment.EnvironmentStingModule;
import io.apisense.sting.motion.MotionStingModule;
import io.apisense.sting.network.NetworkStingModule;
import io.apisense.sting.phone.PhoneStingModule;
import io.apisense.sting.visualization.VisualizationStingModule;

public class BeeApplication extends APSApplication {
    private Rollbar rollbar;

    private static final Map<String, Object> deviceMap;

    static {
        deviceMap = new HashMap<>();
        deviceMap.put("SDKVersion", Build.VERSION.SDK_INT);
        deviceMap.put("releaseName", Build.VERSION.RELEASE);
        deviceMap.put("device", Build.DEVICE);
        deviceMap.put("model", Build.MODEL);
        deviceMap.put("brand", Build.BRAND);
        deviceMap.put("manufacturer", Build.MANUFACTURER);
        deviceMap.put("product", Build.PRODUCT);
        deviceMap.put("language", Locale.getDefault().getDisplayLanguage());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            deviceMap.put("os", Build.VERSION.BASE_OS);
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();


        Sender sender = new SyncSender.Builder().accessToken(BuildConfig.ROLLBAR_KEY).build();
        // Rollbar doesn't seems to upload errors with only a SyncSender.
        Sender buffSender = new BufferedSender.Builder().sender(sender).build();

        rollbar = Rollbar.init(
                ConfigBuilder.withAccessToken(BuildConfig.ROLLBAR_KEY)
                        .environment(BuildConfig.ROLLBAR_ENV)
                        .framework("Android")
                        .handleUncaughtErrors(true)
                        .sender(buffSender)
                        .codeVersion(BuildConfig.VERSION_NAME)
                        .custom(new Provider<Map<String, Object>>() {
                            @Override
                            public Map<String, Object> provide() {
                                return deviceMap;
                            }
                        })
                        .build()
        );

        FacebookSdk.sdkInitialize(getApplicationContext());
    }

    @Override
    protected APISENSE.Sdk generateAPISENSESdk() {
        return new APISENSE(this)
                .useSdkKey(com.apisense.bee.BuildConfig.SDK_KEY)
                .bindStingPackage(new PhoneStingModule(), new NetworkStingModule(),
                        new MotionStingModule(), new EnvironmentStingModule(),
                        new VisualizationStingModule())
                .useScriptExecutionService(true)
                .getSdk();
    }

    public void reportException(final Throwable throwable) {
        // Reported exceptions should not make the application crash, thus the warning state.
        rollbar.warning(throwable);
    }
}
