package com.tube243.tube243.ui.fragments;

import android.support.v4.app.Fragment;

/**
 * Created by JonathanLesuperb on 4/19/2018.
 */

public abstract class BaseFragment extends Fragment
{
    protected Fragment parent;

    /**
     * Could handle back press.
     * @return true if back press was handled
     */
    public boolean onBackPressed()
    {
        return false;
    }

    protected void setParent(Fragment parent)
    {
        this.parent = parent;
    }

    protected Fragment getParent()
    {
        return this.parent;
    }

}
