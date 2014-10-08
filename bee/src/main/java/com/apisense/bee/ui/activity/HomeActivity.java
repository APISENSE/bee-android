package com.apisense.bee.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.apisense.bee.R;
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
        if (! APISENSE.isInit()) {
            APISENSE.init(getBaseContext(), new APISENSEListenner() {
                @Override
                public void onConnected(BeeSenseServiceManager beeSenseServiceManager) {
                    ;
                }
            });
        }
        APISENSE.apisense(getBaseContext(),new APISENSEListenner() {
            @Override
            public void onConnected(BeeSenseServiceManager beeSenseServiceManager) {
                Toast.makeText(getBaseContext(), "capisense initalized!", Toast.LENGTH_LONG);
            }
        });

        setContentView(R.layout.activity_home);

        // Setting good text on login/logout button (may use an icon)
        Button loginButton = (Button) findViewById(R.id.home_login_logout_button);
//        if (isUserAuthenticated()) {
//            loginButton.setText(R.string.logout);
//        } else {
            loginButton.setText(R.string.login);
//        }
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
            // TODO: Redirect to signin Activity / Fragment
            //Intent intent = new Intent(SettingsActivity.this, SigninActivity.class);
            //startActivity(intent);
            //finish();
            Toast.makeText(getApplicationContext(), "This will one day log you in!", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isUserAuthenticated() {
        return APISENSE.apisServerService().isConnected();
    }
}
