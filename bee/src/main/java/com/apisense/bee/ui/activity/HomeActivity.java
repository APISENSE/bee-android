package com.apisense.bee.ui.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.apisense.bee.BeeApplication;
import com.apisense.bee.Callbacks.OnCropStarted;
import com.apisense.bee.Callbacks.OnCropStopped;
import com.apisense.bee.R;
import com.apisense.bee.games.BeeGameActivity;
import com.apisense.bee.games.BeeGameManager;
import com.apisense.bee.games.event.OnGameDataLoadedEvent;
import com.apisense.bee.ui.adapter.SubscribedExperimentsListAdapter;
import com.apisense.bee.widget.ApisenseTextView;
import com.apisense.sdk.APISENSE;
import com.apisense.sdk.core.APSCallback;
import com.apisense.sdk.core.store.Crop;

import java.util.ArrayList;
import java.util.List;


public class HomeActivity extends BeeGameActivity implements View.OnClickListener {
    private final String TAG = getClass().getSimpleName();
    // Data
    protected SubscribedExperimentsListAdapter experimentsAdapter;

    // Gamification
    private Toolbar toolbar;
    private LinearLayout llGamificationPanel;
    private LinearLayout llNoGamificationPanel;
    private ApisenseTextView atvAchPoints;
    private ApisenseTextView atvAchCounts;

    private APISENSE.Sdk apisenseSdk;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        apisenseSdk = ((BeeApplication) getApplication()).getSdk();

        toolbar = (Toolbar) findViewById(R.id.material_toolbar);
        toolbar.setLogo(R.drawable.ic_bee_menu);
        setSupportActionBar(toolbar);

        // Set installed experiment list behavior
        experimentsAdapter = new SubscribedExperimentsListAdapter(getBaseContext(),
                R.layout.list_item_home_experiment,
                new ArrayList<Crop>());
        ListView subscribedCollects = (ListView) findViewById(R.id.home_experiment_lists);
        subscribedCollects.setEmptyView(findViewById(R.id.home_empty_list));
        subscribedCollects.setAdapter(experimentsAdapter);
        subscribedCollects.setOnItemLongClickListener(new StartStopExperimentListener());
        subscribedCollects.setOnItemClickListener(new OpenExperimentDetailsListener());

        // Check visibility of gamification panels
        llGamificationPanel = (LinearLayout) findViewById(R.id.gamification_panel);
        llGamificationPanel.setOnClickListener(this);

        llNoGamificationPanel = (LinearLayout) findViewById(R.id.no_gamification_panel);
        llNoGamificationPanel.setOnClickListener(this);

        //atvAchPoints = (ApisenseTextView) findViewById(R.id.home_game_points);
        //atvAchPoints.setOnClickListener(this);

        atvAchCounts = (ApisenseTextView) findViewById(R.id.home_game_achievements);
        atvAchCounts.setOnClickListener(this);

        apisenseSdk.getCropManager().synchroniseSubscriptions(new OnCropModifiedOnStartup());
        apisenseSdk.getCropManager().restartActive(new OnCropModifiedOnStartup());
        updateUI();
    }

    private class OnCropModifiedOnStartup implements APSCallback<Crop> {
        @Override
        public void onDone(Crop crop) {
            Log.d(TAG, "Crop" + crop.getName() + "started back");
            updateUI();
        }

        @Override
        public void onError(Exception e) {

        }
    }

    protected void updateGamificationPanels() {
        if (BeeGameManager.getInstance().isLoad()) {
            llNoGamificationPanel.setVisibility(View.GONE);
            llGamificationPanel.setVisibility(View.VISIBLE);

            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... params) {
                    //People.LoadPeopleResult result = Plus.PeopleApi.loadConnected(mHelper.getApiClient()).await();
                    //Person player = result.getPersonBuffer().get(0);
                    // toolbar.setLogo(getPlayer().getImage());

                    //toolbar.setTitle(player.getDisplayName());
                    return null;
                }
            }.execute();

        } else {
            llNoGamificationPanel.setVisibility(View.VISIBLE);
            llGamificationPanel.setVisibility(View.GONE);
        }
        // Refresh gamification text views after the refresh of game data
//        atvAchPoints.setText(BeeGameManager.getInstance().getPlayerPoints() + "");
        atvAchCounts.setText(BeeGameManager.getInstance().getAchievementUnlockCount() + "");
    }

    @Override
    public void onRefresh(OnGameDataLoadedEvent event) {
        updateUI();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.no_gamification_panel:
                BeeGameManager.getInstance().initialize(this);
                BeeGameManager.getInstance().connectPlayer();
                break;
            case R.id.gamification_panel:
                Intent intent = new Intent(getApplicationContext(), RewardActivity.class);
                startActivity(intent);
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateUI();
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

    @Override
    protected void onStart() {
        super.onStart();
        updateUI();
    }

    public void setExperiments(List<Crop> experiments) {
        this.experimentsAdapter.setDataSet(experiments);
    }

    private void updateUI() {
        retrieveActiveExperiments();
        updateGamificationPanels();

        Toolbar toolbar = (Toolbar) findViewById(R.id.material_toolbar);

        if (isUserAuthenticated()) {
            SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            toolbar.setTitle(settings.getString("username", "   " + getString(R.string.user_identity, getString(R.string.anonymous_user))));
        } else {
            toolbar.setTitle(getString(R.string.user_identity, "    " + getString(R.string.anonymous_user)));
        }
    }

    private void retrieveActiveExperiments() {
        apisenseSdk.getCropManager().getSubscriptions(new ExperimentListRetrievedCallback());
    }

    private boolean isUserAuthenticated() {
        return apisenseSdk.getSessionManager().isConnected();
    }

    public void doLaunchSettings() {
        Intent settingsIntent = new Intent(this, SettingsActivity.class);
        startActivity(settingsIntent);
    }

    public void doGoToStore(View storeButton) {
        Intent storeIntent = new Intent(this, StoreActivity.class);
        startActivity(storeIntent);
    }

    public void doGoToProfil(View personalInformation) {
        if (!isUserAuthenticated()) {
            Intent slideIntent = new Intent(this, SlideshowActivity.class);
            slideIntent.putExtra("goTo", "register");
            startActivity(slideIntent);
            finish();
        } else {
            // Go to profil activity
        }
    }

    public class ExperimentListRetrievedCallback implements APSCallback<List<Crop>> {
        @Override
        public void onDone(List<Crop> response) {
            Log.i(TAG, "number of Active Experiments: " + response.size());

            // Updating listView
            setExperiments(response);
            experimentsAdapter.notifyDataSetChanged();
        }

        @Override
        public void onError(Exception e) {

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
                        experimentsAdapter.notifyDataSetInvalidated();
                    }
                });
            } else {
                apisenseSdk.getCropManager().start(crop, new OnCropStarted(getBaseContext()) {
                    @Override
                    public void onDone(Crop crop) {
                        super.onDone(crop);
                        experimentsAdapter.notifyDataSetInvalidated();
                    }
                });
            }
            return true;
        }
    }
}
