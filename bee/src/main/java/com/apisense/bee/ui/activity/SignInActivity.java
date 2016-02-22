package com.apisense.bee.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.content.IntentCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.apisense.bee.BeeApplication;
import com.apisense.bee.R;
import com.apisense.sdk.APISENSE;
import com.apisense.sdk.core.APSCallback;


public class SignInActivity extends Activity {
    public static final String ON_THE_FLY = "com.apisense.bee.signin.onTheFly";
    private final String TAG = "SignInFragment";
    private Button mSignInBtn;
    private EditText mPseudoEditText;
    private EditText mPasswordEditText;

    private APISENSE.Sdk apisenseSdk;

    private boolean loginOnTheFly;

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
        apisenseSdk = ((BeeApplication) getApplication()).getSdk();

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
        loginOnTheFly = getIntent().getBooleanExtra(ON_THE_FLY, false);
    }

    /**
     * Check if sign in form is correctly filled
     *
     * @return true or false
     */
    private boolean isInputCorrect() {
        String mPseudo = mPseudoEditText.getText().toString();
        String mPassword = mPasswordEditText.getText().toString();

        return !(TextUtils.isEmpty(mPseudo) || TextUtils.isEmpty(mPassword));
    }

    /**
     * Run sign in task in background
     *
     * @param loginButton button pressed to start task
     */
    public void doLoginLogout(final View loginButton) {
        if (!isInputCorrect()) {
            Snackbar.make(loginButton, getResources().getString(R.string.empty_field), Snackbar.LENGTH_SHORT).show();
            return;
        }

        if (apisenseSdk.getSessionManager().isConnected()) {
            apisenseSdk.getSessionManager().logout(new APSCallback<Void>() {
                @Override
                public void onDone(Void response) {
                    mSignInBtn.setText("Login");
                    Snackbar.make(loginButton, getResources().getString(R.string.status_changed_to_anonymous), Snackbar.LENGTH_SHORT).show();
                }

                @Override
                public void onError(Exception e) {
                    Snackbar.make(loginButton, getResources().getString(R.string.experiment_exception_on_closure), Snackbar.LENGTH_SHORT).show();
                }
            });
        } else {
            apisenseSdk.getSessionManager().login(mPseudoEditText.getText().toString(), mPasswordEditText.getText().toString(),
                    new APSCallback<Void>() {
                        @Override
                        public void onDone(Void response) {
                            mSignInBtn.setText(getString(R.string.logout));
                            if (loginOnTheFly) {
                                finish();
                            } else {
                                Intent intent = new Intent(SignInActivity.this, HomeActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | IntentCompat.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                            }
                        }

                        @Override
                        public void onError(Exception e) {
                            Snackbar.make(loginButton, getResources().getString(R.string.failed_to_connect), Snackbar.LENGTH_SHORT).show();
                        }
                    });
        }
    }
}
