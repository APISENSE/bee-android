package com.apisense.bee.ui.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.apisense.bee.BeeApplication;
import com.apisense.bee.R;
import com.apisense.bee.utils.SensorsDrawer;
import com.apisense.sdk.APISENSE;
import com.apisense.sdk.core.preferences.Sensor;
import com.apisense.sdk.core.store.Crop;

import java.util.List;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SubscribedExperimentsRecyclerAdapter extends
        RecyclerView.Adapter<SubscribedExperimentsRecyclerAdapter.ViewHolder> {

    private List<Crop> mInstalledCrops;
    private APISENSE.Sdk apisenseSdk;
    private Context context;
    private OnItemClickListener mListener;
    private Set<Sensor> mAvailableSensors;

    public interface OnItemClickListener {
        void onItemClick(Crop crop);
    }

    public SubscribedExperimentsRecyclerAdapter(List<Crop> installedCrops, OnItemClickListener listener) {
        mInstalledCrops = installedCrops;
        mListener = listener;
    }

    @Override
    public SubscribedExperimentsRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        apisenseSdk = ((BeeApplication) context.getApplicationContext()).getSdk();
        mAvailableSensors = apisenseSdk.getPreferencesManager().retrieveAvailableSensors();

        LayoutInflater inflater = LayoutInflater.from(context);
        View cropView = inflater.inflate(R.layout.list_item_home_experiment, parent, false);

        return new ViewHolder(cropView);
    }

    @Override
    public void onBindViewHolder(SubscribedExperimentsRecyclerAdapter.ViewHolder holder, int position) {
        Crop crop = mInstalledCrops.get(position);

        holder.mCropTitle.setText(crop.getName());
        holder.mCropDescription.setText(crop.getShortDescription());

        if (apisenseSdk.getCropManager().isRunning(crop)) {
            holder.mCropStatus.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_action_play_blck));
        } else {
            holder.mCropStatus.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_action_break_blck));
        }
        holder.mCropStatus.setAlpha(0.5f);

        SensorsDrawer.draw(context, mAvailableSensors, crop.getUsedStings(), holder.mSensorsContainer);

        holder.bind(crop, mListener);
    }

    @Override
    public int getItemCount() {
        return mInstalledCrops.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.crop_status) ImageView mCropStatus;
        @BindView(R.id.crop_title) TextView mCropTitle;
        @BindView(R.id.crop_description) TextView mCropDescription;
        @BindView(R.id.sensors_container) LinearLayout mSensorsContainer;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bind(final Crop crop, final OnItemClickListener listener) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(crop);
                }
            });
        }
    }
}