package com.apisense.bee.ui.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.apisense.bee.R;
import com.apisense.sdk.core.preferences.Sensor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SensorListAdapter extends ArrayAdapter<Sensor> {
    private final String TAG = "SensorListAdapter";
    private List<Sensor> data;
    private Map<String, Boolean> enabledStings;

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
     * @param sensors          list of experiments
     */
    public SensorListAdapter(Context context, int layoutResourceId, List<Sensor> sensors) {
        super(context, layoutResourceId, sensors);
        Log.i(TAG, "List size : " + sensors.size());
        enabledStings = new HashMap<>();
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
     * Set a specific sting to enabled/disabled by the user.
     * Enabled by default.
     *
     * @param stingName
     * @param enabled
     */
    public void setSensortActivation(String stingName, boolean enabled) {
        enabledStings.put(stingName, enabled);
    }

    /**
     * Return the list of sting names associated with disabled sensors.
     *
     * @return The list of disabled sensors
     */
    public List<String> getDisabledSensors() {
        List<String> disabled = new ArrayList<>();
        for (String stingName : enabledStings.keySet()) {
            if (!enabledStings.get(stingName)) {
                disabled.add(stingName);
            }
        }
        return disabled;
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
        Log.d(TAG, "Redrawing sensor" + item.stingName);

        TextView title = (TextView) convertView.findViewById(R.id.sensor_name);
        title.setText(item.name);
        title.setTypeface(null, Typeface.BOLD);

        TextView description = (TextView) convertView.findViewById(R.id.sensor_description);
        description.setText(item.description);

        ImageView icon = (ImageView) convertView.findViewById(R.id.sensor_icon);
        icon.setImageDrawable(getContext().getResources().getDrawable(item.iconID));

        Switch enabled = (Switch) convertView.findViewById(R.id.sensor_enabled);
        if (enabledStings.containsKey(item.stingName)) {
            enabled.setChecked(enabledStings.get(item.stingName));
        }
        enabled.setOnCheckedChangeListener(new SwitchClickListner(item.stingName));
        return convertView;
    }

    private class SwitchClickListner implements CompoundButton.OnCheckedChangeListener {
        private String stingName;

        public SwitchClickListner(String stingName) {
            this.stingName = stingName;
        }

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean enabled) {
            Log.d(TAG, "Setting sting (" + stingName + ") activation to : " + enabled);
            setSensortActivation(stingName, enabled);
        }
    }
}
