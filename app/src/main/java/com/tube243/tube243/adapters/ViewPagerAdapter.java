package com.tube243.tube243.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by JLesuperb on 2017-10-22.
 */

public class ViewPagerAdapter extends FragmentPagerAdapter
{
    private List<Fragment> fragments;
    private List<String> titles;
    public ViewPagerAdapter(FragmentManager manager)
    {
        super(manager);
        fragments = new ArrayList<>();
        titles = new ArrayList<>();
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    public void addFragment(String title, Fragment fragment)
    {
        titles.add(title);
        fragments.add(fragment);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titles.get(position);
    }
}
