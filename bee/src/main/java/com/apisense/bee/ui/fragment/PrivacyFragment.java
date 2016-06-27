package com.apisense.bee.ui.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.apisense.bee.BeeApplication;
import com.apisense.bee.R;
import com.apisense.bee.callbacks.BeeAPSCallback;
import com.apisense.bee.games.SimpleGameAchievement;
import com.apisense.bee.ui.activity.HomeActivity;
import com.apisense.bee.ui.adapter.DividerItemDecoration;
import com.apisense.bee.ui.adapter.SensorRecyclerAdapter;
import com.apisense.sdk.APISENSE;
import com.apisense.sdk.core.preferences.Preferences;
import com.apisense.sdk.core.preferences.Sensor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class PrivacyFragment extends BaseFragment {

    private static final String TAG = "PrivacyFragment";
    private APISENSE.Sdk apisenseSdk;

    @BindView(R.id.sensors_list) RecyclerView mRecyclerView;

    private SensorRecyclerAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private Unbinder unbinder;
    private Preferences preferences = new Preferences();

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View root = inflater.inflate(R.layout.fragment_privacy, container, false);
        unbinder = ButterKnife.bind(this, root);

        apisenseSdk = ((BeeApplication) getActivity().getApplication()).getSdk();

        homeActivity.getSupportActionBar().setTitle(R.string.title_activity_settings);
        homeActivity.selectDrawerItem(HomeActivity.DRAWER_PRIVACY_IDENTIFIER);

        List<Sensor> sensorList = new ArrayList<>(apisenseSdk.getPreferencesManager().retrieveAvailableSensors());
        Collections.sort(sensorList);
        Log.i(TAG, "Got sensors: " + sensorList);

        mRecyclerView.setHasFixedSize(true); // Performances
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity()));
        mAdapter = new SensorRecyclerAdapter(sensorList);

        apisenseSdk.getPreferencesManager().retrievePreferences(new OnPreferencesReturned(getActivity()));
        new SimpleGameAchievement(getString(R.string.achievement_secretive_bee)).unlock(this);

        return root;
    }

    @Override
    public void onStop() {
        super.onStop();
        preferences.privacyPreferences.disabledSensors = mAdapter.getDisabledSensors();
        if (apisenseSdk.getSessionManager().isConnected()) {
            // Avoid saving preferences if user used the logout button.
            apisenseSdk.getPreferencesManager().savePreferences(preferences, new OnPreferencesSaved(getActivity()));
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
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
                mAdapter.setSensortActivation(stingName, false);
            }

            mRecyclerView.setAdapter(mAdapter);
            mAdapter.notifyDataSetChanged();
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
