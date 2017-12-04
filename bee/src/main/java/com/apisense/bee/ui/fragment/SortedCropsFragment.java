package com.apisense.bee.ui.fragment;


import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.apisense.bee.BeeApplication;
import com.apisense.bee.R;
import com.apisense.bee.callbacks.BeeAPSCallback;
import com.apisense.bee.ui.adapter.ExperimentsRecyclerAdapter;
import com.apisense.bee.ui.adapter.sort.CropField;
import com.apisense.bee.ui.adapter.sort.SortComparator;

import java.util.ArrayList;
import java.util.List;

import io.apisense.sdk.APISENSE;
import io.apisense.sdk.core.store.Crop;

abstract class SortedCropsFragment extends BaseFragment {
    private static final String TAG = "SortedCropsFragment";
    private Menu sortMenu;

    protected ExperimentsRecyclerAdapter experimentsAdapter;
    protected APISENSE.Sdk apisenseSdk;

    SparseArray<SortComparator> sortedMapping = new SparseArray<>(4);
    SparseBooleanArray ascendingMapping = new SparseBooleanArray(4);

    /**
     * Change the experimentsAdapter dataSet with a newly fetched List of Experiment
     *
     * @param experiments The new list of experiments to show
     */
    protected void setExperiments(List<Crop> experiments) {
        experimentsAdapter.setCrops(experiments);
        experimentsAdapter.notifyDataSetChanged();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        apisenseSdk = ((BeeApplication) getActivity().getApplication()).getSdk();
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.store_search, menu);

        final MenuItem searchItem = menu.findItem(R.id.menu_store_action_search);
        sortMenu = menu.findItem(R.id.menu_store_action_sort).getSubMenu();
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.v(TAG, "Looking for crops using substring: " + query);
                experimentsAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                Log.v(TAG, "Looking for crops using substring: " + query);
                experimentsAdapter.getFilter().filter(query);
                return false;
            }
        });

        initMappings();
        setOtherIdle(null);
    }

    protected void initMappings() {
        sortedMapping.put(R.id.menu_store_action_sort_name, CropField.NAME);
        ascendingMapping.put(R.id.menu_store_action_sort_name, CropField.NAME.getType().isDefaultAscending);

        sortedMapping.put(R.id.menu_store_action_sort_author, CropField.AUTHOR);
        ascendingMapping.put(R.id.menu_store_action_sort_author, CropField.AUTHOR.getType().isDefaultAscending);

        sortedMapping.put(R.id.menu_store_action_sort_subscribers, CropField.SUBSCRIBERS);
        ascendingMapping.put(R.id.menu_store_action_sort_subscribers, CropField.SUBSCRIBERS.getType().isDefaultAscending);

        sortedMapping.put(R.id.menu_store_action_sort_update, CropField.UPDATE);
        ascendingMapping.put(R.id.menu_store_action_sort_update, CropField.UPDATE.getType().isDefaultAscending);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        sort(item);
        return super.onOptionsItemSelected(item);
    }

    private void sort(MenuItem item) {
        SortComparator<Crop> sortComparator = sortedMapping.get(item.getItemId());

        if (sortComparator == null) {
            Log.w(TAG, "Unable to retrieve id in store menu (" + item.getItemId() + ")");
            return;
        }

        if (ascendingMapping.get(item.getItemId())) {
            experimentsAdapter.getComparator().sort(sortComparator);
            item.setIcon(sortComparator.getType().upDrawableId);
        } else {
            experimentsAdapter.getComparator().reverseSort(sortComparator);
            item.setIcon(sortComparator.getType().downDrawableId);
        }

        setOtherIdle(item);
        invertNextSortOrder(item);
    }

    /**
     * Set the sorter menus to idle state.
     *
     * @param keptItem The menu item to not set to idle. Will set everything to idle if null.
     */
    private void setOtherIdle(@Nullable MenuItem keptItem) {
        MenuItem currentItem;
        SortComparator parsedComparator;
        for (int i = 0; i < sortMenu.size(); i++) {
            currentItem = sortMenu.getItem(i);

            if (keptItem == null || !keptItem.equals(currentItem)) {
                parsedComparator = sortedMapping.get(currentItem.getItemId());
                currentItem.setIcon(parsedComparator.getType().idleDrawableId);
                ascendingMapping.put(currentItem.getItemId(), parsedComparator.getType().isDefaultAscending);
            }
        }
    }

    private void invertNextSortOrder(MenuItem item) {
        int itemId = item.getItemId();
        ascendingMapping.put(itemId, !ascendingMapping.get(itemId));
    }

    protected final class OnExperimentsRetrieved extends BeeAPSCallback<List<Crop>> {
        private TextView emptyListView;

        OnExperimentsRetrieved(Activity activity, TextView emptyListView) {
            super(activity);
            this.emptyListView = emptyListView;
        }

        @Override
        public void onDone(List<Crop> crops) {
            Log.i(TAG, "number of Active Experiments: " + crops.size());
            if (crops.isEmpty()) {
                emptyListView.setVisibility(View.VISIBLE);
            } else {
                emptyListView.setVisibility(View.GONE);
                setExperiments(new ArrayList<>(crops));
            }
        }
    }
}
