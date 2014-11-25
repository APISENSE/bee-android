package com.apisense.bee.ui.activity;

import android.app.*;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.*;
import android.widget.*;
import com.apisense.api.Callback;
import com.apisense.api.Crop;
import com.apisense.bee.BeeApplication;
import com.apisense.bee.R;
import com.apisense.bee.backend.AsyncTasksCallbacks;
import com.apisense.bee.backend.experiment.RetrieveAvailableExperimentsTask;
import com.apisense.bee.backend.experiment.SubscribeUnsubscribeExperimentTask;
import com.apisense.bee.ui.adapter.AvailableExperimentsListAdapter;
import com.apisense.bee.ui.entity.ExperimentSerializable;

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
        experimentsAdapter = new AvailableExperimentsListAdapter(this,
                                                                 R.layout.fragment_experiment_store_element,
                                                                 new ArrayList<Crop>());

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
            Log.i(TAG, "Number of Active Experiments: " + crops.size());

            // Updating listview
            experimentsAdapter.setDataSet(crops);
            experimentsAdapter.notifyDataSetChanged();
        }

        @Override
        public void onError(Throwable throwable) {
            experimentsRetrieval = null;
        }
    }

    public void getExperiments() {
        // Creating new request to retrieve Experiments
        if (experimentsRetrieval == null) {
            experimentsRetrieval = new RetrieveAvailableExperimentsTask(this, new OnExperimentsRetrieved());
            experimentsRetrieval.execute();
        }
    }

    // TODO: Export (Un)Subscription indocator management to Adapter (with a boolean field 'subscribed' in the Experiment)
    private class onExperimentSubscriptionChanged implements AsyncTasksCallbacks {
        private View statusView;
        private View concernedView;

        public onExperimentSubscriptionChanged(View v){
            super();
            this.concernedView = v;
            this.statusView = concernedView.findViewById(R.id.item);

        }

        @Override
        public void onTaskCompleted(int result, Object response) {
            experimentChangeSubscriptionStatus = null;
            String experimentName = ((TextView) concernedView.findViewById(R.id.experimentelement_sampletitle))
                                                              .getText().toString();
            String toastMessage = "";
            if (result == BeeApplication.ASYNC_SUCCESS) {
                switch ((Integer) response){
                    case SubscribeUnsubscribeExperimentTask.EXPERIMENT_SUBSCRIBED:
                        toastMessage = String.format(getString(R.string.experiment_subscribed), experimentName);
                        experimentsAdapter.showAsSubscribed(statusView);
                        break;
                    case SubscribeUnsubscribeExperimentTask.EXPERIMENT_UNSUBSCRIBED:
                        toastMessage = String.format(getString(R.string.experiment_unsubscribed), experimentName);
                        experimentsAdapter.showAsUnsubscribed(statusView);
                        break;
                }
                // User feedback
                Toast.makeText(getBaseContext(), toastMessage, Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onTaskCanceled() {
            experimentChangeSubscriptionStatus = null;
        }
    }

    // Listeners definitions

    private class OpenExperimentDetailsListener implements AdapterView.OnItemClickListener{
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Intent intent = new Intent(view.getContext(), StoreExperimentDetailsActivity.class);
            Crop exp = (Crop) parent.getAdapter().getItem(position);

            Bundle bundle = new Bundle();
            // TODO : Prefer parcelable in the future. Problem : CREATOR method doesn't exist (to check)
            // bundle.putParcelable("experiment", getItem(position));
            // TODO : Maybe something extending Experiment and using JSONObject to init but it seems to be empty
            bundle.putSerializable("experiment", new ExperimentSerializable(exp));
            intent.putExtras(bundle); //Put your id to your next Intent
            startActivity(intent);
        }
    }

    private class SubscriptionListener implements AdapterView.OnItemLongClickListener {
        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
            Crop exp = (Crop) parent.getAdapter().getItem(position);
            if (experimentChangeSubscriptionStatus == null){
//                experimentChangeSubscriptionStatus = new SubscribeUnsubscribeExperimentTask(APISENSE.apisense(), new onExperimentSubscriptionChanged(view));
//                experimentChangeSubscriptionStatus.execute(exp);
            }
            return true;
        }
    }
}
