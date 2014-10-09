package com.apisense.bee.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.apisense.bee.R;
import com.apisense.bee.backend.AsyncTasksCallbacks;
import com.apisense.bee.backend.SignInTask;
import fr.inria.bsense.APISENSE;
import fr.inria.bsense.APISENSEListenner;
import fr.inria.bsense.appmodel.Experiment;
import fr.inria.bsense.service.BeeSenseServiceManager;

public class HomeActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set up Apisense
        // Todo: Create APISENSEListener implementation elsewhere (move initialization to slideshow?)
        apisenseSetup();

        // Setting up actual UI
        setContentView(R.layout.activity_home);
        TextView user_identity = (TextView) findViewById(R.id.home_user_identity);
        Button loginButton = (Button) findViewById(R.id.home_login_logout_button);

        // Generating messages depending on the logged user
        if (isUserAuthenticated()) {
             loginButton.setText(R.string.logout);
             user_identity.setText(getString(R.string.user_identity, "usernameToRetrieve"));
        } else {
            loginButton.setText(R.string.login);
            user_identity.setText(getString(R.string.user_identity, R.string.anonymous_user));
        }
    }

    /**
     * init APISENSE
     */
    private void apisenseSetup() {
        // Some magic needed?
        APISENSE.init(this, new APISENSEListenner() {
            @Override
            public void onConnected(BeeSenseServiceManager apisense) {
                if (!APISENSE.apisServerService().isConnected()) {
                    Toast.makeText(getBaseContext(), "Not connected, redirecting (or not)", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getBaseContext(), "You ARE connected", Toast.LENGTH_LONG).show();
                }
            }
        });

        APISENSE.apisense(getBaseContext(),new APISENSEListenner() {
            @Override
            public void onConnected(BeeSenseServiceManager beeSenseServiceManager) {
                Toast.makeText(getBaseContext(), "apisense initalized!", Toast.LENGTH_LONG);
            }
        });

    }

    private boolean isUserAuthenticated() {
        return true;
        //Crash at the moment :/
//        return APISENSE.apisServerService().isConnected();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    public void doLaunchSettings(MenuItem button){
        Toast.makeText(getBaseContext(), "Settings clicked", Toast.LENGTH_SHORT).show();
    }

    public void doLaunchPrivacy(MenuItem button){
        Toast.makeText(getBaseContext(), "Privacy clicked", Toast.LENGTH_SHORT).show();
    }

    public void doLoginLogout(View loginButton){
        if (isUserAuthenticated()) {
            try {
                APISENSE.apisMobileService().sendAllTrack();
                APISENSE.apisMobileService().stopAllExperiments(0);
                for(Experiment xp: APISENSE.apisMobileService().getInstalledExperiments().values())
                    APISENSE.apisMobileService().uninstallExperiment(xp);
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), R.string.experiment_exception_on_closure, Toast.LENGTH_SHORT).show();
            }
            APISENSE.apisServerService().disconnect();
            Toast.makeText(getApplicationContext(), R.string.status_changed_to_anonymous, Toast.LENGTH_SHORT).show();
        } else {
            // Hardcoded login
            SignInTask signInTest = new SignInTask(new AsyncTasksCallbacks() {
                @Override
                public void onTaskCompleted(String response) {
                    Toast.makeText(getBaseContext(), "LoggedIn", Toast.LENGTH_LONG).show();
                }

                @Override
                public void onTaskCanceled() {

                }
            });
            signInTest.execute("aveuiller", "password", "");
            // TODO: Redirect to signin Fragment (or make it appear on screen?)
            //Intent intent = new Intent(SettingsActivity.this, SigninActivity.class);
            //startActivity(intent);
            //finish();
        }
    }
}
