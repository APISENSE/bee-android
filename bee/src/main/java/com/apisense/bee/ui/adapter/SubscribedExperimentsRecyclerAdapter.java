package com.apisense.bee.ui.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.apisense.bee.R;
import com.apisense.bee.ui.fragment.HomeDetailsFragment;

import butterknife.BindView;
import io.apisense.sdk.core.store.Crop;

public class SubscribedExperimentsRecyclerAdapter extends
        ExperimentsRecyclerAdapter<SubscribedExperimentsRecyclerAdapter.ViewHolder> {

    private static final float ALPHA_STATUS_ICON = 0.5f;

    public SubscribedExperimentsRecyclerAdapter(FragmentActivity activity) {
        super(new ExperimentClickListener(activity));
    }

    private static final class ExperimentClickListener extends ExperimentAdapterClickListener {
        ExperimentClickListener(FragmentActivity activity) {
            super(activity);
        }

        @Override
        protected Fragment newFragment() {
            return new HomeDetailsFragment();
        }
    }

    @Override
    public ViewHolder onCreateExperimentHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View cropView = inflater.inflate(R.layout.list_item_home_experiment, parent, false);

        return new ViewHolder(cropView);
    }

    @Override
    protected void drawCrop(ViewHolder holder, Crop crop) {
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
    }

    static class ViewHolder extends ExperimentsRecyclerAdapter.ViewHolder {
        @BindView(R.id.crop_status)
        ImageView mCropStatus;
        @BindView(R.id.crop_title)
        TextView mCropTitle;
        @BindView(R.id.crop_owner)
        TextView mCropOwner;
        @BindView(R.id.crop_description)
        TextView mCropDescription;
        @BindView(R.id.sensors_container)
        ViewGroup mSensorsContainer;

        ViewHolder(View itemView) {
            super(itemView);
        }
    }
}
