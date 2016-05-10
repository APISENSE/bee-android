package com.apisense.bee.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.IntentCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.apisense.bee.BeeApplication;
import com.apisense.bee.R;
import com.apisense.bee.callbacks.BeeAPSCallback;
import com.apisense.sdk.core.bee.Bee;


public class RegisterActivity extends Activity {

    /**
     * The default email to populate the email field with.
     */
    private final String TAG = "Register fragment";

    // Values for pseudo and password at the time of the login attempt.
    private String mPseudo = "";
    private String mPassword = "";
    private String mPasswordRepeat = "";

    // UI
    private EditText mPseudoEditText;
    private EditText mPasswordEditText;
    private EditText mPasswordConfirmEditText;
    private Button mRegisterButton;


    public RegisterActivity() {
        // Required empty public constructor
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        // Get all views
        mPseudoEditText = (EditText) findViewById(R.id.registerPseudo);
        mPasswordEditText = (EditText) findViewById(R.id.registerPassword);
        mPasswordConfirmEditText = (EditText) findViewById(R.id.registerPasswordConfirm);
        mRegisterButton = (Button) findViewById(R.id.registerBtn);

        mRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptRegister(view);
            }
        });
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * <p/>
     * If there are form errors (invalid email, missing fields, etc.),
     * The errors are presented and no actual login attempt is made.
     *
     * @param registerButton The button used to call this method.
     */
    public void attemptRegister(final View registerButton) {
        resetFieldsError();
        catchFieldValues();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password.
        if (TextUtils.isEmpty(mPassword)) {
            mPasswordEditText.setError(getString(R.string.error_field_required));
            focusView = mPasswordEditText;
            cancel = true;
        } else if (mPassword.length() < 4) {
            mPasswordEditText.setError(getString(R.string.signin_error_invalid_password));
            focusView = mPasswordEditText;
            cancel = true;
        } else if (mPasswordRepeat.length() < 4 || !mPasswordRepeat.equals(mPassword)) {
            mPasswordConfirmEditText.setError(getString(R.string.register_error_invalid_repeat_password));
            focusView = mPasswordConfirmEditText;
            cancel = true;
        }

        if (cancel)
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        else {
            ((BeeApplication) getApplication()).getSdk().getSessionManager().createBee(mPseudo, mPassword, new BeeAPSCallback<Bee>(this) {
                @Override
                public void onDone(Bee bee) {
                    ((BeeApplication) getApplication()).getSdk().getSessionManager().login(bee.email, mPassword, new BeeAPSCallback<Void>(RegisterActivity.this) {
                        @Override
                        public void onDone(Void aVoid) {
                            Intent intent = new Intent(RegisterActivity.this, HomeActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | IntentCompat.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        }
                    });
                }
            });
        }
    }

    private void catchFieldValues() {
        // Store values at the time of the login attempt.
        mPseudo = mPseudoEditText.getText().toString();
        mPassword = mPasswordEditText.getText().toString();
        mPasswordRepeat = mPasswordConfirmEditText.getText().toString();
    }

    private void resetFieldsError() {
        // Reset errors.
        mPseudoEditText.setError(null);
        mPasswordEditText.setError(null);
        mPasswordConfirmEditText.setError(null);
    }
}
