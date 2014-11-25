package com.apisense.bee.ui.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;

import android.widget.Button;
import android.widget.EditText;
import com.apisense.bee.R;
import com.apisense.bee.backend.user.RegisterTask;
import com.apisense.bee.ui.activity.SlideshowActivity;

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

    // UI
    private EditText mPseudoEditText;
    private EditText mPasswordEditText;
    private EditText mPasswordConfirmEditText;
    private EditText mApisenseUrlEditText;
    private TextView mApisenseHiveLabel;


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

        Button mRegisterButton = (Button) root.findViewById(R.id.registerBtn);
        mRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = attemptRegister();
                getActivity().setResult(Activity.RESULT_OK, intent);
                getActivity().finish();
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
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.),
     * the errors are presented and no actual login attempt is made.
     */
    public Intent attemptRegister() {
        Intent intent = new Intent();

        // Reset errors.
        mPseudoEditText.setError(null);
        mPasswordEditText.setError(null);
        mPasswordConfirmEditText.setError(null);
        mApisenseUrlEditText.setError(null);

        // Store values at the time of the login attempt.
        String pseudo = mPseudoEditText.getText().toString();
        String password = mPasswordEditText.getText().toString();
        String passwordRepeat = mPasswordConfirmEditText.getText().toString();
        String apisenseUrl = mApisenseUrlEditText.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password.
        if (TextUtils.isEmpty(password)) {
            mPasswordEditText.setError(getString(R.string.error_field_required));
            focusView = mPasswordEditText;
            cancel = true;
        } else if (password.length() < 4) {
            mPasswordEditText.setError(getString(R.string.signin_error_invalid_password));
            focusView = mPasswordEditText;
            cancel = true;
        } else if (passwordRepeat.length() < 4 || !passwordRepeat.equals(password)) {
            mPasswordConfirmEditText.setError(getString(R.string.register_error_invalid_repeat_password));
            focusView = mPasswordConfirmEditText;
            cancel = true;
        } else if (TextUtils.isEmpty(apisenseUrl)) {
            mApisenseUrlEditText.setError(getString(R.string.error_field_required));
            focusView = mApisenseUrlEditText;
            cancel = true;
        }

        if (cancel)
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        else {
            intent.putExtra(SlideshowActivity.KEY_AUTHENTICATION_ACTION,SlideshowActivity.REGISTER_ACTION);
            intent.putExtra(SlideshowActivity.REGISTER_PSEUDO, pseudo);
            intent.putExtra(SlideshowActivity.REGISTER_PSEUDO, password);
            intent.putExtra(SlideshowActivity.REGISTER_URL, apisenseUrl);
        }
        return intent;
    }
}
