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
import com.apisense.bee.backend.store.RetrieveExistingTagsTask;
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
    private RetrieveExistingTagsTask tagsRetrieval;
    private RetrieveAvailableExperimentsTask experimentsRetrieval;
    private SubscribeUnsubscribeExperimentTask experimentChangeSubscriptionStatus;

    private String currentTabTag;

    // sliding menu
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;

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
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the ActionBar and the tabs.
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        populateTabs();

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

    /**
     * Retrieve distant Tags and set them as tabs
     */
    private void populateTabs() {
        if (tagsRetrieval == null){
            tagsRetrieval = new RetrieveExistingTagsTask(new OnExistingTagsRetrieved());
            tagsRetrieval.execute();
        }
    }


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

                // Push it back
                mDrawerLayout.setOnGenericMotionListener(new View.OnGenericMotionListener() {
                    @Override
                    public boolean onGenericMotion(View v, MotionEvent event) {
                        mDrawerLayout.closeDrawers();
                        return true;
                    }
                });

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

    private class TagTabListener implements ActionBar.TabListener {
       public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
           String newTag = tab.getText().toString();

           // Changing anything only if the tab changed
           if (!newTag.equals(currentTabTag)) {
               Log.i(TAG, "New Tab selected: " + newTag);
               if (experimentsRetrieval == null) {
                   experimentsRetrieval = new RetrieveAvailableExperimentsTask(StoreActivity.this, new OnExperimentsRetrieved());
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
