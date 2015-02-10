package com.apisense.bee.ui.activity;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.apisense.bee.R;
import com.apisense.bee.backend.AsyncTasksCallbacks;
import com.apisense.bee.backend.user.SignOutTask;

import fr.inria.bsense.APISENSE;

public class SettingsActivity extends FragmentActivity {

    private final String TAG = "SettingsActivity";
    TextView aboutView, versionView;
    // Asynchronous Tasks
    private SignOutTask signOut;
    /**
     * Click event for disconnect
     */
    private final View.OnClickListener disconnectEvent = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (signOut == null) {
                signOut = new SignOutTask(APISENSE.apisense(), new SignedOutCallback());
                signOut.execute();
            }
        }

        ;
    };
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

        mLogoutButton = (Button) findViewById(R.id.settings_logout);
        mRegisterButton = (Button) findViewById(R.id.settings_register);

        mLogoutButton.findViewById(R.id.settings_logout).setOnClickListener(disconnectEvent);

        if (!isUserAuthenticated()) {
            mLogoutButton.setVisibility(View.GONE);
            mRegisterButton.setVisibility(View.VISIBLE);
        }
    }

    public void goToRegister(View v) {
        Intent slideIntent = new Intent(this, SlideshowActivity.class);
        slideIntent.putExtra("goTo", "register");
        startActivity(slideIntent);
        finish();
    }

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

    private boolean isUserAuthenticated() {
        return APISENSE.apisServerService().isConnected();
    }

    public class SignedOutCallback implements AsyncTasksCallbacks {
        @Override
        public void onTaskCompleted(int result, Object response) {
            signOut = null;
            Toast.makeText(getApplicationContext(), R.string.status_changed_to_anonymous, Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(SettingsActivity.this, SlideshowActivity.class);
            startActivity(intent);
            finish();
        }

        @Override
        public void onTaskCanceled() {
            signOut = null;
        }
    }

}
