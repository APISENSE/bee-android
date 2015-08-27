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

import com.apisense.bee.R;
import com.apisense.sdk.core.preferences.Sensor;

import java.util.ArrayList;
import java.util.List;

public class SensorListAdapter extends ArrayAdapter<Sensor> {
    private final String TAG = "SensorListAdapter";
    private List<Sensor> data;

    /**
     * Constructor
     *
     * @param context
     * @param layoutResourceId
     */
    public SensorListAdapter(Context context, int layoutResourceId) {
        this(context, layoutResourceId, new ArrayList<Sensor>());
    }

    /**
     * Constructor
     *
     * @param context
     * @param layoutResourceId
     * @param sensors      list of experiments
     */
    public SensorListAdapter(Context context, int layoutResourceId, List<Sensor> sensors) {
        super(context, layoutResourceId, sensors);
        Log.i(TAG, "List size : " + sensors.size());
        setDataSet(sensors);
    }

    /**
     * Change the dataSet of the adapter
     *
     * @param dataSet
     */
    public void setDataSet(List<Sensor> dataSet) {
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
    public Sensor getItem(int position) {
        return data.get(position);
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
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_sensor, parent, false);

        Sensor item = getItem(position);

        Log.v(TAG, "View asked (as a listItem) for Experiment: " + item);

        TextView title = (TextView) convertView.findViewById(R.id.sensor_name);
        title.setText(item.name);
        title.setTypeface(null, Typeface.BOLD);

        TextView description = (TextView) convertView.findViewById(R.id.sensor_description);
        description.setText(item.description);

        ImageView icon = (ImageView) convertView.findViewById(R.id.sensor_icon);
        icon.setImageDrawable(getContext().getResources().getDrawable(item.iconID));

        return convertView;
    }
}
