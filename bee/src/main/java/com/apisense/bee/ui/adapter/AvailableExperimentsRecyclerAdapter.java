package com.apisense.bee.ui.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.apisense.bee.R;
import com.apisense.bee.ui.fragment.StoreDetailsFragment;

import java.text.SimpleDateFormat;

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

        return new ViewHolder(cropView);
    }

    @Override
    protected void drawCrop(ViewHolder holder, Crop crop) {
        holder.cropTitle.setText(crop.getName());
        holder.cropOwner.setText(context.getString(R.string.exp_details_organization, crop.getOwner()));
        holder.cropDescription.setText(crop.getShortDescription());
        holder.cropVersion.setText(context.getString(R.string.exp_details_version, crop.getVersion()));

        holder.nbSubscribers.setText(context.getString(R.string.exp_details_subscribers, crop.getStatistics().numberOfSubscribers));
        String lastUpdateDate = SimpleDateFormat.getDateInstance().format(crop.getStatistics().updatedAt);
        holder.lastUpdate.setText(context.getString(R.string.exp_details_update, lastUpdateDate));

        sensorsDrawer.draw(context, holder.sensorsContainer, crop.getUsedStings());
    }

    static class ViewHolder extends ExperimentsRecyclerAdapter.ViewHolder {
        @BindView(R.id.store_item_name)
        TextView cropTitle;
        @BindView(R.id.store_item_owner)
        TextView cropOwner;
        @BindView(R.id.store_item_description)
        TextView cropDescription;
        @BindView(R.id.store_sensors_container)
        ViewGroup sensorsContainer;
        @BindView(R.id.store_item_version)
        TextView cropVersion;
        @BindView(R.id.store_item_subscribers)
        TextView nbSubscribers;
        @BindView(R.id.store_item_update)
        TextView lastUpdate;

        ViewHolder(View itemView) {
            super(itemView);
        }
    }
}
