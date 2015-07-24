package com.apisense.bee.ui.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.apisense.bee.BeeApplication;
import com.apisense.bee.Callbacks.OnCropStarted;
import com.apisense.bee.Callbacks.OnCropStopped;
import com.apisense.bee.Callbacks.OnCropUnsubscribed;
import com.apisense.bee.R;
import com.apisense.bee.games.BeeGameActivity;
import com.apisense.sdk.APISENSE;
import com.apisense.sdk.core.APSCallback;
import com.apisense.sdk.core.store.Crop;
import com.rollbar.android.Rollbar;


public class ExperimentDetailsActivity extends BeeGameActivity {
    private static String TAG = "Experiment Details Activity";

    private Crop crop;

    private TextView mExperimentOrganization;
    private TextView mExperimentVersion;
    private TextView mExperimentActivity;

    private MenuItem mStartButton;
    private MenuItem mStopButton;

    private APISENSE.Sdk apisenseSdk;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_experiment_details);
        apisenseSdk = ((BeeApplication) getApplication()).getSdk();

        Toolbar toolbar = (Toolbar) findViewById(R.id.material_toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_action_back);
        setSupportActionBar(toolbar);

        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);

        initializeViews();
        displayExperimentInformation();
    }

    // UI Initialisation

    public void initializeViews() {
        mExperimentOrganization = (TextView) findViewById(R.id.exp_organization);
        mExperimentVersion = (TextView) findViewById(R.id.exp_version);
        mExperimentActivity = (TextView) findViewById(R.id.exp_activity);
    }

    public void displayExperimentInformation() {
        Bundle b = getIntent().getExtras();
        crop = b.getParcelable("crop");

        getSupportActionBar().setTitle(crop.getName());
        mExperimentOrganization.setText(crop.getOwner());
        mExperimentVersion.setText(" - v" + crop.getVersion());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.experiment_details, menu);

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
        return true;
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
