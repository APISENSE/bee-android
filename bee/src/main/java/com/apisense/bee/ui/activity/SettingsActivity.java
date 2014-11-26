package com.apisense.bee.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.apisense.android.api.APS;
import com.apisense.api.Callback;
import com.apisense.bee.R;
import com.apisense.bee.backend.user.SignOutTask;
import net.hockeyapp.android.CheckUpdateTask;

public class SettingsActivity extends Activity {

    private final String TAG = "SettingsActivity";

    // Asynchronous Tasks
    private SignOutTask signOut;
    private CheckUpdateTask checkUpdateTask;

    TextView aboutView, versionView;
    private Button mUpgradeButton;
    private Button mLogoutButton;
    private Button mRegisterButton;

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

        mUpgradeButton = (Button) findViewById(R.id.settings_update);
        mLogoutButton = (Button) findViewById(R.id.settings_logout);
        mRegisterButton = (Button) findViewById(R.id.settings_register);
        
        mUpgradeButton.setOnClickListener(updateEvent);
        mLogoutButton.findViewById(R.id.settings_logout).setOnClickListener(disconnectEvent);

        if (!isUserAuthenticated()) {
            mLogoutButton.setVisibility(View.GONE);
            mRegisterButton.setVisibility(View.VISIBLE);
        }
    }

    public void goToRegister(View v) {
        Intent slideIntent = new Intent(this, SlideshowActivity.class);
        slideIntent.putExtra("goTo","register");
        startActivity(slideIntent);
        finish();
    }

    /**
     * Click event for disconnect
     */
    private final View.OnClickListener disconnectEvent = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (signOut == null) {
                signOut = new SignOutTask(getApplicationContext(), new SignedOutCallback());
                signOut.execute();
            }
        };
    };

//    /**
//     * Click event for update
//     */
//    private final View.OnClickListener updateEvent = new View.OnClickListener() {
//        @Override
//        public void onClick(View v) {
//            checkUpdateTask = (CheckUpdateTask) getLastCustomNonConfigurationInstance();
//            if (checkUpdateTask != null)
//                checkUpdateTask.attach(SettingsActivity.this);
//            else {
//                checkUpdateTask = new CheckUpdateTask(SettingsActivity.this, "http://download.apisense.fr/", "Bee");
//                checkUpdateTask.execute();
//            }
//            Toast.makeText(getBaseContext(), "Clicked on update detected", Toast.LENGTH_SHORT).show();
//        };
//    };
//
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

    public class SignedOutCallback implements Callback<Void> {
        @Override
        public void onCall(Void aVoid) throws Exception {
            signOut = null;
            Toast.makeText(getApplicationContext(), R.string.status_changed_to_anonymous, Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(SettingsActivity.this, SlideshowActivity.class);
            startActivity(intent);
            finish();
        }

        @Override
        public void onError(Throwable throwable) {
            signOut = null;
        }
    }

    private boolean isUserAuthenticated() {
        try {
            return APS.isConnected(getApplicationContext());
        } catch (APS.SDKNotInitializedException e) {
            e.printStackTrace();
            return false;
        }
    }

}
