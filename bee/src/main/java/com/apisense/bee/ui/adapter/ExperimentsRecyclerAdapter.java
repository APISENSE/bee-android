package com.apisense.bee.ui.adapter;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import com.apisense.bee.BeeApplication;
import com.apisense.bee.ui.adapter.filter.CropFilter;
import com.apisense.bee.ui.adapter.sort.CropSorter;
import com.apisense.bee.ui.adapter.sort.Sorter;
import com.apisense.bee.utils.SensorsDrawer;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import butterknife.ButterKnife;
import io.apisense.sdk.APISENSE;
import io.apisense.sdk.core.store.Crop;
import io.apisense.sting.lib.Sensor;

public abstract class ExperimentsRecyclerAdapter<T extends ExperimentsRecyclerAdapter.ViewHolder>
        extends RecyclerView.Adapter<T> implements Filterable {

    private List<Crop> crops;
    private ExperimentsRecyclerAdapter.OnItemClickListener listener;

    private CropFilter filter;
    private CropSorter comparator;

    protected Context context;
    protected SensorsDrawer sensorsDrawer;
    protected APISENSE.Sdk apisenseSdk;

    ExperimentsRecyclerAdapter(ExperimentsRecyclerAdapter.OnItemClickListener listener) {
        this(Collections.<Crop>emptyList(), listener);
    }

    private ExperimentsRecyclerAdapter(List<Crop> installedCrops, ExperimentsRecyclerAdapter.OnItemClickListener listener) {
        this.crops = installedCrops;
        this.listener = listener;
    }

    @Override
    public final T onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        apisenseSdk = ((BeeApplication) context.getApplicationContext()).getSdk();
        Set<Sensor> availableSensors = apisenseSdk.getPreferencesManager().retrieveAvailableSensors();
        sensorsDrawer = new SensorsDrawer(availableSensors);

        return onCreateExperimentHolder(parent, viewType);
    }

    protected abstract T onCreateExperimentHolder(ViewGroup parent, int viewType);

    @Override
    public void onBindViewHolder(T holder, int position) {
        Crop crop = crops.get(position);
        drawCrop(holder, crop);
        holder.bind(crop, listener);
    }

    protected abstract void drawCrop(T holder, Crop crop);

    @Override
    public Filter getFilter() {
        if (filter == null) {
            filter = new CropFilter(this, crops);
        }
        return filter;
    }

    public Sorter<Crop> getComparator() {
        if (comparator == null) {
            this.comparator = new CropSorter(this, crops);
        }
        return comparator;
    }

    @Override
    public int getItemCount() {
        return crops.size();
    }

    public void setCrops(List<Crop> availableCrops) {
        this.crops = availableCrops;
    }

    public List<Crop> getCrops() {
        return Collections.unmodifiableList(crops);
    }

    /**
     * Response to execute when a specific crop is clicked on the {@link ExperimentsRecyclerAdapter}.
     */
    public interface OnItemClickListener {
        void onItemClick(Crop crop);
    }

    /**
     * {@link RecyclerView.ViewHolder} automatically binding view ids to their attributes in subclasses.
     */
    protected static class ViewHolder extends RecyclerView.ViewHolder {
        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bind(final Crop crop, final AvailableExperimentsRecyclerAdapter.OnItemClickListener listener) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(crop);
                }
            });
        }
    }
}
