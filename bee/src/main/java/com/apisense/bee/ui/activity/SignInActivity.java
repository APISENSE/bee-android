package com.apisense.bee.ui.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.IntentCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.apisense.bee.BeeApplication;
import com.apisense.bee.R;
import com.apisense.bee.backend.AsyncTasksCallbacks;
import com.apisense.bee.backend.user.SignInTask;
import com.apisense.bee.games.BeeGameActivity;
import com.gc.materialdesign.widgets.SnackBar;

import fr.inria.bsense.APISENSE;
import fr.inria.bsense.appmodel.Experiment;

public class SignInActivity extends BeeGameActivity {

    private final String TAG = "SignInFragment";
    private Button mSignInBtn;
    private EditText mPseudoEditText;
    private EditText mPasswordEditText;

    /**
     * Default constructor
     */
    public SignInActivity() {
        // Required empty public constructor
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        // Views
        mSignInBtn = (Button) findViewById(R.id.signInLoginBtn);
        mPseudoEditText = (EditText) findViewById(R.id.signInPseudo);
        mPasswordEditText = (EditText) findViewById(R.id.signInPassword);

        // Sign in onClick
        mSignInBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                doLoginLogout(v);
            }
        });

    }

    // - - - - -

    /**
     * Check if current user is authenticated
     *
     * @return true or false
     */
    private boolean isUserAuthenticated() {
        return APISENSE.apisServerService().isConnected();
    }

    /**
     * Check if sign in form is correctly filled
     *
     * @return true or false
     */
    private boolean isInputCorrect() {
        String mPseudo = mPseudoEditText.getText().toString();
        String mPassword = mPasswordEditText.getText().toString();

        if (TextUtils.isEmpty(mPseudo) || TextUtils.isEmpty(mPassword)) {
            return false;
        }

        return true;
    }

    /**
     * Run sign in task in background
     *
     * @param loginButton button pressed to start task
     */
    public void doLoginLogout(View loginButton) {
        if (!isInputCorrect()) {
            new SnackBar(this, getResources().getString(R.string.empty_field), null, null).show();
            return;
        }

        if (isUserAuthenticated()) {
            try {
                APISENSE.apisMobileService().sendAllTrack();
                APISENSE.apisMobileService().stopAllExperiments(0);
                for (Experiment xp : APISENSE.apisMobileService().getInstalledExperiments().values())
                    APISENSE.apisMobileService().uninstallExperiment(xp);
            } catch (Exception e) {
                e.printStackTrace();
                new SnackBar(this, getResources().getString(R.string.experiment_exception_on_closure), null, null).show();
            }
            APISENSE.apisServerService().disconnect();
            mSignInBtn.setText("Login");
            new SnackBar(this, getResources().getString(R.string.status_changed_to_anonymous), null, null).show();
        } else {
            SignInTask signInTask = new SignInTask(APISENSE.apisense(), new AsyncTasksCallbacks() {
                @Override
                public void onTaskCompleted(int result, Object response) {
                    Log.i(TAG, "Connection result: " + result);
                    Log.i(TAG, "Connection details: " + response);
                    if ((Integer) result == BeeApplication.ASYNC_SUCCESS) {
                        mSignInBtn.setText(getString(R.string.logout));

                        // Set username name after sign in
                        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString("username", mPseudoEditText.getText().toString());
                        editor.apply();

                        Intent intent = new Intent(getParent(), HomeActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | IntentCompat.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    } else {
                        new SnackBar(getParent(), getResources().getString(R.string.failed_to_connect), null, null).show();
                    }
                }

                @Override
                public void onTaskCanceled() {

                }
            });

            signInTask.execute(mPseudoEditText.getText().toString(), mPasswordEditText.getText().toString(), "");
        }
    }
}
