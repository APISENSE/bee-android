package com.apisense.bee.ui.fragment;

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
import com.apisense.bee.ui.adapter.SensorListAdapter;
import com.apisense.sdk.APISENSE;
import com.apisense.sdk.core.APSCallback;
import com.apisense.sdk.core.preferences.Preferences;
import com.apisense.sdk.core.preferences.Sensor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PrivacySettingsFragment extends Fragment {
    private static final String TAG = "PrivacySettingsFragment";

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_privacy_settings, container, false);
        APISENSE.Sdk apisenseSdk = ((BeeApplication) getActivity().getApplication()).getSdk();

        List<Sensor> sensorList = new ArrayList<>(apisenseSdk.getPreferencesManager().retrieveAvailableSensors());
        Collections.sort(sensorList);
        Log.i(TAG, "Got sensors: " + sensorList);

        SensorListAdapter sensorsAdapter = new SensorListAdapter(getActivity(), R.layout.list_item_sensor);

        ListView sensorsListView = (ListView) root.findViewById(R.id.sensors_list);
        sensorsListView.setAdapter(sensorsAdapter);
        sensorsListView.setEmptyView(root.findViewById(R.id.store_empty_list));

        sensorsAdapter.setDataSet(sensorList);
        sensorsAdapter.notifyDataSetChanged();
//        apisenseSdk.getPreferencesManager().retrievePreferences(new OnPreferencesReturned());

        return root;
    }


    private class OnPreferencesReturned implements APSCallback<Preferences> {
        @Override
        public void onDone(Preferences preferences) {
            List<String> disabled = preferences.privacyPreferences.disabledSensors;
            Log.i(TAG, "Got disabled sensors: " + disabled);
            // Set switch values.
        }

        @Override
        public void onError(Exception e) {

        }
    }
}
