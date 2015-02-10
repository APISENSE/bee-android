package com.apisense.bee.ui.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.apisense.bee.BeeApplication;
import com.apisense.bee.R;
import com.apisense.bee.backend.AsyncTasksCallbacks;
import com.apisense.bee.backend.experiment.StartStopExperimentTask;
import com.apisense.bee.backend.experiment.SubscribeUnsubscribeExperimentTask;
import com.apisense.bee.ui.entity.ExperimentSerializable;
import com.apisense.bee.widget.BarGraphView;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.GoogleMap;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Map;

import fr.inria.apislog.APISLog;
import fr.inria.bsense.APISENSE;
import fr.inria.bsense.appmodel.Experiment;

public class ExperimentDetailsActivity extends Activity {

    private static String TAG = "Experiment Details Activity";

    private Experiment experiment;

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

        initializeViews();
        displayExperimentInformation();
        displayExperimentActivity();
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
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.experiment_details, menu);
        // mSubscribeButton = menu.findItem(R.id.detail_action_subscribe);
        mStartButton = menu.findItem(R.id.detail_action_start);

        updateStartMenu();
        updateSubscriptionMenu();
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
        Bundle b = getIntent().getExtras();
        // TODO : Switch to parcelable when available
        // Experiment expe =  b.getParcelable("experiment");

        // TODO Send directly experiment instead of experimentSerializable when possible
        ExperimentSerializable experimentS = (ExperimentSerializable) b.getSerializable("experiment");
        try {
            experiment = APISENSE.apisMobileService().getExperiment(experimentS.getName());
        } catch (JSONException e) {
            e.printStackTrace();
            APISLog.send(e, APISLog.ERROR);
        }

        mExperimentName.setText(experiment.niceName);
        mExperimentOrganization.setText(experiment.organization);
        mExperimentVersion.setText(" - v" + experiment.version);
    }

    public void displayExperimentActivity() {
        BarGraphView graph = (BarGraphView) findViewById(R.id.inbox_item_graph);
        graph.setNumDays(barGraphShowDay);

        if (!experiment.state)
            graph.setDeactived();

        try {
            traces = new ArrayList<Long>();
            final Calendar currentCalendar = new GregorianCalendar();

            final Map<String, Object>[] stats = APISENSE.statistic().readUploadStatistic(experiment.name);
            for (Map<String, Object> stat : stats) {
                final String[] uploadTime = stat.get("date").toString().split("-");
                final Calendar uploadCalandar = new GregorianCalendar(
                        Integer.parseInt(uploadTime[0]),
                        Integer.parseInt(uploadTime[1]) - 1,
                        Integer.parseInt(uploadTime[2]));

                int diffDay = currentCalendar.get(Calendar.DAY_OF_YEAR) - uploadCalandar.get(Calendar.DAY_OF_YEAR);
                int indexData = (barGraphShowDay - 1) - diffDay;

                if (indexData >= 0)
                    traces.add(Long.parseLong(stat.get("sizeByte").toString()));
            }
            graph.updateGraphWith(traces);
        } catch (Exception ex) {
            Log.i(TAG, "statistics not available for the experiment " + experiment.name);
            APISLog.send(ex, APISLog.WARNING);
        }
    }


    // Action bar update

    private void updateSubscriptionMenu() {
        // TODO: Change to API method when available (isSubscribedExperiment)
        /* if (!SubscribeUnsubscribeExperimentTask.isSubscribedExperiment(experiment)) {
            mSubscribeButton.setTitle(getString(R.string.action_subscribe));
        } else {
            mSubscribeButton.setTitle(getString(R.string.action_unsubscribe));

        } */
    }

    private void updateStartMenu() {
        if (!experiment.state) {
            mStartButton.setTitle(getString(R.string.action_start));
        } else {
            mStartButton.setTitle(getString(R.string.action_stop));
        }
    }

    // Buttons Handlers

    public void doStartStop(MenuItem item) {
        if (experimentStartStopTask == null) {
            experimentStartStopTask = new StartStopExperimentTask(APISENSE.apisense(), new OnExperimentExecutionStatusChanged());
            experimentStartStopTask.execute(experiment);
        }
    }

    public void doSubscribeUnsubscribe(MenuItem item) {
        if (experimentChangeSubscriptionStatus == null) {
            experimentChangeSubscriptionStatus = new SubscribeUnsubscribeExperimentTask(APISENSE.apisense(), new OnExperimentSubscriptionChanged());
            experimentChangeSubscriptionStatus.execute(experiment);
        }
    }

    // Callbacks

    private class OnExperimentExecutionStatusChanged implements AsyncTasksCallbacks {
        @Override
        public void onTaskCompleted(int result, Object response) {
            experimentStartStopTask = null;
            String toastMessage = "";
            if (result == BeeApplication.ASYNC_SUCCESS) {
                switch ((Integer) response) {
                    case StartStopExperimentTask.EXPERIMENT_STARTED:
                        graph.setActived();
                        toastMessage = String.format(getString(R.string.experiment_started), experiment.niceName);
                        break;
                    case StartStopExperimentTask.EXPERIMENT_STOPPED:
                        graph.setDeactived();
                        toastMessage = String.format(getString(R.string.experiment_stopped), experiment.niceName);
                        break;
                }
                Toast.makeText(getBaseContext(), toastMessage, Toast.LENGTH_SHORT).show();
                graph.updateGraphWith(traces);
                updateStartMenu();
            }
        }

        @Override
        public void onTaskCanceled() {
            experimentStartStopTask = null;
        }
    }

    private class OnExperimentSubscriptionChanged implements AsyncTasksCallbacks {

        @Override
        public void onTaskCompleted(int result, Object response) {
            experimentChangeSubscriptionStatus = null;
            String experimentName = experiment.niceName;
            String toastMessage = "";
            if (result == BeeApplication.ASYNC_SUCCESS) {
                switch ((Integer) response) {
                    case SubscribeUnsubscribeExperimentTask.EXPERIMENT_SUBSCRIBED:
                        toastMessage = String.format(getString(R.string.experiment_subscribed), experimentName);
                        updateSubscriptionMenu();
                        break;
                    case SubscribeUnsubscribeExperimentTask.EXPERIMENT_UNSUBSCRIBED:
                        toastMessage = String.format(getString(R.string.experiment_unsubscribed), experimentName);
                        updateSubscriptionMenu();
                        break;
                }
                // User feedback
                Toast.makeText(getBaseContext(), toastMessage, Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onTaskCanceled() {
            experimentChangeSubscriptionStatus = null;
        }
    }
}
