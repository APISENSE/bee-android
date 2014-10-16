package com.apisense.bee.ui.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ToggleButton;
import com.apisense.bee.R;
import com.apisense.bee.ui.entity.PrivacyGridItem;

import java.util.List;

public class PrivacyGridAdapter extends ArrayAdapter<PrivacyGridItem> {

    private final String TAG = this.getClass().getSimpleName();

    private final Context context;
    private final List<PrivacyGridItem> data;

    public PrivacyGridAdapter(Context context, List<PrivacyGridItem> data) {
        super(context, 0, data);
        this.context = context;
        this.data = data;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public PrivacyGridItem getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    /**
     * Prepare view with data.
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = convertView;
        if (rowView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView = inflater.inflate(R.layout.privacy_grid_item_fragment, parent, false);

            PrivacyGridHolder privacyGridHolder = new PrivacyGridHolder();
            privacyGridHolder.toggleButton = (ToggleButton) rowView.findViewById(R.id.toggle_sensor);
            rowView.setTag(privacyGridHolder);
        }

        PrivacyGridHolder holder = (PrivacyGridHolder) rowView.getTag();
        holder.toggleButton.setText(data.get(position).name);
        holder.toggleButton.setTextOn(data.get(position).name);
        holder.toggleButton.setTextOff(data.get(position).name);
        holder.toggleButton.setChecked(data.get(position).isActivated);

        int stateChecked = android.R.attr.state_checked;
        Drawable sensorOn = context.getResources().getDrawable(data.get(position).imgOn);
        Drawable sensorOff = context.getResources().getDrawable(data.get(position).imgOff);
        StateListDrawable states = new StateListDrawable();
        states.addState(new int[] { stateChecked }, sensorOn);
        states.addState(new int[] { -stateChecked }, sensorOff);
        holder.toggleButton.setCompoundDrawablesWithIntrinsicBounds(null, states, null, null);

        return rowView;
    }

    static class PrivacyGridHolder {
        public ToggleButton toggleButton;
    }

}
