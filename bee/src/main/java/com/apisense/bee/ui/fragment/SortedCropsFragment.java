package com.apisense.bee.ui.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.apisense.bee.R;
import com.apisense.bee.ui.adapter.CropField;
import com.apisense.bee.ui.adapter.ExperimentsRecyclerAdapter;
import com.apisense.bee.ui.adapter.SortComparator;

import java.util.List;

import io.apisense.sdk.core.store.Crop;

abstract class SortedCropsFragment extends BaseFragment {
    private static final String TAG = "SortedCropsFragment";
    private boolean authorSortAscending = true;
    private boolean nameSortAscending = true;
    private Menu sortMenu;

    protected ExperimentsRecyclerAdapter experimentsAdapter;

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
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_store_action_sort_name:
                sort(CropField.NAME, nameSortAscending, item);
                nameSortAscending = !nameSortAscending;
                break;
            case R.id.menu_store_action_sort_author:
                sort(CropField.AUTHOR, authorSortAscending, item);
                authorSortAscending = !authorSortAscending;
                break;
            default:
                Log.w(TAG, "Unable to retrieve id in store menu (" + item.getItemId() + ")");
        }
        return super.onOptionsItemSelected(item);
    }

    private void sort(SortComparator<Crop> sortComparator, boolean ascending, MenuItem item) {
        if (ascending) {
            experimentsAdapter.getComparator().sort(sortComparator);
            item.setIcon(R.drawable.ic_sort_az_white);
        } else {
            experimentsAdapter.getComparator().reverseSort(sortComparator);
            item.setIcon(R.drawable.ic_sort_za_white);
        }

        // Set other menus to diamond arrow

        for (int i = 0; i < sortMenu.size(); i++) {
            if (!item.equals(sortMenu.getItem(i))) {
                sortMenu.getItem(i).setIcon(R.drawable.ic_sort_unsorted_white);
            }
        }
    }
}
