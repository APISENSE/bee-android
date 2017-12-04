package com.apisense.bee.ui.adapter.filter;

import android.widget.Filter;

import com.apisense.bee.ui.adapter.ExperimentsRecyclerAdapter;

import java.util.ArrayList;
import java.util.List;

import io.apisense.sdk.core.store.Crop;

public class CropFilter extends Filter {
    private final ExperimentsRecyclerAdapter adapter;
    private final List<Crop> crops;

    public CropFilter(ExperimentsRecyclerAdapter adapter, List<Crop> crops) {
        this.adapter = adapter;
        this.crops = crops;
    }

    @Override
    protected FilterResults performFiltering(CharSequence constraint) {
        if (constraint.length() == 0) {
            return CropResultHolder.buildResult(crops);
        } else {
            return searchSubstring(constraint);
        }
    }

    private FilterResults searchSubstring(CharSequence constraint) {
        ArrayList<Crop> filtered = new ArrayList<>();
        for (Crop crop : crops) {
            String filter = constraint.toString();
            if (isContained(crop.getName(), filter)
                    || isContained(crop.getOwner(), filter)
                    || isContained(crop.getLongDescription(), filter)
                    || isContained(crop.getShortDescription(), filter)) {
                filtered.add(crop);
            }
        }
        return CropResultHolder.buildResult(filtered);
    }

    private boolean isContained(String field, String constraint) {
        return field.toLowerCase().contains(constraint.toLowerCase());
    }


    @Override
    protected void publishResults(CharSequence constraint, FilterResults results) {
        if (results.values instanceof CropResultHolder) {
            adapter.setCrops(((CropResultHolder) results.values).results);
            adapter.notifyDataSetChanged();
        }
    }

    /**
     * Wrap the List of crops in order to avoid a nasty cast.
     */
    private static final class CropResultHolder {
        List<Crop> results;

        private CropResultHolder(List<Crop> results) {
            this.results = results;
        }

        static FilterResults buildResult(List<Crop> filtered) {
            FilterResults results = new FilterResults();
            results.values = new CropResultHolder(filtered);
            results.count = filtered.size();
            return results;
        }

    }
}
