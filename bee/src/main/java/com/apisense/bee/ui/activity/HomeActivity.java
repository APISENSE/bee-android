package com.apisense.bee.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import com.apisense.bee.BeeApplication;
import com.apisense.bee.R;
import com.apisense.bee.backend.AsyncTasksCallbacks;
import com.apisense.bee.backend.experiment.*;
import com.apisense.bee.backend.user.SignOutTask;
import com.apisense.bee.ui.adapter.SubscribedExperimentsListAdapter;
import com.apisense.bee.ui.entity.ExperimentSerializable;
import fr.inria.bsense.APISENSE;
import fr.inria.bsense.appmodel.Experiment;

import java.util.ArrayList;
import java.util.List;


public class HomeActivity extends Activity {
    private final String TAG = getClass().getSimpleName();

    // Data
    protected SubscribedExperimentsListAdapter experimentsAdapter;

   // Asynchronous Tasks
    private RetrieveInstalledExperimentsTask experimentsRetrieval;
    private SignOutTask signOut;
    private StopExperimentTask experimentStopTask;
    private StartExperimentTask experimentStartTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Set installed experiment list behavior
        experimentsAdapter = new SubscribedExperimentsListAdapter(getBaseContext(),
                                                                  R.layout.fragment_experimentelement,
                                                                  new ArrayList<Experiment>());
        ListView subscribedCollects = (ListView) findViewById(R.id.home_experiment_lists);
        subscribedCollects.setAdapter(experimentsAdapter);
        subscribedCollects.setOnItemLongClickListener(new StartStopExperimentListener());
        subscribedCollects.setOnItemClickListener(new OpenExperimentDetailsListener());

        updateUI();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    public void setExperiments(List<Experiment> experiments) {
        this.experimentsAdapter.setDataSet(experiments);
    }

    private void updateUI(){
        retrieveActiveExperiments();

        // Generating messages depending on the logged user
        /* TextView user_identity = (TextView) findViewById(R.id.home_user_identity);
        Button loginButton = (Button) findViewById(R.id.home_login_logout_button);

        if (isUserAuthenticated()) {
            loginButton.setText(getString(R.string.logout));
            user_identity.setText(getString(R.string.user_identity, "usernameToRetrieve"));
        } else {
            loginButton.setText(R.string.login);
            user_identity.setText(getString(R.string.user_identity, getString(R.string.anonymous_user)));
        } */
    }

    private void retrieveActiveExperiments() {
        if (experimentsRetrieval == null) {
            experimentsRetrieval = new RetrieveInstalledExperimentsTask(new ExperimentListRetrievedCallback());

            experimentsRetrieval.execute();
        }
   }

    private boolean isUserAuthenticated() {
        return APISENSE.apisServerService().isConnected();
    }

    public void doLaunchSettings(MenuItem button){
        Intent settingsIntent = new Intent(this, SettingsActivity.class);
        startActivity(settingsIntent);
    }

    public void doLaunchPrivacy(MenuItem button){
        Intent privacyIntent = new Intent(this, PrivacyActivity.class);
        startActivity(privacyIntent);
    }

    public void doLoginLogout(View loginButton){
//        if (isUserAuthenticated()) {
//            if (signOut == null) {
//                signOut = new SignOutTask(new SignedOutCallback());
//                signOut.execute();
//            }
//        } else {
//            Intent intent = new Intent(HomeActivity.this, SlideshowActivity.class);
//            startActivity(intent);
//            finish();
//        }

    }

    public void doGoToStore(View storeButton) {
        Intent storeIntent = new Intent(this, StoreActivity.class);
        startActivity(storeIntent);
    }
    
    public class ExperimentListRetrievedCallback implements AsyncTasksCallbacks {
        @Override
        public void onTaskCompleted(int result, Object response) {
            experimentsRetrieval = null;
            List<Experiment> exp = (List<Experiment>) response;
            Log.i(TAG, "number of Active Experiments: " + exp.size());

           // Updating listview
            if (exp.size() != 0) {
                setExperiments(exp);
                experimentsAdapter.notifyDataSetChanged();
            } else {
                // TODO : Ask to add an experiment
            }
        }

        @Override
        public void onTaskCanceled() {
            experimentsRetrieval = null;
        }
    }

    private class OpenExperimentDetailsListener implements AdapterView.OnItemClickListener{
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Intent intent = new Intent(view.getContext(), ExperimentDetailsActivity.class);
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

    private class StartStopExperimentListener implements AdapterView.OnItemLongClickListener {
        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
            Experiment exp = (Experiment) parent.getAdapter().getItem(position);
            Log.d(TAG, "Exp state: " + exp.state);
            if (! exp.state) {
                if (experimentStartTask == null) {
                    Log.i(TAG, "Starting experiment: " + exp);
                    experimentStartTask = new StartExperimentTask(new OnExperimentStarted(view));
                    experimentStartTask.execute(exp);
                }
            } else {
                if (experimentStopTask == null) {
                    Log.i(TAG, "Stopping experiment: " + exp);
                    experimentStopTask = new StopExperimentTask(new OnExperimentStopped(view));
                    experimentStopTask.execute(exp);
                }
            }
            return true;
        }
    }


    public class SignedOutCallback implements AsyncTasksCallbacks {
        @Override
        public void onTaskCompleted(int result, Object response) {
            signOut = null;
            updateUI();
            Toast.makeText(getApplicationContext(), R.string.status_changed_to_anonymous, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onTaskCanceled() {
            signOut = null;
        }
    }

    public class OnExperimentStarted implements AsyncTasksCallbacks {
        private View concernedView;

        public OnExperimentStarted(View view) {
            this.concernedView = view;
        }

        @Override
        public void onTaskCompleted(int result, Object response) {
            experimentStartTask = null;
            if (result == BeeApplication.ASYNC_SUCCESS) {
                // User feedback
               String experimentName = ((TextView) concernedView.findViewById(R.id.experimentelement_sampletitle)).getText().toString();
                Toast.makeText(getBaseContext(),
                        String.format(getString(R.string.experiment_started), experimentName),
                        Toast.LENGTH_SHORT).show();
                experimentsAdapter.notifyDataSetInvalidated();
            }
        }

        @Override
        public void onTaskCanceled() {
            experimentStartTask = null;
        }
    }

    public class OnExperimentStopped implements AsyncTasksCallbacks {
        private View concernedView;

        public OnExperimentStopped(View view) {
            this.concernedView = view;
        }

        @Override
        public void onTaskCompleted(int result, Object response) {
            experimentStopTask = null;
            if (result == BeeApplication.ASYNC_SUCCESS) {
                // User feedback
                String experimentName = ((TextView) concernedView.findViewById(R.id.experimentelement_sampletitle)).getText().toString();
                Toast.makeText(getBaseContext(),
                        String.format(getString(R.string.experiment_stopped), experimentName),
                        Toast.LENGTH_SHORT).show();
                experimentsAdapter.notifyDataSetChanged();
            }
        }

        @Override
        public void onTaskCanceled() {
            experimentStopTask = null;
        }
    }

}
