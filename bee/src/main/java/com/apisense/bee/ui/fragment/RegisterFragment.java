package com.apisense.bee.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.IntentCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.apisense.bee.BeeApplication;
import com.apisense.bee.R;
import com.apisense.bee.callbacks.BeeAPSCallback;
import com.apisense.bee.ui.activity.HomeActivity;
import com.apisense.sdk.APISENSE;
import com.apisense.sdk.core.bee.Bee;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class RegisterFragment extends Fragment {

    @BindView(R.id.registerSignUpBtn) Button mSignUpButton;
    @BindView(R.id.registerSignInBtn) Button mSignInButton;
    @BindView(R.id.registerEmailEditText) EditText mPseudoEditText;
    @BindView(R.id.registerPasswordEditText) EditText mPasswordEditText;
    @BindView(R.id.registerPasswordRepeatEditText) EditText mPasswordConfirmEditText;

    // Values for pseudo and password at the time of the login attempt.
    private String mPseudo = "";
    private String mPassword = "";
    private String mPasswordRepeat = "";

    /**
     * The default email to populate the email field with.
     */
    private final String TAG = "Register fragment";

    private Unbinder unbinder;
    private OnLoginClickedListener mLoginCallback;
    private APISENSE.Sdk apisenseSdk;

    public interface OnLoginClickedListener {
        void switchToLogin();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_register, container, false);
        unbinder = ButterKnife.bind(this, view);
        apisenseSdk = ((BeeApplication) getActivity().getApplication()).getSdk();
        mLoginCallback = (OnLoginClickedListener) getActivity();
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick(R.id.registerSignInBtn) void signInClicked(Button view) {
        mLoginCallback.switchToLogin();
    }

    @OnClick(R.id.registerSignUpBtn) void signUpClicked(Button view) {
        attemptRegister(view);
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
        } else if (mPassword.length() < 7) {
            mPasswordEditText.setError(getString(R.string.signin_error_invalid_password));
            focusView = mPasswordEditText;
            cancel = true;
        } else if (!mPasswordRepeat.equals(mPassword)) {
            mPasswordConfirmEditText.setError(getString(R.string.register_error_invalid_repeat_password));
            focusView = mPasswordConfirmEditText;
            cancel = true;
        }

        if (cancel)
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        else {
            apisenseSdk.getSessionManager().createBee(mPseudo, mPassword, new BeeAPSCallback<Bee>(getActivity()) {
                @Override
                public void onDone(Bee bee) {
                    apisenseSdk.getSessionManager().login(bee.email, mPassword, new BeeAPSCallback<Void>(getActivity()) {
                        @Override
                        public void onDone(Void aVoid) {
                            Intent intent = new Intent(getActivity(), HomeActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | IntentCompat.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            getActivity().finish();
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


