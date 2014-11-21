package com.apisense.bee.ui.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.apisense.bee.R;
import fr.inria.bsense.APISENSE;

public class SignInFragment extends Fragment {

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
        mPseudoEditText = (EditText) root.findViewById(R.id.signInPseudo);
        mPasswordEditText = (EditText) root.findViewById(R.id.signInPassword);

        // Sign in onClick
        Button mSignInBtn = (Button) root.findViewById(R.id.signInLoginBtn);
        mSignInBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = doLogin(v);
                getActivity().setResult(Activity.RESULT_OK, intent);
                getActivity().finish();
            }
        });

        // Inflate the layout for this fragment
        return root;
    }

    /**
     * Run sign in task in background
     * @param loginButton button pressed to start task
     */
    public Intent doLogin(View loginButton){
        Intent intent = new Intent();
        if (!isInputCorrect()) {
            Toast.makeText(getActivity(), getResources().getString(R.string.empty_field), Toast.LENGTH_LONG).show();
        } else {
            intent = createLoginIntent(mPseudoEditText.getText().toString(), mPasswordEditText.getText().toString());
        }
        return intent;
    }

    /**
     * Check if sign in form is correctly filled
     * @return true or false
     */
    private boolean isInputCorrect() {
        String mPseudo = mPseudoEditText.getText().toString();
        String mPassword = mPasswordEditText.getText().toString();

        return !TextUtils.isEmpty(mPseudo) && !TextUtils.isEmpty(mPassword);
    }

    private Intent createLoginIntent(String login, String password) {
        Intent intent = new Intent();
        // TODO: Use Constants to set extra
        intent.putExtra("action", "login");
        intent.putExtra("login", login);
        intent.putExtra("password", password);
        return intent;
    }
}
