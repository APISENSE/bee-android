package com.apisense.bee.ui.activity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import com.apisense.bee.BeeApplication;
import com.apisense.bee.R;
import com.apisense.bee.backend.AsyncTasksCallbacks;
import com.apisense.bee.backend.experiment.SubscribeExperimentTask;
import com.apisense.bee.backend.experiment.UnsubscribeExperimentTask;
import com.apisense.bee.ui.entity.ExperimentSerializable;
import com.apisense.bee.widget.BarGraphView;
import fr.inria.bsense.APISENSE;
import fr.inria.bsense.appmodel.Experiment;

/**
 * Shows detailed informations about a given available Experiment from the store
 *
 */
public class StoreExperimentDetailsActivity extends Activity {
    private final String TAG = getClass().getSimpleName();

    private Experiment experiment;

    TextView mExperimentName;
    TextView mExperimentOrganization;
    TextView mExperimentVersion;

     MenuItem mSubscribeButton;

    // Async Tasks
    private SubscribeExperimentTask experimentSubscription;
    private UnsubscribeExperimentTask experimentUnsubscription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_experiment_details);
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);

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
        Bundle b = getIntent().getExtras();
        // TODO : Switch to parcelable when available
        // Experiment expe =  b.getParcelable("experiment");
        // TODO Send directly experiment instead of experimentSerializable when possible
        ExperimentSerializable experimentS  = (ExperimentSerializable) b.getSerializable("experiment");
        experiment = APISENSE.apisServerService().getRemoteExperiment(experimentS.getName());

        mExperimentName.setText(experiment.niceName);
        mExperimentOrganization.setText(experiment.organization);
        mExperimentVersion.setText(" - v" + experiment.version);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.store_experiment_details, menu);

        mSubscribeButton = menu.findItem(R.id.store_detail_action_subscribe);
        updateSubscriptionMenu();
        return true;
    }

    private void updateSubscriptionMenu(){
        // TODO: Change to API method when available (isSubscribedExperiment)
        if (!StoreActivity.isSubscribedExperiment(experiment)) {
            mSubscribeButton.setTitle(getString(R.string.action_subscribe));
        } else {
            mSubscribeButton.setTitle(getString(R.string.action_unsubscribe));

        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_back_in, R.anim.slide_back_out);
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

    private class OnExperimentSubscribed implements AsyncTasksCallbacks {

        @Override
        public void onTaskCompleted(int result, Object response) {
            experimentSubscription = null;
            if (result == BeeApplication.ASYNC_SUCCESS) {
                // User feedback
                Toast.makeText(getBaseContext(),
                               String.format(getString(R.string.experiment_subscribed), experiment.name),
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
                        String.format(getString(R.string.experiment_unsubscribed), experiment.name),
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
