package com.tube243.tube243;

import android.app.DownloadManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.tube243.tube243.data.Params;
import com.tube243.tube243.entities.Tube;

import java.util.concurrent.TimeUnit;

public class MediaPlayerActivity extends AppCompatActivity implements
        AudioPlayerService.OnPlayListener,
        AudioPlayerService.OnErrorListener,
        View.OnClickListener
{

    private Button b1,b2,b3,b4;
    private ImageView iv;
    private MediaPlayer mediaPlayer;

    private double startTime = 0;
    private double finalTime = 0;

    private Handler myHandler = new Handler();
    private int forwardTime = 5000;
    private int backwardTime = 5000;
    private SeekBar seekbar;
    private TextView tx1,tx2;
    private boolean bounded;

    public static int oneTimeOnly = 0;

    @Override
    protected void onDestroy()
    {
        if(bounded)
        {
            unbindService(serviceConnection);
            bounded = false;
        }
        super.onDestroy();
    }


    private ServiceConnection serviceConnection = new ServiceConnection()
    {

        @Override
        public void onServiceConnected(ComponentName className, IBinder service)
        {
            AudioPlayerService.LocalBinder binder = (AudioPlayerService.LocalBinder) service;
            AudioPlayerService audioPlayerService = binder.getService();
            bounded = true;
            audioPlayerService.setOnPlayListener(MediaPlayerActivity.this);
            audioPlayerService.setOnErrorListener(MediaPlayerActivity.this);
            MediaPlayer player = audioPlayerService.getMediaPlayer();
            if(player!=null)
            {
                setMediaPlayer(player);
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0)
        {
            bounded = false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_player);
        /*Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);*/

        Intent intent = getIntent();
        if(!intent.hasExtra("folder") || !intent.hasExtra("filename") || !intent.hasExtra("tubeId"))
        {
            finish();
        }

        if (getSupportActionBar()!= null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        else if (getActionBar() != null)
            getActionBar().setHomeButtonEnabled(true);

        b1 = (Button) findViewById(R.id.button);
        b2 = (Button) findViewById(R.id.button2);
        b3 = (Button)findViewById(R.id.button3);
        b4 = (Button)findViewById(R.id.button4);
        iv = (ImageView)findViewById(R.id.imageView);

        tx1 = (TextView)findViewById(R.id.textView2);
        tx2 = (TextView)findViewById(R.id.textView3);

        seekbar = (SeekBar)findViewById(R.id.seekBar);
        seekbar.setClickable(false);
        b2.setEnabled(false);

        final String folder = intent.getStringExtra("folder");
        final String filename = intent.getStringExtra("filename");
        Long tubeId = intent.getLongExtra("tubeId",Long.MIN_VALUE);
        CharSequence sequence = "Lecture de "+filename;
        setTitle(sequence);

        Tube tube = new Tube(tubeId,filename,folder,"",1);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(MediaPlayerActivity.this);
                alertDialog.setTitle("Confirmation");
                String name = filename.replace(".mp3","");
                alertDialog.setMessage("Voulez-vous télécharger "+name);
                alertDialog.setIcon(android.R.drawable.ic_dialog_info);
                alertDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();

                        Uri uri = Uri.parse(Params.SERVER+"/views/users/tbm/"+folder+"/mp3/"+filename);
                        DownloadManager.Request request = new DownloadManager.Request(uri);
                        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,filename);
                        request.allowScanningByMediaScanner();
                        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

                        DownloadManager manager = (DownloadManager)getSystemService(DOWNLOAD_SERVICE);
                        manager.enqueue(request);
                    }
                });

                alertDialog.setNegativeButton("Annuler", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        dialog.dismiss();
                    }
                });
                alertDialog.show();
            }
        });

        b3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if(mediaPlayer!=null)
                {
                    if(mediaPlayer.isPlaying())
                    {
                        mediaPlayer.pause();
                    }
                    else
                    {
                        mediaPlayer.start();
                    }
                }
            }
        });

        b2.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(mediaPlayer!=null)
                {
                    mediaPlayer.pause();
                    b2.setEnabled(false);
                    b3.setEnabled(true);
                }
            }
        });

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int temp = (int)startTime;

                if((temp+forwardTime)<=finalTime)
                {
                    startTime = startTime + forwardTime;
                    mediaPlayer.seekTo((int) startTime);
                    Toast.makeText(getApplicationContext(),"You have Jumped forward 5 " +
                            "seconds",Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(getApplicationContext(),"Cannot jump forward 5 " +
                            "seconds",Toast.LENGTH_SHORT).show();
                }
            }
        });

        b4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int temp = (int)startTime;

                if((temp-backwardTime)>0){
                    startTime = startTime - backwardTime;
                    mediaPlayer.seekTo((int) startTime);
                    Toast.makeText(getApplicationContext(),"You have Jumped backward 5" +
                            " seconds",Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(getApplicationContext(),"Cannot jump backward 5 " +
                            "seconds",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    @Override
    protected void onStart()
    {
        super.onStart();
        Intent intent = new Intent(getApplicationContext(), AudioPlayerService.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onPause()
    {
        new Notifier(getApplicationContext(),null).show();
        super.onPause();
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {

        switch (item.getItemId())
        {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private Runnable UpdateSongTime = new Runnable() {
        public void run() {
            startTime = mediaPlayer.getCurrentPosition();
            long secs = TimeUnit.MILLISECONDS.toSeconds((long) startTime) -
                    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.
                            toMinutes((long) startTime));
            String txt = TimeUnit.MILLISECONDS.toMinutes((long) startTime)+":"+secs;
            tx1.setText(txt);
            seekbar.setProgress((int)startTime);
            myHandler.postDelayed(this, 100);
        }
    };

    @Override
    public void play(MediaPlayer mediaPlayer)
    {
        //setMediaPlayer(mediaPlayer);
    }

    @Override
    public void error(MediaPlayer mediaPlayer, int i, int i1)
    {

    }

    public void setMediaPlayer(MediaPlayer mediaPlayer)
    {
        if(mediaPlayer!=null && mediaPlayer.isPlaying())
        {
            this.mediaPlayer = mediaPlayer;

            finalTime = mediaPlayer.getDuration();
            startTime = mediaPlayer.getCurrentPosition();

            if (oneTimeOnly == 0)
            {
                seekbar.setMax((int) finalTime);
                oneTimeOnly = 1;
            }

            tx2.setText(TimeUnit.MILLISECONDS.toMinutes((long) finalTime)+" min," + (TimeUnit.MILLISECONDS.toSeconds((long) finalTime) -
                    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long)
                            finalTime))) + " sec"
            );

            tx1.setText(TimeUnit.MILLISECONDS.toMinutes((long) startTime)+" min,"+(TimeUnit.MILLISECONDS.toSeconds((long) startTime) -
                    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long)
                            startTime)))+" sec"

            );
            seekbar.setProgress((int)startTime);
            myHandler.postDelayed(UpdateSongTime,100);
            b2.setEnabled(true);
            b3.setEnabled(false);
        }
    }



    @Override
    public void onClick(View view)
    {

    }
}
