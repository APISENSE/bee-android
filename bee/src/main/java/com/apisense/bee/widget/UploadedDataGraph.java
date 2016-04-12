package com.apisense.bee.widget;

import android.content.Context;
import android.util.AttributeSet;

import com.apisense.bee.R;
import com.apisense.bee.utils.RetroCompatibility;
import com.apisense.sdk.core.statistics.UploadedEntry;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * Graph specifically used to show a collection of {@link UploadedEntry}.
 *
 * Currently display the last 7 days of uploaded data.
 */
public class UploadedDataGraph extends LineChart {
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd-MM", Locale.US);
    private static final int NB_SHOWN_DAYS = 7;
    private static final long ONE_DAY_MS = 86400000l;

    public UploadedDataGraph(Context context) {
        super(context);
        configureUploadGraph();
    }

    public UploadedDataGraph(Context context, AttributeSet attrs) {
        super(context, attrs);
        configureUploadGraph();
    }

    public UploadedDataGraph(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        configureUploadGraph();
    }

    /**
     * Set the given values in the graph.
     * Will display the 7 last days worth of upload.
     *
     * @param uploaded
     */
    public void setValues(Collection<UploadedEntry> uploaded) {
        Date endOfCurrentDay = getEndOfDay();
        Date drawEverythingAfter = new Date(endOfCurrentDay.getTime() - ONE_DAY_MS * NB_SHOWN_DAYS);
        Date drawEverythingBefore = new Date(drawEverythingAfter.getTime() + ONE_DAY_MS);
        List<UploadedEntry> entries = new ArrayList<>(uploaded);
        Set<UploadedEntry> toRemove = new HashSet<>();
        int nbTracesForDate;
        Date uploadDate;

        List<String> xVals = new ArrayList<>();
        List<Entry> yVals = new ArrayList<>();
        for (long i = 0; i < NB_SHOWN_DAYS; i++) {
            nbTracesForDate = 0;
            for (UploadedEntry entry : entries) {
                uploadDate = entry.getUploadDate();
                if (uploadDate.before(drawEverythingBefore) && uploadDate.after(drawEverythingAfter)) {
                    nbTracesForDate += entry.getNumberOfTraces();
                    toRemove.add(entry); // Will not fit anywhere else, avoid useless iterations
                } else if (uploadDate.before(drawEverythingAfter)) {
                    toRemove.add(entry); // Too old to be drawn, avoid useless iterations
                }
            }
            entries.removeAll(toRemove);
            toRemove.clear();

            xVals.add(DATE_FORMAT.format(drawEverythingBefore));
            yVals.add(new BarEntry(nbTracesForDate, (int) i));
            drawEverythingAfter = drawEverythingBefore;
            drawEverythingBefore = new Date(drawEverythingBefore.getTime() + ONE_DAY_MS);
        }

        LineDataSet set = getConfiguredDataSet(yVals);
        List<LineDataSet> dataSets = new ArrayList<>();
        dataSets.add(set);

        LineData data = getConfiguredLineData(xVals, dataSets);
        setData(data);
    }

    private void configureUploadGraph() {
        // Actions on Graph (move, scale, ..)
        setDragEnabled(false);
        setScaleXEnabled(false);
        setScaleYEnabled(false);
        setHighlightPerDragEnabled(false);
        setDrawMarkerViews(false);
        setOnChartValueSelectedListener(null);

        // Configure visible elements
        XAxis xAxis = getXAxis();
        xAxis.setDrawGridLines(false);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setAvoidFirstLastClipping(true);
        getLegend().setForm(Legend.LegendForm.CIRCLE);

        // Disable unused elements
        getAxisLeft().setEnabled(false);
        getAxisRight().setEnabled(false);
        setDescriptionPosition(-1, -1);
        setDrawGridBackground(false);
    }

    private LineDataSet getConfiguredDataSet(List<Entry> yVals) {
        LineDataSet set = new LineDataSet(yVals, getResources().getString(R.string.experiment_activity_7_days));

        // Colors
        int mainColor = RetroCompatibility.retrieveColor(getResources(), R.color.aps_orange_dark);
        set.setColor(mainColor);
        set.setCircleColor(mainColor);
        set.setFillColor(mainColor);

        // Elements to show
        set.setLineWidth(2f);
        set.setHighlightEnabled(false);
        set.setDrawFilled(true);

        return set;
    }

    private LineData getConfiguredLineData(List<String> xVals, List<LineDataSet> dataSets) {
        LineData data = new LineData(xVals, dataSets);
        // Shown values above chart
        data.setValueTextSize(10f);
        data.setValueFormatter(new valuePrinter());
        return data;
    }

    private Date getEndOfDay() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        return calendar.getTime();
    }

    /**
     * Defines how to display each entry value.
     */
    private class valuePrinter implements ValueFormatter {
        @Override
        public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
            return String.valueOf((int) value);
        }
    }
}
