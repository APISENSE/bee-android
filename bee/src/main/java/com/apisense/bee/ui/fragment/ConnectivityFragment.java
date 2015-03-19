package com.apisense.bee.ui.fragment;

import android.app.ProgressDialog;
import android.content.Intent;
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
import com.apisense.bee.widget.ApisenseEditText;
import com.apisense.bee.widget.ApisenseTextView;
import com.gc.materialdesign.widgets.SnackBar;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.plus.People;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;

import java.util.UUID;

import fr.inria.bsense.APISENSE;
import fr.inria.bsense.service.BeeSenseServiceManager;

/**
 * Created by Warnant on 05-03-15.
 */
public class ConnectivityFragment extends Fragment implements View.OnClickListener {

    private final String TAG = "Connectivity fragment";

    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private RegisterTask mRegisterTask = null;

    private ApisenseTextView atvAnonymousSignIn;
    private ApisenseEditText aptUrl;

    private Button btnRegister;
    private Button btnLogin;

    private SignInButton btnGoogleSignIn;
    private Button btnFacebookSignIn;

    private String username;
    private String password;
    private String apisenseUrl;

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

        this.atvAnonymousSignIn = (ApisenseTextView) root.findViewById(R.id.atvAnonymousSignIn);
        this.atvAnonymousSignIn.setOnClickListener(this);

        this.btnGoogleSignIn = (SignInButton) root.findViewById(R.id.btnGoogleSignIn);
        this.btnGoogleSignIn.setOnClickListener(this);

        this.btnFacebookSignIn = (Button) root.findViewById(R.id.btnFacebookSignIn);
        this.btnFacebookSignIn.setOnClickListener(this);

        this.aptUrl = (ApisenseEditText) root.findViewById(R.id.aptUrl);
        this.apisenseUrl = this.aptUrl.getText().toString();

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
            case R.id.atvAnonymousSignIn:
                performAnonymousRegistration();
                break;
            case R.id.btnGoogleSignIn:
                performGoogleRegistration();
                break;
            case R.id.btnFacebookSignIn:
                // TODO
                new SnackBar(getActivity(), getResources().getString(R.string.failed_to_connect), null, null).show();
        }
    }

    private void performAnonymousRegistration() {
        this.username = "anonymous-" + UUID.randomUUID().toString();
        this.password = username;
        mRegisterTask = new RegisterTask(APISENSE.apisense(), new OnUserRegisteredCallback());
        mRegisterTask.execute(username, password, apisenseUrl);

    }

    private void performGoogleRegistration() {
        if (mRegisterTask != null)
            return;

        BeeGameManager.getInstance().initialize((BeeGameActivity) getActivity());
        BeeGameManager.getInstance().connectPlayer();

        mRegisterTask = new GoogleRegisterTask(APISENSE.apisense(), new OnUserRegisteredCallback());
        mRegisterTask.execute("");

    }

    private class OnUserRegisteredCallback implements AsyncTasksCallbacks {
        @Override
        public void onTaskCompleted(int result, Object response) {
            Log.i(TAG, "Register result: " + result);
            Log.i(TAG, "Register details: " + response);
            if (result == BeeApplication.ASYNC_SUCCESS) {
                Intent intent = new Intent(getActivity(), HomeActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | IntentCompat.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        }

        public void onTaskCanceled() {
            fr.inria.asl.utils.Log.getInstance().i("BeeGoogle", "Task canceled");

            mRegisterTask.execute(username, password, apisenseUrl);
        }
    }

    private class GoogleRegisterTask extends RegisterTask {

        private ProgressDialog dialog;

        public GoogleRegisterTask(BeeSenseServiceManager apiServices, AsyncTasksCallbacks listener) {
            super(apiServices, listener);

            this.dialog = new ProgressDialog(getActivity());
            this.dialog.setMessage("Connexion");
        }

        @Override
        protected String doInBackground(String... params) {

            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    dialog.show();
                }
            });

            People.LoadPeopleResult result = Plus.PeopleApi.loadConnected(BeeGameManager.getInstance().getGoogleApiClient()).await();
            Person currentPlayer = result.getPersonBuffer().get(0);

            fr.inria.asl.utils.Log.getInstance().i("BeeGoogle", "Person : " + currentPlayer);

            if (currentPlayer == null) {
                fr.inria.asl.utils.Log.getInstance().i("BeeGoogle", "Person failed");

                cancel(false);
                return "";
            }

            return super.doInBackground(currentPlayer.getNickname(), currentPlayer.getNickname(), apisenseUrl);
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            this.dialog.dismiss();
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            this.dialog.dismiss();
        }
    }


}
