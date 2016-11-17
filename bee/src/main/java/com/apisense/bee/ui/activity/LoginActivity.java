package com.apisense.bee.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.apisense.bee.R;
import com.apisense.bee.ui.fragment.LoginFragment;
import com.apisense.bee.ui.fragment.RegisterFragment;

import butterknife.ButterKnife;
import butterknife.Unbinder;

public class LoginActivity extends AppCompatActivity
        implements LoginFragment.OnRegisterClickedListener, RegisterFragment.OnLoginClickedListener {

    private Unbinder unbinder;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        unbinder = ButterKnife.bind(this);

        if (findViewById(R.id.fragment_container) != null) {
            if (savedInstanceState != null) {
                return;
            }

            switchToLogin();
        }
    }

    @Override
    public void switchToRegister() {
        RegisterFragment registerFragment = new RegisterFragment();
        registerFragment.setArguments(getIntent().getExtras());
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, registerFragment).commit();
    }

    @Override
    public void switchToLogin(String email, String password) {
        LoginFragment loginFragment = new LoginFragment();
        getIntent().putExtra(LoginFragment.LOGIN_EMAIL_KW, email);
        getIntent().putExtra(LoginFragment.LOGIN_PASSWORD_KW, password);
        loginFragment.setArguments(getIntent().getExtras());
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, loginFragment).commit();
    }

    public void switchToLogin() {
        switchToLogin(null, null);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data); // Call fragment activity result
    }
}
