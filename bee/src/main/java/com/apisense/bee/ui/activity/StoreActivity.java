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
    private RetrieveExistingTagsTask tagsRetrieval;
    private RetrieveAvailableExperimentsTask experimentsRetrieval;
    private SubscribeExperimentTask experimentSubscription;
    private UnsubscribeExperimentTask experimentUnsubscription;
    private String currentTabTag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store);
        actionBar = getActionBar();

        // Setting up available experiments list behavior
        experimentsAdapter = new AvailableExperimentsListAdapter(getBaseContext(),
                                                                  R.layout.fragment_experimentelement,
                                                                  new ArrayList<Experiment>());
        ListView subscribedExperiments = (ListView) findViewById(R.id.store_experiment_lists);
        subscribedExperiments.setAdapter(experimentsAdapter);
        subscribedExperiments.setOnItemLongClickListener(new SubscriptionListener());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the ActionBar and the tabs.
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        populateTabs();
        return true;
    }

    /**
     * Retrieve distant Tags and set them as tabs
     */
    private void populateTabs() {
        if (tagsRetrieval == null){
            tagsRetrieval = new RetrieveExistingTagsTask(new OnExistingTagsRetrieved());
            tagsRetrieval.execute();
        }
    }

    /**
     * Specify if the given experiment is already subscribed to
     * (*subscribed* being currently equivalent to *installed*)
     *
     * @param exp The experiment to test
     * @return true if the user already subscribed to an experiment, false otherwise
     */
    // TODO: Improve this with a specific library method (at least Move function to a better place)
    public static boolean isSubscribedExperiment(Experiment exp) {
        Experiment currentExperiment;
        boolean result = false;
        try {
            currentExperiment = APISENSE.apisense().getBSenseMobileService().getExperiment(exp.name);
            result = (currentExperiment != null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * Change the adapter dataSet with a newly fetched List of Experiment
     *
     * @param experiments The new list of experiments to show
     */
    public void setExperiments(List<Experiment> experiments) {
        this.experimentsAdapter.setDataSet(experiments);
    }


    // Callbacks definitions

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
            if (result == BeeApplication.ASYNC_SUCCESS) {
                List<String> tags = (List<String>) response;
                Log.i(TAG, "Fetched tags: " + tags);
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
                // User feedback
                String experimentName = ((TextView) concernedView.findViewById(R.id.experimentelement_sampletitle)).getText().toString();
                Toast.makeText(getBaseContext(),
                               String.format(getString(R.string.store_app_subscribed), experimentName),
                               Toast.LENGTH_SHORT).show();
                experimentsAdapter.showAsSubscribed(concernedView);
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
                // User feedback
                String experimentName = ((TextView) concernedView.findViewById(R.id.experimentelement_sampletitle)).getText().toString();
                Toast.makeText(getBaseContext(),
                        String.format(getString(R.string.store_app_unsubscribed), experimentName),
                        Toast.LENGTH_SHORT).show();
                experimentsAdapter.showAsUnsubscribed(concernedView);
            }
        }

        @Override
        public void onTaskCanceled() {
            experimentUnsubscription = null;
        }
    }


    // Listeners definitions

    private class TagTabListener implements ActionBar.TabListener {
       public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
           String newTag = tab.getText().toString();

           // Changing anything only if the tab changed
           if (!newTag.equals(currentTabTag)) {
               Log.i(TAG, "New Tab selected: " + newTag);
               // Canceling last task if request is still active
               if (experimentsRetrieval != null) {
                   experimentsRetrieval.cancel(true);
               }
               // Creating new request to retrieve Experiments
               if (experimentsRetrieval == null) {
                   experimentsRetrieval = new RetrieveAvailableExperimentsTask(new OnExperimentsRetrieved(), currentTabTag);
                   experimentsRetrieval.execute();
                   currentTabTag = newTag;
               }
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
            if (isSubscribedExperiment(exp)) {
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
