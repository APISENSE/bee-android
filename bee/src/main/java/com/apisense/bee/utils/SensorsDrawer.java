package com.apisense.bee.utils;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.apisense.sdk.core.preferences.Sensor;

import java.util.List;
import java.util.Set;

public class SensorsDrawer {

    private static final int SENSOR_LATERAL_PADDING = 8;
    private static final int SENSOR_HEIGHT = 80;
    private static final int SENSOR_WIDTH = 80;
    private static final float SENSOR_DISABLED_ALPHA = 0.1f;


    public static void draw(Context context, Set<Sensor> availableSensors, List<String> usedStings, ViewGroup view) {
        for(Sensor sensor : availableSensors) {
            ImageView s = new ImageView(context);

            RecyclerView.LayoutParams params = new RecyclerView.LayoutParams(SENSOR_WIDTH, SENSOR_HEIGHT);
            s.setImageDrawable(ContextCompat.getDrawable(context, sensor.iconID));
            s.setLayoutParams(params);
            s.setPadding(SENSOR_LATERAL_PADDING, 0, SENSOR_LATERAL_PADDING, 0);

            if (!usedStings.contains(sensor.stingName)) {
                s.setAlpha(SENSOR_DISABLED_ALPHA);
            }

            view.addView(s);
        }
    }

}
