package com.apisense.bee.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import com.apisense.bee.R;
import com.apisense.bee.backend.AsyncTasksCallbacks;
import com.apisense.bee.backend.experiment.RetrieveExperimentsTask;
import com.apisense.bee.backend.user.SignOutTask;
import com.apisense.bee.ui.adapter.SubscribedExperimentsListAdapter;
import fr.inria.bsense.APISENSE;
import fr.inria.bsense.appmodel.Experiment;

import java.util.ArrayList;
import java.util.List;


public class HomeActivity extends Activity {
    private final String TAG = getClass().getSimpleName();

    // Data
    protected SubscribedExperimentsListAdapter experimentsAdapter;

   // Asynchronous Tasks
    private RetrieveExperimentsTask experimentsRetrieval;
    private SignOutTask signOut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        experimentsAdapter = new SubscribedExperimentsListAdapter(getBaseContext(),
                                                                  R.layout.fragment_experimentelement,
                                                                  new ArrayList<Experiment>());
        ListView subscribedCollects = (ListView) findViewById(R.id.home_experiment_lists);
        subscribedCollects.setAdapter(experimentsAdapter);
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
            experimentsRetrieval = new RetrieveExperimentsTask(new ExperimentListRetrievedCallback(),
                                                               RetrieveExperimentsTask.GET_INSTALLED_EXPERIMENTS);
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
        if (isUserAuthenticated()) {
            if (signOut == null) {
                signOut = new SignOutTask(new SignedOutCallback());
                signOut.execute();
            }
        } else {
            Intent intent = new Intent(HomeActivity.this, SlideshowActivity.class);
            startActivity(intent);
            finish();
        }
    }

    public class ExperimentListRetrievedCallback implements AsyncTasksCallbacks {

        @Override
        public void onTaskCompleted(Object response, String details) {
            experimentsRetrieval = null;
            List<Experiment> exp = (List<Experiment>) response;
            Log.i(TAG, "number of Active Experiments: " + exp.size());

           // Updating listview
            setExperiments(exp);
            experimentsAdapter.notifyDataSetChanged();
        }

        @Override
        public void onTaskCanceled() {
            experimentsRetrieval = null;
        }
    }

    public class SignedOutCallback implements AsyncTasksCallbacks {

        @Override
        public void onTaskCompleted(Object response, String details) {
            signOut = null;
            updateUI();
            Toast.makeText(getApplicationContext(), R.string.status_changed_to_anonymous, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onTaskCanceled() {
            signOut = null;
        }
    }
}
