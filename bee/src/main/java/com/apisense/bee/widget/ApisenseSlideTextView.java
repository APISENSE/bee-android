package com.apisense.bee.widget;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

public class ApisenseSlideTextView extends TextView {

    private Context mContext;
    private static String mFontName = "Roboto/Roboto-Light.ttf" ;

    public ApisenseSlideTextView(Context context) {
        super(context);
        this.mContext = mContext;
        Typeface tfs = Typeface.createFromAsset(mContext.getAssets(), mFontName);
        setTypeface(tfs);
    }

    public ApisenseSlideTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        Typeface tfs = Typeface.createFromAsset(mContext.getAssets(), mFontName);
        setTypeface(tfs);
    }

    public ApisenseSlideTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mContext = context;
        Typeface tfs = Typeface.createFromAsset(mContext.getAssets(), mFontName);
        setTypeface(tfs);
    }
}
