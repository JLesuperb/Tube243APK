package com.tube243.tube243;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.widget.Toast;

import java.io.IOException;

public class AudioPlayerService extends Service implements MediaPlayer.OnPreparedListener,
        MediaPlayer.OnErrorListener
{
    public MediaPlayer getMediaPlayer()
    {
        return mediaPlayer;
    }

    interface OnPlayListener
    {
        void play(MediaPlayer mediaPlayer);
    }

    interface OnErrorListener
    {
        void error(MediaPlayer mediaPlayer, int i, int i1);
    }

    private OnPlayListener onPlayListener;
    private OnErrorListener onErrorListener;

    private final IBinder mBinder = new LocalBinder();
    private MediaPlayer mediaPlayer;

    public void setOnPlayListener(OnPlayListener onPlayListener)
    {
        this.onPlayListener = onPlayListener;
    }

    public void removeOnPlayListener(OnPlayListener onPlayListener)
    {
        this.onPlayListener = onPlayListener;
    }

    public void setOnErrorListener(OnErrorListener onErrorListener)
    {
        this.onErrorListener = onErrorListener;
    }

    public void removeOnErrorListener(OnErrorListener onErrorListener)
    {
        this.onErrorListener = onErrorListener;
    }

    public AudioPlayerService()
    {

    }

    class LocalBinder extends Binder
    {
        AudioPlayerService getService()
        {
            // Return this instance of LocalService so clients can call public methods
            return AudioPlayerService.this;
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        AudioManager audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        String folder = intent.getStringExtra("folder");
        String filename = intent.getStringExtra("filename");
        String f = filename.replace(".mp3","");
        String url = "http://www.tube243.com/views/users/tbm/"+folder+"/"+f+"/pwd-2017";
        if(mediaPlayer!=null)
        {
            if(mediaPlayer.isPlaying())
            {
                mediaPlayer.stop();
                mediaPlayer.release();
                mediaPlayer = new MediaPlayer();
            }
        }
        else
        {
            mediaPlayer = new MediaPlayer();
        }
        mediaPlayer.reset();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        audioManager.setStreamSolo(AudioManager.STREAM_MUSIC, true);
        mediaPlayer.setOnPreparedListener(this);
        try
        {
            mediaPlayer.setDataSource(url);
            mediaPlayer.prepareAsync();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent)
    {
        return mBinder;
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer)
    {
        mediaPlayer.start();
        onPlayListener.play(mediaPlayer);
    }

    @Override
    public void onDestroy() {
        if (mediaPlayer != null) mediaPlayer.release();
    }

    @Override
    public boolean onError(MediaPlayer mediaPlayer, int i, int i1)
    {
        onErrorListener.error(mediaPlayer,i,i1);
        return false;
    }
}
