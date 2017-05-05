package com.apisense.bee.ui.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.apisense.bee.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.apisense.sdk.core.store.Crop;
import io.apisense.sting.lib.Sensor;

public class SensorRecyclerAdapter extends RecyclerView.Adapter<SensorRecyclerAdapter.ViewHolder> {
    private final String TAG = "SensorRecyclerAdapter";
    private List<Sensor> data;
    private Map<String, Boolean> enabledStings;
    private Context context;

    public SensorRecyclerAdapter(List<Sensor> sensors) {
        Log.i(TAG, "List size : " + sensors.size());
        enabledStings = new HashMap<>();
        setDataSet(sensors);
    }

    @Override
    public SensorRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View cropView = inflater.inflate(R.layout.list_item_sensor, parent, false);

        ButterKnife.bind(cropView);

        return new SensorRecyclerAdapter.ViewHolder(cropView);
    }

    @Override
    public void onBindViewHolder(SensorRecyclerAdapter.ViewHolder holder, int position) {
        Sensor item = data.get(position);

        holder.title.setText(item.name);
        holder.title.setTypeface(null, Typeface.BOLD);
        holder.description.setText(item.description);
        holder.icon.setImageDrawable(ContextCompat.getDrawable(context, item.iconID));
        holder.icon.setContentDescription(item.name);
        if (enabledStings.containsKey(item.stingName)) {
            holder.enabled.setChecked(enabledStings.get(item.stingName));
        }
        holder.enabled.setOnCheckedChangeListener(new SwitchClickListener(item.stingName));
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void setDataSet(List<Sensor> dataSet) {
        this.data = dataSet;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.sensor_name) TextView title;
        @BindView(R.id.sensor_description) TextView description;
        @BindView(R.id.sensor_icon) ImageView icon;
        @BindView(R.id.sensor_enabled) SwitchCompat enabled;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bind(final Crop crop, final SubscribedExperimentsRecyclerAdapter.OnItemClickListener listener) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(crop);
                }
            });
        }
    }

    public void setSensorActivation(String stingName, boolean enabled) {
        enabledStings.put(stingName, enabled);
    }

    public List<String> getDisabledSensors() {
        List<String> disabled = new ArrayList<>();
        for (String stingName : enabledStings.keySet()) {
            if (!enabledStings.get(stingName)) {
                disabled.add(stingName);
            }
        }
        return disabled;
    }

    // Private methods

    private class SwitchClickListener implements CompoundButton.OnCheckedChangeListener {
        private String stingName;

        public SwitchClickListener(String stingName) {
            this.stingName = stingName;
        }

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean enabled) {
            Log.d(TAG, "Setting sting (" + stingName + ") activation to : " + enabled);
            setSensorActivation(stingName, enabled);
        }
    }
}
