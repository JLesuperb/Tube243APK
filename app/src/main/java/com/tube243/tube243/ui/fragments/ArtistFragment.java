package com.tube243.tube243.ui.fragments;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.tube243.tube243.R;
import com.tube243.tube243.adapters.ArtistTubeAdapter;
import com.tube243.tube243.data.LocalData;
import com.tube243.tube243.data.Params;
import com.tube243.tube243.entities.Artist;
import com.tube243.tube243.entities.Tube;
import com.tube243.tube243.processes.LocalTextTask;
import com.tube243.tube243.ui.activities.HomeActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by JonathanLesuperb on 4/19/2018.
 */

public class ArtistFragment extends BaseFragment implements ArtistTubeAdapter.OnTubeListener
{
    private static ArtistFragment _instance;
    private LocalData localData;
    List<Tube> tubes;
    private ArtistTubeAdapter artistTubeAdapter;

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
        if(getContext()!=null)
        {
            localData = new LocalData(getContext());
        }
        tubes = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
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
            Artist artist = (Artist) bundle.getSerializable("artist");
            if(artist!=null)
            {
                toolbar.setTitle(artist.getName());
                loadData(artist.getId());
            }
            if (bytes != null)
            {
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                artistImageView.setImageBitmap(bitmap);
            }
        }


        artistTubeAdapter = new ArtistTubeAdapter();
        artistTubeAdapter.setOnTubeListener(this);
        RecyclerView listTubes = view.findViewById(R.id.listTubes);
        listTubes.setLayoutManager(new LinearLayoutManager(getContext()));
        listTubes.setAdapter(artistTubeAdapter);
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

    private void loadData(final Long artistId)
    {
        LocalTextTask textTask = new LocalTextTask();
        textTask.setUrlString(Params.SERVER_HOST+"?controller=utilities&method=artist-tubes&artistId="+artistId+"&userId="+localData.getLong("userId"));
        textTask.setListener(new LocalTextTask.ResultListener() {
            @Override
            public void onResult(Map<String, Object> result)
            {
                try
                {
                    if(result.containsKey("isDone") && (Boolean) result.get("isDone"))
                    {
                        artistTubeAdapter.clear();
                        List<Map<String,Object>> maps = (List<Map<String,Object>>) result.get("data");
                        List<Tube> tubes = new ArrayList<>();
                        for(int i=0;i<maps.size();i++)
                        {
                            Map<String,Object> map = maps.get(i);
                            Tube tube = new Tube(Long.parseLong(map.get("id").toString()),map.get("name").toString(),map.get("folder").toString(),
                                    map.get("size").toString(), Integer.parseInt(map.get("quantity").toString()));
                            tubes.add(tube);
                        }
                        artistTubeAdapter.addAll(tubes);
                        //tubeAdapter.notifyDataSetChanged();
                        //subscribe = result.containsKey("subscribe") && (Boolean) result.get("subscribe");
                    }
                    else
                    {
                        /*AlertDialog.Builder alertDialog = new AlertDialog.Builder(ArtistDetailActivity.this);
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
                                loadData(artistId);
                            }
                        });
                        alertDialog.setNegativeButton("Annuler", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        alertDialog.show();*/
                    }
                }
                catch (Exception ignored)
                {

                }
            }
        });
        textTask.execute();
    }

    @Override
    public void onTubeClick(Tube tube)
    {

    }
}
