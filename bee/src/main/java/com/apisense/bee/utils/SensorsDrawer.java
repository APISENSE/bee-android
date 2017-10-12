package com.apisense.bee.utils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.apisense.bee.ui.layout.SensorsLayout;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import io.apisense.sting.lib.Sensor;

public class SensorsDrawer {
    private static final int SENSOR_SIZE = 27;
    private static final int SENSOR_PADDING = 4;
    private static final float SENSOR_ALPHA = 0.6f;

    private final Set<Sensor> availableSensors;
    private final int sensorSize;
    private final int sensorPadding;

    public SensorsDrawer(Set<Sensor> availableSensors) {
        this.availableSensors = availableSensors;
        sensorSize = dpToPx(SENSOR_SIZE);
        sensorPadding = dpToPx(SENSOR_PADDING);
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
            if (usedStings.contains(sensor.stingName)) {
                ImageView sensorView = parametrizedSensorView(context, sensor);
                view.addView(sensorView);
            }
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

        SensorsLayout.LayoutParams params = new SensorsLayout.LayoutParams(sensorSize, sensorSize);
        sensorView.setLayoutParams(params);

        Drawable drawable = ContextCompat.getDrawable(context, sensor.iconID);
        sensorView.setImageDrawable(drawable);
        sensorView.setPadding(sensorPadding, 0, sensorPadding, 0);

        sensorView.setAlpha(SENSOR_ALPHA);

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
