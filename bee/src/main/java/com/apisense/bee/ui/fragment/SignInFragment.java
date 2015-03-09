package com.apisense.bee.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.IntentCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.apisense.bee.BeeApplication;
import com.apisense.bee.R;
import com.apisense.bee.backend.AsyncTasksCallbacks;
import com.apisense.bee.backend.user.SignInTask;
import com.apisense.bee.ui.activity.HomeActivity;
import com.gc.materialdesign.widgets.SnackBar;

import fr.inria.bsense.APISENSE;
import fr.inria.bsense.appmodel.Experiment;

public class SignInFragment extends Fragment {

    private final String TAG = "SignInFragment";
    private Button mSignInBtn;
    private EditText mPseudoEditText;
    private EditText mPasswordEditText;

    /**
     * Default constructor
     */
    public SignInFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_sign_in, container, false);


        // Views
        mSignInBtn = (Button) root.findViewById(R.id.signInLoginBtn);
        mPseudoEditText = (EditText) root.findViewById(R.id.signInPseudo);
        mPasswordEditText = (EditText) root.findViewById(R.id.signInPassword);

        // Sign in onClick
        mSignInBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                doLoginLogout(v);
            }
        });

        // Inflate the layout for this fragment
        return root;
    }

    // - - - - -

    /**
     * Check if current user is authenticated
     *
     * @return true or false
     */
    private boolean isUserAuthenticated() {
        return APISENSE.apisServerService().isConnected();
    }

    /**
     * Check if sign in form is correctly filled
     *
     * @return true or false
     */
    private boolean isInputCorrect() {
        String mPseudo = mPseudoEditText.getText().toString();
        String mPassword = mPasswordEditText.getText().toString();

        if (TextUtils.isEmpty(mPseudo) || TextUtils.isEmpty(mPassword)) {
            return false;
        }

        return true;
    }

    /**
     * Run sign in task in background
     *
     * @param loginButton button pressed to start task
     */
    public void doLoginLogout(View loginButton) {
        if (!isInputCorrect()) {
            new SnackBar(getActivity(), getResources().getString(R.string.empty_field), null, null).show();
            return;
        }

        if (isUserAuthenticated()) {
            try {
                APISENSE.apisMobileService().sendAllTrack();
                APISENSE.apisMobileService().stopAllExperiments(0);
                for (Experiment xp : APISENSE.apisMobileService().getInstalledExperiments().values())
                    APISENSE.apisMobileService().uninstallExperiment(xp);
            } catch (Exception e) {
                e.printStackTrace();
                new SnackBar(getActivity(), getResources().getString(R.string.experiment_exception_on_closure), null, null).show();
            }
            APISENSE.apisServerService().disconnect();
            mSignInBtn.setText("Login");
            new SnackBar(getActivity(), getResources().getString(R.string.status_changed_to_anonymous), null, null).show();
        } else {
            SignInTask signInTask = new SignInTask(APISENSE.apisense(), new AsyncTasksCallbacks() {
                @Override
                public void onTaskCompleted(int result, Object response) {
                    Log.i(TAG, "Connection result: " + result);
                    Log.i(TAG, "Connection details: " + response);
                    if ((Integer) result == BeeApplication.ASYNC_SUCCESS) {
                        mSignInBtn.setText(getString(R.string.logout));
                        Intent intent = new Intent(getActivity(), HomeActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | IntentCompat.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    } else {
                        new SnackBar(getActivity(), getResources().getString(R.string.failed_to_connect), null, null).show();
                    }
                }

                @Override
                public void onTaskCanceled() {

                }
            });

            signInTask.execute(mPseudoEditText.getText().toString(), mPasswordEditText.getText().toString(), "");
        }
    }
}
