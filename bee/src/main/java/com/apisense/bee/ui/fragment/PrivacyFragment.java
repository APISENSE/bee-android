package com.apisense.bee.ui.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.apisense.bee.BeeApplication;
import com.apisense.bee.R;
import com.apisense.bee.callbacks.BeeAPSCallback;
import com.apisense.bee.games.SimpleGameAchievement;
import com.apisense.bee.ui.activity.HomeActivity;
import com.apisense.bee.ui.adapter.DividerItemDecoration;
import com.apisense.bee.ui.adapter.SensorRecyclerAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.apisense.sdk.APISENSE;
import io.apisense.sdk.core.preferences.Preferences;
import io.apisense.sting.lib.Sensor;

public class PrivacyFragment extends BaseFragment {

    private static final String TAG = "PrivacyFragment";
    private APISENSE.Sdk apisenseSdk;

    @BindView(R.id.sensors_list)
    RecyclerView mRecyclerView;
    @BindView(R.id.sensors_list_empty)
    TextView emptyListView;
    @BindView(R.id.sensors_list_save)
    TextView applyButton;

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
        homeActivity.selectDrawerItem(HomeActivity.DRAWER_SETTINGS_IDENTIFIER);

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
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    @OnClick(R.id.sensors_list_save)
    public void savePreferences(View button) {
        preferences.privacyPreferences.disabledSensors = mAdapter.getDisabledSensors();
        if (apisenseSdk.getSessionManager().isConnected()) {
            // Avoid saving preferences if user used the logout button.
            apisenseSdk.getPreferencesManager().savePreferences(preferences, new OnPreferencesSaved(getActivity()));
        }
    }

    private class OnPreferencesReturned extends BeeAPSCallback<Preferences> {
        OnPreferencesReturned(Activity activity) {
            super(activity);
        }

        @Override
        public void onDone(Preferences prefs) {
            preferences = prefs;
            List<String> disabledSting = preferences.privacyPreferences.disabledSensors;
            Log.i(TAG, "Got disabledSting sensors: " + disabledSting);
            for (String stingName : disabledSting) {
                mAdapter.setSensorActivation(stingName, false);
            }

            mRecyclerView.setAdapter(mAdapter);
            mAdapter.notifyDataSetChanged();
        }

        @Override
        public void onError(Exception e) {
            super.onError(e);
            mRecyclerView.setVisibility(View.GONE);
            applyButton.setVisibility(View.GONE);
            emptyListView.setVisibility(View.VISIBLE);
        }
    }

    private class OnPreferencesSaved extends BeeAPSCallback<Void> {
        OnPreferencesSaved(Activity activity) {
            super(activity);
        }

        @Override
        public void onDone(Void aVoid) {
            Snackbar.make(getView(), "Preferences saved, restarting crops", Snackbar.LENGTH_LONG)
                    .show();
        }
    }
}
