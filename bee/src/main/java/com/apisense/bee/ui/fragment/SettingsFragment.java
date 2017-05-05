package com.apisense.bee.ui.fragment;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityManager;
import android.widget.Button;
import android.widget.Toast;

import com.apisense.bee.BeeApplication;
import com.apisense.bee.R;
import com.apisense.bee.callbacks.BeeAPSCallback;
import com.apisense.bee.ui.activity.HomeActivity;
import com.apisense.bee.ui.activity.LoginActivity;
import com.apisense.bee.utils.RetroCompatibility;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.apisense.sdk.APISENSE;
import io.apisense.sdk.core.APSCallback;
import io.apisense.sdk.core.store.Crop;
import io.apisense.sdk.exception.UserNotConnectedException;

public class SettingsFragment extends BaseFragment {
    private static final String TAG = "Bee::SettingsFragment";
    @BindView(R.id.settings_manage_sensor_link)
    Button manageSensors;
    @BindView(R.id.settings_accessibility_link)
    Button accessibility;

    private APISENSE.Sdk apisenseSdk;
    private Unbinder unbinder;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View root = inflater.inflate(R.layout.fragment_settings, container, false);
        apisenseSdk = ((BeeApplication) getActivity().getApplication()).getSdk();
        unbinder = ButterKnife.bind(this, root);

        homeActivity.getSupportActionBar().setTitle("Settings");
        homeActivity.selectDrawerItem(HomeActivity.DRAWER_ACCOUNT_IDENTIFIER);

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        redrawAccessibilityButton();
    }


    /**
     * Redraw the accessibility button depending on the state of the accessibility service.
     */
    private void redrawAccessibilityButton() {
        if (isAccessibilityEnabled()) {
            redrawAccessibilityButton(R.string.settings_accessibility_enabled, R.color.aps_green);
        } else {
            redrawAccessibilityButton(R.string.settings_accessibility_disabled, R.color.aps_accent);
        }
    }

    /**
     * Set the given text and color to the accessibility button.
     *
     * @param textRes  The text resource to set (given the service name as format).
     * @param colorRes The color to set.
     */
    private void redrawAccessibilityButton(int textRes, int colorRes) {
        accessibility.setBackgroundColor(RetroCompatibility.retrieveColor(getResources(), colorRes));
        accessibility.setText(getString(textRes, getString(R.string.accessibility_service_name)));
    }

    /**
     * Check if an AccessibilityService with the same package as the application is enabled.
     * This method consider that we own only one accessibility service.
     *
     * @return true if an accessibility service is enabled, false otherwise.
     */
    public boolean isAccessibilityEnabled() {
        AccessibilityManager am = (AccessibilityManager) getActivity()
                .getSystemService(Context.ACCESSIBILITY_SERVICE);
        List<AccessibilityServiceInfo> runningServices = am
                .getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_ALL_MASK);

        for (AccessibilityServiceInfo service : runningServices) {
            String associatedApp = service.getResolveInfo().serviceInfo.applicationInfo.packageName;
            if (getActivity().getPackageName().equals(associatedApp)) {
                return true; // Our only AccessibilityService is enabled
            }
        }
        return false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    @OnClick(R.id.settings_manage_sensor_link)
    void manageSensors() {
        homeActivity.showSensors();

    }

    @OnClick(R.id.settings_accessibility_link)
    void manageAccessibilityService() {
        startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS));
    }

    public interface OnSensorClickedListener {
        void showSensors();
    }

    public class SignedOutCallback implements APSCallback<Void> {
        @Override
        public void onDone(Void aVoid) {
            Toast.makeText(getActivity(), R.string.status_changed_to_anonymous, Toast.LENGTH_SHORT).show();
            apisenseSdk.getCropManager().stopAll(new BeeAPSCallback<Crop>(getActivity()) {
                @Override
                public void onDone(Crop crop) {
                    Log.i(TAG, "Crop " + crop.getLocation() + " successfully stopped");
                }
            });
            openSlideShow();
        }

        @Override
        public void onError(Exception e) {
            if (e instanceof UserNotConnectedException) {
                openSlideShow();
            }
        }

        private void openSlideShow() {
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            startActivity(intent);
            getActivity().finish();
        }
    }

}
