package com.tube243.tube243.ui.fragments;

import android.app.SearchManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.transition.Explode;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.tube243.tube243.R;
import com.tube243.tube243.adapters.ViewPagerAdapter;
import com.tube243.tube243.data.LocalData;
import com.tube243.tube243.ui.activities.HomeActivity;
import com.tube243.tube243.ui.dialogs.ProfileDialog;
import com.tube243.tube243.ui.fragments.childs.ArtistsFragment;
import com.tube243.tube243.ui.fragments.childs.SubscribesFragment;
import com.tube243.tube243.ui.fragments.childs.TubesFragment;
import com.tube243.tube243.utils.Utility;
import com.tube243.tube243.widgets.CircleImageView;

import java.io.File;
import java.io.IOException;

/**
 * Created by JonathanLesuperb on 4/19/2018.
 */

public class HomeFragment extends BaseFragment
        implements NavigationView.OnNavigationItemSelectedListener, View.OnKeyListener,
        View.OnClickListener, ProfileDialog.ProfileEditor, SearchView.OnQueryTextListener {
    private static HomeFragment _instance;

    private ArtistsFragment artistsFragment;
    private TubesFragment tubesFragment;
    private SubscribesFragment subscribesFragment;

    public static HomeFragment getInstance()
    {
        if(_instance==null)
        {
            _instance = new HomeFragment();
        }
        return _instance;
    }

    public HomeFragment()
    {
        //Must be empty
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
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
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        Toolbar toolbar = view.findViewById(R.id.toolbar);
        ((HomeActivity)getActivity()).setSupportActionBar(toolbar);

        DrawerLayout drawer = view.findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                getActivity(), drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = view.findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        LocalData data = new LocalData(getContext());

        View header = navigationView.getHeaderView(0);
        AppCompatTextView user_name = header.findViewById(R.id.user_name);
        user_name.setText(data.getString("firstName")+" "+data.getString("lastName"));
        AppCompatTextView user_phone = header.findViewById(R.id.user_phone);
        user_phone.setText(data.getString("phoneNumber"));
        CircleImageView user_image = header.findViewById(R.id.user_image);
        user_image.setOnClickListener(this);
        File folder = Utility.getFolder(".profile");
        File file = new File(folder,".profile.jpeg");
        if(file.exists())
        {
            Bitmap bitmap = BitmapFactory.decodeFile(file.getPath());
            user_image.setImageBitmap(bitmap);
        }
        else
        {
            try
            {
                Drawable drawable = Drawable.createFromStream(getActivity().getAssets().open("images/user_default.png"),null);
                user_image.setImageDrawable(drawable);
            }
            catch (IOException ignored){}
        }

        //Setting TabLayout
        TabLayout tabLayout = view.findViewById(R.id.tabs);
        ViewPager viewPager = view.findViewById(R.id.viewPager);
        setupViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);

        TabLayout.Tab tab = tabLayout.getTabAt(1);
        if(tab!=null)
            tab.select();

        view.setFocusableInTouchMode(true);
        view.requestFocus();
        view.setOnKeyListener(this);
    }

    private void setupViewPager(ViewPager viewPager)
    {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getActivity().getSupportFragmentManager());
        artistsFragment = ArtistsFragment.getInstance();
        artistsFragment.setParent(this);
        adapter.addFragment("Artists", artistsFragment);
        tubesFragment = TubesFragment.getInstance();
        tubesFragment.setParent(this);
        adapter.addFragment("Tubes", tubesFragment);
        subscribesFragment = SubscribesFragment.getInstance();
        subscribesFragment.setParent(this);
        adapter.addFragment("Subs", subscribesFragment);
        viewPager.setAdapter(adapter);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item)
    {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        switch (item.getItemId())
        {

            case R.id.nav_menu_profile:
                ProfileDialog profileDialog = new ProfileDialog();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                {
                    profileDialog.setEnterTransition(new Explode());
                    profileDialog.setExitTransition(new Explode());
                }
                profileDialog.setProfileEditor(this);
                profileDialog.show(fragmentManager, "Sample Fragment");
                break;
            case R.id.nav_menu_notifications:
                NotificationFragment notificationFragment = NotificationFragment.getInstance();
                fragmentManager.beginTransaction()
                        .setCustomAnimations(R.animator.fade_in,R.animator.fade_out,
                                R.animator.fade_in,R.animator.fade_out)
                        .hide(this)
                        .add(R.id.fragment_container, notificationFragment)
                        .addToBackStack(null)
                        .commit();
                break;
            case R.id.nav_menu_desktop:
                DesktopFragment desktopFragment = DesktopFragment.getInstance();
                fragmentManager.beginTransaction()
                        .setCustomAnimations(R.animator.fade_in,R.animator.fade_out,
                                R.animator.fade_in,R.animator.fade_out)
                        .hide(this)
                        .add(R.id.fragment_container, desktopFragment)
                        .addToBackStack(null)
                        .commit();
                break;
            case R.id.nav_about:
                AboutFragment aboutFragment = AboutFragment.getInstance();
                fragmentManager.beginTransaction()
                        .setCustomAnimations(R.animator.fade_in,R.animator.fade_out,
                                R.animator.fade_in,R.animator.fade_out)
                        .hide(this)
                        .add(R.id.fragment_container, aboutFragment)
                        .addToBackStack(null)
                        .commit();
                break;
            case R.id.nav_contact:
                ContactFragment contactFragment = ContactFragment.getInstance();
                fragmentManager.beginTransaction()
                        .setCustomAnimations(R.animator.fade_in,R.animator.fade_out,
                                R.animator.fade_in,R.animator.fade_out)
                        .hide(this)
                        .add(R.id.fragment_container, contactFragment)
                        .addToBackStack(null)
                        .commit();
                break;
        }

        if(getView()!=null)
        {
            DrawerLayout drawer = getView().findViewById(R.id.drawer_layout);
            if(drawer!=null)
            {
                if (drawer.isDrawerOpen(GravityCompat.START))
                {
                    drawer.closeDrawer(GravityCompat.START);
                }
            }
        }
        return false;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.search_menu, menu);
        MenuItem action_search = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) action_search.getActionView();
        searchView.setMaxWidth(Integer.MAX_VALUE);
        searchView.setOnQueryTextListener(this);
        //MenuItemCompat.setShowAsAction(action_search, MenuItemCompat.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW | MenuItemCompat.SHOW_AS_ACTION_IF_ROOM);
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event)
    {
        return false;
    }

    @Override
    public boolean onBackPressed()
    {
        if(getView()!=null)
        {
            DrawerLayout drawer = getView().findViewById(R.id.drawer_layout);
            if(drawer!=null)
            {
                if (drawer.isDrawerOpen(GravityCompat.START))
                {
                    drawer.closeDrawer(GravityCompat.START);
                    return true;
                }
            }
        }
        return super.onBackPressed();
    }

    @Override
    public void onClick(View v)
    {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        switch (v.getId())
        {
            case R.id.user_image:
                if(getView()!=null)
                {
                    DrawerLayout drawer = getView().findViewById(R.id.drawer_layout);
                    if(drawer!=null)
                    {
                        if (drawer.isDrawerOpen(GravityCompat.START))
                        {
                            drawer.closeDrawer(GravityCompat.START);
                        }
                    }
                }
                ProfileDialog profileDialog = new ProfileDialog();
                profileDialog.setProfileEditor(this);
                profileDialog.show(fragmentManager, "Sample Fragment");
                break;
        }
    }

    public void applyFilter(String filterString)
    {
        artistsFragment.applyFilter(filterString);
        tubesFragment.applyFilter(filterString);
        subscribesFragment.applyFilter(filterString);
    }

    @Override
    public void onEdit()
    {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        ProfileFragment profileFragment = ProfileFragment.getInstance();
        fragmentManager.beginTransaction()
                .setCustomAnimations(R.animator.fade_in,R.animator.fade_out,
                        R.animator.fade_in,R.animator.fade_out)
                .hide(this)
                .add(R.id.fragment_container, profileFragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public boolean onQueryTextSubmit(String query)
    {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText)
    {
        applyFilter(newText);
        return true;
    }
}
