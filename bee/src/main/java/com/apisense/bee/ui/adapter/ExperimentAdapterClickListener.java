package com.apisense.bee.ui.adapter;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.inputmethod.InputMethodManager;

import com.apisense.bee.R;

import io.apisense.sdk.core.store.Crop;

public abstract class ExperimentAdapterClickListener implements ExperimentsRecyclerAdapter.OnItemClickListener {
    private final FragmentManager manager;
    private final InputMethodManager inputManager;

    protected ExperimentAdapterClickListener(FragmentActivity activity) {
        this.manager = activity.getSupportFragmentManager();
        this.inputManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
    }

    @Override
    public final void onItemClick(Crop crop) {
        Bundle extra = new Bundle();
        extra.putParcelable("crop", crop);

        inputManager.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);

        Fragment fragment = newFragment();
        fragment.setArguments(extra);
        manager.beginTransaction()
                .replace(R.id.exp_container, fragment)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .addToBackStack(null)
                .commit();
    }

    protected abstract Fragment newFragment();
}
