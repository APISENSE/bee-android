package com.apisense.bee.ui.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;

import com.apisense.bee.R;
import com.apisense.sdk.core.store.Crop;

import java.util.ArrayList;
import java.util.List;

public class AvailableExperimentsListAdapter extends ArrayAdapter<Crop> {
    private final String TAG = getClass().getSimpleName();

    private List<Crop> data;
    private List<Crop> filteredData;


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
     * @param experiments      list of experiments
     */
    public AvailableExperimentsListAdapter(Context context, int layoutResourceId, List<Crop> experiments) {
        super(context, layoutResourceId, experiments);
        Log.i(TAG, "List size : " + experiments.size());
        data = experiments;
        filteredData = experiments;
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
     * @param position position in the ListView
     * @return an experiment
     */
    @Override
    public Crop getItem(int position) {
        return filteredData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return -1;
    }

    /**
     * Prepare view with data.
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null)
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_home_experiment, parent, false);

        Crop item = getItem(position);

        Log.v(TAG, "View asked (as a listItem) for Experiment: " + item);

        TextView title = (TextView) convertView.findViewById(R.id.experimentelement_sampletitle);
        title.setText(item.getName());
        title.setTypeface(null, Typeface.BOLD);

        TextView company = (TextView) convertView.findViewById(R.id.experimentelement_company);
        company.setText(item.getOwner());

        ImageView ivExp = (ImageView) convertView.findViewById(R.id.list_image);
        ivExp.setImageDrawable(getContext().getResources().getDrawable(R.drawable.ic_launcher_bee));


        return convertView;
    }

    /**
     * Change the dataSet of the adapter
     *
     * @param dataSet
     */
    public void setDataSet(List<Crop> dataSet) {
        this.data = dataSet;
        this.filteredData = dataSet;
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

                final List<Crop> items = data;

                int count = items.size();
                final ArrayList<Crop> newItems = new ArrayList<Crop>(count);

                Crop filterableExperiment;

                if (TextUtils.isEmpty(constraint)) {
                    for (Crop exp : items) newItems.add(exp);
                } else {
                    for (Crop exp : items) {
                        if (exp.getName().toLowerCase().contains(filterString)) {
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
                filteredData = (ArrayList<Crop>) results.values;
                notifyDataSetChanged();
            }

        };
    }
}
