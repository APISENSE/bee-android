package com.apisense.bee.ui.activity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.apisense.bee.BeeApplication;
import com.apisense.bee.R;
import com.apisense.bee.games.BeeGameActivity;
import com.apisense.sdk.APISENSE;
import com.apisense.sdk.core.APSCallback;
import com.apisense.sdk.core.store.Crop;
import com.gc.materialdesign.views.ButtonFloat;
import com.rollbar.android.Rollbar;

/**
 * Shows detailed informations about a given available Experiment from the store
 */
public class StoreExperimentDetailsActivity extends BeeGameActivity {
    private final String TAG = getClass().getSimpleName();
    TextView mExperimentOrganization;
    TextView mExperimentVersion;
    MenuItem mSubscribeButton;
    private ButtonFloat experimentSubBtn;

    private Crop crop;
    private APISENSE.Sdk apisenseSdk;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_experiment_details);
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
        apisenseSdk = ((BeeApplication) getApplication()).getSdk();

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
        crop = b.getParcelable("crop");
        getSupportActionBar().setTitle(crop.getName());
        mExperimentOrganization.setText(crop.getOwner());
        mExperimentVersion.setText(" - v" + crop.getVersion());
    }

    private void updateSubscriptionMenu() {
        if (apisenseSdk.getCropManager().isSubscribed(crop)) {
            experimentSubBtn.setDrawableIcon(getResources().getDrawable(R.drawable.ic_cancel));
        } else {
            experimentSubBtn.setDrawableIcon(getResources().getDrawable(R.drawable.ic_action_new));
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_back_in, R.anim.slide_back_out);
    }

    public void doSubscribeUnsubscribe() {
        if (apisenseSdk.getCropManager().isSubscribed(crop)) {
            apisenseSdk.getCropManager().unsubscribe(crop, new OnCropUnsubscribed());
        } else {
            apisenseSdk.getCropManager().subscribe(crop, new OnCropSubscribed());
        }
    }

    private class OnCropUnsubscribed implements APSCallback<Void> {
        @Override
        public void onDone(Void response) {
            updateSubscriptionMenu();
            String toastMessage = String.format(getString(R.string.experiment_unsubscribed), crop.getName());
            Toast.makeText(getBaseContext(), toastMessage, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onError(Exception e) {
            Rollbar.reportException(e);
        }
    }

    private class OnCropSubscribed implements APSCallback<Void> {
        @Override
        public void onDone(Void response) {
            updateSubscriptionMenu();
            String toastMessage = String.format(getString(R.string.experiment_subscribed), crop.getName());
            Toast.makeText(getBaseContext(), toastMessage, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onError(Exception e) {
            Rollbar.reportException(e);
        }
    }
}
