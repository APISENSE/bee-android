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
import com.apisense.bee.ui.activity.SlideshowActivity;

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
                String login = mPseudoEditText.getText().toString();
                String password = mPasswordEditText.getText().toString();
                if (isInputCorrect(login, password)) {
                    Intent intent = createLoginIntent(login, password);
                    getActivity().setResult(Activity.RESULT_OK, intent);
                    getActivity().finish();
                } else {
                    Toast.makeText(getActivity(), getResources().getString(R.string.empty_field), Toast.LENGTH_LONG).show();
                }
            }
        });

        // Inflate the layout for this fragment
        return root;
    }

    /**
     * Check if sign in form is correctly filled
     * @return true or false
     * @param login
     * @param password
     */
    private boolean isInputCorrect(String login, String password) {
        return !TextUtils.isEmpty(login) && !TextUtils.isEmpty(password);
    }

    private Intent createLoginIntent(String login, String password) {
        Intent intent = new Intent();
        intent.putExtra(SlideshowActivity.KEY_AUTHENTICATION_ACTION,SlideshowActivity.LOGIN_ACTION);
        intent.putExtra(SlideshowActivity.LOGIN_PSEUDO, login);
        intent.putExtra(SlideshowActivity.LOGIN_PWD, password);
        return intent;
    }
}
