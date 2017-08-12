package com.example.sankalp.muxicplayer.fragments;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
//import android.widget.Toast;

import com.example.sankalp.muxicplayer.AlbumActivity;
import com.example.sankalp.muxicplayer.MainActivity;
import com.example.sankalp.muxicplayer.R;
import com.example.sankalp.muxicplayer.adapters.AlbumArtistsPlayListAdapter;
import com.example.sankalp.muxicplayer.data.SongsInfo;
import com.example.sankalp.muxicplayer.listeners_and_interfaces.MightyOnClickListener;
import com.example.sankalp.muxicplayer.listeners_and_interfaces.RecycleViewTouchListener;
import com.example.sankalp.muxicplayer.mightyDB.MightyContract;
import com.example.sankalp.muxicplayer.mightyDB.MightySongProvider;
import com.example.sankalp.muxicplayer.utils.Utilities;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by sankalp on 3/1/2017.
 */
public class ArtistSongFragment extends Fragment {

    private ImageView albumThumbnail;
    private Button shuffleAlbum,queueAlbum;
    private TextView albumName,noOfSongs;
    private RecyclerView albumSongList;
    private AlbumArtistsPlayListAdapter adapter;
    private Bundle arguments;
    String artist;
    private MightySongProvider songProvider;
    List<SongsInfo> list;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_album_song,container,false);
        albumThumbnail= (ImageView) view.findViewById(R.id.album_image);
        shuffleAlbum = (Button) view.findViewById(R.id.play_album);
        queueAlbum= (Button) view.findViewById(R.id.queue_album);
        albumName= (TextView) view.findViewById(R.id.album_name);
        noOfSongs= (TextView) view.findViewById(R.id.no_of_songs);
        albumSongList= (RecyclerView) view.findViewById(R.id.album_track_list_view);
        arguments = getArguments();
        if (arguments !=null) {
            artist = arguments.getString(AlbumActivity.ARTIST_BUNDLE);
        }
        albumName.setText(artist);
        list=loadSongsForArtist();
        adapter=new AlbumArtistsPlayListAdapter(getContext(),list);
        albumSongList.setAdapter(adapter);
        albumSongList.setLayoutManager(new LinearLayoutManager(getContext()));
        albumSongList.addOnItemTouchListener(new RecycleViewTouchListener(getContext(), albumSongList, new MightyOnClickListener() {
            @Override
            public void onClick(View view, int position) {
//                        cnt++;
//                        if (cnt==1) {
//                            lastCurrent=position;
////                            list.get(lastCurrent).setIsCurrent(0);
//                        }
//                Toast.makeText(getContext(),"song Clicked",Toast.LENGTH_SHORT).show();
//                list.get(position).setIsCurrent(1);
                songProvider.addSongsToPlayingQueue(getContext(),list);
                songProvider.getFromQueue(getContext());
                new MainActivity().playAudio(position
                        ,list.get(position).getSongTitle()
                        ,list.get(position).getSongArtist()
                        , Utilities.milliSecondsToTimer(list.get(position).getSongDuration())
                ,list.get(position).getSongThumbnail());
                PlayScreenFragment.duration=list.get(position).getSongDuration();
            }
        }));

        shuffleAlbum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shuffleSongs(getContext());
            }
        });

        queueAlbum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                songProvider.addSongsToQueue(getContext(),list);
                songProvider.getFromQueue(getContext());
            }
        });

        return view;

    }

    public void shuffleSongs(Context context){
        for (int i=list.size()-1;i>=0;i--) {
            Random random=new Random();
            int j=random.nextInt(i+1);
            SongsInfo info=list.get(i);
            list.set(i,list.get(j));
            list.set(j,info);
        }
//        Log.d("Shuffle 1:",String.valueOf(list.get(0).getSongTitle()));
        new MightySongProvider().addSongsToPlayingQueue(context,list);
        new MainActivity().playAudio(0,list.get(0).getSongTitle(),list.get(0).getSongArtist(),Utilities.milliSecondsToTimer(list.get(0).getSongDuration())
                ,list.get(0).getSongThumbnail());
        PlayScreenFragment.duration=list.get(0).getSongDuration();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        songProvider=new MightySongProvider();
    }

    private List<SongsInfo> loadSongsForArtist() {
        List<SongsInfo> audioList=new ArrayList<>();
        Cursor cursor=getContext().getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                , null
                , MightyContract.SongEntry.COLUMN_ARTIST+ " = ?"
                , new String[]{artist}
                , MightyContract.SongEntry.COLUMN_TITLE + " ASC");

        if (cursor!=null && cursor.getCount()>0) {
            while (cursor.moveToNext()) {
                long id=cursor.getPosition();
                String songData = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
                String songTitle = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
                String songAlbum = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM));
                String songArtist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
                long songDuration = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION));

                // Save to audioList
                audioList.add(new SongsInfo(id,songTitle, songArtist, songDuration, songData, songAlbum));
            }
        }
        if (cursor!=null) {
            cursor.close();
        }
        return audioList;
    }
}
