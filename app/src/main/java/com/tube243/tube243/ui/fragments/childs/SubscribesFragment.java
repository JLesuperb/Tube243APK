package com.tube243.tube243.ui.fragments.childs;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tube243.tube243.R;
import com.tube243.tube243.adapters.SubscribeAdapter;
import com.tube243.tube243.data.LocalData;
import com.tube243.tube243.data.Params;
import com.tube243.tube243.entities.Artist;
import com.tube243.tube243.processes.LocalTextTask;
import com.tube243.tube243.ui.fragments.BaseFragment;
import com.tube243.tube243.utils.Utility;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by JLesuperb on 2017-10-22.
 */

public class SubscribesFragment extends BaseFragment
        implements SubscribeAdapter.OnSubscribeClickListener
{
    private List<Artist> subscribesArtistList;
    private SubscribeAdapter subscribeAdapter;
    private LocalData localData;

    private static SubscribesFragment _instance;

    public static SubscribesFragment getInstance()
    {
        if(_instance == null)
            _instance = new SubscribesFragment();
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
        return inflater.inflate(R.layout.child_fragment_subscribes,container,false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        /*loadingPgb = (ProgressBar)view.findViewById(R.id.loadingPgb);
        reloadBtn = (AppCompatButton)view.findViewById(R.id.reloadBtn);
        reloadBtn.setOnClickListener(this);*/
        localData = new LocalData(getActivity().getApplicationContext());
        subscribesArtistList = new LinkedList<>();
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity().getApplicationContext(),getGridRow()));
        subscribeAdapter = new SubscribeAdapter(subscribesArtistList);
        recyclerView.setAdapter(subscribeAdapter);
        subscribeAdapter.setOnSubscribeClickListener(this);
        //loadData();
    }

    private int getGridRow()
    {
        return Utility.calculateNoOfColumns(getActivity().getApplicationContext());
    }

    private void loadData()
    {
        LocalTextTask textTask = new LocalTextTask();
        textTask.setUrlString(Params.SERVER_HOST+"?controller=utilities&method=subscribes&userId="+localData.getLong("userId") +"&from-index=15");
        textTask.setListener(new LocalTextTask.ResultListener() {
            @Override
            public void onResult(Map<String, Object> result) {
                /*linearProgress.setVisibility(View.GONE);*/
                if(result.containsKey("isDone") && (Boolean) result.get("isDone")){
                    List<Map<String,Object>> maps = (List<Map<String,Object>>) result.get("data");
                    for(int i=0;i<maps.size();i++){
                        Map<String,Object> map = maps.get(i);
                        Artist artist = new Artist(Integer.parseInt(map.get("id").toString())
                                ,map.get("name").toString(),map.get("folder").toString(),
                                map.get("image").toString(),
                                Integer.parseInt(map.get("quantity").toString()));
                        subscribesArtistList.add(artist);
                        /*subscribesIndex++;*/
                    }
                    subscribeAdapter.notifyDataSetChanged();
                }else{
                    /*if(subscribesArtistList.size()==0)subscribeLayoutOff.setVisibility(View.VISIBLE);*/
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
                            loadData();
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

    @Override
    public void onClickSubscribe(Artist artist)
    {

    }

    public void applyFilter(String filterString)
    {

    }
}
