package com.apisense.bee.ui.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.apisense.bee.R;

import java.util.List;

public class IconAdapter extends ArrayAdapter<Integer> {
    private final String TAG = "IconAdapter";

    /**
     * Constructor
     *
     * @param context
     * @param layoutResourceId
     * @param iconReferences   list of icons to show
     */
    public IconAdapter(Context context, int layoutResourceId, List<Integer> iconReferences) {
        super(context, layoutResourceId, iconReferences);
        Log.i(TAG, "List size : " + iconReferences.size());
    }

    /**
     * Prepare view with data.
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null)
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.grid_item_icon, parent, false);
        int item = getItem(position);

        ImageView iconView = (ImageView) convertView.findViewById(R.id.grid_icon);

        iconView.setImageDrawable(getContext().getResources().getDrawable(item));

        return convertView;
    }

}
