package com.tube243.tube243.ui.fragments;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.tube243.tube243.R;
import com.tube243.tube243.data.Params;
import com.tube243.tube243.entities.Artist;
import com.tube243.tube243.processes.LocalImageTask;
import com.tube243.tube243.ui.activities.HomeActivity;

/**
 * Created by JonathanLesuperb on 4/19/2018.
 */

public class ArtistFragment extends BaseFragment
{
    private static ArtistFragment _instance;
    private Artist artist;

    public static ArtistFragment getInstance()
    {
        if(_instance==null)
        {
            _instance = new ArtistFragment();
        }
        return _instance;
    }

    public ArtistFragment()
    {
        //Must be empty
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
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
        return inflater.inflate(R.layout.fragment_artist, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        Toolbar toolbar = view.findViewById(R.id.toolbar);
        toolbar.setTitle("Artist");
        ((HomeActivity)getActivity()).setSupportActionBar(toolbar);
        ActionBar actionBar = ((HomeActivity) getActivity()).getSupportActionBar();
        if (actionBar != null)
        {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        else if(getActivity().getActionBar()!=null)
        {
            getActivity().getActionBar().setHomeButtonEnabled(true);
        }

        AppCompatImageView artistImageView = view.findViewById(R.id.artistImageView);
        Bundle bundle = this.getArguments();
        if(bundle != null)
        {
            byte[] bytes = bundle.getByteArray("artistBitmap");
            if (bytes != null)
            {
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                artistImageView.setImageBitmap(bitmap);
            }
        }
        /*else
        {
            LocalImageTask imageTask = new LocalImageTask(artistImageView);
            imageTask.setUrlString("http://www.tube243.com/views/users/tbm/"+artist.getFolder()+"/img/"+artist.getImage());
            imageTask.execute("");
        }*/
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
                getActivity().getSupportFragmentManager().popBackStack();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void setArtist(Artist artist)
    {
        this.artist = artist;
    }
}
