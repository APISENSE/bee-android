package com.apisense.bee.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.apisense.bee.BeeApplication;
import com.apisense.bee.Callbacks.OnCropStarted;
import com.apisense.bee.Callbacks.OnCropSubscribed;
import com.apisense.bee.Callbacks.OnCropUnsubscribed;
import com.apisense.bee.R;
import com.apisense.bee.games.IncrementalGameAchievement;
import com.apisense.bee.ui.activity.StoreExperimentDetailsActivity;
import com.apisense.bee.ui.adapter.AvailableExperimentsListAdapter;
import com.apisense.bee.utils.CropPermissionHandler;
import com.apisense.sdk.APISENSE;
import com.apisense.sdk.adapter.SimpleAPSCallback;
import com.apisense.sdk.core.APSCallback;
import com.apisense.sdk.core.store.Crop;

import java.util.ArrayList;
import java.util.List;


public class HomeStoreFragment extends Fragment {
    private final String TAG = getClass().getSimpleName();

    // Content Adapter
    protected AvailableExperimentsListAdapter experimentsAdapter;

    private APISENSE.Sdk apisenseSdk;
    private CropPermissionHandler lastCropPermissionHandler;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_store_home, container, false);
        apisenseSdk = ((BeeApplication) getActivity().getApplication()).getSdk();

        // Setting up available experiments list behavior
        experimentsAdapter = new AvailableExperimentsListAdapter(getActivity().getBaseContext(),
                R.layout.list_item_store_experiment,
                new ArrayList<Crop>());
        ListView subscribedExperiments = (ListView) root.findViewById(R.id.store_experiment_lists);
        subscribedExperiments.setEmptyView(root.findViewById(R.id.store_empty_list));
        subscribedExperiments.setAdapter(experimentsAdapter);
        subscribedExperiments.setOnItemClickListener(new OpenExperimentDetailsListener());
        subscribedExperiments.setOnItemLongClickListener(new SubscriptionListener());

        getExperiments();

        return root;
    }


    /**
     * Change the adapter dataSet with a newly fetched List of Experiment
     *
     * @param experiments The new list of experiments to show
     */
    public void setExperiments(List<Crop> experiments) {
        this.experimentsAdapter.setDataSet(experiments);
    }


    public void getExperiments() {
        apisenseSdk.getStoreManager().findAllCrops(new OnExperimentsRetrieved());
    }

    // Listeners definitions

    private class OpenExperimentDetailsListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Intent intent = new Intent(view.getContext(), StoreExperimentDetailsActivity.class);
            Crop crop = (Crop) parent.getAdapter().getItem(position);

            Bundle bundle = new Bundle();
            bundle.putParcelable("crop", crop);
            intent.putExtras(bundle);
            startActivity(intent);
        }
    }

    private class SubscriptionListener implements AdapterView.OnItemLongClickListener {
        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
            Crop crop = (Crop) parent.getAdapter().getItem(position);
            if (apisenseSdk.getCropManager().isInstalled(crop)) {
                apisenseSdk.getCropManager().unsubscribe(crop, new StoreCropUnsubscribed(crop));
            } else {
                lastCropPermissionHandler = new CropPermissionHandler(getActivity(), crop,
                        new OnCropStarted(getContext()) {
                            @Override
                            public void onDone(Crop crop) {
                                super.onDone(crop);
                                experimentsAdapter.notifyDataSetChanged();
                            }
                        });
                apisenseSdk.getCropManager().subscribe(crop, new StoreCropSubscribed(crop));
            }
            return true;
        }
    }

    // Callbacks definitions

    private class OnExperimentsRetrieved extends SimpleAPSCallback<List<Crop>> {
        @Override
        public void onDone(List<Crop> crops) {
            Log.i(TAG, "Number of Active Experiments: " + crops.size());

            // Updating listview
            setExperiments(crops);
            experimentsAdapter.notifyDataSetChanged();
        }
    }

    private class StoreCropUnsubscribed extends OnCropUnsubscribed {
        public StoreCropUnsubscribed(Crop crop) {
            super(getActivity(), crop.getName());
        }

        @Override
        public void onDone(Crop crop) {
            super.onDone(crop);
            // Increment every subscription related achievements
            new IncrementalGameAchievement(getString(R.string.achievement_bronze_wings)).increment(HomeStoreFragment.this);
            new IncrementalGameAchievement(getString(R.string.achievement_silver_wings)).increment(HomeStoreFragment.this);
            new IncrementalGameAchievement(getString(R.string.achievement_gold_wings)).increment(HomeStoreFragment.this);
            new IncrementalGameAchievement(getString(R.string.achievement_crystal_wings)).increment(HomeStoreFragment.this);
        }
    }

    private class StoreCropSubscribed extends OnCropSubscribed {
        public StoreCropSubscribed(Crop crop) {
            super(getActivity(), crop, lastCropPermissionHandler);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (lastCropPermissionHandler != null) {
            lastCropPermissionHandler.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
}
