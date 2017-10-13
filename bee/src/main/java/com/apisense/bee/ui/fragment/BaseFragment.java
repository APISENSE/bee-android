package com.apisense.bee.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.apisense.bee.ui.activity.HomeActivity;

public class BaseFragment extends Fragment {

    protected HomeActivity homeActivity;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        homeActivity = ((HomeActivity) getActivity());
        return super.onCreateView(inflater, container, savedInstanceState);
    }
}
