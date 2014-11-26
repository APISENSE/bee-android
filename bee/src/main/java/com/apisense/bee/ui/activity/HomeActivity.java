package com.apisense.bee.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import com.apisense.android.api.APS;
import com.apisense.android.api.APSLocalCrop;
import com.apisense.api.Callback;
import com.apisense.api.Crop;
import com.apisense.api.LocalCrop;
import com.apisense.bee.R;
import com.apisense.bee.backend.experiment.*;
import com.apisense.bee.ui.adapter.SubscribedExperimentsListAdapter;
import com.apisense.bee.ui.entity.ExperimentSerializable;
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Set installed experiment list behavior
        experimentsAdapter = new SubscribedExperimentsListAdapter(getBaseContext(),
                                                                  R.layout.fragment_experiment_element,
                                                                  new ArrayList<APSLocalCrop>());
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

    public void doLaunchSettings(MenuItem button){
        Intent settingsIntent = new Intent(this, SettingsActivity.class);
        startActivity(settingsIntent);
    }

    public void doLaunchPrivacy(MenuItem button){
        Intent privacyIntent = new Intent(this, PrivacyActivity.class);
        startActivity(privacyIntent);
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
    
    public class ExperimentListRetrievedCallback implements Callback<List<LocalCrop>> {
            @Override
        public void onCall(List<LocalCrop> crops) throws Exception {
            experimentsRetrieval = null;

            List<APSLocalCrop> exp = localCropToAPSLocalCrop(crops);
            Log.i(TAG, "number of Active Crops: " + exp.size());
            // Updating listview
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
            if (experimentStartStopTask == null) {
                experimentStartStopTask = new StartStopExperimentTask(getApplicationContext(), new OnCropStatusChanged(exp));
                experimentStartStopTask.execute(exp.getName());
            }
            return true;
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
