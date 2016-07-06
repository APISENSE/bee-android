package com.apisense.bee.ui.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.apisense.bee.R;
import com.apisense.bee.callbacks.BeeAPSCallback;
import com.apisense.bee.callbacks.OnCropStarted;
import com.apisense.bee.callbacks.OnCropStopped;
import com.apisense.bee.callbacks.OnCropUnsubscribed;
import com.apisense.bee.utils.CropPermissionHandler;
import com.apisense.bee.widget.UploadedDataGraph;
import com.apisense.sdk.core.statistics.CropLocalStatistics;
import com.apisense.sdk.core.statistics.UploadedEntry;
import com.apisense.sdk.core.store.Crop;

import java.util.Collection;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HomeDetailsFragment extends CommonDetailsFragment {

    @BindView(R.id.detail_stats_local_traces) TextView nbLocalTraces;
    @BindView(R.id.detail_stats_total_uploaded) TextView nbTotalTraces;
    @BindView(R.id.details_stats_upload_graph) UploadedDataGraph uploadGraph;
    @BindView(R.id.no_upload) TextView noUpload;

    private MenuItem mStartButton;
    private MenuItem mStopButton;

    private static String TAG = "ExpDetailsFragment";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.fragment_home_details, container, false);
        homeActivity.getSupportActionBar().setTitle(R.string.title_activity_experiment_details);

        unbinder = ButterKnife.bind(this, view);

        displayStatistics(apisenseSdk.getStatisticsManager().getCropUsage(crop));

        return view;
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
            case R.id.detail_action_update:
                doUpdate();
                break;
        }
        return false;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.home_experiment_details, menu);
        super.onCreateOptionsMenu(menu, inflater);

        mStartButton = (MenuItem) menu.getItem(0);
        mStopButton = (MenuItem) menu.getItem(1);

        if (apisenseSdk.getCropManager().isRunning(crop)) {
            displayStopButton();
        } else {
            displayStartButton();
        }
    }

    // Buttons Handlers
    private void doStartStop() {
        if (apisenseSdk.getCropManager().isRunning(crop)) {
            apisenseSdk.getCropManager().stop(crop, new OnCropStopped(getActivity()) {
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

    private void displayStopButton() {
        mStartButton.setVisible(false);
        mStopButton.setVisible(true);
    }

    private void displayStartButton() {
        mStartButton.setVisible(true);
        mStopButton.setVisible(false);
    }


    @Override
    protected CropPermissionHandler prepareCropPermissionHandler() {
        return new CropPermissionHandler(getActivity(), crop, new OnCropStarted(getActivity()) {
            @Override
            public void onDone(Crop crop) {
                super.onDone(crop);
                displayStopButton();
            }
        });
    }

    // Actions

    private void doSubscribeUnsubscribe() {
        apisenseSdk.getCropManager().unsubscribe(crop, new OnCropUnsubscribed(getActivity(), crop.getName()) {
            @Override
            public void onDone(Crop crop) {
                super.onDone(crop);
                getFragmentManager().popBackStack();
            }
        });
    }

    private void displayStatistics(CropLocalStatistics cropUsage) {
        Log.i(TAG, "Got statistics" + cropUsage);
        nbLocalTraces.setText(getString(R.string.crop_stats_local_traces, cropUsage.getToUpload()));
        nbTotalTraces.setText(getString(R.string.crop_stats_total_uploaded, cropUsage.getTotalUploaded()));

        Collection<UploadedEntry> uploaded = cropUsage.getUploaded();
        if (uploaded.isEmpty()) {
            uploadGraph.setVisibility(View.GONE);
            noUpload.setVisibility(View.VISIBLE);
        } else {
            displayStatisticsGraph(cropUsage.getUploaded());
        }
    }

    private void displayStatisticsGraph(Collection<UploadedEntry> uploaded) {
        uploadGraph.setValues(uploaded);
    }
}
