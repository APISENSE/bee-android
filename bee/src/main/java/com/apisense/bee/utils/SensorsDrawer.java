package com.apisense.bee.utils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.apisense.sdk.core.preferences.Sensor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class SensorsDrawer {
    private static final int SENSOR_LATERAL_PADDING = 8;
    private static final int SENSOR_HEIGHT = 80;
    private static final int SENSOR_WIDTH = 80;
    private static final float SENSOR_DISABLED_ALPHA = 0.1f;
    private final Set<Sensor> availableSensors;

    public SensorsDrawer(Set<Sensor> availableSensors) {
        this.availableSensors = availableSensors;
    }

    public void draw(Context context, ViewGroup view, List<String> usedStings) {
        if (view.getChildCount() == 0) {
            drawSensors(context, view, usedStings);
        }
    }

    private void drawSensors(Context context, ViewGroup view, List<String> usedStings) {
        for (Sensor sensor : asSortedList(availableSensors)) {
            ImageView sensorView = parametrizedSensorView(context, sensor);

            if (!usedStings.contains(sensor.stingName)) {
                sensorView.setAlpha(SENSOR_DISABLED_ALPHA);
            }

            view.addView(sensorView);
        }
    }

    @NonNull
    private ImageView parametrizedSensorView(Context context, Sensor sensor) {
        ImageView sensorView = new ImageView(context);

        RecyclerView.LayoutParams params = new RecyclerView.LayoutParams(SENSOR_WIDTH, SENSOR_HEIGHT);
        sensorView.setImageDrawable(ContextCompat.getDrawable(context, sensor.iconID));
        sensorView.setLayoutParams(params);
        sensorView.setPadding(SENSOR_LATERAL_PADDING, 0, SENSOR_LATERAL_PADDING, 0);
        return sensorView;
    }


    private static <T extends Comparable<? super T>> List<T> asSortedList(Collection<T> c) {
        List<T> list = new ArrayList<>(c);
        Collections.sort(list);
        return list;
    }
}
