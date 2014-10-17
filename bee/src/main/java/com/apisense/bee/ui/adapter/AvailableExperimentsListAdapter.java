package com.apisense.bee.ui.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;
import com.apisense.bee.R;
import com.apisense.bee.backend.experiment.SubscribeUnsubscribeExperimentTask;
import fr.inria.bsense.appmodel.Experiment;

import java.util.ArrayList;
import java.util.List;

public class AvailableExperimentsListAdapter extends ArrayAdapter<Experiment> {
    private final String TAG = getClass().getSimpleName();

    private List<Experiment> data;
    private List<Experiment> filteredData;


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
        Log.i(TAG, "List size : " + experiments.size());
        data = experiments;
        filteredData = experiments;
    }

    /**
     * Change the dataSet of the adapter
     *
     * @param dataSet
     */
    public void setDataSet(List<Experiment> dataSet){
        this.data = dataSet;
        this.filteredData = dataSet;
    }

    /**
     * Get the size of experiment list
     *
     * @return the size of experiment list
     */
    @Override
    public int getCount() {
        return filteredData.size();
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
        return filteredData.get(position);
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
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.fragment_experiment_element, parent, false);

        Experiment item = getItem(position);

        Log.v(TAG, "View asked (as a listItem) for Experiment: " + item);

        TextView title = (TextView) convertView.findViewById(R.id.experimentelement_sampletitle);
        title.setText(item.niceName);
        title.setTypeface(null, Typeface.BOLD);

        TextView company = (TextView) convertView.findViewById(R.id.experimentelement_company);
        company.setText(" by " + item.organization);

        TextView description = (TextView) convertView.findViewById(R.id.experimentelement_short_desc);
        String decode = new String(Base64.decode(item.description.getBytes(), Base64.DEFAULT));
        description.setText(decode);

        // Contains a background color associated to current status
        View status = convertView.findViewById(R.id.experiment_status);
        if (SubscribeUnsubscribeExperimentTask.isSubscribedExperiment(item)){
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

    /**
     * Filter for the ListView
     */
    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {

                String filterString = constraint.toString().toLowerCase();
                FilterResults results = new FilterResults();

                final List<Experiment> items = data;

                int count = items.size();
                final ArrayList<Experiment> newItems = new ArrayList<Experiment>(count);

                Experiment filterableExperiment;

                if (TextUtils.isEmpty(constraint)) {
                    for (Experiment exp : items) newItems.add(exp);
                } else {
                    for (Experiment exp : items) {
                        if (exp.niceName.toLowerCase().contains(filterString)) {
                            newItems.add(exp);
                        }
                    }
                }

                results.values = newItems;
                results.count = newItems.size();
                Log.i(TAG, results.count + " elements found !");
                return results;
            }

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                filteredData = (ArrayList<Experiment>) results.values;
                notifyDataSetChanged();
            }

        };
    }
}
