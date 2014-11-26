package com.apisense.bee.ui.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;
import com.apisense.android.api.APS;
import com.apisense.android.api.APSLocalCrop;
import com.apisense.api.Callback;
import com.apisense.api.Crop;
import com.apisense.bee.BeeApplication;
import com.apisense.bee.R;
import com.apisense.bee.backend.AsyncTasksCallbacks;
import com.apisense.bee.backend.experiment.*;
import com.apisense.bee.ui.entity.ExperimentSerializable;
import com.apisense.bee.widget.BarGraphView;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import org.json.JSONException;
import org.json.simple.parser.ParseException;

import java.util.*;

public class ExperimentDetailsActivity extends Activity {

    private static String TAG = "Experiment Details Activity";

    private APSLocalCrop experiment;

    private CardView mMapCardView;

    private TextView mExperimentName;
    private TextView mExperimentOrganization;
    private TextView mExperimentVersion;
    private TextView mExperimentActivity;

    // private MenuItem mSubscribeButton;
    private MenuItem mStartButton;

    private GoogleMap mGoogleMap;

    private BarGraphView graph;
    private int barGraphShowDay = 7;
    private ArrayList<Long> traces;

    // Async Tasks
    private StartStopExperimentTask experimentStartStopTask;
    private SubscribeUnsubscribeExperimentTask experimentChangeSubscriptionStatus;

    protected boolean canDisplayMap() {
        return (GooglePlayServicesUtil.isGooglePlayServicesAvailable(getApplicationContext())
                == ConnectionResult.SUCCESS);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_experiment_details);
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);

        try {
            experiment = new APSLocalCrop(getIntent().getByteArrayExtra("experiment"));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        initializeViews();
        displayExperimentInformation();
        displayExperimentActivity();
    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(R.anim.slide_back_in,R.anim.slide_back_out);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_back_in, R.anim.slide_back_out);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.experiment_details, menu);
        // mSubscribeButton = menu.findItem(R.id.detail_action_subscribe);
        mStartButton = menu.findItem(R.id.detail_action_start);

        updateStartMenu();
        return true;
    }

    // UI Initialisation

    public void initializeViews() {
        mExperimentName = (TextView) findViewById(R.id.exp_name);
        mExperimentOrganization = (TextView) findViewById(R.id.exp_organization);
        mExperimentVersion = (TextView) findViewById(R.id.exp_version);
        mExperimentActivity = (TextView) findViewById(R.id.exp_activity);

        graph = (BarGraphView) findViewById(R.id.inbox_item_graph);
        graph.setNumDays(barGraphShowDay);
    }
    
    public void displayExperimentInformation() {
        mExperimentName.setText(experiment.getNiceName());
        mExperimentOrganization.setText(experiment.getOrganisation());
        mExperimentVersion.setText(" - v" + experiment.getVersion());
    }

    public void displayExperimentActivity() {
        BarGraphView graph = (BarGraphView) findViewById(R.id.inbox_item_graph);
        graph.setNumDays(barGraphShowDay);

        if (!experiment.isRunning()) {
            graph.setDeactived();
        }

        try {
            traces = new ArrayList<Long>();
            final Calendar currentCalendar = new GregorianCalendar();

            final List<Map<String, Object>> stats = new ArrayList<Map<String, Object>>();
             // APISENSE.statistic().readUploadStatistic(experiment.name);
            for (Map<String,Object> stat : stats){
                final String[] uploadTime = stat.get("date").toString().split("-");
                final Calendar uploadCalandar = new GregorianCalendar(
                        Integer.parseInt(uploadTime[0]),
                        Integer.parseInt(uploadTime[1])-1,
                        Integer.parseInt(uploadTime[2]));

                int diffDay =  currentCalendar.get(Calendar.DAY_OF_YEAR) - uploadCalandar.get(Calendar.DAY_OF_YEAR);
                int indexData = (barGraphShowDay - 1) - diffDay;

                if (indexData >= 0)
                    traces.add(Long.parseLong(stat.get("sizeByte").toString()));
            }
            graph.updateGraphWith(traces);
        } catch (Exception ex) {
            Log.i(TAG, "statistics not available for the experiment " + experiment.getName());
        }
    }


    // Action bar update
    private void updateStartMenu(){
        if (!experiment.isRunning()) {
            mStartButton.setTitle(getString(R.string.action_start));
        } else {
            mStartButton.setTitle(getString(R.string.action_stop));
        }
    }

    // Buttons Handlers

    public void doStartStop(MenuItem item) {
        if (experimentStartStopTask == null) {
            experimentStartStopTask = new StartStopExperimentTask(getApplicationContext(), new OnExperimentExecutionStatusChanged());
            experimentStartStopTask.execute(experiment.getName());
        }
    }

    // Callbacks

    private class OnExperimentExecutionStatusChanged implements Callback<Integer> {

        @Override
        public void onCall(Integer response) throws Exception {
            experimentStartStopTask = null;
            String toastMessage = "";
                switch(response) {
                    case StartStopExperimentTask.EXPERIMENT_STARTED:
                        graph.setActived();
                        toastMessage = String.format(getString(R.string.experiment_started), experiment.getNiceName());
                        break;
                    case StartStopExperimentTask.EXPERIMENT_STOPPED:
                        graph.setDeactived();
                        toastMessage = String.format(getString(R.string.experiment_stopped), experiment.getNiceName());
                        break;
                }
                Toast.makeText(getBaseContext(), toastMessage, Toast.LENGTH_SHORT).show();
                graph.updateGraphWith(traces);
                updateStartMenu();
            }

        @Override
        public void onError(Throwable throwable) {
            experimentStartStopTask = null;
        }
    }
}
