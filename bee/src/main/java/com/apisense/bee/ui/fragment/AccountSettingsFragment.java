package com.apisense.bee.ui.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.apisense.bee.BeeApplication;
import com.apisense.bee.R;
import com.apisense.bee.games.BeeGameActivity;
import com.apisense.bee.games.SimpleGameAchievement;
import com.apisense.bee.ui.activity.SlideshowActivity;
import com.apisense.sdk.APISENSE;
import com.apisense.sdk.core.APSCallback;

public class AccountSettingsFragment extends Fragment implements View.OnClickListener {

    private APISENSE.Sdk apisenseSdk;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_settings_account, container, false);
        apisenseSdk = ((BeeApplication) getActivity().getApplication()).getSdk();

        // Click listeners
        Button mLogoutButton = (Button) root.findViewById(R.id.settings_logout);
        mLogoutButton.setOnClickListener(this);
        Button mRegisterButton = (Button) root.findViewById(R.id.settings_register);
        mRegisterButton.setOnClickListener(this);
        Button mShareButton = (Button) root.findViewById(R.id.settings_share);
        mShareButton.setOnClickListener(this);

        // UI Setup
        if (!isUserAuthenticated()) {
            mLogoutButton.setVisibility(View.GONE);
            mRegisterButton.setVisibility(View.VISIBLE);
        }

        return root;
    }

    protected void doApplicationShare() {
        new SimpleGameAchievement(getString(R.string.achievement_recruiting_bee)).unlock(this);
        Intent sendIntent = new Intent(Intent.ACTION_SEND)
                .setAction(Intent.ACTION_SEND)
                .putExtra(Intent.EXTRA_TEXT, "linktobee");
        sendIntent.setType("text/plain");
        startActivity(Intent.createChooser(sendIntent, getResources().getText(R.string.action_share)));
    }

    protected void doDisconnect() {
        apisenseSdk.getSessionManager().logout(new SignedOutCallback());
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

    private boolean isUserAuthenticated() {
        return apisenseSdk.getSessionManager().isConnected();
    }

    public class SignedOutCallback implements APSCallback<Void> {
        @Override
        public void onDone(Void aVoid) {
            Toast.makeText(getActivity(), R.string.status_changed_to_anonymous, Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getActivity(), SlideshowActivity.class);
            startActivity(intent);
        }

        @Override
        public void onError(Exception e) {

        }
    }

}
