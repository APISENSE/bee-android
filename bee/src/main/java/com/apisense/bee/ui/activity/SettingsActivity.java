package com.apisense.bee.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.apisense.android.api.APS;
import com.apisense.bee.R;
import com.apisense.bee.backend.user.SignOutTask;
import com.apisense.core.api.Callback;

public class SettingsActivity extends Activity {

    private final String TAG = "SettingsActivity";

    // Asynchronous Tasks
    private SignOutTask signOut;

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
        Button mLogoutButton = (Button) findViewById(R.id.settings_logout);
        mLogoutButton.findViewById(R.id.settings_logout).setOnClickListener(disconnectEvent);
    }

    public void goToRegister(View v) {
        Intent slideIntent = new Intent(this, SlideshowActivity.class);
        slideIntent.putExtra("goTo", SlideshowActivity.REGISTER);
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

    /**
     *
     * Helper to get the app version info
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
