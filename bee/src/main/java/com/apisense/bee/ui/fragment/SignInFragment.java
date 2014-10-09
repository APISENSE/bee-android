package com.apisense.bee.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.apisense.bee.R;
import com.apisense.bee.backend.AsyncTasksCallbacks;
import com.apisense.bee.backend.SignInTask;
import com.apisense.bee.ui.activity.HomeActivity;
import com.apisense.bee.ui.activity.SlideshowActivity;
import fr.inria.bsense.APISENSE;
import fr.inria.bsense.appmodel.Experiment;

public class SignInFragment extends Fragment {

    private Button mSignInBtn;
    private EditText mEmail;
    private EditText mPassword;

    private final String TAG = "SignInFragment";

    public SignInFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_sign_in, container, false);


        // Views
        mSignInBtn = (Button) root.findViewById(R.id.signInLoginBtn);
        mEmail = (EditText) root.findViewById(R.id.signInEmail);
        mPassword = (EditText) root.findViewById(R.id.signInPassword);

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

    private boolean isUserAuthenticated() {
        return APISENSE.apisServerService().isConnected();
    }

    public void doLoginLogout(View loginButton){
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
            mSignInBtn.setText("Log in");
            Toast.makeText(getActivity(), R.string.status_changed_to_anonymous, Toast.LENGTH_SHORT).show();
        } else {
            SignInTask signInTask = new SignInTask(new AsyncTasksCallbacks() {
                @Override
                public void onTaskCompleted(Object response) {
                    Log.i(TAG, "Connection result: " + response);
                    if (response.equals("success")) {
                        mSignInBtn.setText("Disconnect");
                        Intent intent = new Intent(getActivity(), HomeActivity.class);
                        startActivity(intent);
                    }
                }

                @Override
                public void onTaskCanceled() {

                }
            });

            signInTask.execute(mEmail.getText().toString(), mPassword.getText().toString(), "");
        }
    }
}
