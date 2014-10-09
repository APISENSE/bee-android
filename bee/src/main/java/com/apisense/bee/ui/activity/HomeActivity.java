package com.apisense.bee.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import com.apisense.bee.R;
import com.apisense.bee.backend.AsyncTasksCallbacks;
import com.apisense.bee.backend.RetrieveCollectsTask;
import com.apisense.bee.ui.adapter.SubscribedCollectsListAdapter;
import fr.inria.bsense.APISENSE;
import fr.inria.bsense.appmodel.Experiment;

import java.util.ArrayList;
import java.util.List;


public class HomeActivity extends Activity {
    private final String TAG = getClass().getSimpleName();
    protected List<Experiment> collects = new ArrayList<Experiment>();
    private RetrieveCollectsTask collectsRetrieval;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        ListView subscribedCollects = (ListView) findViewById(R.id.home_collect_lists);
        ArrayAdapter experimentsAdapter = new SubscribedCollectsListAdapter(getBaseContext(),
                                                                            R.layout.fragment_collectelement,
                                                                            collects);
        subscribedCollects.setAdapter(experimentsAdapter);

        updateUI();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    private void updateUI(){
        TextView user_identity = (TextView) findViewById(R.id.home_user_identity);
        Button loginButton = (Button) findViewById(R.id.home_login_logout_button);

        // Generating messages depending on the logged user
        if (isUserAuthenticated()) {
            loginButton.setText(getString(R.string.logout));
            user_identity.setText(getString(R.string.user_identity, "usernameToRetrieve"));
            retrieveActiveCollects();
        } else {
            loginButton.setText(R.string.login);
            user_identity.setText(getString(R.string.user_identity, getString(R.string.anonymous_user)));
        }
    }

    private void retrieveActiveCollects() {
        if (collectsRetrieval == null) {
            collectsRetrieval = new RetrieveCollectsTask(new CollectListRetrieved());
            collectsRetrieval.execute();
        }
   }

    private boolean isUserAuthenticated() {
        return APISENSE.apisServerService().isConnected();
    }

    public void doLaunchSettings(MenuItem button){
        Toast.makeText(getBaseContext(), "Settings clicked", Toast.LENGTH_SHORT).show();
    }

    public void doLaunchPrivacy(MenuItem button){
        Toast.makeText(getBaseContext(), "Privacy clicked", Toast.LENGTH_SHORT).show();
    }

    public void doLoginLogout(View loginButton){
        if (isUserAuthenticated()) {
            try {
                APISENSE.apisMobileService().sendAllTrack();
                APISENSE.apisMobileService().stopAllExperiments(0);
                for(Experiment xp: APISENSE.apisMobileService().getInstalledExperiments().values())
                    APISENSE.apisMobileService().uninstallExperiment(xp);
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), R.string.experiment_exception_on_closure, Toast.LENGTH_SHORT).show();
            }
            APISENSE.apisServerService().disconnect();
            updateUI();
            Toast.makeText(getApplicationContext(), R.string.status_changed_to_anonymous, Toast.LENGTH_SHORT).show();
        } else {
            Intent intent = new Intent(HomeActivity.this, SlideshowActivity.class);
            startActivity(intent);
            finish();
        }
    }

    public class CollectListRetrieved implements AsyncTasksCallbacks {

        @Override
        public void onTaskCompleted(Object response) {
            collects = (List<Experiment>) response;
            Log.i(TAG, "number of Active Experiments: " + collects.size());
        }

        @Override
        public void onTaskCanceled() {
            collectsRetrieval = null;
        }
    }
}
