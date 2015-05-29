package com.apisense.bee.ui.fragment;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.apisense.bee.R;
import com.apisense.bee.backend.AsyncTasksCallbacks;
import com.apisense.bee.backend.user.SignOutTask;
import com.apisense.bee.ui.activity.SlideshowActivity;

import fr.inria.bsense.APISENSE;

public class AccountSettingsFragment extends Fragment implements View.OnClickListener {

    private TextView versionView;
    // Asynchronous Tasks
    private SignOutTask signOut;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_account_settings, container, false);

        versionView = (TextView) root.findViewById(R.id.settings_version);

        if (versionView != null)
            versionView.setText(getAppInfo().versionName);

        Button mLogoutButton = (Button) root.findViewById(R.id.settings_logout);
        mLogoutButton.setOnClickListener(this);
        Button mRegisterButton = (Button) root.findViewById(R.id.settings_register);
        mRegisterButton.setOnClickListener(this);
        Button mShareButton = (Button) root.findViewById(R.id.settings_share);
        mShareButton.setOnClickListener(this);


        if (!isUserAuthenticated()) {
            mLogoutButton.setVisibility(View.GONE);
            mRegisterButton.setVisibility(View.VISIBLE);
        }

        return root;
    }

    protected void doApplicationShare() {
        Intent sendIntent = new Intent(Intent.ACTION_SEND)
                .setAction(Intent.ACTION_SEND)
                .putExtra(Intent.EXTRA_TEXT, "linktobee");
        sendIntent.setType("text/plain");
        startActivity(Intent.createChooser(sendIntent, getResources().getText(R.string.action_share)));
    }

    protected void doDisconnect() {
        if (signOut == null) {
            signOut = new SignOutTask(APISENSE.apisense(), new SignedOutCallback());
            signOut.execute();
        }
    }

    public void goToRegister() {
        Intent slideIntent = new Intent(getActivity(), SlideshowActivity.class);
        slideIntent.putExtra("goTo", "register");
        startActivity(slideIntent);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.settings_logout:
                doDisconnect();
                break;
            case R.id.settings_register:
                goToRegister();
                break;
            case R.id.settings_share:
                doApplicationShare();
                break;
        }
    }

    /**
     * Helper to get the app version info
     *
     * @return a PackageInfo object
     */
    private PackageInfo getAppInfo() {
        PackageManager manager = getActivity().getPackageManager();
        try {
            return manager.getPackageInfo(getActivity().getPackageName(), 0);
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
            Toast.makeText(getActivity(), R.string.status_changed_to_anonymous, Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getActivity(), SlideshowActivity.class);
            startActivity(intent);
        }

        @Override
        public void onTaskCanceled() {
            signOut = null;
        }
    }

}
