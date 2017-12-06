package com.apisense.bee.ui.fragment;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.apisense.bee.R;
import com.apisense.bee.callbacks.BeeAPSCallback;
import com.apisense.bee.ui.activity.HomeActivity;
import com.apisense.bee.ui.adapter.DividerItemDecoration;
import com.apisense.bee.ui.adapter.SubscribedExperimentsRecyclerAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.apisense.sdk.adapter.SimpleAPSCallback;
import io.apisense.sdk.core.store.Crop;

public class HomeFragment extends SortedCropsFragment {
    private final String TAG = getClass().getSimpleName();

    @BindView(R.id.store)
    FloatingActionButton storeButton;
    @BindView(R.id.home_experiment_lists)
    RecyclerView recyclerView;
    @BindView(R.id.home_empty_list)
    TextView emptyListView;

    private OnStoreClickedListener mStoreListener;

    private Timer autoUpdateRunning;

    public interface OnStoreClickedListener {
        void switchToStore();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.fragment_home, container, false);
        ButterKnife.bind(this, view);
        mStoreListener = (OnStoreClickedListener) getActivity();

        homeActivity.getSupportActionBar().setTitle(R.string.title_activity_home);
        homeActivity.selectDrawerItem(HomeActivity.DRAWER_HOME_IDENTIFIER);

        experimentsAdapter = new SubscribedExperimentsRecyclerAdapter(getActivity());
        recyclerView.setAdapter(experimentsAdapter);

        recyclerView.setHasFixedSize(true); // Performances
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity()));

        retrieveActiveExperiments();

        apisenseSdk.getCropManager().synchroniseSubscriptions(new OnCropModifiedOnStartup());

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        retrieveActiveExperiments();
        autoUpdateRunning = new Timer();
        autoUpdateRunning.schedule(new TimerTask() {
            @Override
            public void run() {
                getActivity().runOnUiThread(new Runnable() {
                    public void run() {
                        updateDisplayedExperiments();
                    }
                });
            }
        }, 0, 3000); // updates each 3 seconds
    }

    private void updateDisplayedExperiments() {
        apisenseSdk.getCropManager().getSubscriptions(new SimpleAPSCallback<List<Crop>>() {
            @Override
            public void onDone(List<Crop> crops) {
                final List<Crop> displayed = ((SubscribedExperimentsRecyclerAdapter) experimentsAdapter).getCrops();
                final Map<String, Crop> selected = new HashMap<>();
                final List<Crop> updated = new ArrayList<>();

                // Retrieve the new version of the selected crops.
                for (Crop crop : crops) {
                    if (displayed.contains(crop)) {
                        selected.put(crop.getLocation(), crop);
                    }
                }

                // Insert the crop in the same order as before.
                for (Crop crop : displayed) {
                    updated.add(selected.get(crop.getLocation()));
                }
                experimentsAdapter.setCrops(updated);
                experimentsAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        autoUpdateRunning.cancel();
    }

    @OnClick(R.id.store)
    public void doGoToStore(View storeButton) {
        mStoreListener.switchToStore();
    }

    private void retrieveActiveExperiments() {
        apisenseSdk.getCropManager().getSubscriptions(new OnExperimentsRetrieved(getActivity(), emptyListView));
    }

    private class OnCropModifiedOnStartup extends BeeAPSCallback<Crop> {
        OnCropModifiedOnStartup() {
            super(getActivity());
        }

        @Override
        public void onDone(Crop crop) {
            Log.d(TAG, "Crop " + crop.getName() + " started back");
            retrieveActiveExperiments();
            experimentsAdapter.notifyDataSetChanged();
        }
    }
}
