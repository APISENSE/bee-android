package com.apisense.bee.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.apisense.bee.Callbacks.OnCropSubscribed;
import com.apisense.bee.Callbacks.OnCropUnsubscribed;
import com.apisense.bee.R;
import com.apisense.sdk.core.store.Crop;
import com.gc.materialdesign.views.ButtonFloat;

/**
 * Shows detailed informations about a given available Experiment from the store
 */
public class StoreExperimentDetailsActivity extends ExperimentDetailsActivity {
    private final String TAG = "StoreExpDetailsAct";
    private ButtonFloat experimentSubBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_experiment_details);
        initExperimentDetailsActivity();
        updateSubscriptionMenu();
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
        if (apisenseSdk.getCropManager().isSubscribed(crop)) {
            experimentSubBtn.setDrawableIcon(getResources().getDrawable(R.drawable.ic_cancel));
        } else {
            experimentSubBtn.setDrawableIcon(getResources().getDrawable(R.drawable.ic_action_new));
        }
    }

    public void doSubscribeUnsubscribe() {
        if (apisenseSdk.getCropManager().isSubscribed(crop)) {
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
        }
    }
}
