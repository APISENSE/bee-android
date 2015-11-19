package com.apisense.bee.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.apisense.bee.Callbacks.OnCropSubscribed;
import com.apisense.bee.Callbacks.OnCropUnsubscribed;
import com.apisense.bee.R;
import com.apisense.bee.games.IncrementalGameAchievement;
import com.apisense.sdk.core.store.Crop;
import com.gc.materialdesign.views.ButtonFloat;

import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Shows detailed informations about a given available Experiment from the store
 */
public class StoreExperimentDetailsActivity extends ExperimentDetailsActivity {
    private static final String TAG = "StoreExpDetailsAct";
    private static final String CREATION_DATE_PATTERN = "MMMM yyyy";
    private ButtonFloat experimentSubBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_experiment_details);
        initExperimentDetailsActivity();
        updateCropStats();
        updateSubscriptionMenu();
    }

    private void updateCropStats() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(CREATION_DATE_PATTERN, Locale.getDefault());
        TextView creationDateView = (TextView) findViewById(R.id.detail_stats_creation_date);
        TextView nbSubscribersView = (TextView) findViewById(R.id.detail_stats_subscribers);
        TextView exportedVolumeView = (TextView) findViewById(R.id.detail_stats_data_volume);
        creationDateView.setText(getString(R.string.crop_stats_creation_date, dateFormat.format(crop.getStatistics().creationDate())));
        nbSubscribersView.setText(getString(R.string.crop_stats_subscribers, crop.getStatistics().numberOfSubscribers));
        exportedVolumeView.setText(getString(R.string.crop_stats_data_volume, crop.getStatistics().size));
    }

    @Override
    public void initializeViews() {
        super.initializeViews();
        this.experimentSubBtn = (ButtonFloat) findViewById(R.id.experimentSubBtn);
        this.experimentSubBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doSubscribeUnsubscribe();
            }
        });

    }

    private void updateSubscriptionMenu() {
        if (apisenseSdk.getCropManager().isInstalled(crop)) {
            experimentSubBtn.setDrawableIcon(getResources().getDrawable(R.drawable.ic_cancel));
        } else {
            experimentSubBtn.setDrawableIcon(getResources().getDrawable(R.drawable.ic_action_new));
        }
    }

    public void doSubscribeUnsubscribe() {
        if (apisenseSdk.getCropManager().isInstalled(crop)) {
            apisenseSdk.getCropManager()
                    .unsubscribe(crop, new StoreDetailsCropUnsubscribed());
        } else {
            apisenseSdk.getCropManager()
                    .subscribe(crop, new StoreDetailsCropSubscribed());
        }
    }

    private class StoreDetailsCropUnsubscribed extends OnCropUnsubscribed {
        public StoreDetailsCropUnsubscribed() {
            super(getBaseContext(), crop.getName());
        }

        @Override
        public void onDone(Crop crop) {
            super.onDone(crop);
            updateSubscriptionMenu();
        }
    }

    private class StoreDetailsCropSubscribed extends OnCropSubscribed {
        public StoreDetailsCropSubscribed() {
            super(getBaseContext(), crop, apisenseSdk);
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
