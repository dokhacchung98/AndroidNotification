package com.example.androidnotification;

import android.content.Context;
import android.util.DisplayMetrics;

public class Extention {
    public static int convertDpToPixel(int dp, Context context) {
        return dp * (context.getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }

    public static int convertPixelsToDp(int px, Context context) {
        return px / (context.getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }
}
