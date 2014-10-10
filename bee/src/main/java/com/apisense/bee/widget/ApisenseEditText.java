package com.apisense.bee.widget;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.EditText;

public class ApisenseEditText extends EditText {

    private Context mContext;
    private static String mFontName = "Roboto/Roboto-Light.ttf" ;

    public ApisenseEditText(Context context) {
        super(context);
        this.mContext = mContext;
        Typeface tfs = Typeface.createFromAsset(mContext.getAssets(), mFontName);
        setTypeface(tfs);
    }

    public ApisenseEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        Typeface tfs = Typeface.createFromAsset(mContext.getAssets(), mFontName);
        setTypeface(tfs);
    }

    public ApisenseEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mContext = context;
        Typeface tfs = Typeface.createFromAsset(mContext.getAssets(), mFontName);
        setTypeface(tfs);
    }
}
