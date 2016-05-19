package com.apisense.bee.ui.activity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.apisense.bee.BeeApplication;
import com.apisense.bee.R;
import com.apisense.bee.callbacks.BeeAPSCallback;
import com.apisense.bee.callbacks.OnCropStarted;
import com.apisense.bee.callbacks.OnCropStopped;
import com.apisense.bee.games.BeeGameActivity;
import com.apisense.bee.games.BeePlayer;
import com.apisense.bee.ui.adapter.SubscribedExperimentsListAdapter;
import com.apisense.bee.utils.CropPermissionHandler;
import com.apisense.bee.widget.ApisenseTextView;
import com.apisense.sdk.APISENSE;
import com.apisense.sdk.core.store.Crop;
import com.google.android.gms.common.images.ImageManager;
import com.google.android.gms.games.achievement.Achievement;

import java.util.ArrayList;
import java.util.List;


public class HomeActivity extends BeeGameActivity {
    private final String TAG = getClass().getSimpleName();
    // Data
    protected SubscribedExperimentsListAdapter experimentsAdapter;

    // Gamification
    private Toolbar toolbar;
    private LinearLayout gamificationPanel;
    private LinearLayout noGamificationPanel;
    private ApisenseTextView achievementsCounts;

    private APISENSE.Sdk apisenseSdk;
    private CropPermissionHandler lastCropPermissionHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        apisenseSdk = ((BeeApplication) getApplication()).getSdk();

        toolbar = (Toolbar) findViewById(R.id.material_toolbar);
        if (toolbar != null) {
            toolbar.setLogo(R.drawable.ic_launcher_bee);
            setSupportActionBar(toolbar);
        }

        // Check visibility of gamification panels
        gamificationPanel = (LinearLayout) findViewById(R.id.gamification_panel);
        noGamificationPanel = (LinearLayout) findViewById(R.id.no_gamification_panel);
        achievementsCounts = (ApisenseTextView) findViewById(R.id.home_game_achievements);

        // Set installed experiment list behavior
        experimentsAdapter = new SubscribedExperimentsListAdapter(getBaseContext(),
                R.layout.list_item_home_experiment,
                new ArrayList<Crop>());
        ListView subscribedCollects = (ListView) findViewById(R.id.home_experiment_lists);
        if (subscribedCollects != null) {
            subscribedCollects.setEmptyView(findViewById(R.id.home_empty_list));
            subscribedCollects.setAdapter(experimentsAdapter);
            subscribedCollects.setOnItemLongClickListener(new StartStopExperimentListener());
            subscribedCollects.setOnItemClickListener(new OpenExperimentDetailsListener());
        }

        apisenseSdk.getCropManager().synchroniseSubscriptions(new OnCropModifiedOnStartup());
        apisenseSdk.getCropManager().restartActive(new OnCropModifiedOnStartup());

        refreshGPGData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        retrieveActiveExperiments();
    }

    @Override
    public void onSignInSucceeded() {
        super.onSignInSucceeded();
        noGamificationPanel.setVisibility(View.GONE);
        gamificationPanel.setVisibility(View.VISIBLE);

        refreshGPGData();
    }

    @Override
    public void onSignInFailed() {
        noGamificationPanel.setVisibility(View.VISIBLE);
        gamificationPanel.setVisibility(View.GONE);
        Log.w(TAG, "Error on GPG signin: " + String.valueOf(getSignInError()));
    }

    private void refreshGPGData() {
        refreshPlayGamesData(new Pending<BeePlayer>() {
            @Override
            public void onFetched(BeePlayer player) {
                if (toolbar != null) {
                    ImageManager.create(HomeActivity.this).loadImage(new ImageManager.OnImageLoadedListener() {
                        @Override
                        public void onImageLoaded(Uri uri, Drawable drawable, boolean b) {
                            toolbar.setLogo(drawable);
                        }
                    }, player.userImage);
                    toolbar.setLogoDescription(player.username);
                    toolbar.setTitle(player.username);
                }
            }
        });

        refreshAchievements(new Pending<List<Achievement>>() {
            @Override
            public void onFetched(List<Achievement> achievements) {
                achievementsCounts.setText(String.valueOf(countUnlocked(achievements)));
            }
        });
    }


    private class OnCropModifiedOnStartup extends BeeAPSCallback<Crop> {
        public OnCropModifiedOnStartup() {
            super(HomeActivity.this);
        }

        @Override
        public void onDone(Crop crop) {
            Log.d(TAG, "Crop" + crop.getName() + "started back");
            retrieveActiveExperiments();
            experimentsAdapter.notifyDataSetChanged();
        }
    }

    public void onGamificationPannelClicked(View v) {
        switch (v.getId()) {
            case R.id.no_gamification_panel:
                beginUserInitiatedSignIn();
                break;
            case R.id.gamification_panel:
                Intent intent = new Intent(getApplicationContext(), RewardActivity.class);
                startActivity(intent);
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                doLaunchSettings();
                break;
        }
        return true;
    }

    public void setExperiments(List<Crop> experiments) {
        this.experimentsAdapter.setDataSet(experiments);
    }

    private void retrieveActiveExperiments() {
        apisenseSdk.getCropManager().getSubscriptions(new ExperimentListRetrievedCallback());
    }

    public void doLaunchSettings() {
        Intent settingsIntent = new Intent(this, SettingsActivity.class);
        startActivity(settingsIntent);
    }

    public void doGoToStore(View storeButton) {
        Intent storeIntent = new Intent(this, StoreActivity.class);
        startActivity(storeIntent);
    }

    public class ExperimentListRetrievedCallback extends BeeAPSCallback<List<Crop>> {
        public ExperimentListRetrievedCallback() {
            super(HomeActivity.this);
        }

        @Override
        public void onDone(List<Crop> response) {
            Log.i(TAG, "number of Active Experiments: " + response.size());

            // Updating listView
            setExperiments(response);
            experimentsAdapter.notifyDataSetChanged();
        }
    }

    private class OpenExperimentDetailsListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Intent intent = new Intent(view.getContext(), HomeExperimentDetailsActivity.class);
            Crop crop = (Crop) parent.getAdapter().getItem(position);

            Bundle bundle = new Bundle();
            bundle.putParcelable("crop", crop);
            intent.putExtras(bundle);
            startActivity(intent);
        }
    }

    private class StartStopExperimentListener implements AdapterView.OnItemLongClickListener {
        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
            final Crop crop = (Crop) parent.getAdapter().getItem(position);
            if (apisenseSdk.getCropManager().isRunning(crop)) {
                apisenseSdk.getCropManager().stop(crop, new OnCropStopped(getBaseContext()) {
                    @Override
                    public void onDone(Crop crop) {
                        super.onDone(crop);
                        experimentsAdapter.notifyDataSetChanged();
                    }
                });
            } else {
                lastCropPermissionHandler = new CropPermissionHandler(HomeActivity.this, crop,
                        new OnCropStarted(getBaseContext()) {
                            @Override
                            public void onDone(Crop crop) {
                                super.onDone(crop);
                                experimentsAdapter.notifyDataSetChanged();
                            }
                        });
                lastCropPermissionHandler.startOrRequestPermissions();
            }
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (lastCropPermissionHandler != null) {
            lastCropPermissionHandler.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
}
