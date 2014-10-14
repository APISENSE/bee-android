package com.apisense.bee.ui.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import com.apisense.bee.BeeApplication;
import com.apisense.bee.R;
import com.apisense.bee.backend.AsyncTasksCallbacks;
import com.apisense.bee.backend.experiment.RetrieveAvailableExperimentsTask;
import com.apisense.bee.backend.store.RetrieveExistingTagsTask;
import com.apisense.bee.ui.adapter.SubscribedExperimentsListAdapter;
import fr.inria.bsense.appmodel.Experiment;

import java.util.ArrayList;
import java.util.List;
// TODO: Think about index usage (when to load next 'page' of experiment)
// TODO: Think about Tags usage (Server filter is better)

public class StoreActivity extends Activity {
    private final String TAG = getClass().getSimpleName();

    protected  ActionBar actionBar;

    // Content Adapter
    // TODO: Make a specific adapter when presentation will diverge
    protected SubscribedExperimentsListAdapter experimentsAdapter;

    // Asynchronous Task
    private RetrieveAvailableExperimentsTask experimentsRetrieval;
    private RetrieveExistingTagsTask tagsRetrieval;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store);
        actionBar = getActionBar();
        experimentsAdapter = new SubscribedExperimentsListAdapter(getBaseContext(),
                                                                  R.layout.fragment_experimentelement,
                                                                  new ArrayList<Experiment>());
        ListView subscribedExperiments = (ListView) findViewById(R.id.store_experiment_lists);
        subscribedExperiments.setAdapter(experimentsAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the ActionBar and the tabs.
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        populateTabs();
        return true;
    }

    private void populateTabs() {
        if (tagsRetrieval == null){
            tagsRetrieval = new RetrieveExistingTagsTask(new onExistingTagsRetrieved());
            tagsRetrieval.execute();
        }
    }

    public void setExperiments(List<Experiment> experiments) {
        this.experimentsAdapter.setDataSet(experiments);
    }

    private class onExperimentsRetrieved implements AsyncTasksCallbacks {
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

    private class onExistingTagsRetrieved implements AsyncTasksCallbacks {
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

   private class TagTabListener implements ActionBar.TabListener {
       public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
           // TODO: Filter retrieved experiments by Tag
           if (experimentsRetrieval != null) {
               experimentsRetrieval.cancel(true);
           } else {
               experimentsRetrieval = new RetrieveAvailableExperimentsTask(new onExperimentsRetrieved());
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
}
