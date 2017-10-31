package com.apisense.bee.ui.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.apisense.bee.BeeApplication;
import com.apisense.bee.R;
import com.apisense.bee.callbacks.BeeAPSCallback;
import com.apisense.bee.callbacks.FacebookLoginCallback;
import com.apisense.bee.ui.activity.HomeActivity;
import io.apisense.sdk.APISENSE;
import io.apisense.sdk.core.APSCallback;
import io.apisense.sdk.core.bee.LoginProvider;
import com.facebook.CallbackManager;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class LoginFragment extends Fragment {
    @BindView(R.id.signInLoginBtn)
    Button mSignInBtn;
    @BindView(R.id.signInPseudo)
    EditText mPseudoEditText;
    @BindView(R.id.signInPassword)
    EditText mPasswordEditText;
    @BindView(R.id.forgot_password_button)
    TextView mForgotPassword;

    @BindView(R.id.fb_login_button)
    LoginButton fbButton;
    @BindView(R.id.google_login_button)
    SignInButton googleButton;

    public static final String LOGIN_EMAIL_KW = "login_email";
    public static final String LOGIN_PASSWORD_KW = "login_psswd";

    private static final String TAG = "SignIn Fragment";
    private static final int GOOGLE_SIGN_IN_REQUEST_CODE = 9001;

    private APISENSE.Sdk apisenseSdk;
    private Unbinder unbinder;
    private OnRegisterClickedListener mRegisterCallback;
    private CallbackManager facebookCallbackManager;
    private GoogleApiClient googleApiClient;

    public interface OnRegisterClickedListener {
        void switchToRegister();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        unbinder = ButterKnife.bind(this, view);
        apisenseSdk = ((BeeApplication) getActivity().getApplication()).getSdk();
        mRegisterCallback = (OnRegisterClickedListener) getActivity();
        facebookCallbackManager = CallbackManager.Factory.create();

        if (fbButton != null) {
            prepareFacebookLogin(fbButton);
        }
        if (googleButton != null) {
            googleButton.setSize(SignInButton.SIZE_WIDE);
            googleButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    signInWithGoogle();
                }
            });
        }

        return view;
    }

    /**
     * Call google API to log user in
     */
    private void signInWithGoogle() {
        if (googleApiClient != null) {
            googleApiClient.disconnect();
        }

        String gAppId = getString(R.string.google_web_client_id);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestIdToken(gAppId)
                .build();
        googleApiClient = new GoogleApiClient.Builder(getContext())
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        final Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
        startActivityForResult(signInIntent, GOOGLE_SIGN_IN_REQUEST_CODE);
    }

    /**
     * Call facebook API to sign user in
     *
     * @param loginButton The facebook button.
     */
    private void prepareFacebookLogin(LoginButton loginButton) {
        loginButton.setReadPermissions("email");
        loginButton.setFragment(this);
        loginButton.registerCallback(facebookCallbackManager,
                new FacebookLoginCallback(getActivity(), new OnLoggedIn(getActivity(), loginButton))
        );
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == LoginFragment.GOOGLE_SIGN_IN_REQUEST_CODE) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);

            if (result.isSuccess()) {
                GoogleSignInAccount account = result.getSignInAccount();
                if (account != null) {
                    apisenseSdk.getSessionManager().login(account.getEmail(), account.getIdToken(),
                            LoginProvider.GOOGLE, new OnLoggedIn(getActivity(), mSignInBtn)
                    );
                } else {
                    Log.e(TAG, "No account retrieved");
                    Snackbar.make(mSignInBtn,
                            getString(R.string.failed_to_connect), Snackbar.LENGTH_SHORT).show();
                }
            } else {
                Log.e(TAG, "That's an error: " + result.getStatus());
                Snackbar.make(mSignInBtn,
                        getString(R.string.failed_to_connect), Snackbar.LENGTH_SHORT).show();
            }
        } else {
            facebookCallbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle extras = getArguments();
        if (extras != null) {
            String email = extras.getString(LOGIN_EMAIL_KW);
            if (email != null) {
                mPseudoEditText.setText(email);
            }

            String password = extras.getString(LOGIN_PASSWORD_KW);
            if (password != null) {
                mPasswordEditText.setText(password);
            }
        }
    }

    private void askForEmail() {
        mPseudoEditText.setError(getString(R.string.missing_email_address));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick(R.id.signInLoginBtn)
    void onLoginBtnClicked(Button view) {
        doLogin(view);
    }

    @OnClick(R.id.signInRegisterBtn)
    void onRegisterBtnClicked(Button view) {
        mRegisterCallback.switchToRegister();
    }

    @OnClick(R.id.forgot_password_button)
    void requestPasswordReset(View view) {
        String email = mPseudoEditText.getText().toString();
        if (email.isEmpty()) {
            askForEmail();
        } else {
            apisenseSdk.getSessionManager().resetPassword(email,
                    new OnPasswordResetRequested(getActivity(), view));
        }
    }

    // Private methods

    /**
     * Check if sign in form is correctly filled
     *
     * @return true or false
     */
    private boolean isInputCorrect(String email, String password) {
        return !(TextUtils.isEmpty(email) || TextUtils.isEmpty(password));
    }

    /**
     * Run sign in task in background
     *
     * @param loginButton button pressed to start task
     */
    private void doLogin(final Button loginButton) {
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);

        String email = mPseudoEditText.getText().toString();
        String password = mPasswordEditText.getText().toString();
        if (!isInputCorrect(email, password)) {
            Snackbar.make(loginButton, getResources().getString(R.string.empty_field), Snackbar.LENGTH_SHORT).show();
            return;
        }

        if (apisenseSdk.getSessionManager().isConnected()) {
            apisenseSdk.getSessionManager().logout(new APSCallback<Void>() {
                @Override
                public void onDone(Void response) {
                    loginButton.setText(getResources().getString(R.string.login));
                    Snackbar.make(loginButton,
                            getResources().getString(R.string.status_changed_to_anonymous),
                            Snackbar.LENGTH_SHORT).show();
                }

                @Override
                public void onError(Exception e) {
                    Snackbar.make(loginButton,
                            getResources().getString(R.string.experiment_exception_on_closure),
                            Snackbar.LENGTH_SHORT).show();
                }
            });
        } else {
            apisenseSdk.getSessionManager().login(
                    mPseudoEditText.getText().toString(), mPasswordEditText.getText().toString(),
                    new OnLoggedIn(getActivity(), loginButton)
            );
        }
    }

    private static class OnLoggedIn implements APSCallback<Void> {
        private final Activity activity;
        private final Button loginButton;

        public OnLoggedIn(Activity activity, Button loginButton) {
            this.activity = activity;
            this.loginButton = loginButton;
        }

        @Override
        public void onDone(Void response) {
            loginButton.setText(activity.getString(R.string.logout));
            Intent intent = new Intent(activity, HomeActivity.class);
            activity.startActivity(intent);
            activity.finish();
        }

        @Override
        public void onError(Exception e) {
            Snackbar.make(loginButton, activity.getString(R.string.failed_to_connect),
                    Snackbar.LENGTH_SHORT).show();
        }
    }

    /**
     * Behavior when the password reset request has ended.
     */
    private static class OnPasswordResetRequested extends BeeAPSCallback<Void> {
        private final View source;

        OnPasswordResetRequested(Activity activity, View source) {
            super(activity);
            this.source = source;
        }

        @Override
        public void onDone(Void aVoid) {
            Snackbar.make(source, activity.getString(R.string.mail_incoming), Snackbar.LENGTH_LONG)
                    .show();
        }

        @Override
        public void onError(Exception e) {
            Snackbar.make(source, e.getMessage(), Snackbar.LENGTH_LONG).show();
        }
    }
}
