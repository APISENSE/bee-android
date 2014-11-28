package com.apisense.bee.ui.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;
import com.apisense.android.api.APS;
import com.apisense.android.api.APSLocalCrop;
import com.apisense.api.APSLogEvent;
import com.apisense.api.Callable;
import com.apisense.api.Callback;
import com.apisense.bee.R;
import com.apisense.bee.backend.experiment.*;
import com.apisense.bee.widget.BarGraphView;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
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
    private ArrayList<Long> traces = new ArrayList<Long>();;

    // Async Tasks
    private StartStopExperimentTask experimentStartStopTask;
    private BroadcastReceiver eventReceiver;

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
    protected void onStart() {
        super.onStart();
        displayExperimentInformation();
        displayExperimentActivity();

        eventReceiver = APS.registerToAPSEvent(this, new Callable<Void, APSLogEvent>() {
            @Override
            public Void call(APSLogEvent apsLogEvent) throws Exception {
                Log.i(TAG, "Got event (" + apsLogEvent.getClass().getSimpleName() + ") for crop: " + apsLogEvent.cropName);
                updateCrop();
                if (apsLogEvent instanceof APSLogEvent.StartCrop) {
                    showAsActivated();
                } else if (apsLogEvent instanceof APSLogEvent.StopCrop) {
                    showAsDeactivated();
                }
                return null;
            }

            private void updateCrop() {
                try {
                    experiment = APS.getCropDescription(getApplicationContext(), experiment.getName());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        APS.unregisterToAPSEvent(this, eventReceiver);
    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(R.anim.slide_back_in, R.anim.slide_back_out);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_back_in, R.anim.slide_back_out);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
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
        updateGraph();
    }

    private void updateGraph(){
        final Calendar currentCalendar = new GregorianCalendar();

        final List<Map<String, Object>> stats = new ArrayList<Map<String, Object>>();
        // TODO: Add fetch Statistic method call
//        APISENSE.statistic().readUploadStatistic(experiment.name);
        for (Map<String,Object> stat : stats){
            final String[] uploadTime = stat.get("date").toString().split("-");
            final Calendar uploadCalendar = new GregorianCalendar(
                    Integer.parseInt(uploadTime[0]),
                    Integer.parseInt(uploadTime[1])-1,
                    Integer.parseInt(uploadTime[2]));

            int diffDay =  currentCalendar.get(Calendar.DAY_OF_YEAR) - uploadCalendar.get(Calendar.DAY_OF_YEAR);
            int indexData = (barGraphShowDay - 1) - diffDay;

            if (indexData >= 0)
                traces.add(Long.parseLong(stat.get("sizeByte").toString()));
        }
        graph.updateGraphWith(traces);
        Log.i(TAG, "statistics not available for the experiment " + experiment.getName());
    }

    // Action bar update
    private void updateStartMenu(){
        if (!experiment.isRunning()) {
            mStartButton.setTitle(getString(R.string.action_start));
        } else {
            mStartButton.setTitle(getString(R.string.action_stop));
        }
    }

    private void showAsActivated(){
        mStartButton.setTitle(getString(R.string.action_stop));
        graph.setActived();
        updateGraph();
    }

    private void showAsDeactivated() {
        mStartButton.setTitle(getString(R.string.action_start));
        graph.setDeactived();
        updateGraph();
    }

    // Callbacks

    private class OnExperimentExecutionStatusChanged implements Callback<Integer> {

        @Override
        public void onCall(Integer response) throws Exception {
            experimentStartStopTask = null;
            String toastMessage = "";
            switch (response) {
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
            throwable.printStackTrace();
        }

    }

    // Buttons Handlers

    public void doStartStop(MenuItem item) {
        experimentStartStopTask = new StartStopExperimentTask(getApplicationContext());
        experimentStartStopTask.execute(experiment.getName());
    }
}
