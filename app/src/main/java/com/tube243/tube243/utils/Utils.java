package com.tube243.tube243.utils;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;

/**
 * Created by JonathanLesuperb on 2018/04/27.
 */

public class Utils
{
    @org.jetbrains.annotations.Contract(pure = true)
    public static float getScreenHeight(Context context)
    {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity)context).getWindowManager()
                .getDefaultDisplay()
                .getMetrics(displayMetrics);
        return displayMetrics.heightPixels;
    }
}
