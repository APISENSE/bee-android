package com.apisense.bee.ui.fragment;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.apisense.bee.R;
import com.apisense.bee.callbacks.BeeAPSCallback;
import com.apisense.bee.callbacks.OnCropSubscribed;
import com.apisense.bee.callbacks.OnCropUnsubscribed;
import com.apisense.bee.games.IncrementalGameAchievement;
import io.apisense.sdk.core.store.Crop;
import io.apisense.sdk.core.store.CropGlobalStatistics;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class StoreDetailsFragment extends CommonDetailsFragment {

    @BindView(R.id.experimentSubBtn) FloatingActionButton mSubButton;
    @BindView(R.id.detail_stats_subscribers)  TextView mSubscribers;

    MenuItem updateButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.fragment_store_details, container, false);
        homeActivity.getSupportActionBar().setTitle(R.string.title_activity_store_experiment_details);

        unbinder = ButterKnife.bind(this, view);
        updateSubscriptionMenu();

        return view;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.detail_action_update:
                doUpdate();
                break;
        }
        return false;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.store_experiment_details, menu);
        super.onCreateOptionsMenu(menu, inflater);
        updateButton = menu.findItem(R.id.detail_action_update);
        setUpdateButtonVisible(apisenseSdk.getCropManager().isInstalled(crop));
    }

    // OnClick

    @OnClick(R.id.experimentSubBtn)
    void doSubscribeUnsubscribe() {
        if (apisenseSdk.getCropManager().isInstalled(crop)) {
            apisenseSdk.getCropManager().unsubscribe(crop, new StoreDetailsCropUnsubscribed());
        } else {
            apisenseSdk.getCropManager().subscribe(crop, new StoreDetailsCropSubscribed(this));
        }
    }

    // Usage

    private void setUpdateButtonVisible(boolean value) {
        updateButton.setVisible(value);
    }

    protected void displayExperimentInformation() {
        super.displayExperimentInformation();
        apisenseSdk.getStatisticsManager().findGlobalStatistics(crop.getSlug(), new BeeAPSCallback<CropGlobalStatistics>(getActivity()) {
            @Override
            public void onDone(CropGlobalStatistics stats) {
                    mSubscribers.setText(getString(R.string.crop_stats_subscribers, stats.numberOfSubscribers));
            }
        });
    }

    private class StoreDetailsCropUnsubscribed extends OnCropUnsubscribed {
        public StoreDetailsCropUnsubscribed() {
            super(getActivity(), crop.getName());
        }

        @Override
        public void onDone(Crop crop) {
            super.onDone(crop);
            updateSubscriptionMenu();
            setUpdateButtonVisible(false);
        }
    }

    private void updateSubscriptionMenu() {
        if (apisenseSdk.getCropManager().isInstalled(crop)) {
            mSubButton.setImageResource(R.drawable.ic_trash);
        } else {
            mSubButton.setImageResource(R.drawable.ic_action_new);
        }
    }

    private class StoreDetailsCropSubscribed extends OnCropSubscribed {
        private Fragment mFragment;

        public StoreDetailsCropSubscribed(Fragment fragment) {
            super(getActivity(), crop, cropPermissionHandler);
            mFragment = fragment;
        }

        @Override
        public void onDone(Crop crop) {
            super.onDone(crop);
            updateSubscriptionMenu();
            setUpdateButtonVisible(true);
            // Increment every subscription related achievements
            new IncrementalGameAchievement(getString(R.string.achievement_bronze_wings)).increment(mFragment);
            new IncrementalGameAchievement(getString(R.string.achievement_silver_wings)).increment(mFragment);
            new IncrementalGameAchievement(getString(R.string.achievement_gold_wings)).increment(mFragment);
            new IncrementalGameAchievement(getString(R.string.achievement_crystal_wings)).increment(mFragment);
        }
    }
}
