package com.apisense.bee.ui.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import com.apisense.android.api.APS;
import com.apisense.android.api.APSLocalCrop;
import com.apisense.api.*;
import com.apisense.bee.R;
import com.apisense.bee.backend.experiment.*;
import com.apisense.bee.backend.user.SignOutTask;
import com.apisense.bee.ui.adapter.SubscribedExperimentsListAdapter;
import org.json.simple.parser.ParseException;

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
                Log.i(TAG, "Got event (" + apsLogEvent.getClass().getSimpleName() + ") for crop: " + apsLogEvent.cropName);
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
            Intent slideIntent = new Intent(this, SlideshowActivity.class);
            slideIntent.putExtra("goTo","register");
            startActivity(slideIntent);
            finish();
        } else {
            // Go to profil activity
        }
    }

    public class ExperimentListRetrievedCallback implements Callback<List<LocalCrop>> {
            @Override
        public void onCall(List<LocalCrop> crops) throws Exception {
            experimentsRetrieval = null;

            List<APSLocalCrop> exp = localCropToAPSLocalCrop(crops);
            Log.i(TAG, "number of Active Crops: " + exp.size());

            experimentsAdapter.clear();
            experimentsAdapter.addAll(exp);
            experimentsAdapter.notifyDataSetChanged();
        }

        // TODO: Delete when Callback type is fixed to APSLocalCrop
        private List<APSLocalCrop> localCropToAPSLocalCrop(List<LocalCrop> crops) {
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
        }
    }

    private class OpenCropDetailsListener implements AdapterView.OnItemClickListener{
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Intent intent = new Intent(view.getContext(), ExperimentDetailsActivity.class);

            Crop exp = (Crop) parent.getAdapter().getItem(position);
            intent.putExtra("experiment", exp.getByte());

            startActivity(intent);
        }
    }

    private class StartStopCropListener implements AdapterView.OnItemLongClickListener {
        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
            Crop exp = (Crop) parent.getAdapter().getItem(position);
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
            Intent intent = new Intent(HomeActivity.this, SlideshowActivity.class);
            startActivity(intent);
            finish();
        }

        @Override
        public void onError(Throwable throwable) {
            signOut = null;
        }
    }
}
