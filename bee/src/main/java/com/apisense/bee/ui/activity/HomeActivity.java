package com.apisense.bee.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import com.apisense.android.api.APS;
import com.apisense.api.Callback;
import com.apisense.api.Crop;
import com.apisense.api.LocalCrop;
import com.apisense.bee.R;
import com.apisense.bee.backend.AsyncTasksCallbacks;
import com.apisense.bee.backend.experiment.*;
import com.apisense.bee.backend.user.SignOutTask;
import com.apisense.bee.ui.adapter.SubscribedExperimentsListAdapter;
import com.apisense.bee.ui.entity.ExperimentSerializable;

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Set installed experiment list behavior
        experimentsAdapter = new SubscribedExperimentsListAdapter(getBaseContext(),
                                                                  R.layout.fragment_experiment_element,
                                                                  new ArrayList<LocalCrop>());
        ListView subscribedCollects = (ListView) findViewById(R.id.home_experiment_lists);
        subscribedCollects.setEmptyView(findViewById(R.id.home_empty_list));
        subscribedCollects.setAdapter(experimentsAdapter);
        subscribedCollects.setOnItemLongClickListener(new StartStopCropListener());
        subscribedCollects.setOnItemClickListener(new OpenCropDetailsListener());

        updateUI();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
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

    @Override
    protected void onStart() {
        super.onStart();
        updateUI();
    }

    public void setCrops(List<LocalCrop> experiments) {
        this.experimentsAdapter.addAll(experiments);
    }

    private void updateUI(){
        retrieveActiveCrops();

        // Generating messages depending on the logged user
        TextView user_identity = (TextView) findViewById(R.id.home_user_identity);
        // Button loginButton = (Button) findViewById(R.id.home_login_logout_button);

        if (isUserAuthenticated()) {
            String username = getString(R.string.user_identity);
            try {
                username = APS.getUsername(this);
            } catch (APS.SDKNotInitializedException e) {
                e.printStackTrace();
            }
            user_identity.setText(username);
        } else {
            user_identity.setText(getString(R.string.user_identity, getString(R.string.anonymous_user)));
        }
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

    /**
     * Click event for disconnect
     */
    private void doDisconnect() {
        signOut = new SignOutTask(this, new SignedOutCallback());
        signOut.execute();
    }

    private void doLaunchAbout() {
        Intent aboutIntent = new Intent(this, AboutActivity.class);
        startActivity(aboutIntent);
    }

    public void doLoginForm(MenuItem button) {
        Intent slideIntent = new Intent(this, SlideshowActivity.class);
        slideIntent.putExtra("goTo", SlideshowActivity.REGISTER);
        startActivity(slideIntent);
        finish();
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
            List<LocalCrop> exp = (List<LocalCrop>) crops;
            Log.i(TAG, "number of Active Crops: " + exp.size());

            // Updating listview
            setCrops(exp);
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
            Crop exp = (Crop) parent.getAdapter().getItem(position);

            Bundle bundle = new Bundle();
            // TODO : Prefer parcelable in the future. Problem : CREATOR method doesn't exist (to check)
            // bundle.putParcelable("experiment", getItem(position));
            // TODO : Maybe something extending Crop and using JSONObject to init but it seems to be empty
            bundle.putSerializable("experiment", new ExperimentSerializable(exp));
            intent.putExtras(bundle); //Put your id to your next Intent
            startActivity(intent);
        }
    }

    private class StartStopCropListener implements AdapterView.OnItemLongClickListener {
        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
            Crop exp = (Crop) parent.getAdapter().getItem(position);
            if (experimentStartStopTask == null) {
                experimentStartStopTask = new StartStopExperimentTask(getApplicationContext(), new OnCropStatusChanged(exp));
                experimentStartStopTask.execute(exp.getName());
            }
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

    private class OnCropStatusChanged implements Callback<Integer> {
        private Crop concernedExp;

        public OnCropStatusChanged(Crop exp) {
            this.concernedExp = exp;
        }

        @Override
        public void onCall(Integer response) throws Exception {
            experimentStartStopTask = null;
            String experimentName = concernedExp.getNiceName();
            String toastMessage = "";
                switch(response) {
                    case StartStopExperimentTask.EXPERIMENT_STARTED:
                        toastMessage = String.format(getString(R.string.experiment_started), experimentName);
                        break;
                    case StartStopExperimentTask.EXPERIMENT_STOPPED:
                        toastMessage = String.format(getString(R.string.experiment_stopped), experimentName);
                        break;
                }
                Toast.makeText(getBaseContext(), toastMessage, Toast.LENGTH_SHORT).show();
                experimentsAdapter.notifyDataSetInvalidated();
            }

        @Override
        public void onError(Throwable throwable) {
            experimentStartStopTask = null;
        }
    }
}
