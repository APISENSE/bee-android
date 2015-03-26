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
import android.widget.Toast;

import com.apisense.bee.BeeApplication;
import com.apisense.bee.R;
import com.apisense.bee.backend.AsyncTasksCallbacks;
import com.apisense.bee.backend.experiment.RetrieveInstalledExperimentsTask;
import com.apisense.bee.backend.experiment.StartStopExperimentTask;
import com.apisense.bee.backend.user.SignOutTask;
import com.apisense.bee.games.BeeGameActivity;
import com.apisense.bee.games.BeeGameManager;
import com.apisense.bee.games.event.OnGameDataLoadedEvent;
import com.apisense.bee.ui.adapter.SubscribedExperimentsListAdapter;
import com.apisense.bee.ui.entity.ExperimentSerializable;
import com.apisense.bee.widget.ApisenseTextView;

import java.util.ArrayList;
import java.util.List;

import fr.inria.bsense.APISENSE;
import fr.inria.bsense.appmodel.Experiment;


public class HomeActivity extends BeeGameActivity implements View.OnClickListener {
    private final String TAG = getClass().getSimpleName();
    // Data
    protected SubscribedExperimentsListAdapter experimentsAdapter;

    // Asynchronous Tasks
    private RetrieveInstalledExperimentsTask experimentsRetrieval;
    private SignOutTask signOut;
    private StartStopExperimentTask experimentStartStopTask;

    // Gamification
    private Toolbar toolbar;
    private LinearLayout llGamificationPanel;
    private LinearLayout llNoGamificationPanel;
    private ApisenseTextView atvAchPoints;
    private ApisenseTextView atvAchCounts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        toolbar = (Toolbar) findViewById(R.id.material_toolbar);
        setSupportActionBar(toolbar);

        // Set installed experiment list behavior
        experimentsAdapter = new SubscribedExperimentsListAdapter(getBaseContext(),
                R.layout.fragment_experiment_element,
                new ArrayList<Experiment>());
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

        updateUI();

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
                finish();
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
            case R.id.action_about:
                doLaunchAbout();
                break;
            case R.id.action_settings:
                doLaunchSettings();
                break;
            case R.id.action_privacy:
                doLaunchPrivacy();
                break;
            case R.id.action_share:
                doApplicationShare();
                break;
        }
        return true;
    }

    protected void doApplicationShare() {
        Intent sendIntent = new Intent(Intent.ACTION_SEND)
                .setAction(Intent.ACTION_SEND)
                .putExtra(Intent.EXTRA_TEXT, "linktobee");
        sendIntent.setType("text/plain");
        startActivity(Intent.createChooser(sendIntent, getResources().getText(R.string.action_share)));
    }

    @Override
    protected void onStart() {
        super.onStart();
        updateUI();
    }

    public void setExperiments(List<Experiment> experiments) {
        this.experimentsAdapter.setDataSet(experiments);
    }

    private void updateUI() {
        retrieveActiveExperiments();
        updateGamificationPanels();

        Toolbar toolbar = (Toolbar) findViewById(R.id.material_toolbar);

        if (isUserAuthenticated()) {
            SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            toolbar.setTitle(settings.getString("username", getString(R.string.user_identity, getString(R.string.anonymous_user))));
        } else {
            toolbar.setTitle(getString(R.string.user_identity, getString(R.string.anonymous_user)));
        }
    }

    private void retrieveActiveExperiments() {
        if (experimentsRetrieval == null) {
            experimentsRetrieval = new RetrieveInstalledExperimentsTask(APISENSE.apisense(), new ExperimentListRetrievedCallback());

            experimentsRetrieval.execute();
        }
    }

    private boolean isUserAuthenticated() {
        return APISENSE.apisServerService().isConnected();
    }

    public void doLaunchSettings() {
        Intent settingsIntent = new Intent(this, SettingsActivity.class);
        startActivity(settingsIntent);
    }

    public void doLaunchPrivacy() {
        Intent privacyIntent = new Intent(this, PrivacyActivity.class);
        startActivity(privacyIntent);
    }

    private void doLaunchAbout() {
        Intent aboutIntent = new Intent(this, AboutActivity.class);
        startActivity(aboutIntent);
    }

    public void doLoginForm(MenuItem button) {
        Intent slideIntent = new Intent(this, SlideshowActivity.class);
        slideIntent.putExtra("goTo", "register");
        startActivity(slideIntent);
        finish();
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

    public class ExperimentListRetrievedCallback implements AsyncTasksCallbacks {
        @Override
        public void onTaskCompleted(int result, Object response) {
            experimentsRetrieval = null;
            List<Experiment> exp = (List<Experiment>) response;
            Log.i(TAG, "number of Active Experiments: " + exp.size());

            // Updating listview
            setExperiments(exp);
            experimentsAdapter.notifyDataSetChanged();
        }

        @Override
        public void onTaskCanceled() {
            experimentsRetrieval = null;
        }
    }

    private class OpenExperimentDetailsListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Intent intent = new Intent(view.getContext(), ExperimentDetailsActivity.class);
            Experiment exp = (Experiment) parent.getAdapter().getItem(position);

            Bundle bundle = new Bundle();
            // TODO : Prefer parcelable in the future. Problem : CREATOR method doesn't exist (to check)
            // bundle.putParcelable("experiment", getItem(position));
            // TODO : Maybe something extending Experiment and using JSONObject to init but it seems to be empty
            bundle.putSerializable("experiment", new ExperimentSerializable(exp));
            intent.putExtras(bundle); //Put your id to your next Intent
            startActivity(intent);
        }
    }

    private class StartStopExperimentListener implements AdapterView.OnItemLongClickListener {
        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
            Experiment exp = (Experiment) parent.getAdapter().getItem(position);
            if (experimentStartStopTask == null) {
                experimentStartStopTask = new StartStopExperimentTask(APISENSE.apisense(), new OnExperimentStatusChanged(exp));
                experimentStartStopTask.execute(exp);
            }
            return true;
        }
    }


    public class SignedOutCallback implements AsyncTasksCallbacks {
        @Override
        public void onTaskCompleted(int result, Object response) {
            signOut = null;
            Toast.makeText(getApplicationContext(), R.string.status_changed_to_anonymous, Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(HomeActivity.this, SlideshowActivity.class);
            startActivity(intent);
            finish();
        }

        @Override
        public void onTaskCanceled() {
            signOut = null;
        }
    }

    private class OnExperimentStatusChanged implements AsyncTasksCallbacks {
        private Experiment concernedExp;

        public OnExperimentStatusChanged(Experiment exp) {
            this.concernedExp = exp;
        }

        @Override
        public void onTaskCompleted(int result, Object response) {
            experimentStartStopTask = null;
            String experimentName = concernedExp.niceName;
            String toastMessage = "";
            if (result == BeeApplication.ASYNC_SUCCESS) {
                switch ((Integer) response) {
                    case StartStopExperimentTask.EXPERIMENT_STARTED:
                        toastMessage = String.format(getString(R.string.experiment_started), experimentName);
                        break;
                    case StartStopExperimentTask.EXPERIMENT_STOPPED:
                        toastMessage = String.format(getString(R.string.experiment_stopped), experimentName);
                        break;
                }
                Toast.makeText(getBaseContext(), toastMessage, Toast.LENGTH_SHORT).show();
                experimentsAdapter.notifyDataSetInvalidated();
            }
        }

        @Override
        public void onTaskCanceled() {
            experimentStartStopTask = null;
        }
    }
}
