package com.apisense.bee.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.apisense.bee.R;
import com.apisense.bee.ui.activity.SlideshowActivity;
import com.apisense.bee.widget.ApisenseTextView;

/**
 * Created by Warnant on 05-03-15.
 */
public class ConnectivityFragment extends Fragment implements View.OnClickListener {

    private ApisenseTextView atvAnonymousCo;
    private Button btnRegister;
    private Button btnLogin;

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
                getActivity().finish();
                break;
            case R.id.btnRegister:
                Intent slideIntentRegister = new Intent(getActivity(), SlideshowActivity.class);
                slideIntentRegister.putExtra("goTo", "register");
                startActivity(slideIntentRegister);
                getActivity().finish();
                break;
            default:
                return;
        }
    }
}
