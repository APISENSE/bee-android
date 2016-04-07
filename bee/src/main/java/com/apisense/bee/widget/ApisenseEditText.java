package com.apisense.bee.widget;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.EditText;

public class ApisenseEditText extends EditText {
    private static String mFontName = "micross/micross.ttf";

    private static Typeface tfs;

    public static Typeface createTypeface(Context context) {
        if (tfs == null) {
            tfs = Typeface.createFromAsset(context.getAssets(), mFontName);
        }
        return tfs;
    }

    public ApisenseEditText(Context context) {
        super(context);
        setTypeface(createTypeface(context));
    }

    public ApisenseEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        setTypeface(createTypeface(context));
    }

    public ApisenseEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setTypeface(createTypeface(context));
    }
}
