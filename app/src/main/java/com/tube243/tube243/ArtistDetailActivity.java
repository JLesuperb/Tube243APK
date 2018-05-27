package com.tube243.tube243;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.tube243.tube243.adapters.ArtistTubeAdapter;
import com.tube243.tube243.data.LocalData;
import com.tube243.tube243.data.Params;
import com.tube243.tube243.entities.Artist;
import com.tube243.tube243.entities.Tube;
import com.tube243.tube243.processes.LocalImageTask;
import com.tube243.tube243.processes.LocalTextTask;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class ArtistDetailActivity extends AppCompatActivity implements View.OnClickListener
{

    private Artist artist;
    private ArtistTubeAdapter tubeAdapter;
    private List<Tube> tubesList;
    private LocalData localData;
    private boolean subscribe;
    private Long artistId;
    private FloatingActionButton likeBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artist_detail);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar()!= null)
        {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        else if (getActionBar() != null)
        {
            getActionBar().setHomeButtonEnabled(true);
        }

        Intent intent = getIntent();
        if(!intent.hasExtra("artistId")){
            finish();
        }

        localData = new LocalData(getApplicationContext());

        setTitle(intent.getStringExtra("artistName"));
        artistId = intent.getLongExtra("artistId",Long.MIN_VALUE);
        loadData(artistId);
        artist = new Artist(intent.getIntExtra("artistId",Integer.MIN_VALUE),intent.getStringExtra("artistName"),intent.getStringExtra("artistFolder"),intent.getStringExtra("artistImage"),intent.getIntExtra("artistCounter",Integer.MIN_VALUE));
        setKeys();

    }

    private void setKeys()
    {
        AppCompatImageView artistImageView = (AppCompatImageView)findViewById(R.id.artistImageView);
        if(getIntent().hasExtra("artistBitmap"))
        {
            byte[] bytes = getIntent().getByteArrayExtra("artistBitmap");
            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
            artistImageView.setImageBitmap(bitmap);
        }
        else
        {
            LocalImageTask imageTask = new LocalImageTask(artistImageView);
            imageTask.setUrlString(Params.SERVER+"/views/users/tbm/"+artist.getFolder()+"/img/"+artist.getImage());
            imageTask.execute("");
        }

        tubesList = new LinkedList<>();

        tubeAdapter = new ArtistTubeAdapter(tubesList);
        RecyclerView listTubes = (RecyclerView)findViewById(R.id.listTubes);
        LinearLayoutManager layoutManager = new LinearLayoutManager(ArtistDetailActivity.this);
        listTubes.setLayoutManager(layoutManager);
        listTubes.setAdapter(tubeAdapter);
        tubeAdapter.setOnTubeListener(new ArtistTubeAdapter.OnTubeListener() {
            @Override
            public void onTubeClick(Tube tube) {
                Intent intent = new Intent(getApplicationContext(),MediaPlayerActivity.class);
                intent.putExtra("tubeId",tube.getId());
                intent.putExtra("folder",tube.getFolder());
                intent.putExtra("filename",tube.getName());
                startActivity(intent);
            }
        });
        tubeAdapter.notifyDataSetChanged();

        likeBtn = (FloatingActionButton) findViewById(R.id.likeBtn);
        likeBtn.setOnClickListener(this);
    }

    private void loadData(final Long artistId)
    {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Chargement...");
        progressDialog.show();


        LocalTextTask textTask = new LocalTextTask();
        textTask.setUrlString(Params.SERVER_HOST+"?controller=utilities&method=artist-tubes&artistId="+artistId+"&userId="+localData.getLong("userId"));
        textTask.setListener(new LocalTextTask.ResultListener() {
            @Override
            public void onResult(Map<String, Object> result) {
                progressDialog.dismiss();
                try{

                    if(result.containsKey("isDone") && (Boolean) result.get("isDone"))
                    {
                        List<Map<String,Object>> maps = (List<Map<String,Object>>) result.get("data");
                        for(int i=0;i<maps.size();i++){
                            Map<String,Object> map = maps.get(i);
                            Tube tube = new Tube(Long.parseLong(map.get("id").toString()),map.get("name").toString(),map.get("folder").toString(),
                                    map.get("size").toString(), Integer.parseInt(map.get("quantity").toString()));
                            tubesList.add(tube);
                        }
                        tubeAdapter.notifyDataSetChanged();
                        subscribe = result.containsKey("subscribe") && (Boolean) result.get("subscribe");
                        CharSequence sequence = (subscribe) ? "Se desabonner" : "S'abonner";
                        //likeBtn.setText(sequence);
                        if(subscribe)
                        {
                            likeBtn.setImageResource(R.drawable.ic_action_follow);
                        }
                        else
                        {
                            likeBtn.setImageResource(R.drawable.ic_action_unlike);
                        }
                    }
                    else{
                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(ArtistDetailActivity.this);
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
                        alertDialog.show();
                    }
                }
                catch (Exception ignored){

                }
            }
        });
        textTask.execute();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId())
        {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.likeBtn:
                String method = (subscribe) ? "unset-like" : "set-like";
                final ProgressDialog progressDialog = new ProgressDialog(this);
                progressDialog.setIndeterminate(true);
                progressDialog.setCancelable(false);
                progressDialog.setMessage("Chargement...");
                progressDialog.show();


                LocalTextTask textTask = new LocalTextTask();
                textTask.setUrlString(Params.SERVER_HOST+"?controller=utilities&method="+method+"&artistId="+artistId+"&userId="+localData.getLong("userId"));
                textTask.setListener(new LocalTextTask.ResultListener() {
                    @Override
                    public void onResult(Map<String, Object> result) {
                        if(result.containsKey("isDone") && (Boolean) result.get("isDone"))
                        {
                            progressDialog.dismiss();
                            subscribe = result.containsKey("subscribe") && (Boolean) result.get("subscribe");
                            CharSequence sequence = (subscribe) ? "Se desabonner" : "S'abonner";
                            //likeBtn.setText(sequence);
                            if(subscribe)
                            {
                                likeBtn.setImageResource(R.drawable.ic_action_follow);
                            }
                            else
                            {
                                likeBtn.setImageResource(R.drawable.ic_action_unlike);
                            }
                            Toast.makeText(getApplicationContext(),sequence,Toast.LENGTH_LONG).show();
                        }
                    }
                });
                textTask.execute();
                break;

        }
    }
}
