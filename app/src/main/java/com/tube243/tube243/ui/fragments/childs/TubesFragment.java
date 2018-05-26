package com.tube243.tube243.ui.fragments.childs;

import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.transition.Fade;
import android.transition.TransitionInflater;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tube243.tube243.R;
import com.tube243.tube243.adapters.TubeAdapter;
import com.tube243.tube243.core.DatabaseHelper;
import com.tube243.tube243.data.Params;
import com.tube243.tube243.entities.Tube;
import com.tube243.tube243.processes.LocalTextTask;
import com.tube243.tube243.ui.fragments.BaseFragment;
import com.tube243.tube243.ui.fragments.MediaFragment;
import com.tube243.tube243.utils.Utility;
import com.tube243.tube243.widgets.AutofitRecyclerView;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by JLesuperb on 2017-10-22.
 */

public class TubesFragment extends BaseFragment
        implements TubeAdapter.OnTubeClickListener
{
    private List<Tube> tubeList;
    private TubeAdapter tubeAdapter;
    //private LinearLayout linearProgress;

    private static TubesFragment _instance;
    private DatabaseHelper db;

    public static TubesFragment getInstance()
    {
        if(_instance == null)
            _instance = new TubesFragment();
        return _instance;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null)
        {
            //mContent = getSupportFragmentManager().getFragment(savedInstanceState, "myFragmentName");
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
    }



    @Override
    public void onResume()
    {
        super.onResume();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        tubeList = new LinkedList<>();
        //linearProgress = view.findViewById(R.id.linearProgress);
        //linearProgress.setVisibility(View.GONE);
        AutofitRecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity().getApplicationContext(),getGridRow()));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            recyclerView.setOnScrollChangeListener(new View.OnScrollChangeListener()
            {
                @Override
                public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY)
                {

                }
            });
        }
        //recyclerView.setLayoutManager(new GridAutofitLayoutManager(getActivity().getApplicationContext(),300));
        tubeAdapter = new TubeAdapter(tubeList);
        recyclerView.setAdapter(tubeAdapter);
        tubeAdapter.setOnTubeClickListener(this);
        //loadDataFromServer();

        db = new DatabaseHelper(getContext());

        if(db.getTubesCount()<=0)
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
        //linearProgress.setVisibility(View.VISIBLE);
        LocalTextTask textTask = new LocalTextTask();
        textTask.setUrlString(Params.SERVER_HOST+"?controller=utilities&method=tubes&from-index=15");
        textTask.setListener(new LocalTextTask.ResultListener()
        {
            @Override
            public void onResult(Map<String, Object> result)
            {
                //linearProgress.setVisibility(View.GONE);
                try
                {
                    if(result.containsKey("isDone") && (Boolean) result.get("isDone"))
                    {
                        List<Map<String,Object>> maps = (List<Map<String,Object>>) result.get("data");
                        for(int i=0;i<maps.size();i++)
                        {
                            Map<String,Object> map = maps.get(i);
                            Tube tube = new Tube(Integer.parseInt(map.get("id").toString()),map.get("name").toString(),map.get("folder").toString(),
                                    map.get("size").toString(), Integer.parseInt(map.get("quantity").toString()));
                            db.insert(tube);
                            //tubeList.add(tube);
                            /*tubesIndex++;*/
                        }
                        if(db.getTubesCount()>0)
                        {
                            loadDataFromDB();
                        }
                    }
                    else
                    {

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
                                /*loadTubesData();*/
                            }
                        });
                        alertDialog.setNegativeButton("Annuler", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        alertDialog.show();
                    }
                }
                catch (Exception ex)
                {
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
        tubeList.addAll(db.getAllTubes());
        tubeAdapter.notifyDataSetChanged();
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        //return super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.child_fragment_tubes,container,false);
    }

    @Override
    public void onClickTube(TubeAdapter.ViewHolder holder, Tube tube)
    {
        Fragment fragment = getParent();
        if(fragment!=null)
        {
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            MediaFragment mediaFragment = MediaFragment.getInstance();
            mediaFragment.setTube(tube);
            fragmentManager
                    .beginTransaction()
                    .setCustomAnimations(R.animator.fade_in,R.animator.fade_out,
                            R.animator.fade_in,R.animator.fade_out)
                    .hide(fragment)
                    .add(R.id.fragment_container, mediaFragment)
                    .addToBackStack(null)
                    .commit();
        }

    }

    public void applyFilter(String filterString)
    {
        tubeAdapter.setFilterPattern(filterString);
        tubeAdapter.notifyDataSetChanged();
    }
}
