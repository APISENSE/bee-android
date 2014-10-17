package com.apisense.bee.ui.activity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;
import com.apisense.bee.BeeApplication;
import com.apisense.bee.R;
import com.apisense.bee.backend.AsyncTasksCallbacks;
import com.apisense.bee.backend.experiment.StartExperimentTask;
import com.apisense.bee.backend.experiment.StopExperimentTask;
import com.apisense.bee.backend.experiment.SubscribeExperimentTask;
import com.apisense.bee.backend.experiment.UnsubscribeExperimentTask;
import com.apisense.bee.ui.entity.ExperimentSerializable;
import com.apisense.bee.widget.BarGraphView;
import fr.inria.bsense.APISENSE;
import fr.inria.bsense.appmodel.Experiment;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Map;

public class ExperimentDetailsActivity extends Activity {

    private static String TAG = "Experiment Details Activity";

    Experiment experiment;

    TextView mExperimentName;
    TextView mExperimentOrganization;
    TextView mExperimentVersion;
    TextView mExperimentActivity;

    MenuItem mSubscribeButton;
    MenuItem mStartButton;

    private BarGraphView graph;
    private int barGraphShowDay = 7;
    private ArrayList<Long> traces;

    // Async Tasks
    private StopExperimentTask experimentStopTask;
    private StartExperimentTask experimentStartTask;
    private SubscribeExperimentTask experimentSubscription;
    private UnsubscribeExperimentTask experimentUnsubscription;


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
        mSubscribeButton = menu.findItem(R.id.detail_action_subscribe);
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
        ExperimentSerializable experimentS  = (ExperimentSerializable) b.getSerializable("experiment");
        try {
            experiment = APISENSE.apisMobileService().getExperiment(experimentS.getName());
        } catch (JSONException e) {
            e.printStackTrace();
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
            Log.i(TAG, "statistics not available for the experiment " + experiment.name);
        }
    }


    // Action bar update

    private void updateSubscriptionMenu() {
        // TODO: Change to API method when available (isSubscribedExperiment)
        if (!StoreActivity.isSubscribedExperiment(experiment)) {
            mSubscribeButton.setTitle(getString(R.string.action_subscribe));
        } else {
            mSubscribeButton.setTitle(getString(R.string.action_unsubscribe));

        }
    }

    private void updateStartMenu(){
        if (!experiment.state) {
            mStartButton.setTitle(getString(R.string.action_start));
        } else {
            mStartButton.setTitle(getString(R.string.action_stop));
        }
    }

    // Buttons Handlers

    public void doStartStop(MenuItem item) {
        Log.d(TAG, "Exp state: " + experiment.state);
        if (! experiment.state) {
            if (experimentStartTask == null) {
                Log.i(TAG, "Starting experiment: " + experiment);
                experimentStartTask = new StartExperimentTask(new OnExperimentStarted());
                experimentStartTask.execute(experiment);
            }
        } else {
            if (experimentStopTask == null) {
                Log.i(TAG, "Stopping experiment: " + experiment);
                experimentStopTask = new StopExperimentTask(new OnExperimentStopped());
                experimentStopTask.execute(experiment);
            }
        }

    }

    public void doSubscribeUnsubscribe(MenuItem item) {
        // TODO: Change to API method when available (isSubscribedExperiment)
        if (StoreActivity.isSubscribedExperiment(experiment)) {
            if (experimentUnsubscription == null) {
                Log.i(TAG, "Asking un-subscription to experiment: " + experiment);
                experimentUnsubscription = new UnsubscribeExperimentTask(new OnExperimentUnsubscribed());
                experimentUnsubscription.execute(experiment);
            }
        } else {
            if (experimentSubscription == null) {
                Log.i(TAG, "Asking subscription to experiment: " + experiment);
                experimentSubscription = new SubscribeExperimentTask(new OnExperimentSubscribed());
                experimentSubscription.execute(experiment);
            }
        }
    }

    // Callbacks

    private class OnExperimentStarted implements AsyncTasksCallbacks {
        @Override
        public void onTaskCompleted(int result, Object response) {
            experimentStartTask = null;
            if (result == BeeApplication.ASYNC_SUCCESS) {
                // User feedback
                graph.setActived();
                graph.updateGraphWith(traces);
                Toast.makeText(getBaseContext(),
                        String.format(getString(R.string.experiment_started), experiment.niceName),
                        Toast.LENGTH_SHORT).show();
                updateStartMenu();
            }
        }

        @Override
        public void onTaskCanceled() {
            experimentStartTask = null;
        }
    }

    private class OnExperimentStopped implements AsyncTasksCallbacks {

        @Override
        public void onTaskCompleted(int result, Object response) {
            experimentStopTask = null;
            if (result == BeeApplication.ASYNC_SUCCESS) {
                graph.setDeactived();
                graph.updateGraphWith(traces);
                // User feedback
                Toast.makeText(getBaseContext(),
                        String.format(getString(R.string.experiment_stopped), experiment.niceName),
                        Toast.LENGTH_SHORT).show();
                updateStartMenu();
            }
        }

        @Override
        public void onTaskCanceled() {
            experimentStopTask = null;
        }
    }

    private class OnExperimentSubscribed implements AsyncTasksCallbacks {

        @Override
        public void onTaskCompleted(int result, Object response) {
            experimentSubscription = null;
            if (result == BeeApplication.ASYNC_SUCCESS) {
                // User feedback
                Toast.makeText(getBaseContext(),
                        String.format(getString(R.string.experiment_subscribed), experiment.niceName),
                        Toast.LENGTH_SHORT).show();
                updateSubscriptionMenu();
            }
        }

        @Override
        public void onTaskCanceled() {
            experimentSubscription = null;
        }
    }

    private class OnExperimentUnsubscribed implements AsyncTasksCallbacks {

        @Override
        public void onTaskCompleted(int result, Object response) {
            experimentUnsubscription = null;
            if (result == BeeApplication.ASYNC_SUCCESS) {
                // User feedback
                Toast.makeText(getBaseContext(),
                        String.format(getString(R.string.experiment_unsubscribed), experiment.niceName),
                        Toast.LENGTH_SHORT).show();
                updateSubscriptionMenu();
            }
        }

        @Override
        public void onTaskCanceled() {
            experimentUnsubscription = null;
        }
    }
}
