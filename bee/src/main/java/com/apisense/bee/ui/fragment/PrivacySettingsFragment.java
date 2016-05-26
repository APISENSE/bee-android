package com.apisense.bee.ui.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.apisense.bee.BeeApplication;
import com.apisense.bee.R;
import com.apisense.bee.callbacks.BeeAPSCallback;
import com.apisense.bee.games.SimpleGameAchievement;
import com.apisense.bee.ui.adapter.SensorListAdapter;
import com.apisense.sdk.APISENSE;
import com.apisense.sdk.core.preferences.Preferences;
import com.apisense.sdk.core.preferences.Sensor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PrivacySettingsFragment extends Fragment {
    private static final String TAG = "PrivacySettingsFragment";
    private SensorListAdapter sensorsAdapter;
    private APISENSE.Sdk apisenseSdk;

    private Preferences preferences = new Preferences();

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_settings_privacy, container, false);
        apisenseSdk = ((BeeApplication) getActivity().getApplication()).getSdk();

        List<Sensor> sensorList = new ArrayList<>(apisenseSdk.getPreferencesManager().retrieveAvailableSensors());
        Collections.sort(sensorList);
        Log.i(TAG, "Got sensors: " + sensorList);

        sensorsAdapter = new SensorListAdapter(getActivity(), R.layout.list_item_sensor);

        ListView sensorsListView = (ListView) root.findViewById(R.id.sensors_list);
        sensorsListView.setAdapter(sensorsAdapter);
        sensorsListView.setEmptyView(root.findViewById(R.id.store_empty_list));

        sensorsAdapter.setDataSet(sensorList);
        sensorsAdapter.notifyDataSetChanged();
        apisenseSdk.getPreferencesManager().retrievePreferences(new OnPreferencesReturned(getActivity()));
        new SimpleGameAchievement(getString(R.string.achievement_secretive_bee)).unlock(this);

        return root;
    }

    @Override
    public void onStop() {
        super.onStop();
        preferences.privacyPreferences.disabledSensors = sensorsAdapter.getDisabledSensors();
        if (apisenseSdk.getSessionManager().isConnected()) {
            // Avoid saving preferences if user used the logout button.
            apisenseSdk.getPreferencesManager().savePreferences(preferences, new OnPreferencesSaved(getActivity()));
        }
    }

    private class OnPreferencesReturned extends BeeAPSCallback<Preferences> {
        public OnPreferencesReturned(Activity activity) {
            super(activity);
        }

        @Override
        public void onDone(Preferences prefs) {
            preferences = prefs;
            List<String> disabledSting = preferences.privacyPreferences.disabledSensors;
            Log.i(TAG, "Got disabledSting sensors: " + disabledSting);
            for (String stingName : disabledSting) {
                sensorsAdapter.setSensortActivation(stingName, false);
            }
            sensorsAdapter.notifyDataSetChanged();
        }
    }

    private class OnPreferencesSaved extends BeeAPSCallback<Void> {
        public OnPreferencesSaved(Activity activity) {
            super(activity);
        }

        @Override
        public void onDone(Void aVoid) {
            // Nothing to do here
        }
    }
}
