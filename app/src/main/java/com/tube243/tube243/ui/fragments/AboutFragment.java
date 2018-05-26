package com.tube243.tube243.ui.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tube243.tube243.R;
import com.tube243.tube243.ui.activities.HomeActivity;

/**
 * Created by JonathanLesuperb on 4/19/2018.
 */

public class AboutFragment extends BaseFragment
{
    private static AboutFragment _instance;

    public static AboutFragment getInstance()
    {
        if(_instance==null)
        {
            _instance = new AboutFragment();
        }
        return _instance;
    }

    public AboutFragment()
    {
        //Must be empty
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // If activity recreated (such as from screen rotate), restore
        // the previous article selection set by onSaveInstanceState().
        // This is primarily necessary when in the two-pane layout.
        /*if (savedInstanceState != null)
        {
            mCurrentPosition = savedInstanceState.getInt(ARG_POSITION);
        }*/
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_notification, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        Toolbar toolbar = view.findViewById(R.id.toolbar);
        toolbar.setTitle("About");
        ((HomeActivity)getActivity()).setSupportActionBar(toolbar);

    }
}
