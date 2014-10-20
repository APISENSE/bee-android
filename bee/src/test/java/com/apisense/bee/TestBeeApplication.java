package com.apisense.bee;

import android.app.Application;
import fr.inria.asl.android.utils.AndroidLogger;
import fr.inria.asl.utils.Log;

public class TestBeeApplication extends Application {
    @Override
    public void onCreate(){
        super.onCreate();
        // set android logger
        Log.setLogger(new AndroidLogger(getBaseContext()));
    }
}
