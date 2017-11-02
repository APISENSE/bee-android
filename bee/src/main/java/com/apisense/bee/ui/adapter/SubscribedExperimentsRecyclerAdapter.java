package com.apisense.bee.ui.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.apisense.bee.BeeApplication;
import com.apisense.bee.R;
import com.apisense.bee.utils.SensorsDrawer;
import io.apisense.sdk.APISENSE;
import io.apisense.sdk.core.store.Crop;
import io.apisense.sting.lib.Sensor;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SubscribedExperimentsRecyclerAdapter extends
        RecyclerView.Adapter<SubscribedExperimentsRecyclerAdapter.ViewHolder> {

    private static final float ALPHA_STATUS_ICON = 0.5f;
    private List<Crop> mInstalledCrops;
    private APISENSE.Sdk apisenseSdk;
    private Context context;
    private OnItemClickListener mListener;
    private SensorsDrawer sensorsDrawer;

    public SubscribedExperimentsRecyclerAdapter(OnItemClickListener listener) {
        this(Collections.<Crop>emptyList(), listener);
    }

    public interface OnItemClickListener {
        void onItemClick(Crop crop);
    }

    public SubscribedExperimentsRecyclerAdapter(List<Crop> installedCrops, OnItemClickListener listener) {
        mInstalledCrops = installedCrops;
        mListener = listener;
    }

    public void setInstalledCrops(List<Crop> installedCrops) {
        this.mInstalledCrops = installedCrops;
    }

    @Override
    public SubscribedExperimentsRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        apisenseSdk = ((BeeApplication) context.getApplicationContext()).getSdk();
        Set<Sensor> mAvailableSensors = apisenseSdk.getPreferencesManager().retrieveAvailableSensors();
        sensorsDrawer = new SensorsDrawer(mAvailableSensors);

        LayoutInflater inflater = LayoutInflater.from(context);
        View cropView = inflater.inflate(R.layout.list_item_home_experiment, parent, false);

        return new ViewHolder(cropView);
    }

    @Override
    public void onBindViewHolder(SubscribedExperimentsRecyclerAdapter.ViewHolder holder, int position) {
        Crop crop = mInstalledCrops.get(position);

        holder.mCropTitle.setText(crop.getName());
        holder.mCropOwner.setText(context.getString(R.string.exp_details_organization, crop.getOwner()));
        holder.mCropDescription.setText(crop.getShortDescription());

        if (apisenseSdk.getCropManager().isRunning(crop)) {
            holder.mCropStatus.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_action_play_blck));
        } else {
            holder.mCropStatus.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_action_break_blck));
        }
        holder.mCropStatus.setAlpha(ALPHA_STATUS_ICON);

        sensorsDrawer.draw(context, holder.mSensorsContainer, crop.getUsedStings());

        holder.bind(crop, mListener);
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public void onViewAttachedToWindow(ViewHolder holder) {
        super.onViewAttachedToWindow(holder);
    }

    @Override
    public int getItemCount() {
        return mInstalledCrops.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.crop_status) ImageView mCropStatus;
        @BindView(R.id.crop_title) TextView mCropTitle;
        @BindView(R.id.crop_owner) TextView mCropOwner;
        @BindView(R.id.crop_description) TextView mCropDescription;
        @BindView(R.id.sensors_container) ViewGroup mSensorsContainer;

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
