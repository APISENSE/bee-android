package com.apisense.bee.ui.activity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.apisense.bee.Callbacks.OnCropStarted;
import com.apisense.bee.Callbacks.OnCropStopped;
import com.apisense.bee.Callbacks.OnCropUnsubscribed;
import com.apisense.bee.R;
import com.apisense.sdk.core.store.Crop;


public class HomeExperimentDetailsActivity extends ExperimentDetailsActivity {
    private static String TAG = "HomeExpDetailsAct";

    private MenuItem mStartButton;
    private MenuItem mStopButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_experiment_details);
        initExperimentDetailsActivity();
    }

    // UI Initialisation
    @Override
    public void initializeViews() {
        mExperimentOrganization = (TextView) findViewById(R.id.exp_organization);
        mExperimentVersion = (TextView) findViewById(R.id.exp_version);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home_experiment_details, menu);

        mStartButton = menu.findItem(R.id.detail_action_start);
        mStopButton = menu.findItem(R.id.detail_action_stop);

        if (apisenseSdk.getCropManager().isRunning(crop)) {
            displayStopButton();
        } else {
            displayStartButton();
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.detail_action_start:
                doStartStop();
                break;
            case R.id.detail_action_stop:
                doStartStop();
                break;
            case R.id.detail_action_unsubscribe:
                doSubscribeUnsubscribe();
                break;

        }
        return false;
    }

    private void displayStopButton() {
        mStartButton.setVisible(false);
        mStopButton.setVisible(true);
    }

    private void displayStartButton() {
        mStartButton.setVisible(true);
        mStopButton.setVisible(false);
    }

    // Buttons Handlers
    public void doStartStop() {
        if (apisenseSdk.getCropManager().isRunning(crop)) {
            apisenseSdk.getCropManager().stop(crop, new OnCropStopped(getBaseContext(), crop.getName()) {
                @Override
                public void onDone(Void aVoid) {
                    super.onDone(aVoid);
                    displayStartButton();
                }
            });
        } else {
            apisenseSdk.getCropManager().start(crop, new OnCropStarted(getBaseContext(), crop.getName()) {
                @Override
                public void onDone(Void aVoid) {
                    super.onDone(aVoid);
                    displayStopButton();
                }
            });
        }

    }

    public void doSubscribeUnsubscribe() {
        apisenseSdk.getCropManager().unsubscribe(crop, new OnCropUnsubscribed(getBaseContext(), crop.getName()) {
            @Override
            public void onDone(Crop crop) {
                super.onDone(crop);
                finish();
            }
        });
    }
}
