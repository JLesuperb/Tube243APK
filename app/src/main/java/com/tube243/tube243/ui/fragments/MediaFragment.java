package com.tube243.tube243.ui.fragments;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.tube243.tube243.R;
import com.tube243.tube243.entities.Tube;
import com.tube243.tube243.ui.activities.HomeActivity;

import java.io.IOException;
import java.util.concurrent.TimeUnit;


/**
 * Created by JonathanLesuperb on 4/19/2018.
 */

public class MediaFragment extends BaseFragment implements MediaPlayer.OnCompletionListener,
        MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener, MediaPlayer.OnSeekCompleteListener,
        MediaPlayer.OnInfoListener, MediaPlayer.OnBufferingUpdateListener, AudioManager.OnAudioFocusChangeListener, View.OnClickListener {
    private static MediaFragment _instance;
    private MediaPlayer mediaPlayer;
    private int resumePosition;
    private boolean isPlaying;
    private boolean isStopped;
    private boolean isPaused;
    private AudioManager audioManager;
    private Tube tube;

    private double startTime = 0;
    private double finalTime = 0;

    private Handler myHandler = new Handler();
    private int forwardTime = 5000;
    private int backwardTime = 5000;
    public static int oneTimeOnly = 0;

    public static MediaFragment getInstance()
    {
        if(_instance==null)
        {
            _instance = new MediaFragment();
        }
        return _instance;
    }

    public MediaFragment()
    {
        //Must be empty
    }

    public void setTube(Tube tube)
    {
        this.tube = tube;
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
        return inflater.inflate(R.layout.fragment_media, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        Toolbar toolbar = view.findViewById(R.id.toolbar);
        String filename = tube.getName();
        String f = filename.replace(".mp3","");
        f = f.replace("-"," ");
        toolbar.setTitle(f);
        toolbar.setSubtitle("Artist");
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

        ImageButton backwardBtn = view.findViewById(R.id.backwardBtn);
        backwardBtn.setOnClickListener(this);
        ImageButton playBtn = view.findViewById(R.id.playBtn);
        playBtn.setOnClickListener(this);
        ImageButton forwardBtn = view.findViewById(R.id.forwardBtn);
        forwardBtn.setOnClickListener(this);
        SeekBar seekBar = view.findViewById(R.id.seekBar);
        seekBar.setClickable(false);
        seekBar.setPadding(0, 0, 0, 0);
        TelephonyManager telephonyManager = (TelephonyManager)getContext().getSystemService(Context.TELEPHONY_SERVICE);
        if(telephonyManager != null)
        {
            telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
        }
    }

    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);
        if(getView()!=null)
        {
            Toolbar toolbar = getView().findViewById(R.id.toolbar);
            toolbar.setTitle(tube.getName());
            toolbar.setSubtitle("Media");
        }
        initMediaPlayer();
    }

    @Override
    public void onDetach()
    {
        super.onDetach();
        stopMedia();
        removeAudioFocus();
    }

    private void initMediaPlayer()
    {
        String folder = tube.getFolder();
        String filename = tube.getName();
        String f = filename.replace(".mp3","");
        String url = "http://www.tube243.com/views/users/tbm/"+folder+"/"+f+"/pwd-2017";

        mediaPlayer = new MediaPlayer();
        //Set up MediaPlayer event listeners
        mediaPlayer.setOnCompletionListener(this);
        mediaPlayer.setOnErrorListener(this);
        mediaPlayer.setOnPreparedListener(this);
        mediaPlayer.setOnBufferingUpdateListener(this);
        mediaPlayer.setOnSeekCompleteListener(this);
        mediaPlayer.setOnInfoListener(this);
        //Reset so that the MediaPlayer is not pointing to another data source
        mediaPlayer.reset();

        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try
        {
            // Set the data source to the mediaFile location
            mediaPlayer.setDataSource(url);
        }
        catch (IOException e)
        {
            e.printStackTrace();
            Toast.makeText(getContext(),e.getMessage(),Toast.LENGTH_LONG).show();
           // stopSelf();
        }
        mediaPlayer.prepareAsync();
    }

    private void playMedia()
    {
        if (!mediaPlayer.isPlaying())
        {
            mediaPlayer.start();

            finalTime = mediaPlayer.getDuration();
            startTime = mediaPlayer.getCurrentPosition();

            isPlaying = true;
            if(getView()!=null)
            {
                SeekBar seekBar = getView().findViewById(R.id.seekBar);
                seekBar.setMax((int) finalTime);
                oneTimeOnly = 1;
                seekBar.setProgress((int)startTime);
                myHandler.postDelayed(UpdateSongTime,100);
                TextView fullTimeTxt = getView().findViewById(R.id.fullTimeTxt);

                fullTimeTxt.setText(String.format("%02d:%02d",
                        TimeUnit.MILLISECONDS.toMinutes((long) finalTime),
                        TimeUnit.MILLISECONDS.toSeconds((long) finalTime) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long)
                                        finalTime)))
                );

                TextView currentTimeTxt = getView().findViewById(R.id.currentTimeTxt);

                currentTimeTxt.setText(String.format("%02d:%02d",
                        TimeUnit.MILLISECONDS.toMinutes((long) startTime),
                        TimeUnit.MILLISECONDS.toSeconds((long) startTime) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long)
                                        startTime)))
                );

                ImageButton playBtn = getView().findViewById(R.id.playBtn);
                playBtn.setImageDrawable(getResources().getDrawable(R.drawable.round_pause_circle_outline_black_48));
            }
        }
    }

    private double lastPlayedTime = Double.MIN_VALUE;

    private Runnable UpdateSongTime = new Runnable()
    {
        public void run()
        {
            if(isPlaying)
            {
                startTime = mediaPlayer.getCurrentPosition();
                if(lastPlayedTime<startTime)
                {
                    if(getView()!=null)
                    {
                        SeekBar seekBar = getView().findViewById(R.id.seekBar);
                        seekBar.setProgress((int)startTime);

                        TextView currentTimeTxt = getView().findViewById(R.id.currentTimeTxt);
                        currentTimeTxt.setText(String.format("%02d:%02d",
                                TimeUnit.MILLISECONDS.toMinutes((long) startTime),
                                TimeUnit.MILLISECONDS.toSeconds((long) startTime) -
                                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.
                                                toMinutes((long) startTime))));
                    }
                    lastPlayedTime = startTime;
                }
                else if(lastPlayedTime==startTime)
                {
                    if(getView()!=null)
                    {
                        SeekBar seekBar = getView().findViewById(R.id.seekBar);
                        seekBar.setVisibility(View.GONE);
                        ProgressBar loadingBar = getView().findViewById(R.id.loadingBar);
                        loadingBar.setVisibility(View.VISIBLE);
                    }
                }
            }
            myHandler.postDelayed(this, 100);
        }
    };

    private PhoneStateListener phoneStateListener = new PhoneStateListener() {
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            if (state == TelephonyManager.CALL_STATE_RINGING)
            {
                //Incoming call: Pause music
                if(isPlaying)
                {
                    pauseMedia();
                }
            }
            else if(state == TelephonyManager.CALL_STATE_IDLE)
            {
                //Not in call: Play music
                if(isPaused)
                {
                    resumeMedia();
                }
            }
            else if(state == TelephonyManager.CALL_STATE_OFFHOOK)
            {
                //A call is dialing, active or on hold
            }
            super.onCallStateChanged(state, incomingNumber);
        }
    };

    private void stopMedia()
    {
        if (mediaPlayer == null) return;
        if (mediaPlayer.isPlaying())
        {
            isStopped = true;
            isPlaying = false;
            mediaPlayer.stop();
            mediaPlayer.release();
        }
        if(getView()!=null)
        {
            ImageButton playBtn = getView().findViewById(R.id.playBtn);
            playBtn.setImageDrawable(getResources().getDrawable(R.drawable.round_play_circle_outline_black_48));
        }
    }

    private void pauseMedia()
    {
        if (mediaPlayer!=null && mediaPlayer.isPlaying())
        {
            isPaused = true;
            isPlaying = false;
            mediaPlayer.pause();
            resumePosition = mediaPlayer.getCurrentPosition();
            if(getView()!=null)
            {
                ImageButton playBtn = getView().findViewById(R.id.playBtn);
                playBtn.setImageDrawable(getResources().getDrawable(R.drawable.round_play_circle_outline_black_48));
            }
        }
    }

    private void resumeMedia()
    {
        if (mediaPlayer!=null && !mediaPlayer.isPlaying())
        {
            isPaused = false;
            isPlaying = true;
            mediaPlayer.seekTo(resumePosition);
            mediaPlayer.start();
            if(getView()!=null)
            {
                ImageButton playBtn = getView().findViewById(R.id.playBtn);
                playBtn.setImageDrawable(getResources().getDrawable(R.drawable.round_pause_circle_outline_black_48));
            }
        }
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

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent)
    {
        //Invoked indicating buffering status of
        //a media resource being streamed over the network.
        if(getView()!=null)
        {
            SeekBar seekBar = getView().findViewById(R.id.seekBar);
            int max = seekBar.getMax();
            int progress = (max*percent)/100;
            seekBar.setSecondaryProgress(progress);
        }
    }

    @Override
    public void onCompletion(MediaPlayer mp)
    {
        //Invoked when playback of a media source has completed.
        stopMedia();
    }

    //Handle errors
    @Override
    public boolean onError(MediaPlayer mp, int what, int extra)
    {
        //Invoked when there has been an error during an asynchronous operation.
        switch (what)
        {
            case MediaPlayer.MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK:
                Toast.makeText(getContext(),"MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK",Toast.LENGTH_LONG).show();
                break;
            case MediaPlayer.MEDIA_ERROR_SERVER_DIED:
                Toast.makeText(getContext(),"MEDIA_ERROR_SERVER_DIED",Toast.LENGTH_LONG).show();
                break;
            case MediaPlayer.MEDIA_ERROR_UNKNOWN:
                Toast.makeText(getContext(),"MEDIA_ERROR_UNKNOWN",Toast.LENGTH_LONG).show();
                break;
            case MediaPlayer.MEDIA_ERROR_TIMED_OUT:
                Toast.makeText(getContext(),"MEDIA_ERROR_TIMED_OUT",Toast.LENGTH_LONG).show();
                break;
            case MediaPlayer.MEDIA_ERROR_IO:
                Toast.makeText(getContext(),"MEDIA_ERROR_IO",Toast.LENGTH_LONG).show();
                break;
            case MediaPlayer.MEDIA_ERROR_MALFORMED:
                Toast.makeText(getContext(),"MEDIA_ERROR_MALFORMED",Toast.LENGTH_LONG).show();
                break;
        }
        return false;
    }

    @Override
    public boolean onInfo(MediaPlayer mp, int what, int extra)
    {
        //Invoked to communicate some info.
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp)
    {
        //Invoked when the media source is ready for playback.
        requestAudioFocus();
        if(getView()!=null)
        {
            ProgressBar loadingBar = getView().findViewById(R.id.loadingBar);
            loadingBar.setIndeterminate(false);
            //loadingBar.setVisibility(View.GONE);
            SeekBar seekBar = getView().findViewById(R.id.seekBar);
            seekBar.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onSeekComplete(MediaPlayer mp)
    {
        //Invoked indicating the completion of a seek operation.
    }

    @Override
    public void onAudioFocusChange(int focusChange)
    {
        //Invoked when the audio focus of the system is updated.
        switch (focusChange)
        {
            case AudioManager.AUDIOFOCUS_GAIN:
                // resume playback
                resumeMedia();
                break;
            case AudioManager.AUDIOFOCUS_GAIN_TRANSIENT:
                // You have audio focus for a short time
                resumeMedia();
                break;
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                // Lost focus for a short time, but it's ok to keep playing
                // at an attenuated level
                resumeMedia();
                break;
            case AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK:
                // Play over existing audio
                resumeMedia();
                break;
            case AudioManager.AUDIOFOCUS_LOSS:
                // Lost focus for an unbounded amount of time: stop playback and release media player
                stopMedia();
                break;
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                // Lost focus for a short time, but we have to stop
                // playback. We don't release the media player because playback
                // is likely to resume
                pauseMedia();
                break;
        }
    }

    private void requestAudioFocus()
    {
        audioManager = (AudioManager) getContext().getSystemService(Context.AUDIO_SERVICE);
        if (audioManager != null)
        {
            int result = audioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
            if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED)
            {
                //Focus gained
                playMedia();
            }
        }
    }

    private void removeAudioFocus()
    {
        if(audioManager!=null)
        {
            audioManager.abandonAudioFocus(this);
        }
       // AudioManager.AUDIOFOCUS_REQUEST_GRANTED == audioManager.abandonAudioFocus(this);
    }

    @Override
    public void onPause()
    {
        super.onPause();
        if(mediaPlayer!=null && mediaPlayer.isPlaying())
        {
            isPlaying = true;
        }
        pauseMedia();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        if(isPlaying)
            resumeMedia();
    }

    @Override
    public void onClick(View v)
    {
        int temp;
        switch (v.getId())
        {
            case R.id.forwardBtn:
                temp = (int)startTime;

                if((temp+forwardTime)<=finalTime)
                {
                    startTime = startTime + forwardTime;
                    mediaPlayer.seekTo((int) startTime);
                    Toast.makeText(getContext(),"You have Jumped forward 5 " +
                            "seconds",Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(getContext(),"Cannot jump forward 5 " +
                            "seconds",Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.backwardBtn:
                temp = (int)startTime;

                if((temp-backwardTime)>0){
                    startTime = startTime - backwardTime;
                    mediaPlayer.seekTo((int) startTime);
                    Toast.makeText(getContext(),"You have Jumped backward 5 " +
                            "seconds",Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(getContext(),"Cannot jump backward 5 " +
                            "seconds",Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.playBtn:
                if(isPlaying)
                {
                    pauseMedia();
                }
                else if(isPaused)
                {
                    resumeMedia();
                }
                else if(isStopped)
                {
                    playMedia();
                }
                break;
        }
    }
}
