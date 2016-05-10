package com.apisense.bee.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.IntentCompat;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.apisense.bee.BeeApplication;
import com.apisense.bee.R;
import com.apisense.bee.ui.activity.HomeActivity;
import com.apisense.bee.ui.activity.RegisterActivity;
import com.apisense.bee.ui.activity.SignInActivity;
import com.apisense.bee.widget.ApisenseTextView;
import com.apisense.sdk.APISENSE;
import com.apisense.sdk.adapter.SimpleAPSCallback;
import com.apisense.sdk.core.bee.Bee;
import com.google.android.gms.common.SignInButton;

import java.util.UUID;

public class ConnectivityFragment extends Fragment implements View.OnClickListener {
    private final String TAG = "Connectivity fragment";

    private ApisenseTextView atvAnonymousSignIn;
    private ApisenseTextView atvTermsPrivacy;

    private Button btnRegister;
    private Button btnLogin;

    private SignInButton btnGoogleSignIn;
    private Button btnFacebookSignIn;

    private APISENSE.Sdk apisenseSdk;

    /**
     * Default constructor
     */
    public ConnectivityFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_slide_connectivity, container, false);
        apisenseSdk = ((BeeApplication) getActivity().getApplication()).getSdk();

        this.btnLogin = (Button) root.findViewById(R.id.btnLogin);
        this.btnLogin.setOnClickListener(this);
        this.btnRegister = (Button) root.findViewById(R.id.btnRegister);
        this.btnRegister.setOnClickListener(this);

        this.atvAnonymousSignIn = (ApisenseTextView) root.findViewById(R.id.atvAnonymousSignIn);
        this.atvAnonymousSignIn.setText(Html.fromHtml(getString(R.string.slide_connect_anonym)));
        this.atvAnonymousSignIn.setOnClickListener(this);

        this.btnGoogleSignIn = (SignInButton) root.findViewById(R.id.btnGoogleSignIn);
        this.btnGoogleSignIn.setOnClickListener(this);

        this.btnFacebookSignIn = (Button) root.findViewById(R.id.btnFacebookSignIn);
        this.btnFacebookSignIn.setOnClickListener(this);

        this.atvTermsPrivacy = (ApisenseTextView) root.findViewById(R.id.atvTermsPrivacy);
        this.atvTermsPrivacy.setText(Html.fromHtml(getString(R.string.slide_connect_join)));

        // Inflate the layout for this fragment
        return root;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnLogin:
                Intent signInIntent = new Intent(getActivity(), SignInActivity.class);
                startActivity(signInIntent);
                break;
            case R.id.btnRegister:
                Intent registerIntent = new Intent(getActivity(), RegisterActivity.class);
                startActivity(registerIntent);
                break;
            case R.id.atvAnonymousSignIn:
                performAnonymousRegistration();
                break;
            case R.id.btnGoogleSignIn:
                //TODO performGoogleRegistration();
                Snackbar.make(v, getResources().getString(R.string.registration_not_implemented), Snackbar.LENGTH_SHORT).show();
                break;
            case R.id.btnFacebookSignIn:
                // TODO
                Snackbar.make(v, getResources().getString(R.string.registration_not_implemented), Snackbar.LENGTH_SHORT).show();
        }
    }

    private void performAnonymousRegistration() {
        String username = "anonymous-" + UUID.randomUUID().toString();
        String password = username;
        // TODO: [Implement] apisenseSdk.getSessionManager().createAnonymousBee()
        apisenseSdk.getSessionManager().createBee(username, password, new OnUserRegisteredCallback());
    }

    private void performGoogleRegistration() {
//        BeeGameManager.getInstance().initialize((BeeGameActivity) getActivity());
//        BeeGameManager.getInstance().connectPlayer();
        // TODO: [Implement] apisenseSdk.getSessionManager().createBeeFromGoogle()
    }

    private class OnUserRegisteredCallback extends SimpleAPSCallback<Bee> {
        @Override
        public void onDone(Bee bee) {
            Intent intent = new Intent(getActivity(), HomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | IntentCompat.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
    }
}
