package com.tube243.tube243.ui.transitions;

import android.annotation.TargetApi;
import android.os.Build;
import android.transition.ChangeBounds;
import android.transition.ChangeImageTransform;
import android.transition.ChangeTransform;
import android.transition.TransitionSet;

/**
 * Created by JonathanLesuperb on 4/21/2018.
 */
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class ZoomTransition extends TransitionSet
{
    public ZoomTransition()
    {
        setOrdering(ORDERING_TOGETHER);
        addTransition(new ChangeBounds()).
                addTransition(new ChangeTransform()).
                addTransition(new ChangeImageTransform());
    }
}
