package com.apisense.bee.ui.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.*;
import com.apisense.bee.BeeApplication;
import com.apisense.bee.R;
import com.apisense.bee.backend.AsyncTasksCallbacks;
import com.apisense.bee.backend.experiment.RetrieveAvailableExperimentsTask;
import com.apisense.bee.backend.experiment.SubscribeUnsubscribeExperiment;
import com.apisense.bee.backend.store.RetrieveExistingTagsTask;
import com.apisense.bee.ui.adapter.AvailableExperimentsListAdapter;
import com.apisense.bee.ui.entity.ExperimentSerializable;
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
    private SubscribeUnsubscribeExperiment experimentChangeSubscriptionStatus;
    private String currentTabTag;

    // sliding menu
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store);
        actionBar = getActionBar();

        // Setting up available experiments list behavior
        experimentsAdapter = new AvailableExperimentsListAdapter(getBaseContext(),
                                                                  R.layout.fragment_experiment_store_element,
                                                                  new ArrayList<Experiment>());
        ListView subscribedExperiments = (ListView) findViewById(R.id.store_experiment_lists);
        subscribedExperiments.setEmptyView(findViewById(R.id.store_empty_list));
        subscribedExperiments.setAdapter(experimentsAdapter);
        subscribedExperiments.setOnItemClickListener(new OpenExperimentDetailsListener());
        subscribedExperiments.setOnItemLongClickListener(new SubscriptionListener());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the ActionBar and the tabs.
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        // Color
        ColorDrawable colorDrawable = new ColorDrawable();
        colorDrawable.setColor(getResources().getColor(R.color.orange_light));
        actionBar.setBackgroundDrawable(colorDrawable);
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
            Log.i(TAG, "Number of Active Experiments: " + exp.size());

            // Updating listview
            setExperiments(exp);
            experimentsAdapter.notifyDataSetChanged();
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

                // Add tag inside drawer layout too
                String[] arrayTags = tags.toArray(new String[tags.size()]);
                mDrawerLayout = (DrawerLayout) findViewById(R.id.sliding_menu);
                mDrawerList = (ListView) findViewById(R.id.left_drawer);
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(),R.layout.sliding_store_menu_element,arrayTags);
                mDrawerList.setAdapter(adapter);
                mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> arg0, View v, int position, long id) {
                        Log.i(TAG, ((TextView) v).getText().toString() + " clicked !");
                        mDrawerLayout.closeDrawers();
                        Toast.makeText(getApplicationContext(), ((TextView) v).getText().toString() + " clicked !", Toast.LENGTH_SHORT).show();
                        // TODO : Interact (doc : http://developer.android.com/training/implementing-navigation/nav-drawer.html)
                    }
                });
            }
        }

        @Override
        public void onTaskCanceled() {
            tagsRetrieval = null;
        }
    }

    // TODO: Export (Un)Subscription indocator management to Adapter (with a boolean field 'subscribed' in the Experiment)
    private class onExperimentSubscriptionChanged implements AsyncTasksCallbacks {
        private View statusView;
        private View concernedView;

        public onExperimentSubscriptionChanged(View v){
            super();
            this.concernedView = v;
            this.statusView = concernedView.findViewById(R.id.experiment_status);

        }

        @Override
        public void onTaskCompleted(int result, Object response) {
            experimentChangeSubscriptionStatus = null;
            String experimentName = ((TextView) concernedView.findViewById(R.id.experimentelement_sampletitle))
                                                              .getText().toString();
            String toastMessage = "";
            if (result == BeeApplication.ASYNC_SUCCESS) {
                switch ((Integer) response){
                    case SubscribeUnsubscribeExperiment.EXPERIMENT_SUBSCRIBED:
                        toastMessage = String.format(getString(R.string.experiment_subscribed), experimentName);
                        experimentsAdapter.showAsSubscribed(statusView);
                        break;
                    case SubscribeUnsubscribeExperiment.EXPERIMENT_UNSUBSCRIBED:
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

    private class OpenExperimentDetailsListener implements AdapterView.OnItemClickListener{
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Intent intent = new Intent(view.getContext(), StoreExperimentDetailsActivity.class);
            Experiment exp = (Experiment) parent.getAdapter().getItem(position);

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
            Experiment exp = (Experiment) parent.getAdapter().getItem(position);
            if (experimentChangeSubscriptionStatus == null){
                experimentChangeSubscriptionStatus = new SubscribeUnsubscribeExperiment(new onExperimentSubscriptionChanged(view));
                experimentChangeSubscriptionStatus.changeStatus(exp);
            }
            return true;
        }
    }
}
