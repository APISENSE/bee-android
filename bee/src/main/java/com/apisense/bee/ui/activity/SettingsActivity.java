package com.apisense.bee.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import com.apisense.bee.R;
import fr.inria.bsense.APISENSE;
import fr.inria.bsense.appmodel.Experiment;

public class SettingsActivity extends Activity {

    private final String TAG = "SettingsActivity";

    //TODO: Use HockeyApp library again
    //private CheckUpdateTask checkUpdateTask;

    TextView aboutView, versionView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        versionView = (TextView) findViewById(R.id.settings_version);
        aboutView = (TextView) findViewById(R.id.settings_about_content);

        if (versionView != null)
            versionView.setText(getAppInfo().versionName);

        if (aboutView != null) {
            aboutView.setText(Html.fromHtml(getString(R.string.settings_about_content)));
            aboutView.setMovementMethod(LinkMovementMethod.getInstance());
        }

        findViewById(R.id.settings_update).setOnClickListener(updateEvent);
        findViewById(R.id.settings_logout).setOnClickListener(disconnectEvent);
    }

    /**
     * Click event for disconnect
     */
    private final View.OnClickListener disconnectEvent = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            try {
                APISENSE.apisMobileService().sendAllTrack();
                APISENSE.apisMobileService().stopAllExperiments(0);
                for(Experiment xp: APISENSE.apisMobileService().getInstalledExperiments().values())
                    APISENSE.apisMobileService().uninstallExperiment(xp);
            } catch (Exception e) {
                e.printStackTrace();
            }
            APISENSE.apisServerService().disconnect();
            Intent intent = new Intent(SettingsActivity.this, SlideshowActivity.class);
            startActivity(intent);
            finish();
        };
    };

    /**
     * Click event for update
     */
    private final View.OnClickListener updateEvent = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
/*
           checkUpdateTask = (CheckUpdateTask) getLastCustomNonConfigurationInstance();
            if (checkUpdateTask != null)
                checkUpdateTask.attach(SettingsActivity.this);
            else {
                checkUpdateTask = new CheckUpdateTask(SettingsActivity.this, "http://download.apisense.fr/", "Bee");
                checkUpdateTask.execute();
            }
    //*/
            Toast.makeText(getBaseContext(), "Clicked on update detected", Toast.LENGTH_SHORT).show();
        };
    };

//    @Override
//    public Object onRetainCustomNonConfigurationInstance() {
//        if(checkUpdateTask!=null)
//            checkUpdateTask.detach();
//        return checkUpdateTask;
//    }

    /**
     * Helper to get the app version info
     *
     * @return a PackageInfo object
     */
    private PackageInfo getAppInfo() {
        PackageManager manager = getPackageManager();
        try {
            return manager.getPackageInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
}
