package com.apisense.bee.ui.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.os.Bundle;
import com.apisense.core.api.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.apisense.android.api.APS;
import com.apisense.android.api.APSLocalCrop;
import com.apisense.bee.R;
import com.apisense.bee.backend.experiment.RetrieveInstalledExperimentsTask;
import com.apisense.bee.backend.experiment.StartStopExperimentTask;
import com.apisense.bee.backend.user.SignOutTask;
import com.apisense.bee.ui.adapter.SubscribedExperimentsListAdapter;
import com.apisense.core.api.APSLogEvent;
import com.apisense.core.api.Callable;
import com.apisense.core.api.Callback;


import java.util.ArrayList;
import java.util.List;


public class HomeActivity extends Activity {
    private final String TAG = getClass().getSimpleName();

    // Data
    protected SubscribedExperimentsListAdapter experimentsAdapter;

   // Asynchronous Tasks
    private RetrieveInstalledExperimentsTask experimentsRetrieval;
    private StartStopExperimentTask experimentStartStopTask;
    private SignOutTask signOut;

    private BroadcastReceiver eventReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        initializeCropsView();
    }

    @Override
    protected void onStart() {
        super.onStart();

        updateProfile();
        retrieveActiveCrops();

        eventReceiver = APS.registerToAPSEvent(this, new Callable<Void, APSLogEvent>() {
            @Override
            public Void call(APSLogEvent apsLogEvent) throws Exception {
                Log.i(TAG, "Got event (" + apsLogEvent + ") for crop: " + apsLogEvent.cropName);
                if (apsLogEvent instanceof APSLogEvent.StartCrop){
                    showAsStarted(apsLogEvent.cropName);
                }
                else if (apsLogEvent instanceof APSLogEvent.StopCrop){
                    showAsStopped(apsLogEvent.cropName);
                }
                return null;
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        APS.unregisterToAPSEvent(this, eventReceiver);
    }

    private void initializeCropsView(){
        experimentsAdapter = new SubscribedExperimentsListAdapter(getBaseContext(),
                R.layout.fragment_experiment_element,
                new ArrayList<APSLocalCrop>());
        ListView subscribedCrops = (ListView) findViewById(R.id.home_experiment_lists);
        subscribedCrops.setEmptyView(findViewById(R.id.home_empty_list));
        subscribedCrops.setAdapter(experimentsAdapter);
        subscribedCrops.setOnItemLongClickListener(new StartStopCropListener());
        subscribedCrops.setOnItemClickListener(new OpenCropDetailsListener());
    }

    private void showAsStarted(String cropName) {
        String toastMessage = String.format(getString(R.string.experiment_started), cropName);
        Toast.makeText(getBaseContext(), toastMessage, Toast.LENGTH_SHORT).show();
        retrieveActiveCrops();
    }

    private void showAsStopped(String cropName) {
        String toastMessage = String.format(getString(R.string.experiment_stopped), cropName);
        Toast.makeText(getBaseContext(), toastMessage, Toast.LENGTH_SHORT).show();
        retrieveActiveCrops();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.connectOrDisconnect:
                doDisconnect();
                break;
            case R.id.action_about:
                doLaunchAbout();
                break;
            case R.id.action_settings:
                doLaunchSettings();
                break;
            case R.id.action_privacy:
                doLaunchPrivacy();
                break;
        }
        return true;
    }

    private void updateProfile(){
        String username = getString(R.string.user_identity, getString(R.string.anonymous_user));
        if (isUserAuthenticated()) {
            try {
                username = String.format(getString(R.string.user_identity), APS.getUsername(this));
            } catch (APS.SDKNotInitializedException e) {
                e.printStackTrace();
            }
        }

        TextView user_identity = (TextView) findViewById(R.id.home_user_identity);
        user_identity.setText(username);
    }

    private void retrieveActiveCrops() {
        if (experimentsRetrieval == null) {
             experimentsRetrieval = new RetrieveInstalledExperimentsTask(this, new ExperimentListRetrievedCallback());
            experimentsRetrieval.execute();
        }
   }

    private boolean isUserAuthenticated() {
        boolean response;
        try {
            response = APS.isConnected(this);
        } catch (APS.SDKNotInitializedException e) {
            e.printStackTrace();
            return false;
        }
        return response;
    }

    public void doLaunchSettings(){
        Intent settingsIntent = new Intent(this, SettingsActivity.class);
        startActivity(settingsIntent);
    }

    public void doLaunchPrivacy(){
        Intent privacyIntent = new Intent(this, PrivacyActivity.class);
        startActivity(privacyIntent);
    }

    private void doDisconnect() {
        signOut = new SignOutTask(this, new SignedOutCallback());
        signOut.execute();
    }

    private void doLaunchAbout() {
        Intent aboutIntent = new Intent(this, AboutActivity.class);
        startActivity(aboutIntent);
    }

    public void doGoToStore(View storeButton) {
        Intent storeIntent = new Intent(this, StoreActivity.class);
        startActivity(storeIntent);
    }

    public void doGoToProfil(View personalInformation) {
        if (!isUserAuthenticated()) {
            Intent slideIntent = new Intent(this, LauncherActivity.class);
            startActivity(slideIntent);
            finish();


        } else {
            // Go to profil activity
        }
    }

    public class ExperimentListRetrievedCallback implements Callback<List<APSLocalCrop>> {
            @Override
        public void onCall(List<APSLocalCrop> crops) throws Exception {
            experimentsRetrieval = null;


            Log.i(TAG, "number of Active Crops: " + crops.size());

            experimentsAdapter.clear();
            experimentsAdapter.addAll(crops);
            experimentsAdapter.notifyDataSetChanged();
        }

        @Override
        public void onError(Throwable throwable) {
            experimentsRetrieval = null;
        }
    }

    private class OpenCropDetailsListener implements AdapterView.OnItemClickListener{
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Intent intent = new Intent(view.getContext(), ExperimentDetailsActivity.class);

            APSLocalCrop exp = (APSLocalCrop) parent.getAdapter().getItem(position);
            intent.putExtra("experiment", exp.getName());


            startActivity(intent);
        }
    }

    private class StartStopCropListener implements AdapterView.OnItemLongClickListener {
        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
            APSLocalCrop exp = (APSLocalCrop) parent.getAdapter().getItem(position);
            experimentStartStopTask = new StartStopExperimentTask(getApplicationContext());
            experimentStartStopTask.execute(exp.getName());
            return true;
        }
    }

    public class SignedOutCallback implements Callback<Void> {
        @Override
        public void onCall(Void aVoid) {
            signOut = null;
            Toast.makeText(getApplicationContext(), R.string.status_changed_to_anonymous, Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getBaseContext(), LauncherActivity.class);
            startActivity(intent);
            finish();
        }

        @Override
        public void onError(Throwable throwable) {
            signOut = null;
        }
    }
}
