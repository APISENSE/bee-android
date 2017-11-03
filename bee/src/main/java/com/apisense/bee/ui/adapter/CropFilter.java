package com.apisense.bee.ui.adapter;

import android.widget.Filter;

import java.util.ArrayList;
import java.util.List;

import io.apisense.sdk.core.store.Crop;

class CropFilter extends Filter {
    private final ExperimentsRecyclerAdapter adapter;
    private final List<Crop> crops;

    CropFilter(ExperimentsRecyclerAdapter adapter, List<Crop> crops) {
        this.adapter = adapter;
        this.crops = crops;
    }

    @Override
    protected FilterResults performFiltering(CharSequence constraint) {
        if (constraint.length() == 0) {
            return buildResult(crops);
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
        return buildResult(filtered);
    }

    private boolean isContained(String field, String constraint) {
        return field.toLowerCase().contains(constraint.toLowerCase());
    }

    private FilterResults buildResult(List<Crop> filtered) {
        FilterResults results = new FilterResults();
        results.values = filtered;
        results.count = filtered.size();
        return results;
    }

    @Override
    protected void publishResults(CharSequence constraint, FilterResults results) {
        adapter.setCrops((List<Crop>) results.values);
        adapter.notifyDataSetChanged();
    }
}
