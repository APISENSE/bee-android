package com.apisense.bee.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.apisense.bee.BeeApplication;
import com.apisense.bee.R;
import com.apisense.bee.backend.AsyncTasksCallbacks;
import com.apisense.bee.backend.user.SignInTask;
import com.apisense.bee.ui.activity.HomeActivity;
import fr.inria.bsense.APISENSE;
import fr.inria.bsense.appmodel.Experiment;

public class SignInFragment extends Fragment {

    private Button mSignInBtn;
    private EditText mPseudoEditText;
    private EditText mPasswordEditText;

    private final String TAG = "SignInFragment";

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
     * @return true or false
     */
    private boolean isUserAuthenticated() {
        return APISENSE.apisServerService().isConnected();
    }

    /**
     * Check if sign in form is correctly filled
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
     * @param loginButton button pressed to start task
     */
    public void doLoginLogout(View loginButton){
        if (!isInputCorrect()) {
            Toast.makeText(getActivity(), getResources().getString(R.string.empty_field), Toast.LENGTH_LONG).show();
            return;
        }

        if (isUserAuthenticated()) {
            try {
                APISENSE.apisMobileService().sendAllTrack();
                APISENSE.apisMobileService().stopAllExperiments(0);
                for(Experiment xp: APISENSE.apisMobileService().getInstalledExperiments().values())
                    APISENSE.apisMobileService().uninstallExperiment(xp);
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(getActivity(), R.string.experiment_exception_on_closure, Toast.LENGTH_SHORT).show();
            }
            APISENSE.apisServerService().disconnect();
            mSignInBtn.setText("Login");
            Toast.makeText(getActivity(), R.string.status_changed_to_anonymous, Toast.LENGTH_SHORT).show();
        } else {
            SignInTask signInTask = new SignInTask(new AsyncTasksCallbacks() {
                @Override
                public void onTaskCompleted(Object response, String details) {
                    Log.i(TAG, "Connection result: " + response);
                    Log.i(TAG, "Connection details: " + details);
                    if ((Integer) response == BeeApplication.ASYNC_SUCCESS) {
                        mSignInBtn.setText(getString(R.string.logout));
                        Intent intent = new Intent(getActivity(), HomeActivity.class);
                        startActivity(intent);
                    } else {
                        Toast.makeText(getActivity(), getResources().getString(R.string.failed_to_connect), Toast.LENGTH_LONG).show();
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
