package com.apisense.bee.ui.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.apisense.bee.R;
import com.apisense.bee.ui.activity.StoreActivity;
import fr.inria.bsense.appmodel.Experiment;

import java.util.List;

public class AvailableExperimentsListAdapter extends ArrayAdapter<Experiment> {
    private final String TAG = getClass().getSimpleName();
    private List<Experiment> data;

    /**
     * Constructor
     *
     * @param context
     * @param layoutResourceId
     */
    public AvailableExperimentsListAdapter(Context context, int layoutResourceId) {
        super(context, layoutResourceId);
    }

    /**
     * Constructor
     *
     * @param context
     * @param layoutResourceId
     * @param experiments
     *            list of experiments
     */
    public AvailableExperimentsListAdapter(Context context, int layoutResourceId, List<Experiment> experiments) {
        super(context, layoutResourceId, experiments);
        this.setDataSet(experiments);
    }

    /**
     * Change the dataSet of the adapter
     *
     * @param dataSet
     */
    public void setDataSet(List<Experiment> dataSet){
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
    public Experiment getItem(int position) {
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
        return Long.valueOf(getItem(position).id);
    }

    /**
     * Prepare view with data.
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null)
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.fragment_experiment_element, null);

        Experiment item = getItem(position);

        Log.v(TAG, "View asked (as a listItem)for Experiment: " + item);

        TextView title = (TextView) convertView.findViewById(R.id.experimentelement_sampletitle);
        title.setText(item.niceName);
        title.setTypeface(null, Typeface.BOLD);

        TextView company = (TextView) convertView.findViewById(R.id.experimentelement_company);
        company.setText(" by " + item.organization);

        TextView description = (TextView) convertView.findViewById(R.id.experimentelement_short_desc);
        description.setText(item.description);

        // Contains a background color associated to current status
        View status = convertView.findViewById(R.id.experiment_status);
        if (StoreActivity.isSubscribedExperiment(item)){
            showAsSubscribed(status);
        } else {
            showAsUnsubscribed(status);
        }
        return convertView;
    }

    public void showAsSubscribed(View v){
        v.setBackgroundColor(getContext().getResources().getColor(R.color.orange_light));
    }

    public void showAsUnsubscribed(View v){
        v.setBackgroundColor(getContext().getResources().getColor(R.color.white));
    }

}
