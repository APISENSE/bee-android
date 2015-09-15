package com.apisense.bee.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.IntentCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.apisense.bee.BeeApplication;
import com.apisense.bee.R;
import com.apisense.bee.games.BeeGameActivity;
import com.apisense.sdk.APISENSE;
import com.apisense.sdk.core.APSCallback;
import com.apisense.sdk.core.store.Crop;
import com.gc.materialdesign.widgets.SnackBar;

public class SignInActivity extends Activity {

    private final String TAG = "SignInFragment";
    private Button mSignInBtn;
    private EditText mPseudoEditText;
    private EditText mPasswordEditText;

    private APISENSE.Sdk apisenseSdk;

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
    public void doLoginLogout(View loginButton) {
        if (!isInputCorrect()) {
            new SnackBar(this, getResources().getString(R.string.empty_field), null, null).show();
            return;
        }

        if (apisenseSdk.getSessionManager().isConnected()) {
            apisenseSdk.getSessionManager().logout(new APSCallback<Void>() {
                @Override
                public void onDone(Void response) {
                    mSignInBtn.setText("Login");
                    new SnackBar(SignInActivity.this, getResources().getString(R.string.status_changed_to_anonymous), null, null).show();
                }

                @Override
                public void onError(Exception e) {
                    new SnackBar(SignInActivity.this, getResources().getString(R.string.experiment_exception_on_closure), null, null).show();
                }
            });
        } else {
            apisenseSdk.getSessionManager().login(mPseudoEditText.getText().toString(), mPasswordEditText.getText().toString(),
                    new APSCallback<Void>() {
                        @Override
                        public void onDone(Void response) {
                            mSignInBtn.setText(getString(R.string.logout));
                            Intent intent = new Intent(SignInActivity.this, HomeActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | IntentCompat.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        }

                        @Override
                        public void onError(Exception e) {
                            new SnackBar(SignInActivity.this, getResources().getString(R.string.failed_to_connect), null, null).show();
                        }
                    });
        }
    }
}
