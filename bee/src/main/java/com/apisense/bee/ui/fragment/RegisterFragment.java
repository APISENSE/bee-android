package com.apisense.bee.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.apisense.bee.BeeApplication;
import com.apisense.bee.R;
import com.apisense.bee.callbacks.BeeAPSCallback;
import io.apisense.sdk.APISENSE;
import io.apisense.sdk.core.bee.Bee;

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

    /**
     * The default email to populate the email field with.
     */
    private final String TAG = "Register fragment";

    private Unbinder unbinder;
    private OnLoginClickedListener mLoginCallback;
    private APISENSE.Sdk apisenseSdk;

    public interface OnLoginClickedListener {
        void switchToLogin();

        void switchToLogin(String email, String password);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_register, container, false);
        unbinder = ButterKnife.bind(this, view);
        apisenseSdk = ((BeeApplication) getActivity().getApplication()).getSdk();
        mLoginCallback = (OnLoginClickedListener) getActivity();

        mPasswordConfirmEditText.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    mSignUpButton.performClick();
                    return true;
                }

                return false;
            }
        });

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick(R.id.registerSignInBtn)
    void signInClicked(Button view) {
        mLoginCallback.switchToLogin();
    }

    @OnClick(R.id.registerSignUpBtn)
    void signUpClicked(Button view) {
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
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);

        resetFieldsError();
        String pseudo = mPseudoEditText.getText().toString();
        String password = mPasswordEditText.getText().toString();
        String passwordRepeat = mPasswordConfirmEditText.getText().toString();

        View focusView = findIncorrectField(password, passwordRepeat);
        if (focusView != null)
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        else {
            createAccount(pseudo, password);
        }
    }

    /**
     * Tells whether a field from the creation form is incorrect.
     *
     * @param password       The password to validate.
     * @param passwordRepeat The password confirmation.
     * @return The view containing a validation error if any, null if no error found.
     */
    private View findIncorrectField(String password, String passwordRepeat) {
        // Check for a valid password.
        if (TextUtils.isEmpty(password)) {
            mPasswordEditText.setError(getString(R.string.error_field_required));
            return mPasswordEditText;
        } else if (password.length() < 7) {
            mPasswordEditText.setError(getString(R.string.signin_error_invalid_password));
            return mPasswordEditText;
        } else if (!passwordRepeat.equals(password)) {
            mPasswordConfirmEditText.setError(getString(R.string.register_error_invalid_repeat_password));
            return mPasswordConfirmEditText;
        }
        return null;
    }

    /**
     * Actually creates the account, tells the user to check for validation email,
     * and redirects the application to the already filled login form.
     *
     * @param pseudo   The pseudo to use on account creation.
     * @param password The password to use on account creation.
     */
    private void createAccount(final String pseudo, final String password) {
        apisenseSdk.getSessionManager()
                .createBee(pseudo, password, new BeeAPSCallback<Bee>(getActivity()) {
                    @Override
                    public void onDone(Bee bee) {
                        Toast.makeText(getActivity(),
                                getString(R.string.validation_mail_incoming), Toast.LENGTH_LONG).show();
                        mLoginCallback.switchToLogin(pseudo, password);
                    }

                    @Override
                    public void onError(Exception e) {
                        super.onError(e);
                        Snackbar.make(mPseudoEditText, e.getMessage(), Snackbar.LENGTH_LONG).show();
                    }
                });
    }

    /**
     * Remove any validation error shown on the form.
     */
    private void resetFieldsError() {
        mPseudoEditText.setError(null);
        mPasswordEditText.setError(null);
        mPasswordConfirmEditText.setError(null);
    }
}


