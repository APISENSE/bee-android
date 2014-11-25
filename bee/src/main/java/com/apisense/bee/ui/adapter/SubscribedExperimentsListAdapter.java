package com.apisense.bee.ui.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.apisense.api.Crop;
import com.apisense.api.LocalCrop;
import com.apisense.bee.R;
import com.google.android.gms.location.LocationRequest;

import java.util.List;

public class SubscribedExperimentsListAdapter extends ArrayAdapter<LocalCrop> {
    private final String TAG = getClass().getSimpleName();

    private List<LocalCrop> data;

    /**
     * Constructor
     *
     * @param context
     * @param layoutResourceId
     */
    public SubscribedExperimentsListAdapter(Context context, int layoutResourceId) {
        super(context, layoutResourceId);
        //apisense = ((BeeSenseApplication) getContext().getApplicationContext()).getBService();
    }

    /**
     * Constructor
     *
     * @param context
     * @param layoutResourceId
     * @param experiments
     *            list of experiments
     */
    public SubscribedExperimentsListAdapter(Context context, int layoutResourceId, List<LocalCrop> experiments) {
        super(context, layoutResourceId, experiments);
        //apisense = ((BeeSenseApplication) getContext().getApplicationContext()).getBService();
        this.setDataSet(experiments);
    }

    /**
     * Change the dataSet of the adapter
     *
     * @param dataSet
     */
    public void setDataSet(List<LocalCrop> dataSet){
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
     * @param position
     *            position in the ListView
     * @return an experiment
     */
    @Override
    public LocalCrop getItem(int position) {
        return data.get(position);
    }

    /**
     * Get the experiment ID
     *
     * @param position
     *            position in the ListView
     * @return the experiment ID
     */
    @Override
    public long getItemId(int position) {
        return Long.valueOf(getItem(position).getName());
    }

    /**
     * Prepare view with data.
     */
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null)
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.fragment_experiment_element, parent, false);

        final Crop item = getItem(position);

        Log.v(TAG, "View asked (as a listItem) for Experiment: " + item);
        TextView title = (TextView) convertView.findViewById(R.id.experimentelement_sampletitle);
        title.setText(item.getNiceName());
        title.setTypeface(null, Typeface.BOLD);

        TextView company = (TextView) convertView.findViewById(R.id.experimentelement_company);
//        company.setText(" " + getContext().getString(R.string.by) + " " + item.organization);

        TextView description = (TextView) convertView.findViewById(R.id.experimentelement_short_desc);
        String decode = new String(Base64.decode(item.getDescription().getBytes(), Base64.DEFAULT));
        description.setText(decode);

        TextView textStatus = (TextView) convertView.findViewById(R.id.experimentelement_status);
//        String state = (item.state) ? getContext().getString(R.string.running) : getContext().getString(R.string.not_running) ;
//        textStatus.setText(" - " + state);

        // Display state of the current experiment
        View status = convertView.findViewById(R.id.item);
//        if (item.state){
        if (true) {
            showAsStarted(status);
        } else {
            showAsStopped(status);
        }

        return convertView;
    }

    public void showAsStarted(View v){
        v.setBackgroundColor(getContext().getResources().getColor(R.color.white));
    }

    public void showAsStopped(View v){
        v.setBackgroundColor(getContext().getResources().getColor(R.color.light_grey));
    }
}
