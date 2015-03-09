package com.apisense.bee.ui.fragment;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.IntentCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.apisense.bee.BeeApplication;
import com.apisense.bee.R;
import com.apisense.bee.backend.AsyncTasksCallbacks;
import com.apisense.bee.backend.user.RegisterTask;
import com.apisense.bee.games.BeeGameActivity;
import com.apisense.bee.games.BeeGameManager;
import com.apisense.bee.ui.activity.HomeActivity;
import com.apisense.bee.ui.activity.SlideshowActivity;
import com.apisense.bee.widget.ApisenseTextView;
import com.gc.materialdesign.widgets.SnackBar;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.plus.model.people.Person;

import fr.inria.bsense.APISENSE;

/**
 * Created by Warnant on 05-03-15.
 */
public class ConnectivityFragment extends Fragment implements View.OnClickListener {

    private final String TAG = "Connectivity fragment";

    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private RegisterTask mRegisterTask = null;

    private ApisenseTextView atvAnonymousCo;
    private Button btnRegister;
    private Button btnLogin;

    private SignInButton btnGoogleSignIn;
    private Button btnFacebookSignIn;

    /**
     * Default constructor
     */
    public ConnectivityFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_connectivity, container, false);

        this.btnLogin = (Button) root.findViewById(R.id.btnLogin);
        this.btnLogin.setOnClickListener(this);
        this.btnRegister = (Button) root.findViewById(R.id.btnRegister);
        this.btnRegister.setOnClickListener(this);

        this.atvAnonymousCo = (ApisenseTextView) root.findViewById(R.id.atvAnonymousCo);
        this.atvAnonymousCo.setOnClickListener(this);

        this.btnGoogleSignIn = (SignInButton) root.findViewById(R.id.btnGoogleSignIn);
        this.btnGoogleSignIn.setOnClickListener(this);

        this.btnFacebookSignIn = (Button) root.findViewById(R.id.btnFacebookSignIn);
        this.btnFacebookSignIn.setOnClickListener(this);

        // Inflate the layout for this fragment
        return root;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnLogin:
                Intent slideIntentLogin = new Intent(getActivity(), SlideshowActivity.class);
                slideIntentLogin.putExtra("goTo", "signin");
                startActivity(slideIntentLogin);
                break;
            case R.id.btnRegister:
                Intent slideIntentRegister = new Intent(getActivity(), SlideshowActivity.class);
                slideIntentRegister.putExtra("goTo", "register");
                startActivity(slideIntentRegister);
                break;
            case R.id.atvAnonymousCo:
                Intent homeIntent = new Intent(getActivity(), HomeActivity.class);
                startActivity(homeIntent);
                break;
            case R.id.btnGoogleSignIn:
                performGoogleRegistration();
                break;
            case R.id.btnFacebookSignIn:
                // TODO
                new SnackBar(getActivity(), getResources().getString(R.string.failed_to_connect), null, null).show();
            default:
                return;
        }
    }

    private void performGoogleRegistration() {
        if (mRegisterTask != null)
            return;

        BeeGameManager.getInstance().initialize((BeeGameActivity) this.getActivity());
        BeeGameManager.getInstance().connectPlayer();


        mRegisterTask = new RegisterTask(APISENSE.apisense(), new AsyncTasksCallbacks() {
            @Override
            public void onTaskCompleted(int result, Object response) {
                Log.i(TAG, "Register result: " + result);
                Log.i(TAG, "Register details: " + response);
                if ((Integer) result == BeeApplication.ASYNC_SUCCESS) {
                    Intent intent = new Intent(getActivity(), HomeActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | IntentCompat.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
            }

            @Override
            public void onTaskCanceled() {

            }
        });

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(final Void... params) {
                Person currentPlayer = BeeGameManager.getInstance().getPlayer();

                Log.i(TAG, "Google register result: " + currentPlayer);

                if (currentPlayer == null) {

                    new SnackBar(getActivity(), getResources().getString(R.string.failed_to_connect), null, null).show();
                    return null;
                }

                mRegisterTask.execute(currentPlayer.getDisplayName(), currentPlayer.getDisplayName(), getString(R.string.hive_url));

                return null;
            }


        }.execute();
    }
}
