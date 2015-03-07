package com.apisense.bee.ui.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.apisense.bee.R;

import java.util.List;

import fr.inria.bsense.appmodel.Experiment;
import fr.inria.bsense.service.BeeSenseServiceManager;

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
     * @param experiments      list of experiments
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
    public void setDataSet(List<Experiment> dataSet) {
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
    public Experiment getItem(int position) {
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
        return Long.valueOf(getItem(position).id);
    }

    /**
     * Prepare view with data.
     */
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null)
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.fragment_experiment_element, parent, false);

        final Experiment item = getItem(position);

        Log.v(TAG, "View asked (as a listItem) for Experiment: " + item);
        TextView title = (TextView) convertView.findViewById(R.id.experimentelement_sampletitle);
        title.setText(item.niceName);
        title.setTypeface(null, Typeface.BOLD);

        TextView company = (TextView) convertView.findViewById(R.id.experimentelement_company);
        company.setText(" " + getContext().getString(R.string.by) + " " + item.organization);

        TextView description = (TextView) convertView.findViewById(R.id.experimentelement_short_desc);
        String decode = new String(Base64.decode(item.description.getBytes(), Base64.DEFAULT));
        description.setText(decode);

        ImageView ivExp = (ImageView) convertView.findViewById(R.id.list_image);
        if (item.state) {
            ivExp.setBackgroundResource(R.drawable.icon_mission_running);
        } else {
            ivExp.setBackgroundResource(R.drawable.icon_mission_break);
        }
        /*
        TextView textStatus = (TextView) convertView.findViewById(R.id.experimentelement_status);
        String state = (item.state) ? getContext().getString(R.string.running) : getContext().getString(R.string.not_running);
        textStatus.setText(" - " + state);
        */


//        convertView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(v.getContext(), ExperimentDetailsActivity.class);
//
//                Bundle bundle = new Bundle();
//                // TODO : Prefer parcelable in the future. Problem : CREATOR method doesn't exist (to check)
//                // bundle.putParcelable("experiment", getItem(position));
//                // TODO : Maybe something extending Experiment and using JSONObject to init but it seems to be empty
//                bundle.putSerializable("experiment", new ExperimentSerializable(item));
//                intent.putExtras(bundle); //Put your id to your next Intent
//                v.getContext().startActivity(intent);
//            }
//        });

        return convertView;
    }

}
