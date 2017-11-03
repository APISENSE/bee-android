package com.apisense.bee.ui.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.apisense.bee.R;
import com.apisense.bee.ui.fragment.StoreDetailsFragment;

import butterknife.BindView;
import io.apisense.sdk.core.store.Crop;

public class AvailableExperimentsRecyclerAdapter extends
        ExperimentsRecyclerAdapter<AvailableExperimentsRecyclerAdapter.ViewHolder> {

    public AvailableExperimentsRecyclerAdapter(FragmentActivity activity) {
        super(new ExperimentClickListener(activity));
    }

    private static final class ExperimentClickListener extends ExperimentAdapterClickListener {
        ExperimentClickListener(FragmentActivity activity) {
            super(activity);
        }

        @Override
        protected Fragment newFragment() {
            return new StoreDetailsFragment();
        }
    }

    @Override
    protected ViewHolder onCreateExperimentHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View cropView = inflater.inflate(R.layout.list_item_store_experiment, parent, false);

        return new AvailableExperimentsRecyclerAdapter.ViewHolder(cropView);
    }

    @Override
    protected void drawCrop(ViewHolder holder, Crop crop) {
        holder.mCropTitle.setText(crop.getName());
        holder.mCropOwner.setText(context.getString(R.string.exp_details_organization, crop.getOwner()));
        holder.mCropDescription.setText(crop.getShortDescription());
        holder.mCropVersion.setText(context.getString(R.string.exp_details_version, crop.getVersion()));

        sensorsDrawer.draw(context, holder.mSensorsContainer, crop.getUsedStings());
    }

    static class ViewHolder extends ExperimentsRecyclerAdapter.ViewHolder {
        @BindView(R.id.store_item_name)
        TextView mCropTitle;
        @BindView(R.id.store_item_owner)
        TextView mCropOwner;
        @BindView(R.id.store_item_description)
        TextView mCropDescription;
        @BindView(R.id.store_sensors_container)
        ViewGroup mSensorsContainer;
        @BindView(R.id.store_item_version)
        TextView mCropVersion;

        ViewHolder(View itemView) {
            super(itemView);
        }
    }
}
