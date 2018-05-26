package com.tube243.tube243.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import com.tube243.tube243.*;
import com.tube243.tube243.data.LocalData;
import com.tube243.tube243.ui.fragments.BaseFragment;
import com.tube243.tube243.ui.fragments.HomeFragment;
import com.tube243.tube243.ui.fragments.ProfileFragment;
import com.tube243.tube243.utils.Utility;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by JonathanLesuperb on 4/17/2018.
 */

public class HomeActivity extends AppCompatActivity
{

    List<WeakReference<Fragment>> fragList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        if(savedInstanceState!=null)
        {
            return;
        }

        LocalData data = new LocalData(getApplicationContext());
        if(data.getLong("userId")!=null)
        {
            if(data.getString("firstName")!=null && data.getString("lastName")!=null)
            {
                HomeFragment homeFragment = HomeFragment.getInstance();
                homeFragment.setArguments(getIntent().getExtras());
                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .add(R.id.fragment_container, homeFragment).commit();
            }
            else
            {
                ProfileFragment profileFragment = ProfileFragment.getInstance();
                profileFragment.setArguments(getIntent().getExtras());
                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .add(R.id.fragment_container, profileFragment).commit();
            }
        }
        else
        {
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }

    }

    @Override
    public void onAttachFragment (Fragment fragment)
    {
        fragList.add(new WeakReference(fragment));
    }

    @Override
    public void onBackPressed()
    {
        boolean handled = false;
        for(WeakReference<Fragment> ref : fragList)
        {
            Fragment f = ref.get();
            if(f != null)
            {
                if(f.isVisible())
                {
                    handled = ((BaseFragment)f).onBackPressed();
                    if(handled)
                    {
                        break;
                    }
                }
            }
        }

        if(!handled)
        {
            //this.moveTaskToBack(true);
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        Utility.deleteCache(getApplicationContext());
    }
}
