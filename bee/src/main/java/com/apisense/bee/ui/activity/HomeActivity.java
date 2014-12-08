package com.apisense.bee.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;
import com.apisense.android.APSApplication;
import com.apisense.android.api.APS;
import com.apisense.android.api.APSLocalCrop;
import com.apisense.android.ui.crops.CropsManagerFragment;
import com.apisense.bee.R;
import com.apisense.bee.backend.user.SignOutTask;
import com.apisense.core.api.Callback;
import com.apisense.core.api.Log;


public class HomeActivity extends FragmentActivity implements  CropsManagerFragment.onCropsFragmentManagerListenner {
    private final String TAG = getClass().getSimpleName();

    // Asynchronous Tasks
    //private RetrieveInstalledExperimentsTask experimentsRetrieval;
    //private StartStopExperimentTask experimentStartStopTask;
    private SignOutTask signOut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);


        APS.ready((APSApplication) this.getApplicationContext(), new Callback<Void>() {
            @Override
            public void onCall(Void aVoid) throws Exception {

                getSupportFragmentManager().beginTransaction()
                        .add(R.id.home_experiment_fragment, CropsManagerFragment.newInstance(null))
                        .commit();

            }

            @Override
            public void onError(Throwable throwable) {

            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        updateProfile();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
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
        }
        return true;
    }

    private void updateProfile(){
        String username = getString(R.string.user_identity, getString(R.string.anonymous_user));
        if (isUserAuthenticated()) {
            try {
                username = String.format(getString(R.string.user_identity), APS.getUsername(this));
                final String uuidRegex = "[0-9a-f]{8}-[0-9a-f]{4}-[1-5][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}";
                if (username.matches("anonymous-" + uuidRegex)){
                    username = "Anonymous";
                };
            } catch (APS.SDKNotInitializedException e) {
                e.printStackTrace();
            }
        }

        try {

            if (!APS.getInstalledCrop(this).isEmpty()){

                findViewById(R.id.home_empty_list).setVisibility(View.INVISIBLE);

            }
            else{

                findViewById(R.id.home_experiment_fragment).setVisibility(View.INVISIBLE);

            }
        } catch (Exception e) {
            Log.e(e);
        }

        TextView user_identity = (TextView) findViewById(R.id.home_user_identity);
        user_identity.setText(username);
    }

    private boolean isUserAuthenticated() {
        boolean response;
        try {
            response = APS.isConnected(this);
        } catch (APS.SDKNotInitializedException e) {
            e.printStackTrace();
            return false;
        }
        return response;
    }

    public void doLaunchSettings(){
        Intent settingsIntent = new Intent(this, SettingsActivity.class);
        startActivity(settingsIntent);
    }

    public void doLaunchPrivacy(){
        Intent privacyIntent = new Intent(this, PrivacyActivity.class);
        startActivity(privacyIntent);
    }

    private void doDisconnect() {
        signOut = new SignOutTask(this, new SignedOutCallback());
        signOut.execute();
    }

    private void doLaunchAbout() {
        Intent aboutIntent = new Intent(this, AboutActivity.class);
        startActivity(aboutIntent);
    }

    public void doGoToStore(View storeButton) {
        Intent storeIntent = new Intent(this, StoreActivity.class);
        startActivity(storeIntent);
    }

    public void doGoToProfil(View personalInformation) {
        if (!isUserAuthenticated()) {
            Intent slideIntent = new Intent(this, LauncherActivity.class);
            startActivity(slideIntent);
            finish();


        } else {
            // Go to profil activity
        }
    }

    @Override
    public void onCropClicked(APSLocalCrop apsLocalCrop) {

        final Intent intent = new Intent(this, ExperimentDetailsActivity.class);
        intent.putExtra("experiment",apsLocalCrop.getName());
        startActivity(intent);

    }

    public class SignedOutCallback implements Callback<Void> {
        @Override
        public void onCall(Void aVoid) {
            signOut = null;
            Toast.makeText(getApplicationContext(), R.string.status_changed_to_anonymous, Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getBaseContext(), LauncherActivity.class);
            startActivity(intent);
            finish();
        }

        @Override
        public void onError(Throwable throwable) {
            signOut = null;
        }
    }
}
