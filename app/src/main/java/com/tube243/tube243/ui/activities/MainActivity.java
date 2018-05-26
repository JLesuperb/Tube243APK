package com.tube243.tube243.ui.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;

import com.tube243.tube243.AboutActivity;
import com.tube243.tube243.ContactActivity;
import com.tube243.tube243.DesktopActivity;
import com.tube243.tube243.NotificationActivity;
import com.tube243.tube243.ProfileEditorActivity;
import com.tube243.tube243.R;
import com.tube243.tube243.adapters.SubscribeAdapter;
import com.tube243.tube243.adapters.TubeAdapter;
import com.tube243.tube243.adapters.ViewPagerAdapter;
import com.tube243.tube243.core.Single;
import com.tube243.tube243.data.LocalData;
import com.tube243.tube243.entities.Artist;
import com.tube243.tube243.entities.Tube;
import com.tube243.tube243.ui.fragments.childs.ArtistsFragment;
import com.tube243.tube243.ui.fragments.childs.SubscribesFragment;
import com.tube243.tube243.ui.fragments.childs.TubesFragment;
import com.tube243.tube243.utils.Utility;

import java.io.File;
import java.io.IOException;

/**
 * Created by JonathanLesuperb on 4/17/2018.
 */

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        View.OnClickListener,
        SubscribeAdapter.OnSubscribeClickListener,TubeAdapter.OnTubeClickListener
{
    private LocalData localData;
    private Single single;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        localData = new LocalData(getApplicationContext());

        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.setDrawerListener(drawerToggle);
        drawerToggle.syncState();
        single = Single.getInstance();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View header = navigationView.getHeaderView(0);
        AppCompatTextView user_name = header.findViewById(R.id.user_name);
        user_name.setText(localData.getString("firstName")+" "+localData.getString("lastName"));
        AppCompatTextView user_phone = header.findViewById(R.id.user_phone);
        user_phone.setText(localData.getString("phoneNumber"));
        AppCompatImageView user_image = header.findViewById(R.id.user_image);
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
                Drawable drawable = Drawable.createFromStream(getAssets().open("images/user_default.png"),null);
                user_image.setImageDrawable(drawable);
            }
            catch (IOException ignored){}
        }

        //TabLayout
        TabLayout tabLayout = findViewById(R.id.tabs);
        ViewPager viewPager = findViewById(R.id.viewPager);
        setupViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);

        TabLayout.Tab tab = tabLayout.getTabAt(1);
        if(tab!=null)
            tab.select();
    }

    private void setupViewPager(ViewPager viewPager)
    {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment("Artists", ArtistsFragment.getInstance());
        adapter.addFragment("Tubes", TubesFragment.getInstance());
        adapter.addFragment("Subs", SubscribesFragment.getInstance());
        viewPager.setAdapter(adapter);
    }

    @Override
    public void onBackPressed()
    {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        switch (item.getItemId())
        {

            case R.id.nav_menu_profile:
                //showProfilePopUp();
                break;
            case R.id.nav_menu_notifications:
                startActivity(new Intent(MainActivity.this,NotificationActivity.class));
                break;
            case R.id.nav_menu_desktop:
                startActivity(new Intent(MainActivity.this,DesktopActivity.class));
                break;
            case R.id.nav_about:
                startActivity(new Intent(MainActivity.this,AboutActivity.class));
                break;
            case R.id.nav_contact:
                startActivity(new Intent(MainActivity.this,ContactActivity.class));
                break;
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.user_image:
                showProfilePopUp();
                break;
        }
    }

    private void showProfilePopUp()
    {
        LayoutInflater layoutInflater = LayoutInflater.from(MainActivity.this);
        final View view = layoutInflater.inflate(R.layout.profile_popup,null);
        AlertDialog.Builder builder =  new AlertDialog.Builder(MainActivity.this);

        AppCompatEditText first_name_edit = view.findViewById(R.id.first_name_edit);
        first_name_edit.setText(localData.getString("firstName"));

        AppCompatEditText last_name_edit = view.findViewById(R.id.last_name_edit);
        last_name_edit.setText(localData.getString("lastName"));

        AppCompatEditText phone_number_edit = view.findViewById(R.id.phone_number_edit);
        phone_number_edit.setText(localData.getString("phoneNumber"));

        AppCompatImageView image_profile = view.findViewById(R.id.image_profile);
        if(localData.getString("imageProfile")!=null) {
            File file = new File(single.getParentFolder(),"profile/"+localData.getString("imageProfile"));
            if(file.exists())
            {
                Bitmap bitmap = BitmapFactory.decodeFile(file.getPath());
                image_profile.setImageBitmap(bitmap);
            }
            else
            {
                try
                {
                    Drawable drawable = Drawable.createFromStream(getAssets().open("images/user_default.png"),null);
                    image_profile.setImageDrawable(drawable);
                }
                catch (IOException ignored){}
            }
        }
        else
        {
            try
            {
                Drawable drawable = Drawable.createFromStream(getAssets().open("images/user_default.png"),null);
                image_profile.setImageDrawable(drawable);
            }
            catch (IOException ignored){}
        }

        builder.setView(view);
        builder.setPositiveButton("Editer", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                Intent intent = new Intent(getApplicationContext(),ProfileEditorActivity.class);
                startActivity(intent);
            }
        });
        builder.setNegativeButton("Annuler", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    @Override
    public void onClickTube(TubeAdapter.ViewHolder holder, Tube tube)
    {

    }

    @Override
    public void onClickSubscribe(Artist artist)
    {

    }
}
