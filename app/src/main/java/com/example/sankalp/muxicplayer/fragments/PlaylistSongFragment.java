package com.example.sankalp.muxicplayer.fragments;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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
public class PlaylistSongFragment extends Fragment {

    private TextView playlistName,playlistDescription;
    private ImageView playlistThumbnail;
    private Button shufflePlaylist,queuePlaylist;
    private RecyclerView playlistSongList;
    private AlbumArtistsPlayListAdapter adapter;
    private Bundle arguments;
    public static int songPosition;
    List<SongsInfo> list;
    String[] data;
    private static final int NAME=0;
    private static final int DESC=1;
    private static final int ID=2;
    private MightySongProvider songProvider;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_album_song,container,false);
        int id=0;
        playlistThumbnail= (ImageView) view.findViewById(R.id.album_image);
        shufflePlaylist = (Button) view.findViewById(R.id.play_album);
        queuePlaylist= (Button) view.findViewById(R.id.queue_album);
        playlistName= (TextView) view.findViewById(R.id.album_name);
        playlistDescription= (TextView) view.findViewById(R.id.no_of_songs);
        playlistSongList= (RecyclerView) view.findViewById(R.id.album_track_list_view);

        arguments = getArguments();
        if (arguments !=null) {
            data = arguments.getStringArray(AlbumActivity.PLAYLIST_BUNDLE);
            playlistName.setText(data[NAME]);
            if (data[DESC].equals("") || data[DESC] == null) {
                playlistDescription.setText("No Description specified");
            } else {

                playlistDescription.setText(data[DESC]);
            }
            id=Integer.parseInt(data[ID]);
        }
//        getDataFromPlaylistSong();
        list=loadSongsForPlaylist(id);
        Log.d("PlaylistSongFragment",String.valueOf(list.size()));

        adapter=new AlbumArtistsPlayListAdapter(getContext(),list);
        playlistSongList.setAdapter(adapter);
        playlistSongList.setLayoutManager(new LinearLayoutManager(getContext()));
        playlistSongList.addOnItemTouchListener(new RecycleViewTouchListener(getContext(), playlistSongList, new MightyOnClickListener() {
            @Override
            public void onClick(View view, int position) {
//                Toast.makeText(getContext(),"song Clicked",Toast.LENGTH_SHORT).show();
//                list.get(position).setIsCurrent(1);
                songProvider.addSongsToPlayingQueue(getContext(),list);
                songProvider.getFromQueue(getContext());
                new MainActivity().playAudio(position
                        ,list.get(position).getSongTitle()
                        ,list.get(position).getSongArtist()
                        ,Utilities.milliSecondsToTimer(list.get(position).getSongDuration())
                ,list.get(position).getSongThumbnail());
                PlayScreenFragment.duration=list.get(position).getSongDuration();
            }
        }));

        shufflePlaylist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shuffleSongs(getContext());
            }
        });

        queuePlaylist.setOnClickListener(new View.OnClickListener() {
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

    public List<SongsInfo> loadSongsForPlaylist(long playlistID){
        List<SongsInfo> list=new ArrayList<>();
        Log.d("PlaylistSongFragment",String.valueOf(playlistID));
//        Uri uri= MightyContract.PlaylistEntry.buildUriForPlaylists(String.valueOf(playlistID));
        Uri uri= MightyContract.PlaylistEntry.buildPlaylistUri(playlistID);
       Cursor cursor = getContext().getContentResolver().query(uri
        ,null
        ,null
        ,null
        ,MightyContract.SongEntry.COLUMN_TITLE + " ASC ");
        if (cursor != null && cursor.moveToFirst()){

                Log.d("PlaylistSongFragment", String.valueOf(cursor.getCount()));

                do {

                    long id=cursor.getInt(cursor.getColumnIndex(MightyContract.SongEntry._ID));
                    String songData = cursor.getString(cursor.getColumnIndex(MightyContract.SongEntry.COLUMN_DATA));
                    String songTitle = cursor.getString(cursor.getColumnIndex(MightyContract.SongEntry.COLUMN_TITLE));
                    String songAlbum = cursor.getString(cursor.getColumnIndex(MightyContract.SongEntry.COLUMN_ALBUM));
                    String songArtist = cursor.getString(cursor.getColumnIndex(MightyContract.SongEntry.COLUMN_ARTIST));
                    long songDuration = cursor.getLong(cursor.getColumnIndex(MightyContract.SongEntry.COLUMN_DURATION));

                    list.add(new SongsInfo(id,songTitle, songArtist, songDuration, songData, songAlbum));
                } while (cursor.moveToNext());
        }
        if (cursor!=null) {
            cursor.close();
        }
        return list;
    }

//    public void getDataFromPlaylistSong(){
//        Cursor cursor = new MightyDbHelper(getContext()).getReadableDatabase().query(MightyContract.PlaylistSongEntry.TABLE_NAME
//                ,null
//                ,null
//                ,null
//                ,null
//                ,null
//                ,null);
//        if (cursor != null && cursor.moveToFirst()) {
//                Log.d("getDataFromPlaylistSong","playlistID : songID");
//                Log.d("getDataFromPlaylistSong", String.valueOf(cursor.getCount()));
//
//            do {
//                int sondId = cursor.getInt(cursor.getColumnIndex(MightyContract.PlaylistSongEntry.COLUMN_SONG_ID));
//                int playlistId = cursor.getInt(cursor.getColumnIndex(MightyContract.PlaylistSongEntry.COLUMN_PLAYLIST_ID));
//                Log.d("getDataFromPlaylistSong", String.valueOf(playlistId)+" : "+String.valueOf(sondId));
//            } while (cursor.moveToNext());
//
//        }
//        if (cursor!=null) {
//            cursor.close();
//        }
//    }
}
