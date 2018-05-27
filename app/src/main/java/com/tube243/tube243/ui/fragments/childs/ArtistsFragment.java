package com.tube243.tube243.ui.fragments.childs;

import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.transition.Fade;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tube243.tube243.R;
import com.tube243.tube243.adapters.ArtistAdapter;
import com.tube243.tube243.core.DatabaseHelper;
import com.tube243.tube243.data.Params;
import com.tube243.tube243.entities.Artist;
import com.tube243.tube243.processes.LocalTextTask;
import com.tube243.tube243.ui.fragments.ArtistFragment;
import com.tube243.tube243.ui.fragments.BaseFragment;
import com.tube243.tube243.ui.transitions.ZoomTransition;
import com.tube243.tube243.utils.Utility;

import java.io.ByteArrayOutputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by JLesuperb on 2017-10-22.
 */

public class ArtistsFragment extends BaseFragment
        implements ArtistAdapter.OnArtistClickListener, SwipeRefreshLayout.OnRefreshListener
{
    private List<Artist> artistList;
    private ArtistAdapter artistAdapter;
    //private LinearLayout linearProgress;

    private static ArtistsFragment _instance;
    private DatabaseHelper db;

    public static ArtistsFragment getInstance()
    {
        if(_instance == null)
            _instance = new ArtistsFragment();
        return _instance;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public void onResume()
    {
        super.onResume();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        //return super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.child_fragment_artists,container,false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        artistList = new LinkedList<>();
        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.getRecycledViewPool().setMaxRecycledViews(0, 0);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity().getApplicationContext(),getGridRow()));
        artistAdapter = new ArtistAdapter(artistList);
        artistAdapter.setContext(getActivity().getApplicationContext());
        recyclerView.setAdapter(artistAdapter);
        artistAdapter.setOnArtistClickListener(this);
        //loadDataFromServer();

        SwipeRefreshLayout swipeContainer = view.findViewById(R.id.swipeContainer);
        swipeContainer.setOnRefreshListener(this);
        swipeContainer.setColorSchemeResources(R.color.colorPrimary,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark);

        db = new DatabaseHelper(getContext());
        if(db.getArtistsCount()<=0)
        {
            loadDataFromServer();
        }
        else
        {
            loadDataFromDB();
        }
    }

    private int getGridRow()
    {
        return Utility.calculateNoOfColumns(getActivity().getApplicationContext());
    }

    private void loadDataFromServer()
    {
        LocalTextTask textTask = new LocalTextTask();
        textTask.setUrlString(Params.SERVER_HOST+"?controller=utilities&method=artists&from-index=15");
        textTask.setListener(new LocalTextTask.ResultListener()
        {
            @Override
            public void onResult(Map<String, Object> result)
            {
                if(result.containsKey("isDone") && (Boolean) result.get("isDone"))
                {
                    List<Map<String,Object>> maps = (List<Map<String,Object>>) result.get("data");
                    for(int i=0;i<maps.size();i++)
                    {
                        Map<String,Object> map = maps.get(i);
                        Artist artist = new Artist(Integer.parseInt(map.get("id").toString())
                                ,map.get("name").toString(),map.get("folder").toString(),
                                map.get("image").toString(),
                                Integer.parseInt(map.get("quantity").toString()));
                        db.insert(artist);
                    }
                    if(db.getArtistsCount()>0)
                    {
                        loadDataFromDB();
                    }
                }
                else
                {

                    /*if(artistList.size()==0) artistLayoutOff.setVisibility(View.VISIBLE);*/
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity().getApplicationContext());
                    alertDialog.setIcon(android.R.drawable.ic_dialog_alert);
                    alertDialog.setTitle("Erreurs");
                    String msg = "Veuillez vérifier votre connexion internet";
                    alertDialog.setMessage(msg);
                    alertDialog.setCancelable(false);
                    alertDialog.setPositiveButton("Réessayer", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            loadDataFromServer();
                        }
                    });
                    alertDialog.setNegativeButton("Annuler", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    //alertDialog.show();
                }
            }
        });
        textTask.execute();
    }

    private void loadDataFromDB()
    {
        artistList.addAll(db.getAllArtists());
        artistAdapter.notifyDataSetChanged();
    }

    @Override
    public void onClickArtist(ArtistAdapter.ViewHolder holder, Artist artist)
    {
        /*Intent intent = new Intent(getActivity().getApplicationContext(),ArtistDetailActivity.class);
        intent.putExtra("artistId",artist.getId());
        intent.putExtra("artistName",artist.getName());
        intent.putExtra("artistFolder",artist.getFolder());
        intent.putExtra("artistImage",artist.getImage());
        intent.putExtra("artistCounter",artist.getCounter());
        if(artist.getImageBitmap()!=null)
        {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            artist.getImageBitmap().compress(Bitmap.CompressFormat.PNG,100,stream);
            byte[] bytes = stream.toByteArray();
            intent.putExtra("artistBitmap",bytes);
        }
        startActivity(intent);*/
        Fragment fragment = getParent();
        if(fragment!=null)
        {
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            ArtistFragment artistFragment = ArtistFragment.getInstance();
            Bundle bundle = new Bundle();
            bundle.putSerializable("artist",artist);
            if(artist.getImageBitmap()!=null)
            {
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                artist.getImageBitmap().compress(Bitmap.CompressFormat.PNG,100,stream);
                byte[] bytes = stream.toByteArray();
                bundle.putByteArray("artistBitmap",bytes);
            }
            artistFragment.setArguments(bundle);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            {
                artistFragment.setSharedElementEnterTransition(new ZoomTransition());
                artistFragment.setEnterTransition(new Fade());
                setExitTransition(new Fade());
                artistFragment.setSharedElementReturnTransition(new ZoomTransition());
            }

            artistFragment.setArtist(artist);
            fragmentManager
                    .beginTransaction()
                    .setCustomAnimations(R.animator.fade_in,R.animator.fade_out,
                            R.animator.fade_in,R.animator.fade_out)
                    .addSharedElement(holder.artistImageView, "artistImage")
                    .hide(fragment)
                    .setReorderingAllowed(true)
                    .add(R.id.fragment_container, artistFragment)
                    .addToBackStack(null)
                    .commit();
        }
    }

    @Override
    public void onRefresh()
    {

    }

    public void applyFilter(String filterString)
    {

    }
}
