package com.apisense.bee.ui.fragment;

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
import com.apisense.bee.ui.activity.HomeActivity;
import com.apisense.sdk.APISENSE;
import com.apisense.sdk.core.APSCallback;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class LoginFragment extends Fragment {
    @BindView(R.id.signInLoginBtn) Button mSignInBtn;
    @BindView(R.id.signInPseudo) EditText mPseudoEditText;
    @BindView(R.id.signInPassword) EditText mPasswordEditText;

    private final String TAG = "SignIn Fragment";
    private APISENSE.Sdk apisenseSdk;
    private Unbinder unbinder;
    private OnRegisterClickedListener mRegisterCallback;

    public interface OnRegisterClickedListener {
        void switchToRegister();
    }

    public LoginFragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        unbinder = ButterKnife.bind(this, view);
        apisenseSdk = ((BeeApplication) getActivity().getApplication()).getSdk();
        mRegisterCallback = (OnRegisterClickedListener) getActivity();

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick(R.id.signInLoginBtn) void onLoginBtnClicked(Button view) {
        doLogin(view);
    }

    @OnClick(R.id.signInRegisterBtn) void onRegisterBtnClicked(Button view) {
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
                            loginButton.setText(getString(R.string.logout));
                            Intent intent = new Intent(getActivity(), HomeActivity.class);
                            getActivity().startActivity(intent);
                            getActivity().finish();
                        }

                        @Override
                        public void onError(Exception e) {
                            Snackbar.make(loginButton, getResources().getString(R.string.failed_to_connect), Snackbar.LENGTH_SHORT).show();
                        }
                    });
        }
    }
}
