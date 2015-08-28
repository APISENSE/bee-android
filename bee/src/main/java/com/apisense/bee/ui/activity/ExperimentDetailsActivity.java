package com.apisense.bee.ui.activity;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.apisense.bee.BeeApplication;
import com.apisense.bee.R;
import com.apisense.bee.games.BeeGameActivity;
import com.apisense.sdk.APISENSE;
import com.apisense.sdk.core.store.Crop;

import java.util.List;

/**
 * Common class for Activities showing details of an experiment
 * <p/>
 * Warning: The layout of the Activity extending this class must
 * include the layout "common_experiment_details", i.e. contain the line:
 * <include layout="@layout/common_experiment_details"/>
 */
public abstract class ExperimentDetailsActivity extends BeeGameActivity {
    protected TextView nameView;
    protected TextView organizationView;
    protected TextView versionView;
    protected TextView descriptionView;
    protected TextView stingListView;

    protected Crop crop;
    protected APISENSE.Sdk apisenseSdk;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
        apisenseSdk = ((BeeApplication) getApplication()).getSdk();
    }

    protected void initExperimentDetailsActivity() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.material_toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_action_back);
        setSupportActionBar(toolbar);

        Bundle b = getIntent().getExtras();
        crop = b.getParcelable("crop");

        initializeViews();
        displayExperimentInformation();
    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(R.anim.slide_back_in, R.anim.slide_back_out);
    }

    protected void initializeViews() {
        nameView = (TextView) findViewById(R.id.detail_exp_name);
        versionView = (TextView) findViewById(R.id.detail_exp_version);
        organizationView = (TextView) findViewById(R.id.detail_exp_organization);
        stingListView = (TextView) findViewById(R.id.detail_exp_used_stings);
        descriptionView = (TextView) findViewById(R.id.detail_exp_description);
    }

    public void displayExperimentInformation() {
        getSupportActionBar().setTitle(crop.getName());
        nameView.setText(getString(R.string.exp_details_name, crop.getName()));
        organizationView.setText(getString(R.string.exp_details_organization, crop.getOwner()));
        versionView.setText(getString(R.string.exp_details_version, crop.getVersion()));
        descriptionView.setText(getString(R.string.exp_details_description, crop.getShortDescription()));
        stingListView.setText("Sensors: " + crop.getUsedStings().toString());
    }


}
