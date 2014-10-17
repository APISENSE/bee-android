package com.apisense.bee.ui.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;
import com.apisense.bee.BeeApplication;
import com.apisense.bee.R;
import com.apisense.bee.backend.AsyncTasksCallbacks;
import com.apisense.bee.backend.experiment.SubscribeUnsubscribeExperimentTask;
import com.apisense.bee.ui.entity.ExperimentSerializable;
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
    private SubscribeUnsubscribeExperimentTask experimentChangeSubscriptionStatus;

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
        if (!SubscribeUnsubscribeExperimentTask.isSubscribedExperiment(experiment)) {
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
        if (experimentChangeSubscriptionStatus == null) {
            experimentChangeSubscriptionStatus = new SubscribeUnsubscribeExperimentTask(new OnExperimentSubscriptionChanged());
            experimentChangeSubscriptionStatus.execute(experiment);
        }
    }

    private class OnExperimentSubscriptionChanged implements AsyncTasksCallbacks {

        @Override
        public void onTaskCompleted(int result, Object response) {
            experimentChangeSubscriptionStatus = null;
            String experimentName = experiment.niceName;
            String toastMessage = "";
            if (result == BeeApplication.ASYNC_SUCCESS) {
                switch ((Integer) response){
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
