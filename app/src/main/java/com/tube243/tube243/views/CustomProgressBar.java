package com.tube243.tube243.views;

import android.content.Context;
import android.graphics.Canvas;
import android.widget.ProgressBar;

/**
 * Created by JonathanLesuperb on 2018/05/24.
 */

public class CustomProgressBar extends ProgressBar
{

    public CustomProgressBar(Context context)
    {
        super(context);
    }

    @Override
    protected synchronized void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);
    }
}
