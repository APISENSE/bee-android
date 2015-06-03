package com.apisense.bee.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.apisense.bee.BeeApplication;
import com.apisense.bee.R;
import com.apisense.bee.ui.activity.StoreExperimentDetailsActivity;
import com.apisense.bee.ui.adapter.AvailableExperimentsListAdapter;
import com.apisense.sdk.APISENSE;
import com.apisense.sdk.core.APSCallback;
import com.apisense.sdk.core.store.Crop;

import java.util.ArrayList;
import java.util.List;


public class HomeStoreFragment extends Fragment {
    private final String TAG = getClass().getSimpleName();

    // Content Adapter
    protected AvailableExperimentsListAdapter experimentsAdapter;

    private APISENSE.Sdk apisenseSdk;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_store_home, container, false);
        apisenseSdk = ((BeeApplication) getActivity().getApplication()).getSdk();

        // Setting up available experiments list behavior
        experimentsAdapter = new AvailableExperimentsListAdapter(getActivity().getBaseContext(),
                R.layout.fragment_experiment_store_element,
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
            if (apisenseSdk.getCropManager().isSubscribed(crop)) {
                apisenseSdk.getCropManager().unsubscribe(crop, new OnCropUnsubscribed(view));
            } else {
                apisenseSdk.getCropManager().subscribe(crop, new OnCropSubscribed(view));
            }
            return true;
        }
    }

    // Callbacks definitions

    private class OnExperimentsRetrieved implements APSCallback<List<Crop>> {
        @Override
        public void onDone(List<Crop> crops) {
            Log.i(TAG, "Number of Active Experiments: " + crops.size());

            // Updating listview
            setExperiments(crops);
            experimentsAdapter.notifyDataSetChanged();
        }

        @Override
        public void onError(Exception e) {
        }
    }

    private class OnCropUnsubscribed implements APSCallback<Void> {
        protected String experimentName;

        public OnCropUnsubscribed(View v) {
            experimentName = ((TextView) v.findViewById(R.id.experimentelement_sampletitle)).getText().toString();
        }

        @Override
        public void onDone(Void response) {
            String toastMessage = String.format(getString(R.string.experiment_unsubscribed), experimentName);
            Toast.makeText(getActivity().getBaseContext(), toastMessage, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onError(Exception e) {
            String toastMessage = String.format("Error while unsubscribing from %s", experimentName);
            Toast.makeText(getActivity().getBaseContext(), toastMessage, Toast.LENGTH_SHORT).show();
        }
    }

        private class OnCropSubscribed implements APSCallback<Void> {
            protected String experimentName;

            public OnCropSubscribed(View v) {
                experimentName = ((TextView) v.findViewById(R.id.experimentelement_sampletitle)).getText().toString();
            }

            @Override
        public void onDone(Void response) {
            String toastMessage = String.format(getString(R.string.experiment_subscribed), experimentName);
            Toast.makeText(getActivity().getBaseContext(), toastMessage, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onError(Exception e) {
            String toastMessage = String.format("Error while subscribing to %s", experimentName);
            Toast.makeText(getActivity().getBaseContext(), toastMessage, Toast.LENGTH_SHORT).show();
        }
    }
}
