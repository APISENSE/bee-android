package com.apisense.bee.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
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
import com.google.android.gms.common.SignInButton;


public class SignInActivity extends Activity {
    public static final String ON_THE_FLY = "com.apisense.bee.signin.onTheFly";
    private final String TAG = "SignInFragment";
    private FloatingActionButton mSignInBtn;
    private Button btnRegister;

    private FloatingActionButton btnGoogleSignIn;
    private FloatingActionButton btnFacebookSignIn;

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
        this.mSignInBtn = (FloatingActionButton) findViewById(R.id.signInLoginBtn);
        this.mPseudoEditText = (EditText) findViewById(R.id.signInPseudo);
        this.mPasswordEditText = (EditText) findViewById(R.id.signInPassword);
        this.btnRegister = (Button) findViewById(R.id.btnRegister);
        this.btnGoogleSignIn = (FloatingActionButton) findViewById(R.id.btnGoogleSignIn);
        this.btnFacebookSignIn = (FloatingActionButton) findViewById(R.id.btnFacebookSignIn);


        // Sign in onClick
        this.mSignInBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                doLoginLogout(v);
            }
        });
        this.loginOnTheFly = getIntent().getBooleanExtra(ON_THE_FLY, false);

        // Register onClick
        this.btnRegister.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent registerIntent = new Intent(SignInActivity.this, RegisterActivity.class);
                startActivity(registerIntent);
            }
        });

        //Google button
        this.btnGoogleSignIn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Snackbar.make(v, getResources().getString(R.string.registration_not_implemented), Snackbar.LENGTH_SHORT).show();
            }
        });

        //Facebook button
        this.btnFacebookSignIn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Snackbar.make(v, getResources().getString(R.string.registration_not_implemented), Snackbar.LENGTH_SHORT).show();
            }
        });
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
