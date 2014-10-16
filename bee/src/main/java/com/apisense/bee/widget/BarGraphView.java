package com.apisense.bee.widget;


import static com.apisense.bee.R.styleable.BarChartView_viewsWeekdayColor;
import static com.apisense.bee.R.styleable.BarChartView_viewsWeekendColor;
import static java.util.Calendar.DAY_OF_WEEK;
import static java.util.Calendar.DAY_OF_YEAR;
import static java.util.Calendar.SATURDAY;
import static java.util.Calendar.SUNDAY;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import com.apisense.bee.R;
import com.apisense.bee.widget.BarGraphDrawable;

public class BarGraphView extends LinearLayout {

    private static final long[] NO_TRAFFIC = new long[] { 0, 0 };

    private int[] weekdayColors, weekendColors;

    private long[][] data;

    private int[][] colors;

    /**
     * Create graph view from context and attributes
     *
     * @param context
     * @param attrs
     */
    public BarGraphView(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.BarChartView);
        weekdayColors = new int[] { array.getColor(BarChartView_viewsWeekdayColor, 0), array.getColor(BarChartView_viewsWeekdayColor, 0) };
        weekendColors = new int[] { array.getColor(BarChartView_viewsWeekendColor, 0), array.getColor(BarChartView_viewsWeekendColor, 0) };

        array.recycle();
    }

    /**
     * This change the color of bar charts with gray
     */
    public void setDeactived() {
        int weekdayColorDeactived = getResources().getColor(R.color.barchart_weekday_deactived);
        int weekendColorDeactived = getResources().getColor(R.color.barchart_weekend_deactived);
        weekdayColors = new int[] { weekdayColorDeactived, weekdayColorDeactived };
        weekendColors = new int[] { weekendColorDeactived, weekendColorDeactived };
    }

    /**
     * This should be set before the graph is updated with traffic data, and
     * will not take effect until {@link # updateGraphWith(java.util.List)} is called.
     *
     * @param numDays
     *            the number of days to display in the graph, 1 bar per day
     */
    public void setNumDays(int numDays) {
        data = new long[numDays][];
        colors = new int[numDays][];
    }

    /**
     * Updates the graph to display the supplied data, which will be padded or
     * truncated to match the number of days specifed with
     * {@link #setNumDays(int)}.
     *
     * @param tracesData
     *            a list of traffic data by day in reverse-chronological order
     */
    @SuppressLint("NewApi")
    @SuppressWarnings("deprecation")
    public void updateGraphWith(ArrayList<Long> tracesData) {
        if (android.os.Build.VERSION.SDK_INT >= 16)
            setBackground(createBarGraphDrawableFor(tracesData));
        else
            setBackgroundDrawable(createBarGraphDrawableFor(tracesData));
    }

    @SuppressLint("UseValueOf")
    private BarGraphDrawable createBarGraphDrawableFor(ArrayList<Long> tracesData) {
        GregorianCalendar calendar = new GregorianCalendar();
        int daySummaryIndex = 0;
        for (int barIndex = data.length - 1; barIndex >= 0; --barIndex) {
            if (daySummaryIndex < tracesData.size()) {
                int tracesIndex = data.length - barIndex - 1;
                data[barIndex] = new long[] { tracesData.get(tracesIndex), 0L };
            } else {
                calendar.add(DAY_OF_YEAR, -1);
                data[barIndex] = NO_TRAFFIC;
            }

            int dayOfWeek = calendar.get(DAY_OF_WEEK);
            colors[barIndex] = dayOfWeek == SATURDAY || dayOfWeek == SUNDAY ? weekendColors : weekdayColors;
            ++daySummaryIndex;
        }
        return new BarGraphDrawable(data, colors);
    }
}
