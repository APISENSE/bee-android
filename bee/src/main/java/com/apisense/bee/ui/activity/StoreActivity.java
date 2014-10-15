package com.apisense.bee.ui.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.apisense.bee.BeeApplication;
import com.apisense.bee.R;
import com.apisense.bee.backend.AsyncTasksCallbacks;
import com.apisense.bee.backend.experiment.RetrieveAvailableExperimentsTask;
import com.apisense.bee.backend.experiment.SubscribeExperimentTask;
import com.apisense.bee.backend.experiment.UnsubscribeExperimentTask;
import com.apisense.bee.backend.store.RetrieveExistingTagsTask;
import com.apisense.bee.ui.adapter.AvailableExperimentsListAdapter;
import fr.inria.bsense.APISENSE;
import fr.inria.bsense.appmodel.Experiment;

import java.util.ArrayList;
import java.util.List;
// TODO: Think about index usage (when to load next 'page' of experiment)
// TODO: Think about Tags usage (Server filter is better)

public class StoreActivity extends Activity {
    private final String TAG = getClass().getSimpleName();

    protected  ActionBar actionBar;

    // Content Adapter
    protected AvailableExperimentsListAdapter experimentsAdapter;

    // Asynchronous Task
    private RetrieveAvailableExperimentsTask experimentsRetrieval;
    private RetrieveExistingTagsTask tagsRetrieval;
    private SubscribeExperimentTask experimentSubscription;
    private UnsubscribeExperimentTask experimentUnsubscription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store);
        actionBar = getActionBar();
        experimentsAdapter = new AvailableExperimentsListAdapter(getBaseContext(),
                                                                  R.layout.fragment_experimentelement,
                                                                  new ArrayList<Experiment>());
        ListView subscribedExperiments = (ListView) findViewById(R.id.store_experiment_lists);
        subscribedExperiments.setOnItemLongClickListener(new SubscriptionListener());
        subscribedExperiments.setAdapter(experimentsAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the ActionBar and the tabs.
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        populateTabs();
        return true;
    }

    private boolean isSubscribed(Experiment exp) {
        // TODO: Improve this (At least with an asynchTask)
        // At the moment => if the experiment is installed, then the user subscribed to it
        Experiment currentExperiment;
        boolean result = false;
        try {
            currentExperiment = APISENSE.apisense().getBSenseMobileService().getExperiment(exp.name);
            if (currentExperiment != null) {
                result = true;
            }
        } catch (Exception e) {
            Log.e(TAG, "An error occured while looking for experiment (" + exp.name + "): " + e.getMessage());
        }
        return result;
    }

    private void populateTabs() {
        if (tagsRetrieval == null){
            tagsRetrieval = new RetrieveExistingTagsTask(new OnExistingTagsRetrieved());
            tagsRetrieval.execute();
        }
    }

    public void setExperiments(List<Experiment> experiments) {
        this.experimentsAdapter.setDataSet(experiments);
    }

    private class OnExperimentsRetrieved implements AsyncTasksCallbacks {
        @Override
        public void onTaskCompleted(int result, Object response) {
            experimentsRetrieval = null;
            List<Experiment> exp = (List<Experiment>) response;
            Log.i(TAG, "number of Active Experiments: " + exp.size());

            // Updating listview
            if (exp.size() != 0) {
                setExperiments(exp);
                experimentsAdapter.notifyDataSetChanged();
            }
        }

        @Override
        public void onTaskCanceled() {
            experimentsRetrieval = null;
        }
    }

    private class OnExistingTagsRetrieved implements AsyncTasksCallbacks {
        @Override
        public void onTaskCompleted(int result, Object response) {
            tagsRetrieval = null;
            if ((Integer) result == BeeApplication.ASYNC_SUCCESS) {
                List<String> tags = (List<String>) response;
                Log.d(TAG, "Fetched tags: " + tags);
                for(String tag : tags){
                    Log.v(TAG, "Adding tab for tag: " + tag);
                    actionBar.addTab(actionBar.newTab()
                                     .setText(tag)
                                     .setTabListener(new TagTabListener()));
                }
            }
        }

        @Override
        public void onTaskCanceled() {
            tagsRetrieval = null;
        }
    }

    private class OnExperimentSubscribed implements AsyncTasksCallbacks {
        private View concernedView;

        public OnExperimentSubscribed(View v){
            super();
            this.concernedView = v;
        }

        @Override
        public void onTaskCompleted(int result, Object response) {
            experimentSubscription = null;
            if (result == BeeApplication.ASYNC_SUCCESS) {
                String experimentName = ((TextView) concernedView.findViewById(R.id.experimentelement_sampletitle)).getText().toString();
                Toast.makeText(getBaseContext(),
                               String.format(getString(R.string.store_app_subscribed), experimentName),
                               Toast.LENGTH_SHORT).show();
                // Setting Background as subscribed Experiment
                concernedView.setBackgroundColor(getResources().getColor(R.color.orange_light));
            }
        }

        @Override
        public void onTaskCanceled() {
            experimentSubscription = null;
        }
    }

    private class OnExperimentUnsubscribed implements AsyncTasksCallbacks {
        private View concernedView;

        public OnExperimentUnsubscribed(View v){
            super();
            this.concernedView = v;
        }

        @Override
        public void onTaskCompleted(int result, Object response) {
            experimentUnsubscription = null;
            if (result == BeeApplication.ASYNC_SUCCESS) {
                String experimentName = ((TextView) concernedView.findViewById(R.id.experimentelement_sampletitle)).getText().toString();
                Toast.makeText(getBaseContext(),
                        String.format(getString(R.string.store_app_unsubscribed), experimentName),
                        Toast.LENGTH_SHORT).show();
                // Setting Background as unsubscribed Experiment
                concernedView.setBackgroundColor(getResources().getColor(R.color.white));
            }
        }

        @Override
        public void onTaskCanceled() {
            experimentUnsubscription = null;
        }
    }

    private class TagTabListener implements ActionBar.TabListener {
       public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
           // TODO: Filter retrieved experiments by Tag
           if (experimentsRetrieval != null) {
               experimentsRetrieval.cancel(true);
           } else {
               experimentsRetrieval = new RetrieveAvailableExperimentsTask(new OnExperimentsRetrieved());
               experimentsRetrieval.execute();
           }
       }

       public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {
           // hide the given tab
       }

       public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {
           // probably ignore this event
       }
   }


    private class SubscriptionListener implements AdapterView.OnItemLongClickListener {
        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
            Experiment exp = (Experiment) parent.getAdapter().getItem(position);
            if (isSubscribed(exp)) {
                if (experimentUnsubscription == null) {
                    Log.i(TAG, "Asking un-subscription to experiment: " + exp);
                    experimentUnsubscription = new UnsubscribeExperimentTask(new OnExperimentUnsubscribed(view));
                    experimentUnsubscription.execute(exp);
                }
            } else {
                if (experimentSubscription == null) {
                    Log.i(TAG, "Asking subscription to experiment: " + exp);
                    experimentSubscription = new SubscribeExperimentTask(new OnExperimentSubscribed(view));
                    experimentSubscription.execute(exp);
                }
            }
            return true;
        }
    }
}
