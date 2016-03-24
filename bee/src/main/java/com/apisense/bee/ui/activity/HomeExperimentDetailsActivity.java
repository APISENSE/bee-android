package com.apisense.bee.ui.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.apisense.bee.Callbacks.OnCropStarted;
import com.apisense.bee.Callbacks.OnCropStopped;
import com.apisense.bee.Callbacks.OnCropUnsubscribed;
import com.apisense.bee.R;
import com.apisense.bee.utils.CropPermissionHandler;
//import com.apisense.bee.widget.UploadedDataGraph;
import com.apisense.sdk.core.statistics.CropUsageStatistics;
import com.apisense.sdk.core.statistics.UploadedEntry;
import com.apisense.sdk.core.store.Crop;

import java.util.Collection;


public class HomeExperimentDetailsActivity extends ExperimentDetailsActivity {
    private static final String TAG = "HomeExpDetailsAct";

    private MenuItem mStartButton;
    private MenuItem mStopButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_experiment_details);
        initExperimentDetailsActivity();
        displayStatistics(apisenseSdk.getStatisticsManager().getCropUsage(crop));
    }

    @Override
    protected CropPermissionHandler prepareCropPermissionHandler() {
        return new CropPermissionHandler(this, crop, new OnCropStarted(getBaseContext()) {
            @Override
            public void onDone(Crop crop) {
                super.onDone(crop);
                displayStopButton();
            }
        });
    }

    private void displayStatistics(CropUsageStatistics cropUsage) {
        Log.i(TAG, "Got statistics" + cropUsage);
        TextView nbLocalTraces = (TextView) findViewById(R.id.detail_stats_local_traces);
        TextView nbTotalTraces = (TextView) findViewById(R.id.detail_stats_total_uploaded);

        nbLocalTraces.setText(getString(R.string.crop_stats_local_traces, cropUsage.getToUpload()));
        nbTotalTraces.setText(getString(R.string.crop_stats_total_uploaded, cropUsage.getTotalUploaded()));
    //    displayStatisticsGraph(cropUsage.getUploaded());
    }

    /*
    private void displayStatisticsGraph(Collection<UploadedEntry> uploaded) {
        UploadedDataGraph uploadGraph = (UploadedDataGraph) findViewById(R.id.details_stats_upload_graph);
        uploadGraph.setValues(uploaded);
    }*/

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
            apisenseSdk.getCropManager().stop(crop, new OnCropStopped(getBaseContext()) {
                @Override
                public void onDone(Crop crop) {
                    super.onDone(crop);
                    displayStartButton();
                }
            });
        } else {
            cropPermissionHandler.startOrRequestPermissions();
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
