package com.apisense.bee.widget;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

public class ApisenseTextView extends TextView {
    private static String mFontName = "Roboto/Roboto-Light.ttf";

    private static Typeface tfs;

    public static Typeface createTypeface(Context context) {
        if (tfs == null) {
            tfs = Typeface.createFromAsset(context.getAssets(), mFontName);
        }
        return tfs;
    }

    public ApisenseTextView(Context context) {
        super(context);
        setTypeface(createTypeface(context));
    }

    public ApisenseTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setTypeface(createTypeface(context));
    }

    public ApisenseTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setTypeface(createTypeface(context));
    }
}
