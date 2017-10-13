package com.apisense.bee.ui.fragment;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.apisense.bee.BeeApplication;
import com.apisense.bee.R;
import com.apisense.bee.callbacks.BeeAPSCallback;
import com.apisense.bee.ui.activity.HomeActivity;
import com.apisense.bee.ui.adapter.DividerItemDecoration;
import com.apisense.bee.ui.adapter.SubscribedExperimentsRecyclerAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.apisense.sdk.APISENSE;
import io.apisense.sdk.core.store.Crop;

public class HomeFragment extends BaseFragment {

    private final String TAG = getClass().getSimpleName();

    @BindView(R.id.store) FloatingActionButton storeButton;
    @BindView(R.id.home_experiment_lists) RecyclerView mRecyclerView;
    @BindView(R.id.home_empty_list) TextView mEmptyHome;

    private OnStoreClickedListener mStoreListener;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private APISENSE.Sdk apisenseSdk;

    public interface OnStoreClickedListener {
        void switchToStore();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.fragment_home, container, false);
        ButterKnife.bind(this, view);
        apisenseSdk = ((BeeApplication) getActivity().getApplication()).getSdk();
        mStoreListener = (OnStoreClickedListener) getActivity();

        homeActivity.getSupportActionBar().setTitle(R.string.title_activity_home);
        homeActivity.selectDrawerItem(HomeActivity.DRAWER_HOME_IDENTIFIER);

        mRecyclerView.setHasFixedSize(true); // Performances
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity()));

        retrieveActiveExperiments();

        apisenseSdk.getCropManager().synchroniseSubscriptions(new OnCropModifiedOnStartup());

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        retrieveActiveExperiments();
    }

    /* onClick */

    @OnClick(R.id.store)
    public void doGoToStore(View storeButton) {
        mStoreListener.switchToStore();
    }

    /* Crop managment */

    public void setExperiments(ArrayList<Crop> experiments) {
        mAdapter = new SubscribedExperimentsRecyclerAdapter(experiments, new SubscribedExperimentsRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Crop crop) {
                Bundle extra = new Bundle();
                extra.putParcelable("crop", crop);

                HomeDetailsFragment homeDetailsFragment = new HomeDetailsFragment();
                homeDetailsFragment.setArguments(extra);
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.exp_container, homeDetailsFragment)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .addToBackStack(null)
                        .commit();
            }
        });
        mRecyclerView.setAdapter(mAdapter);
    }

    private void retrieveActiveExperiments() {
        apisenseSdk.getCropManager().getSubscriptions(new ExperimentListRetrievedCallback());
    }

    /* Callbacks */

    private class ExperimentListRetrievedCallback extends BeeAPSCallback<List<Crop>> {
        public ExperimentListRetrievedCallback() {
            super(getActivity());
        }

        @Override
        public void onDone(List<Crop> response) {
            Log.i(TAG, "number of Active Experiments: " + response.size());
            if(response.isEmpty()) {
                mEmptyHome.setVisibility(View.VISIBLE);
            } else {
                mEmptyHome.setVisibility(View.GONE);
                setExperiments(new ArrayList<Crop>(response));
            }
        }
    }

    private class OnCropModifiedOnStartup extends BeeAPSCallback<Crop> {
        public OnCropModifiedOnStartup() {
            super(getActivity());
        }

        @Override
        public void onDone(Crop crop) {
            Log.d(TAG, "Crop" + crop.getName() + "started back");
            retrieveActiveExperiments();
            mAdapter.notifyDataSetChanged();
        }
    }
}
