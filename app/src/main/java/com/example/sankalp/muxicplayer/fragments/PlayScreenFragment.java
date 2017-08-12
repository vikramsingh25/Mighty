package com.example.sankalp.muxicplayer.fragments;

import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatSeekBar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sankalp.muxicplayer.R;
import com.example.sankalp.muxicplayer.services.MightyPlayerService;
import com.example.sankalp.muxicplayer.utils.StorageUtils;
import com.example.sankalp.muxicplayer.utils.Utilities;

/**
 * Created by sankalp on 9/27/2016.
 */
public class PlayScreenFragment extends Fragment implements AppCompatSeekBar.OnSeekBarChangeListener{


    public static TextView songTitle,songArtist,runningTime, songDuration;
    private FloatingActionButton play_pause,next,previous,shuffle,repeat;
    private AppCompatSeekBar seekBar;
    public static ImageView albumArt;
//    private TextView songTitleLabel;
//    private TextView songCurrentDurationLabel;
//    private TextView songTotalDurationLabel;
    private Handler mHandler = new Handler();
//    private Button startMedia;
//    private Button stopMedia;
//    private MediaPlayer mp;
    public static boolean isRepeat;

    String title,artist,thumbnail;
//    public static String duration;

    public static long duration;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.play_screen,container,false);
//        String[] currentSong=new StorageUtils(getContext()).loadCurrentSongInfo();

        songTitle= (TextView) view.findViewById(R.id.songTitle);
        songTitle.setText(title);

        songArtist= (TextView) view.findViewById(R.id.songArtist);
        songArtist.setText(artist);

        runningTime= (TextView) view.findViewById(R.id.currentTimeText);

        songDuration = (TextView) view.findViewById(R.id.totalTimeText);
        songDuration.setText(Utilities.milliSecondsToTimer(duration));

        play_pause= (FloatingActionButton) view.findViewById(R.id.play);
        if (CurrentSongFragment.currentPlaybackStatus == MightyPlayerService.PlaybackStatus.PLAYING) {
            play_pause.setImageResource(R.drawable.pause);
        } else if(CurrentSongFragment.currentPlaybackStatus == MightyPlayerService.PlaybackStatus.PAUSED) {
            play_pause.setImageResource(R.drawable.play);
        }
        play_pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CurrentSongFragment.currentPlaybackStatus == MightyPlayerService.PlaybackStatus.PLAYING) {
//                    Log.d("CurrentSongFragment","In if");
                    play_pause.setImageResource(R.drawable.play);
                    //pause song here
                    MightyPlayerService.transportControls.pause();
                    CurrentSongFragment.currentPlaybackStatus= MightyPlayerService.PlaybackStatus.PAUSED;
                } else if (CurrentSongFragment.currentPlaybackStatus == MightyPlayerService.PlaybackStatus.PAUSED) {
//                    Log.d("CurrentSongFragment","In else");
                    play_pause.setImageResource(R.drawable.pause);
                    //play song here
                    MightyPlayerService.transportControls.play();
                    CurrentSongFragment.currentPlaybackStatus= MightyPlayerService.PlaybackStatus.PLAYING;
                }
            }
        });

        next= (FloatingActionButton) view.findViewById(R.id.next);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MightyPlayerService.transportControls.skipToNext();
            }
        });

        previous= (FloatingActionButton) view.findViewById(R.id.previous);
        previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MightyPlayerService.transportControls.skipToPrevious();
            }
        });

        shuffle= (FloatingActionButton) view.findViewById(R.id.shuffle);
        shuffle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new PlayingQueueFragment().shuffleSongs(getContext());

            }
        });

        repeat= (FloatingActionButton) view.findViewById(R.id.repeat);
        repeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isRepeat) {
                    isRepeat = false;
                    Toast.makeText(getContext(),"Repeat off",Toast.LENGTH_SHORT).show();
                } else {
                    isRepeat=true;
                    Toast.makeText(getContext(),"Repeat on",Toast.LENGTH_SHORT).show();
                }
            }
        });

        seekBar= (AppCompatSeekBar) view.findViewById(R.id.seekbar);
        seekBar.setOnSeekBarChangeListener(this);
        seekBar.setProgress(MightyPlayerService.mediaPlayer.getCurrentPosition());
//        seekBar.setProgress(0);
//        seekBar.setMax(100);
        seekBar.setEnabled(true);
        updateProgressBar();

        albumArt= (ImageView) view.findViewById(R.id.songProfileImage);
//        albumArt.setImageBitmap(BitmapFactory.decodeFile(thumbnail));
        return view;
    }

//    public void setSongInfo(String title,String artist,String duration){
//        songTitle.setText(title);
//        songArtist.setText(artist);
//        songDuration.setText(duration);
//    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        title=getArguments().getString("songTitle");
        artist=getArguments().getString("songArtist");
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        setHasOptionsMenu(true);
        inflater.inflate(R.menu.play_screen_menu,menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    public void updateProgressBar() {
        mHandler.postDelayed(mUpdateTimeTask, 100);
    }

    /**
     * Background Runnable thread
     * */
    private Runnable mUpdateTimeTask = new Runnable() {
        public void run() {
            long totalDuration =duration;
            long currentDuration = MightyPlayerService.mediaPlayer.getCurrentPosition();

            // Displaying Total Duration time
            songDuration.setText(""+Utilities.milliSecondsToTimer(totalDuration));
            // Displaying time completed playing
            runningTime.setText(""+Utilities.milliSecondsToTimer(currentDuration));

            // Updating progress bar
            int progress = (int)(Utilities.getProgressPercentage(currentDuration, totalDuration));
            //Log.d("Progress", ""+progress);
            seekBar.setProgress(progress);

            // Running this thread after 100 milliseconds
            mHandler.postDelayed(this, 100);
        }
    };





    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {


    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
//        mHandler.removeCallbacks(mUpdateTimeTask);

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
//        mHandler.removeCallbacks(mUpdateTimeTask);
        int totalDuration = MightyPlayerService.mediaPlayer.getDuration();
        int currentPosition = Utilities.progressToTimer(seekBar.getProgress(), totalDuration);

        // forward or backward to certain seconds
        MightyPlayerService.mediaPlayer.seekTo(currentPosition);

        // update timer progress again
        updateProgressBar();

    }
}
