package com.apisense.bee.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.AnimatedStateListDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;
import com.apisense.bee.R;
import com.apisense.bee.backend.experiment.SubscribeUnsubscribeExperimentTask;
import com.apisense.android.api.APSLocalCrop;
import com.apisense.core.CropJSONImpl;
import com.apisense.core.api.Callback;
import com.apisense.core.api.Crop;

import java.util.Map;

/**
 * Shows detailed informations about a given available Experiment from the store
 *
 */
public class StoreExperimentDetailsActivity extends Activity {
    private final String TAG = getClass().getSimpleName();

    private Crop experiment;

    private TextView mExperimentName;
    private TextView mExperimentOrganization;
    private TextView mExperimentVersion;

    private MenuItem mSubscribeButton;
    private MenuItem  mHomeButton;

    // Async Tasks
    private SubscribeUnsubscribeExperimentTask experimentChangeSubscriptionStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_experiment_details);
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);

        experiment =  new CropJSONImpl((Map)getIntent().getSerializableExtra("experiment"));

        initializeViews();
        displayExperimentInformation();
    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(R.anim.slide_back_in,R.anim.slide_back_out);
    }
    public void initializeViews() {
        mExperimentName = (TextView) findViewById(R.id.store_detail_exp_name);
        mExperimentOrganization = (TextView) findViewById(R.id.store_detail_exp_organization);
        mExperimentVersion = (TextView) findViewById(R.id.store_detail_exp_version);
    }

    public void displayExperimentInformation() {
        mExperimentName.setText(experiment.getNiceName());
        mExperimentOrganization.setText(experiment.getOrganisation());
        mExperimentVersion.setText(" - v" + experiment.getVersion());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.store_experiment_details, menu);

        mSubscribeButton = menu.findItem(R.id.store_detail_action_subscribe);
        mHomeButton = menu.findItem(R.id.store_detail_back_to_home_button);
        updateSubscriptionMenu();
        return true;
    }

    private void updateSubscriptionMenu(){
        if (! SubscribeUnsubscribeExperimentTask.isInstalled(this, experiment.getName()) ) {
            showAsUnsubscribed();
        } else {
            showAsSubscribed();
        }
    }

    private void showAsSubscribed(){
        mSubscribeButton.setTitle(getString(R.string.action_unsubscribe));
        mHomeButton.setVisible(true);
    }

    private void showAsUnsubscribed() {
        mSubscribeButton.setTitle(getString(R.string.action_subscribe));
        mHomeButton.setVisible(false);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_back_in, R.anim.slide_back_out);
    }

    public void doSubscribeUnsubscribe(MenuItem item) {
        if (experimentChangeSubscriptionStatus == null) {
            experimentChangeSubscriptionStatus = new SubscribeUnsubscribeExperimentTask(getApplicationContext(),
                                                                                        new OnSubscribed(), new OnUnSubscribed());
            experimentChangeSubscriptionStatus.execute(experiment.getName());
        }
    }

    public void goBackHome(MenuItem item) {
        Intent intent = new Intent(this, HomeActivityBis.class);
        startActivity(intent);
        finish();
    }

    private class OnSubscribed implements Callback<APSLocalCrop> {
        private final String experimentName;

        public OnSubscribed(){
            this.experimentName = experiment.getNiceName();
        }
        @Override
        public void onCall(APSLocalCrop apsLocalCrop) throws Exception {
            experimentChangeSubscriptionStatus = null;

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String toastMessage = String.format(getString(R.string.experiment_subscribed), experimentName);
                    Toast.makeText(getBaseContext(), toastMessage, Toast.LENGTH_SHORT).show();

                    showAsSubscribed();
                }
            });


        }

        @Override
        public void onError(Throwable throwable) {
            throwable.printStackTrace();
            experimentChangeSubscriptionStatus = null;
            showAsUnsubscribed();
        }
    }

    private class OnUnSubscribed implements Callback<Void> {
        private final String experimentName;

        public OnUnSubscribed(){
            this.experimentName = experiment.getNiceName();
        }

        @Override
        public void onCall(Void aVoid) throws Exception {
            experimentChangeSubscriptionStatus = null;

            String toastMessage = String.format(getString(R.string.experiment_unsubscribed), experimentName);
            Toast.makeText(getBaseContext(), toastMessage, Toast.LENGTH_SHORT).show();

            showAsUnsubscribed();
        }

        @Override
        public void onError(Throwable throwable) {
            throwable.printStackTrace();
            experimentChangeSubscriptionStatus = null;
            showAsSubscribed();
        }
    }

}
