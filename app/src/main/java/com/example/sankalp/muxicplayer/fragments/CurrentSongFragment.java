package com.example.sankalp.muxicplayer.fragments;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
//import android.widget.Toast;

import com.example.sankalp.muxicplayer.MainActivity;
import com.example.sankalp.muxicplayer.PlayingScreenActivity;
import com.example.sankalp.muxicplayer.R;
import com.example.sankalp.muxicplayer.services.MightyPlayerService;

/**
 * Created by sankalp on 10/19/2016.
 */
public class CurrentSongFragment extends Fragment {
    public static ImageView currentSongThumbnail;
    public static TextView currentSongTitle,currentSongArtist;
    public static ImageButton play_pause;
    public static MightyPlayerService.PlaybackStatus currentPlaybackStatus;
    private MightyPlayerService service;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        service=new MightyPlayerService();
//        currentPlaybackStatus= MightyPlayerService.PlaybackStatus.PLAYING;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view=inflater.inflate(R.layout.current_song_fragment,container,false);

        currentSongThumbnail= (ImageView) view.findViewById(R.id.current_song_thumbnail);
        currentSongTitle= (TextView) view.findViewById(R.id.current_song_title);
        currentSongArtist= (TextView) view.findViewById(R.id.current_song_artist);
        play_pause= (ImageButton) view.findViewById(R.id.current_song_pause);

        if (MainActivity.isPlayerServiceBound) {
            currentSongTitle.setText(MightyPlayerService.audioList.get(MightyPlayerService.audioIndex).getSongTitle());
            currentSongArtist.setText(MightyPlayerService.audioList.get(MightyPlayerService.audioIndex).getSongArtist());
        }
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(getContext(),"Playbar clicked",Toast.LENGTH_SHORT).show();
                Intent playScreenIntent=new Intent(getContext(), PlayingScreenActivity.class);
                playScreenIntent.putExtra("TrackName",currentSongTitle.getText());
                playScreenIntent.putExtra("TrackArtist",currentSongArtist.getText());
                startActivity(playScreenIntent);
            }
        });

//        if (currentPlaybackStatus == MightyPlayerService.PlaybackStatus.PLAYING) {
//            Log.d("CurrentSongFragment","In if");
//            play_pause.setImageResource(R.drawable.play_light);
//            //pause song here
//            MightyPlayerService.transportControls.pause();
//            currentPlaybackStatus= MightyPlayerService.PlaybackStatus.PAUSED;
//        } else if (currentPlaybackStatus == MightyPlayerService.PlaybackStatus.PAUSED) {
//            Log.d("CurrentSongFragment","In else");
//            play_pause.setImageResource(R.drawable.pause_light);
//            //play song here
//            MightyPlayerService.transportControls.play();
//            currentPlaybackStatus= MightyPlayerService.PlaybackStatus.PLAYING;
//        }

        play_pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(getContext(),"play_pause",Toast.LENGTH_LONG).show();
                if (currentPlaybackStatus == MightyPlayerService.PlaybackStatus.PLAYING) {
                    Log.d("CurrentSongFragment","In if");
                    play_pause.setImageResource(R.drawable.play);
                    //pause song here
                    MightyPlayerService.transportControls.pause();
                    currentPlaybackStatus= MightyPlayerService.PlaybackStatus.PAUSED;
                } else if (currentPlaybackStatus == MightyPlayerService.PlaybackStatus.PAUSED) {
                    Log.d("CurrentSongFragment","In else");
                    play_pause.setImageResource(R.drawable.pause);
                    //play song here
                    MightyPlayerService.transportControls.play();
                    currentPlaybackStatus= MightyPlayerService.PlaybackStatus.PLAYING;
                }
            }
        });

//        currentSongTitle.setText(getArguments().getString("CurrentSongTitle"));
//        currentSongArtist.setText(getArguments().getString("CurrentSongArtist"));

        return view;
    }

//    public void setPlaybackStatus(MightyPlayerService.PlaybackStatus playbackStatus){
//        currentPlaybackStatus=playbackStatus;
//    }
}
