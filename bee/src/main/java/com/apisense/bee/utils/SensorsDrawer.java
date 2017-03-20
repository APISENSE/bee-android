package com.apisense.bee.utils;

import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import io.apisense.sting.lib.Sensor;

public class SensorsDrawer {
    private static final int SENSOR_LATERAL_PADDING = 2;
    private static final float SENSOR_DISABLED_ALPHA = 0.1f;
    private final Set<Sensor> availableSensors;
    private final int sensorSize;
    private final int lateralPadding;

    public SensorsDrawer(Set<Sensor> availableSensors) {
        this.availableSensors = availableSensors;
        this.sensorSize = sensorDimension(availableSensors.size());
        this.lateralPadding = dpToPx(SENSOR_LATERAL_PADDING);
    }

    /**
     * Retrieve the sensor dimension to set in order to make them all fit on the screen.
     *
     * @param sensorNumber The number of sensor to draw.
     * @return The size in pixels to use for each sensor.
     */
    private static int sensorDimension(int sensorNumber) {
        return (Resources.getSystem().getDisplayMetrics().widthPixels - (2 * dpToPx(SENSOR_LATERAL_PADDING))) / (sensorNumber + 1);
    }

    /**
     * Empty the content of the given view, then draw all sensors on it,
     * highlighting the ones from usedStings.
     *
     * @param context    The view context.
     * @param view       The {@link ViewGroup} to draw onto.
     * @param usedStings The list of Stings to highlight.
     */
    public void draw(Context context, ViewGroup view, List<String> usedStings) {
        if (view.getChildCount() != 0) {
            view.removeAllViews();
        }
        drawSensors(context, view, usedStings);

    }

    /**
     * Sort and actual drawing of the sensors, highlighting the ones from usedStings.
     *
     * @param context    The view context.
     * @param view       The {@link ViewGroup} to draw onto.
     * @param usedStings The list of Stings to highlight.
     */
    private void drawSensors(Context context, ViewGroup view, List<String> usedStings) {
        for (Sensor sensor : asSortedList(availableSensors)) {
            ImageView sensorView = parametrizedSensorView(context, sensor);

            if (!usedStings.contains(sensor.stingName)) {
                sensorView.setAlpha(SENSOR_DISABLED_ALPHA);
            }

            view.addView(sensorView);
        }
    }

    /**
     * Return the view for one sensor.
     *
     * @param context The view context.
     * @param sensor  The sensor to draw.
     * @return An {@link ImageView} of the sensor.
     */
    @NonNull
    private ImageView parametrizedSensorView(Context context, Sensor sensor) {
        ImageView sensorView = new ImageView(context);

        RecyclerView.LayoutParams params = new RecyclerView.LayoutParams(sensorSize, sensorSize);
        sensorView.setImageDrawable(ContextCompat.getDrawable(context, sensor.iconID));
        sensorView.setLayoutParams(params);
        sensorView.setPadding(this.lateralPadding, 0, this.lateralPadding, 0);
        return sensorView;
    }


    private static <T extends Comparable<? super T>> List<T> asSortedList(Collection<T> c) {
        List<T> list = new ArrayList<>(c);
        Collections.sort(list);
        return list;
    }

    private static int dpToPx(final int dp) {
        return Math.round(dp * (Resources.getSystem().getDisplayMetrics().xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }
}
