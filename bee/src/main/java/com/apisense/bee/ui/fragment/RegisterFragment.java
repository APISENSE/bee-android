package com.apisense.bee.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.IntentCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;
import com.apisense.bee.BeeApplication;
import com.apisense.bee.backend.AsyncTasksCallbacks;

import android.widget.Button;
import android.widget.EditText;
import com.apisense.bee.R;
import com.apisense.bee.backend.user.RegisterTask;
import com.apisense.bee.ui.activity.HomeActivity;
import fr.inria.bsense.APISENSE;
import fr.inria.bsense.APISENSEListenner;
import fr.inria.bsense.service.BeeSenseServiceManager;

public class RegisterFragment extends Fragment {

    private final String TAG = "Register fragment";

    /**
     * The default email to populate the email field with.
     */
    public static final String EXTRA_EMAIL = "com.example.android.authenticatordemo.extra.EMAIL";

    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private RegisterTask mRegisterTask = null;

    // Values for pseudo and password at the time of the login attempt.
    private String mPseudo = "";
    private String mPassword = "";
    private String mPasswordRepeat = "";
    private String mApisenseUrl = "";

    // UI
    private EditText mPseudoEditText;
    private EditText mPasswordEditText;
    private EditText mPasswordConfirmEditText;
    private EditText mApisenseUrlEditText;
    private TextView mApisenseHiveLabel;
    private Button mRegisterButton;


    public RegisterFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_register, container, false);

        // Get all views
        mPseudoEditText = (EditText) root.findViewById(R.id.registerPseudo);
        mPasswordEditText = (EditText) root.findViewById(R.id.registerPassword);
        mPasswordConfirmEditText = (EditText) root.findViewById(R.id.registerPasswordConfirm);
        mApisenseHiveLabel = (TextView) root.findViewById(R.id.apisenseHive);
        mApisenseUrlEditText = (EditText) root.findViewById(R.id.apisenseEditText);
        mRegisterButton = (Button) root.findViewById(R.id.registerBtn);

        mRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptRegister();
            }
        });

        mRegisterButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (View.VISIBLE == mApisenseUrlEditText.getVisibility()) {
                    mApisenseUrlEditText.setVisibility(View.INVISIBLE);
                    mApisenseHiveLabel.setVisibility(View.INVISIBLE);
                } else {
                    mApisenseUrlEditText.setVisibility(View.VISIBLE);
                    mApisenseHiveLabel.setVisibility(View.VISIBLE);
                }
                return false;
            }
        });

        // Inflate the layout for this fragment
        return root;
    }

    /**
     * Attempts to sign in or register the account specified by the login form. If there are form errors (invalid email, missing fields, etc.), the errors are presented and no actual login attempt is made.
     */
    public void attemptRegister() {
        if (mRegisterTask != null)
            return;

        // Reset errors.
        mPseudoEditText.setError(null);
        mPasswordEditText.setError(null);
        mPasswordConfirmEditText.setError(null);
        mApisenseUrlEditText.setError(null);

        // Store values at the time of the login attempt.
        mPseudo = mPseudoEditText.getText().toString();
        mPassword = mPasswordEditText.getText().toString();
        mPasswordRepeat = mPasswordConfirmEditText.getText().toString();
        mApisenseUrl = mApisenseUrlEditText.getText().toString();

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
        } else if (TextUtils.isEmpty(mApisenseUrl)) {
            mApisenseUrlEditText.setError(getString(R.string.error_field_required));
            focusView = mApisenseUrlEditText;
            cancel = true;
        }

        if (cancel)
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            mRegisterTask = new RegisterTask(APISENSE.apisense(), new AsyncTasksCallbacks() {
                @Override
                public void onTaskCompleted(int result, Object response) {
                    Log.i(TAG, "Register result: " + result);
                    Log.i(TAG, "Register details: " + response);
                    if ((Integer)result == BeeApplication.ASYNC_SUCCESS) {
                        Intent intent = new Intent(getActivity(), HomeActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | IntentCompat.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }
                }

                @Override
                public void onTaskCanceled() {

                }
            });

            mRegisterTask.execute(mPseudo, mPassword, mApisenseUrl);
        }
    }
}
