package com.apisense.bee.ui.activity;


import android.content.BroadcastReceiver;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import com.apisense.android.ui.feedz.FeedzManagerFragment;
import com.apisense.core.api.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import com.apisense.android.api.APS;
import com.apisense.android.api.APSLocalCrop;
import com.apisense.bee.R;
import com.apisense.bee.backend.experiment.*;
import com.apisense.bee.widget.BarGraphView;
import com.apisense.core.api.APSLogEvent;
import com.apisense.core.api.Callable;


import java.util.*;

public class ExperimentDetailsActivity extends FragmentActivity {

    private static String TAG = "Experiment Details Activity";

    private APSLocalCrop experiment;

    private TextView mExperimentName;
    private TextView mExperimentOrganization;
    private TextView mExperimentVersion;
    private TextView mExperimentActivity;

    // private MenuItem mSubscribeButton;
    private MenuItem mStartButton;

    private BarGraphView graph;
    private int barGraphShowDay = 7;
    private ArrayList<Long> traces = new ArrayList<>();

    // Async Tasks
    private StartStopExperimentTask experimentStartStopTask;
    private BroadcastReceiver eventReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_experiment_details);
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);

        try {
            experiment = APS.getCropDescription(getBaseContext(),getIntent().getStringExtra("experiment"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        initializeViews();

        if (savedInstanceState == null) {

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.activity_experiment_detail_feedz_fragment,
                            FeedzManagerFragment.newInstance(experiment.getName()))
                    .commit();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        displayExperimentInformation();
        displayExperimentActivity();

        eventReceiver = APS.registerToAPSEvent(this, new Callable<Void, APSLogEvent>() {
            @Override
            public Void call(APSLogEvent apsLogEvent) throws Exception {
                Log.i(TAG, "Got event (" + apsLogEvent + ") for crop: " + apsLogEvent.cropName);
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
        if(experiment.isRunning()) {
            showAsActivated();
        } else {
            showAsDeactivated();
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

    // Buttons Handlers

    public void doStartStop(MenuItem item) {
        experimentStartStopTask = new StartStopExperimentTask(getApplicationContext());
        experimentStartStopTask.execute(experiment.getName());
    }
}
