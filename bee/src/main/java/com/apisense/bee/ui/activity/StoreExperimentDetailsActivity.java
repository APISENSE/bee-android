package com.apisense.bee.ui.activity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.apisense.bee.BeeApplication;
import com.apisense.bee.R;
import com.apisense.bee.backend.AsyncTasksCallbacks;
import com.apisense.bee.backend.experiment.SubscribeUnsubscribeExperimentTask;
import com.apisense.bee.games.BeeGameActivity;
import com.apisense.bee.games.BeeGameManager;
import com.apisense.bee.games.event.MissionSubscribeEvent;
import com.apisense.bee.ui.entity.ExperimentSerializable;
import com.gc.materialdesign.views.ButtonFloat;

import fr.inria.bsense.APISENSE;
import fr.inria.bsense.appmodel.Experiment;

/**
 * Shows detailed informations about a given available Experiment from the store
 */
public class StoreExperimentDetailsActivity extends BeeGameActivity {
    private final String TAG = getClass().getSimpleName();
    TextView mExperimentOrganization;
    TextView mExperimentVersion;
    MenuItem mSubscribeButton;
    private Experiment experiment;
    // Async Tasks
    private SubscribeUnsubscribeExperimentTask experimentChangeSubscriptionStatus;

    private ButtonFloat experimentSubBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_experiment_details);
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);

        Toolbar toolbar = (Toolbar) findViewById(R.id.material_toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_action_back);
        setSupportActionBar(toolbar);

        initializeViews();
        displayExperimentInformation();
        updateSubscriptionMenu();
    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(R.anim.slide_back_in, R.anim.slide_back_out);
    }

    public void initializeViews() {
        mExperimentOrganization = (TextView) findViewById(R.id.store_detail_exp_organization);
        mExperimentVersion = (TextView) findViewById(R.id.store_detail_exp_version);
        this.experimentSubBtn = (ButtonFloat) findViewById(R.id.experimentSubBtn);
        this.experimentSubBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doSubscribeUnsubscribe();
            }
        });

    }

    public void displayExperimentInformation() {
        Bundle b = getIntent().getExtras();
        // TODO : Switch to parcelable when available
        // Experiment expe =  b.getParcelable("experiment");
        // TODO Send directly experiment instead of experimentSerializable when possible
        ExperimentSerializable experimentS = (ExperimentSerializable) b.getSerializable("experiment");
        experiment = APISENSE.apisServerService().getRemoteExperiment(experimentS.getName());

        getSupportActionBar().setTitle(experiment.niceName);
        mExperimentOrganization.setText(experiment.organization);
        mExperimentVersion.setText(" - v" + experiment.version);
    }

    private void updateSubscriptionMenu() {
        // TODO: Change to API method when available (isSubscribedExperiment)
        if (!SubscribeUnsubscribeExperimentTask.isSubscribedExperiment(experiment)) {
            experimentSubBtn.setDrawableIcon(getDrawable(R.drawable.ic_action_new));
        } else {
            experimentSubBtn.setDrawableIcon(getDrawable(R.drawable.ic_cancel));

        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_back_in, R.anim.slide_back_out);
    }

    public void doSubscribeUnsubscribe() {
        if (experimentChangeSubscriptionStatus == null) {
            experimentChangeSubscriptionStatus = new SubscribeUnsubscribeExperimentTask(APISENSE.apisense(), new OnExperimentSubscriptionChanged());
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
                switch ((Integer) response) {
                    case SubscribeUnsubscribeExperimentTask.EXPERIMENT_SUBSCRIBED:
                        toastMessage = String.format(getString(R.string.experiment_subscribed), experimentName);
                        BeeGameManager.getInstance().fireGameEventPerformed(new MissionSubscribeEvent(StoreExperimentDetailsActivity.this));

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
