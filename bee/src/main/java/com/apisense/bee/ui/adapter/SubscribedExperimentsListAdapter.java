package com.apisense.bee.ui.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.apisense.bee.BeeApplication;
import com.apisense.bee.R;
import com.apisense.bee.utils.RetroCompatibility;
import com.apisense.sdk.APISENSE;
import com.apisense.sdk.core.store.Crop;

import java.util.List;

public class SubscribedExperimentsListAdapter extends ArrayAdapter<Crop> {
    private final String TAG = getClass().getSimpleName();

    private APISENSE.Sdk apisenseSdk;
    private List<Crop> data;

    /**
     * Constructor
     *
     * @param context
     * @param layoutResourceId
     * @param experiments      list of experiments
     */
    public SubscribedExperimentsListAdapter(Context context, int layoutResourceId, List<Crop> experiments) {
        super(context, layoutResourceId, experiments);
        apisenseSdk = ((BeeApplication) context.getApplicationContext()).getSdk();
        this.setDataSet(experiments);
    }

    /**
     * Change the dataSet of the adapter
     *
     * @param dataSet
     */
    public void setDataSet(List<Crop> dataSet) {
        this.data = dataSet;
    }

    /**
     * Get the size of experiment list
     *
     * @return the size of experiment list
     */
    @Override
    public int getCount() {
        return data.size();
    }

    /**
     * Get an experiment from position in the ListView
     *
     * @param position position in the ListView
     * @return an experiment
     */
    @Override
    public Crop getItem(int position) {
        return data.get(position);
    }

    /**
     * Get the experiment ID
     *
     * @param position position in the ListView
     * @return the experiment ID
     */
    @Override
    public long getItemId(int position) {
        return -1;
    }

    /**
     * Prepare view with data.
     */
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null)
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_home_experiment, parent, false);

        final Crop item = getItem(position);

        Log.v(TAG, "View asked (as a listItem) for Experiment: " + item);
        TextView title = (TextView) convertView.findViewById(R.id.experimentelement_sampletitle);
        title.setText(item.getName());
        title.setTypeface(null, Typeface.BOLD);

        TextView company = (TextView) convertView.findViewById(R.id.experimentelement_company);
        company.setText(item.getOwner());

        if (!item.isActive()) {
            convertView.setBackgroundColor(
                    RetroCompatibility.retrieveColor(convertView.getResources(), R.color.aps_disabled_crop)
            );
        }

        ImageView ivExp = (ImageView) convertView.findViewById(R.id.home_item_icon);
        if (apisenseSdk.getCropManager().isRunning(item)) {
            ivExp.setBackgroundResource(R.drawable.icon_mission_running);
        } else {
            ivExp.setBackgroundResource(R.drawable.icon_mission_break);
        }

        return convertView;
    }

}
