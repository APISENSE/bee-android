package com.apisense.bee.ui.adapter.sort;

import android.util.Log;

import com.apisense.bee.ui.adapter.ExperimentsRecyclerAdapter;

import java.util.Collections;
import java.util.List;

import io.apisense.sdk.core.store.Crop;

public class CropSorter implements Sorter<Crop> {
    private static final String TAG = "CropComparator";
    private final ExperimentsRecyclerAdapter adapter;
    private final List<Crop> crops;

    public CropSorter(ExperimentsRecyclerAdapter adapter, List<Crop> crops) {
        this.adapter = adapter;
        this.crops = crops;
    }

    @Override
    public void sort(SortComparator<Crop> comparator) {
        Log.d(TAG, "Sorting crops with field: " + comparator);
        Collections.sort(crops, comparator.getComparator());
        adapter.setCrops(crops);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void reverseSort(SortComparator<Crop> comparator) {
        Collections.sort(crops, comparator.getComparator());
        Collections.reverse(crops);
        adapter.setCrops(crops);
        adapter.notifyDataSetChanged();
    }
}
