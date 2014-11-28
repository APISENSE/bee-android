package com.apisense.bee.ui.activity;

import android.app.*;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.*;
import android.widget.*;
import com.apisense.android.api.APSLocalCrop;
import com.apisense.api.Callback;
import com.apisense.api.Crop;
import com.apisense.bee.BeeApplication;
import com.apisense.bee.R;
import com.apisense.bee.backend.AsyncTasksCallbacks;
import com.apisense.bee.backend.experiment.RetrieveAvailableExperimentsTask;
import com.apisense.bee.backend.experiment.SubscribeUnsubscribeExperimentTask;
import com.apisense.bee.ui.adapter.AvailableExperimentsListAdapter;
import org.json.simple.parser.ParseException;

import java.util.ArrayList;
import java.util.List;

public class StoreActivity extends Activity implements SearchView.OnQueryTextListener {
    private final String TAG = getClass().getSimpleName();

    protected  ActionBar actionBar;

    // Search view
    private SearchView mSearchView;

    // Content Adapter
    protected AvailableExperimentsListAdapter experimentsAdapter;

    // Asynchronous Task
    private RetrieveAvailableExperimentsTask experimentsRetrieval;
    private SubscribeUnsubscribeExperimentTask experimentChangeSubscriptionStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store);

        actionBar = getActionBar();
        setUpAvailableCropsList();
    }

    private void setUpAvailableCropsList() {
        experimentsAdapter = new AvailableExperimentsListAdapter(getApplicationContext(),
                                                                 R.layout.fragment_experiment_store_element,
                                                                 new ArrayList<APSLocalCrop>());

        ListView subscribedExperiments = (ListView) findViewById(R.id.store_experiments_list);
        subscribedExperiments.setEmptyView(findViewById(R.id.store_empty_list));
        subscribedExperiments.setAdapter(experimentsAdapter);
        subscribedExperiments.setOnItemClickListener(new OpenExperimentDetailsListener());
        subscribedExperiments.setOnItemLongClickListener(new SubscriptionListener());

        getExperiments();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search_view, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        mSearchView = (SearchView) searchItem.getActionView();
        mSearchView.setQueryHint(getResources().getString(R.string.search_hint));
        setupSearchView(searchItem);

        return true;
    }

    private void setupSearchView(MenuItem searchItem) {
        if (isAlwaysExpanded()) {
            mSearchView.setIconifiedByDefault(false);
        } else {
            searchItem.setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_IF_ROOM
                    | MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);
        }

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        if (searchManager != null) {
            List<SearchableInfo> searchables = searchManager.getSearchablesInGlobalSearch();

            SearchableInfo info = searchManager.getSearchableInfo(getComponentName());
            for (SearchableInfo inf : searchables) {
                if (inf.getSuggestAuthority() != null
                        && inf.getSuggestAuthority().startsWith("applications")) {
                    info = inf;
                }
            }
            mSearchView.setSearchableInfo(info);
        }
        mSearchView.setOnQueryTextListener(this);
    }

    public boolean onQueryTextChange(String query) {
        experimentsAdapter.getFilter().filter(query);
        return true;
    }

    public boolean onQueryTextSubmit(String query) {
        experimentsAdapter.getFilter().filter(query);
        mSearchView.clearFocus();
        return false;
    }

    public boolean onClose() {
        return false;
    }

    protected boolean isAlwaysExpanded() {
        return false;
    }

    // - - - - - - - - - - - - -

    // Callbacks definitions

    private class OnExperimentsRetrieved implements Callback<List<Crop>> {

        @Override
        public void onCall(List<Crop> crops) throws Exception {
            experimentsRetrieval = null;

            final List<APSLocalCrop> apsCrops = cropToAPSLocalCrop(crops);
            for (APSLocalCrop apsCrop: apsCrops){ Log.v(TAG, "Got Crop:" + apsCrop); }

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    experimentsAdapter.setDataSet(apsCrops);
                    experimentsAdapter.notifyDataSetChanged();
                }
            });
            Log.i(TAG, "Number of Active Experiments: " + experimentsAdapter.getCount());
        }

        // TODO: Delete when Callback type is fixed to APSLocalCrop
        private List<APSLocalCrop> cropToAPSLocalCrop(List<Crop> crops) {
            List<APSLocalCrop> result = new ArrayList<APSLocalCrop>();
            for (Crop crop : crops){
                APSLocalCrop apsCrop = null;
                try {
                    apsCrop = new APSLocalCrop(crop.getByte());
                    result.add(apsCrop);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            Log.d(TAG, "Number of converted crops: " + result.size());
            return result;
        }

        @Override
        public void onError(Throwable throwable) {
            experimentsRetrieval = null;
            throwable.printStackTrace();
        }
    }

    public void getExperiments() {
        // Creating new request to retrieve Experiments
        if (experimentsRetrieval == null) {
            experimentsRetrieval = new RetrieveAvailableExperimentsTask(this, new OnExperimentsRetrieved());
            experimentsRetrieval.execute();
        }
    }


    private class OnSubscribed implements Callback<APSLocalCrop>{
        private final String experimentName;
        private final View statusView;

        public OnSubscribed(View experimentView){
            super();
            this.statusView = experimentView.findViewById(R.id.experiment_status);
            this.experimentName = ((TextView) experimentView.findViewById(R.id.experimentelement_sampletitle))
                    .getText().toString();
        }

        @Override
        public void onCall(APSLocalCrop apsLocalCrop) throws Exception {
            experimentChangeSubscriptionStatus = null;

            String toastMessage = String.format(getString(R.string.experiment_subscribed), experimentName);
            Toast.makeText(getBaseContext(), toastMessage, Toast.LENGTH_SHORT).show();
            experimentsAdapter.showAsSubscribed(statusView);
        }

        @Override
        public void onError(Throwable throwable) {
            throwable.printStackTrace();
            experimentChangeSubscriptionStatus = null;
            experimentsAdapter.showAsUnsubscribed(statusView);
        }
    }

    private class OnUnSubscribed implements Callback<Void> {
        private final String experimentName;
        private final View statusView;

        public OnUnSubscribed(View experimentView){
            super();
            this.experimentName =  ((TextView) experimentView.findViewById(R.id.experimentelement_sampletitle))
                                   .getText().toString();
            this.statusView = experimentView.findViewById(R.id.experiment_status);
        }

        @Override
        public void onCall(Void aVoid) throws Exception {
            experimentChangeSubscriptionStatus = null;

            String toastMessage = String.format(getString(R.string.experiment_unsubscribed), experimentName);
            Toast.makeText(getBaseContext(), toastMessage, Toast.LENGTH_SHORT).show();

            experimentsAdapter.showAsUnsubscribed(statusView);
        }

        @Override
        public void onError(Throwable throwable) {
            throwable.printStackTrace();
            experimentChangeSubscriptionStatus = null;
            experimentsAdapter.showAsSubscribed(statusView);
        }
    }

    // Listeners definitions

    private class OpenExperimentDetailsListener implements AdapterView.OnItemClickListener{
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Intent intent = new Intent(view.getContext(), StoreExperimentDetailsActivity.class);

            APSLocalCrop exp = (APSLocalCrop) parent.getAdapter().getItem(position);
            intent.putExtra("experiment", exp.getByte());

            startActivity(intent);
        }
    }

    private class SubscriptionListener implements AdapterView.OnItemLongClickListener {
        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
            Crop exp = (Crop) parent.getAdapter().getItem(position);
            if (experimentChangeSubscriptionStatus == null){
                experimentChangeSubscriptionStatus = new SubscribeUnsubscribeExperimentTask(getApplicationContext(),
                                                                                            new OnSubscribed(view), new OnUnSubscribed(view));
                experimentChangeSubscriptionStatus.execute(exp.getName());
            }
            return true;
        }
    }
}
