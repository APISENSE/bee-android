package com.apisense.bee.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.apisense.bee.BeeApplication;
import com.apisense.bee.R;
import com.apisense.bee.utils.SensorsDrawer;

import java.util.List;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.apisense.sdk.APISENSE;
import io.apisense.sdk.core.store.Crop;
import io.apisense.sting.lib.Sensor;

public class AvailableExperimentsRecyclerAdapter extends
        RecyclerView.Adapter<AvailableExperimentsRecyclerAdapter.ViewHolder> {

    private final String TAG = getClass().getSimpleName();
    private List<Crop> mAvailableCrops;
    private OnItemClickListener mListener;
    private Context context;
    private SensorsDrawer sensorsDrawer;

    public interface OnItemClickListener {
        void onItemClick(Crop crop);
    }

    public AvailableExperimentsRecyclerAdapter(List<Crop> installedCrops, OnItemClickListener listener) {
        mAvailableCrops = installedCrops;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        APISENSE.Sdk apisenseSdk = ((BeeApplication) context.getApplicationContext()).getSdk();
        Set<Sensor> mAvailableSensors = apisenseSdk.getPreferencesManager().retrieveAvailableSensors();
        sensorsDrawer = new SensorsDrawer(mAvailableSensors);
        LayoutInflater inflater = LayoutInflater.from(context);
        View cropView = inflater.inflate(R.layout.list_item_store_experiment, parent, false);

        return new AvailableExperimentsRecyclerAdapter.ViewHolder(cropView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Crop crop = mAvailableCrops.get(position);

        holder.mCropTitle.setText(crop.getName());
        holder.mCropDescription.setText(crop.getShortDescription());
        holder.mCropVersion.setText(context.getString(R.string.exp_details_version, crop.getVersion()));

        sensorsDrawer.draw(context, holder.mSensorsContainer,  crop.getUsedStings());

        holder.bind(crop, mListener);
    }

    @Override
    public int getItemCount() {
        return mAvailableCrops.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.store_item_name) TextView mCropTitle;
        @BindView(R.id.store_item_description) TextView mCropDescription;
        @BindView(R.id.store_sensors_container) ViewGroup mSensorsContainer;
        @BindView(R.id.store_item_version) TextView mCropVersion;

        public ViewHolder(View itemView) {
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
