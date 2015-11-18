package com.apisense.bee.utils;

import android.content.res.Resources;
import android.os.Build;

public class RetroCompatibility {
    public static int retrieveColor(Resources res, int colorId) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return res.getColor(colorId, null);
        } else {
            return res.getColor(colorId);
        }
    }
}
