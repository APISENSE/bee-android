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
        sdk = new APISENSE(this).getSdk();
        fr.spoonware.SpoonwareAndroid.init(this,
                "https://spoonware.lille.inria.fr/rest",
                "bee-3cbc5f3d-4f78-4b2b-a110-38297867726b",  // Application ID
                "8up76ft1kkb1kqd49f20ismub2");  // Application TOKEN
        Rollbar.init(this, "61805ed6db4d4a12832bc706019eeb1e", "production");
    }

}
