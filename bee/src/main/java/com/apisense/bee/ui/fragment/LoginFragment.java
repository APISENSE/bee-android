package com.apisense.bee.ui.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.apisense.bee.BeeApplication;
import com.apisense.bee.R;
import com.apisense.bee.callbacks.FacebookLoginCallback;
import com.apisense.bee.ui.activity.HomeActivity;
import com.apisense.sdk.APISENSE;
import com.apisense.sdk.core.APSCallback;
import com.facebook.CallbackManager;
import com.facebook.login.widget.LoginButton;

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

    @BindView(R.id.fb_login_button)
    LoginButton fbButton;

    private final String TAG = "SignIn Fragment";
    private APISENSE.Sdk apisenseSdk;
    private Unbinder unbinder;
    private OnRegisterClickedListener mRegisterCallback;
    private CallbackManager facebookCallbackManager;

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

        return view;
    }

    private void prepareFacebookLogin(LoginButton loginButton) {
        loginButton.setReadPermissions("email");
        loginButton.setFragment(this);
        loginButton.registerCallback(facebookCallbackManager,
                new FacebookLoginCallback(apisenseSdk, new OnLoggedIn(getActivity(), loginButton)));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        facebookCallbackManager.onActivityResult(requestCode, resultCode, data);
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

    // Private methods

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
    public void doLogin(final Button loginButton) {
        if (!isInputCorrect()) {
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
        public void onDone(Void aVoid) {
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
}
