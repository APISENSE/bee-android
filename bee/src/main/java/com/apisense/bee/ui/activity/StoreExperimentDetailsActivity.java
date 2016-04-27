package com.apisense.bee.ui.activity;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.apisense.bee.R;
import com.apisense.bee.callbacks.BeeAPSCallback;
import com.apisense.bee.callbacks.OnCropSubscribed;
import com.apisense.bee.callbacks.OnCropUnsubscribed;
import com.apisense.bee.games.IncrementalGameAchievement;
import com.apisense.sdk.core.store.Crop;

import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Shows detailed informations about a given available Experiment from the store
 */
public class StoreExperimentDetailsActivity extends ExperimentDetailsActivity {
    private static final String TAG = "StoreExpDetailsAct";
    private static final String CREATION_DATE_PATTERN = "MMMM yyyy";
    private FloatingActionButton experimentSubBtn;

    MenuItem updateButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_experiment_details);
        initExperimentDetailsActivity();
        updateCropStats();
        updateSubscriptionMenu();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.store_experiment_details, menu);

        updateButton = menu.findItem(R.id.detail_action_update);
        updateButton.setVisible(apisenseSdk.getCropManager().isInstalled(crop));

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case R.id.detail_action_update:
                apisenseSdk.getCropManager().update(crop.getLocation(), new BeeAPSCallback<Crop>(this) {
                    @Override
                    public void onDone(Crop crop) {
                        Toast.makeText(
                                StoreExperimentDetailsActivity.this,
                                getString(R.string.experiment_updated, crop.getName()),
                                Snackbar.LENGTH_SHORT
                        ).show();
                    }
                });
                break;
        }
        return false;
    }

    private void updateCropStats() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(CREATION_DATE_PATTERN, Locale.getDefault());
        TextView creationDateView = (TextView) findViewById(R.id.detail_stats_creation_date);
        TextView nbSubscribersView = (TextView) findViewById(R.id.detail_stats_subscribers);
        TextView exportedVolumeView = (TextView) findViewById(R.id.detail_stats_data_volume);
        if (creationDateView != null)
            creationDateView.setText(getString(R.string.crop_stats_creation_date, dateFormat.format(crop.getStatistics().creationDate())));
        if (nbSubscribersView != null)
            nbSubscribersView.setText(getString(R.string.crop_stats_subscribers, crop.getStatistics().numberOfSubscribers));
        if (exportedVolumeView != null)
            exportedVolumeView.setText(getString(R.string.crop_stats_data_volume, crop.getStatistics().size));
    }

    @Override
    public void initializeViews() {
        super.initializeViews();
        this.experimentSubBtn = (FloatingActionButton) findViewById(R.id.experimentSubBtn);
        this.experimentSubBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doSubscribeUnsubscribe();
            }
        });

    }

    private void updateSubscriptionMenu() {
        if (apisenseSdk.getCropManager().isInstalled(crop)) {
            experimentSubBtn.setImageResource(R.drawable.ic_cancel);
//            experimentSubBtn.setSrc(getResources().getDrawable(R.drawable.ic_cancel));
        } else {
            experimentSubBtn.setImageResource(R.drawable.ic_action_new);
        }
    }

    public void doSubscribeUnsubscribe() {
        if (apisenseSdk.getCropManager().isInstalled(crop)) {
            apisenseSdk.getCropManager().unsubscribe(crop, new StoreDetailsCropUnsubscribed());
        } else {
            apisenseSdk.getCropManager().subscribe(crop, new StoreDetailsCropSubscribed());
        }
    }

    private class StoreDetailsCropUnsubscribed extends OnCropUnsubscribed {
        public StoreDetailsCropUnsubscribed() {
            super(StoreExperimentDetailsActivity.this, crop.getName());
        }

        @Override
        public void onDone(Crop crop) {
            super.onDone(crop);
            updateSubscriptionMenu();
        }
    }

    private class StoreDetailsCropSubscribed extends OnCropSubscribed {
        public StoreDetailsCropSubscribed() {
            super(StoreExperimentDetailsActivity.this, crop, cropPermissionHandler);
        }

        @Override
        public void onDone(Crop crop) {
            super.onDone(crop);
            updateSubscriptionMenu();
            // Increment every subscription related achievements
            new IncrementalGameAchievement(getString(R.string.achievement_bronze_wings))
                    .increment(StoreExperimentDetailsActivity.this);
            new IncrementalGameAchievement(getString(R.string.achievement_silver_wings))
                    .increment(StoreExperimentDetailsActivity.this);
            new IncrementalGameAchievement(getString(R.string.achievement_gold_wings))
                    .increment(StoreExperimentDetailsActivity.this);
            new IncrementalGameAchievement(getString(R.string.achievement_crystal_wings))
                    .increment(StoreExperimentDetailsActivity.this);
        }
    }
}
