package com.apisense.bee.ui.adapter;

import android.util.Log;

import java.util.Collections;
import java.util.List;

import io.apisense.sdk.core.store.Crop;

class CropSorter implements Sorter<Crop> {
    private static final String TAG = "CropComparator";
    private final AvailableExperimentsRecyclerAdapter adapter;
    private final List<Crop> crops;

    CropSorter(AvailableExperimentsRecyclerAdapter adapter, List<Crop> crops) {
        this.adapter = adapter;
        this.crops = crops;
    }

    @Override
    public void sort(SortComparator<Crop> comparator) {
        Log.d(TAG, "Sorting crops with field: " + comparator);
        Collections.sort(crops, comparator.getComparator());
        adapter.setAvailableCrops(crops);
        adapter.notifyDataSetChanged();
    }
}
