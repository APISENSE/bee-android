package com.apisense.bee.ui.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.apisense.bee.R;
import com.apisense.bee.ui.activity.ExperimentDetailsActivity;
import fr.inria.bsense.appmodel.Experiment;
import fr.inria.bsense.service.BeeSenseServiceManager;

import java.util.List;

public class SubscribedExperimentsListAdapter extends ArrayAdapter<Experiment> {
    private final String TAG = getClass().getSimpleName();

    private BeeSenseServiceManager apisense = null;
    private List<Experiment> data;

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
    public SubscribedExperimentsListAdapter(Context context, int layoutResourceId, List<Experiment> experiments) {
        super(context, layoutResourceId, experiments);
        //apisense = ((BeeSenseApplication) getContext().getApplicationContext()).getBService();
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
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.fragment_experimentelement, null);

        Experiment item = getItem(position);

        Log.v(TAG, "View asked (as a listItem)for Experiment: " + item);

        TextView title = (TextView) convertView.findViewById(R.id.experimentelement_sampletitle);
        title.setText(item.niceName);
        title.setTypeface(null, Typeface.BOLD);

        TextView company = (TextView) convertView.findViewById(R.id.experimentelement_company);
        company.setText(" by " + item.organization);

        TextView description = (TextView) convertView.findViewById(R.id.experimentelement_short_desc);
        description.setText(item.description);

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), ExperimentDetailsActivity.class);
                v.getContext().startActivity(intent);
            }
        });
        return convertView;
    }
}
