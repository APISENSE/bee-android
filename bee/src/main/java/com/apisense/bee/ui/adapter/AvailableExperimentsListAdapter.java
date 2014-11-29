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
import android.widget.TextView;
import com.apisense.android.api.APSLocalCrop;
import com.apisense.bee.R;
import com.apisense.bee.backend.experiment.SubscribeUnsubscribeExperimentTask;

import java.util.ArrayList;
import java.util.List;

public class AvailableExperimentsListAdapter extends ArrayAdapter<APSLocalCrop> {
    private final String TAG = getClass().getSimpleName();

    private List<APSLocalCrop> data;
    private List<APSLocalCrop> filteredData;

    /**
     * Constructor
     *  @param context
     * @param layoutResourceId
     * @param experiments
     */
    public AvailableExperimentsListAdapter(Context context, int layoutResourceId, ArrayList<APSLocalCrop> experiments) {
        super(context, layoutResourceId, experiments);
        setDataSet(experiments);
    }

    /**
     * Change the dataSet of the adapter
     *
     * @param dataSet
     */
    public void setDataSet(List<APSLocalCrop> dataSet){
        this.data = dataSet;
        this.filteredData = dataSet;
        notifyDataSetChanged();
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
    public APSLocalCrop getItem(int position) {
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
        return position;
    }

    /**
     * Prepare view with data.
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null)
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.fragment_experiment_element, parent, false);

        APSLocalCrop item = getItem(position);

        Log.v(TAG, "View asked (as a listItem) for APSLocalCrop: " + item);

        TextView title = (TextView) convertView.findViewById(R.id.experimentelement_sampletitle);
        title.setText(item.getNiceName());
        title.setTypeface(null, Typeface.BOLD);

        TextView company = (TextView) convertView.findViewById(R.id.experimentelement_company);
        company.setText(" " + getContext().getString(R.string.by) + item.getOrganisation());

        TextView description = (TextView) convertView.findViewById(R.id.experimentelement_short_desc);
//        String decode = new String(Base64.decode(item.getDescription().getBytes(), Base64.DEFAULT));
        description.setText(item.getDescription());

        // Contains a background color associated to current status
        View status = convertView.findViewById(R.id.item);
        if (SubscribeUnsubscribeExperimentTask.isInstalled(getContext(), item.getName())){
            showAsSubscribed(status);
        } else {
            showAsUnsubscribed(status);
        }
        return convertView;
    }


    public void showAsSubscribed(View v){
        v.setBackgroundColor(getContext().getResources().getColor(R.color.light_grey));
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

                final List<APSLocalCrop> items = data;

                int count = items.size();
                final ArrayList<APSLocalCrop> newItems = new ArrayList<APSLocalCrop>(count);

                APSLocalCrop filterableAPSLocalCrop;

                if (TextUtils.isEmpty(constraint)) {
                    for (APSLocalCrop exp : items) newItems.add(exp);
                } else {
                    for (APSLocalCrop exp : items) {
                        if (exp.getNiceName().toLowerCase().contains(filterString)) {
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
                filteredData = (ArrayList<APSLocalCrop>) results.values;
                notifyDataSetChanged();
            }

        };
    }
}
