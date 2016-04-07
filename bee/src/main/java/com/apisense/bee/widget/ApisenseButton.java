package com.apisense.bee.widget;

/**
 * Created by tibo on 04/04/16.
 */

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.Button;


public class ApisenseButton extends Button {
    private static String mFontName = "micross/micross.ttf";

    private static Typeface tfs;

    public static Typeface createTypeface(Context context) {
        if (tfs == null) {
            tfs = Typeface.createFromAsset(context.getAssets(), mFontName);
        }
        return tfs;
    }

    public ApisenseButton(Context context) {
        super(context);
        setTypeface(createTypeface(context));
    }

    public ApisenseButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        setTypeface(createTypeface(context));
    }

    public ApisenseButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setTypeface(createTypeface(context));
    }
}