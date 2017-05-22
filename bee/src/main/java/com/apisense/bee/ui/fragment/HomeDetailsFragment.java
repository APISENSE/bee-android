package com.apisense.bee.ui.fragment;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.apisense.bee.R;
import com.apisense.bee.callbacks.OnCropStarted;
import com.apisense.bee.callbacks.OnCropStopped;
import com.apisense.bee.callbacks.OnCropUnsubscribed;
import com.apisense.bee.utils.CropPermissionHandler;
import com.apisense.bee.widget.UploadedDataGraph;
import com.apisense.bee.widget.VisualizationPagerAdapter;
import com.rd.PageIndicatorView;

import java.util.ArrayList;
import java.util.Collection;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.apisense.sdk.core.statistics.CropLocalStatistics;
import io.apisense.sdk.core.statistics.UploadedEntry;
import io.apisense.sdk.core.store.Crop;
import io.apisense.sting.visualization.VisualizationManager;

public class HomeDetailsFragment extends CommonDetailsFragment {
    @BindView(R.id.viewpager)
    ViewPager viewPager;
    @BindView(R.id.pagerIndicator)
    PageIndicatorView pagerIndicator;
    VisualizationPagerAdapter pagerAdapter;
    int currentPagerPosition = 0;

    private MenuItem mStartButton;
    private MenuItem mStopButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.fragment_home_details, container, false);
        if (homeActivity.getSupportActionBar() != null) {
            homeActivity.getSupportActionBar().setTitle(R.string.title_activity_experiment_details);
        }

        unbinder = ButterKnife.bind(this, view);

        VisualizationManager visManager = VisualizationManager.getInstance();

        View statisticsGraph = getStatisticsGraph(inflater, apisenseSdk.getStatisticsManager().getCropUsage(crop));

        final ArrayList<View> visualizations = new ArrayList<>();
        visualizations.add(statisticsGraph);
        visualizations.addAll(visManager.getCropVisualizations(getContext(), crop));

        pagerAdapter = new VisualizationPagerAdapter(visualizations);

        viewPager.setAdapter(pagerAdapter);
        viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if (currentPagerPosition != position) {
                    currentPagerPosition = position;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                if (state == ViewPager.SCROLL_STATE_IDLE) {
                    //refresh visualization when scroll stops
                    pagerAdapter.invalidateView(currentPagerPosition);
                }
            }
        });

        viewPager.post(new Runnable() {
            @Override
            public void run() {
                int width = viewPager.getWidth();

                float indicatorWidth = width / visualizations.size();
                float indicatorRadius = (indicatorWidth * 6 / 10) / 2;
                float indicatorPadding = indicatorWidth * 4 / 10;

                int defaultRadius = (int) getResources().getDimension(R.dimen.viewpager_indicator_radius);

                if (defaultRadius > indicatorRadius) {
                    pagerIndicator.setRadius(indicatorRadius);
                    pagerIndicator.setPadding(indicatorPadding);
                }
            }
        });

        return view;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.detail_action_start:
                doStartStop();
                break;
            case R.id.detail_action_stop:
                doStartStop();
                break;
            case R.id.detail_action_unsubscribe:
                doSubscribeUnsubscribe();
                break;
            case R.id.detail_action_update:
                doUpdate();
                break;
        }
        return false;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.home_experiment_details, menu);
        super.onCreateOptionsMenu(menu, inflater);

        mStartButton = menu.getItem(0);
        mStopButton = menu.getItem(1);

        if (apisenseSdk.getCropManager().isRunning(crop)) {
            displayStopButton();
        } else {
            displayStartButton();
        }
    }

    // Buttons Handlers
    private void doStartStop() {
        if (apisenseSdk.getCropManager().isRunning(crop)) {
            apisenseSdk.getCropManager().stop(crop, new OnCropStopped(getActivity()) {
                @Override
                public void onDone(Crop crop) {
                    super.onDone(crop);
                    displayStartButton();
                }
            });
        } else {
            cropPermissionHandler.startOrRequestPermissions();
        }
    }

    private void displayStopButton() {
        mStartButton.setVisible(false);
        mStopButton.setVisible(true);
    }

    private void displayStartButton() {
        mStartButton.setVisible(true);
        mStopButton.setVisible(false);
    }


    @Override
    protected CropPermissionHandler prepareCropPermissionHandler() {
        return new CropPermissionHandler(getActivity(), crop, new OnCropStarted(getActivity()) {
            @Override
            public void onDone(Crop crop) {
                super.onDone(crop);
                displayStopButton();
            }
        });
    }

    // Actions

    private void doSubscribeUnsubscribe() {
        apisenseSdk.getCropManager().unsubscribe(crop, new OnCropUnsubscribed(getActivity(), crop.getName()) {
            @Override
            public void onDone(Crop crop) {
                super.onDone(crop);
                getFragmentManager().popBackStack();
            }
        });
    }

    private View getStatisticsGraph(LayoutInflater inflater, CropLocalStatistics cropUsage) {
        View statisticGraph = inflater.inflate(R.layout.stats_uploaded_data, viewPager, false);
        UploadedData stats = new UploadedData();

        ButterKnife.bind(stats, statisticGraph);

        stats.localTraces.setText(getString(R.string.crop_stats_local_traces, cropUsage.getToUpload()));
        stats.totalTraces.setText(getString(R.string.crop_stats_total_uploaded, cropUsage.getTotalUploaded()));

        Collection<UploadedEntry> uploaded = cropUsage.getUploaded();
        if (uploaded.isEmpty()) {
            stats.chart.setVisibility(View.GONE);
            stats.noUpload.setVisibility(View.VISIBLE);
        } else {
            stats.chart.setValues(cropUsage.getUploaded());
        }

        return statisticGraph;
    }

    class UploadedData {
        @BindView(R.id.local_traces)
        TextView localTraces;
        @BindView(R.id.total_uploaded)
        TextView totalTraces;
        @BindView(R.id.uploaded_chart)
        UploadedDataGraph chart;
        @BindView(R.id.no_upload)
        TextView noUpload;
    }
}
