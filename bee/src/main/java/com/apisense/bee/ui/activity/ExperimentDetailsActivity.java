package com.apisense.bee.ui.activity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.apisense.bee.BeeApplication;
import com.apisense.bee.R;
import com.apisense.bee.games.BeeGameActivity;
import com.apisense.sdk.APISENSE;
import com.apisense.sdk.core.store.Crop;

public abstract class ExperimentDetailsActivity extends BeeGameActivity {
    protected TextView mExperimentOrganization;
    protected TextView mExperimentVersion;

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

    protected abstract void initializeViews();

    public void displayExperimentInformation() {
        getSupportActionBar().setTitle(crop.getName());
        mExperimentOrganization.setText(crop.getOwner());
        mExperimentVersion.setText(" - v" + crop.getVersion());
    }


}
