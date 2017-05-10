package com.apisense.bee.widget;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import io.apisense.sting.visualization.widget.VisualizationView;

public class VisualizationPagerAdapter extends PagerAdapter {
    private List<View> visualizations;

    public VisualizationPagerAdapter(List<View> visualizations) {
        this.visualizations = visualizations;
    }

    @Override
    public Object instantiateItem(ViewGroup collection, int position) {
        View ret = visualizations.get(position);
        collection.addView(ret);
        return ret;
    }

    @Override
    public void destroyItem(ViewGroup collection, int position, Object view) {
        collection.removeView((View) view);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public int getCount() {
        return visualizations.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return "";
    }

    public void invalidateView(int position) {
        View ret = visualizations.get(position);
        if (ret instanceof VisualizationView) {
            ((VisualizationView) ret).notifyDataChanged();
        } else {
            ret.invalidate();
        }
    }
}
