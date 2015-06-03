package com.apisense.bee.ui.activity;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import com.apisense.bee.BeeApplication;
import com.apisense.bee.R;
import com.apisense.sdk.core.privacy.PrivacyOperations;


public class PrivacyActivity extends ActionBarActivity {
    private final String TAG = "PrivacyActivity";
    private PrivacyOperations privacyManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy);
        privacyManager = ((BeeApplication) getApplication()).getSdk().getPrivacyManager();
    }
}
