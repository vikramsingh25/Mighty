package com.example.sankalp.muxicplayer.fragments;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
//import android.widget.Toast;

import com.example.sankalp.muxicplayer.AlbumActivity;
import com.example.sankalp.muxicplayer.R;
import com.example.sankalp.muxicplayer.adapters.ArtistsListAdapter;
import com.example.sankalp.muxicplayer.data.SongsInfo;
import com.example.sankalp.muxicplayer.listeners_and_interfaces.MightyOnClickListener;
import com.example.sankalp.muxicplayer.listeners_and_interfaces.RecycleViewTouchListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sankalp on 9/18/2016.
 */
public class ArtistsFragment extends Fragment {

    private RecyclerView artistRecyclerView;
    private List<SongsInfo> list;
    private ArtistsListAdapter adapter;
    public static final String ARTIST_KEY="artist_key";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_artists,container,false);
        artistRecyclerView = (RecyclerView) view.findViewById(R.id.artist_recycler_view);
        list=new ArrayList<>();
        list=loadAudio();
        adapter=new ArtistsListAdapter(getContext(),list);
        artistRecyclerView.setAdapter(adapter);
        artistRecyclerView.setLayoutManager(new GridLayoutManager(getContext(),2));
        artistRecyclerView.addOnItemTouchListener(new RecycleViewTouchListener(getContext(),artistRecyclerView, new MightyOnClickListener() {
            @Override
            public void onClick(View view, int position) {
                String artist=list.get(position).getSongArtist();
//                Toast.makeText(getContext(),"clicked artist "+position,Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getContext(),AlbumActivity.class).putExtra(ARTIST_KEY,artist));
            }

        }));
        return view;
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 0:
//                Toast.makeText(getContext(),"play_next",Toast.LENGTH_LONG).show();
                break;
            case 1:
//                Toast.makeText(getContext(),"add_to_queue",Toast.LENGTH_LONG).show();
                break;
            case 2:
//                Toast.makeText(getContext(),"add_to_playlist",Toast.LENGTH_LONG).show();
//                                    isPlaylistDialog=1;
//                                    showAddToPlaylistDialog();
                break;
            case 3:
//                Toast.makeText(getContext(),"share",Toast.LENGTH_LONG).show();
                break;
            case 4:
//                Toast.makeText(getContext(),"delete",Toast.LENGTH_LONG).show();
                break;
        }
        return super.onContextItemSelected(item);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public List<SongsInfo> loadAudio(){

        List<SongsInfo> audioList=new ArrayList<>();
        ContentResolver contentResolver=getActivity().getContentResolver();
        Uri songUri= MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String groupby=MediaStore.Audio.Media.ARTIST;
        String selection=MediaStore.Audio.Media.IS_MUSIC + "!=0 ) GROUP BY (" +groupby;
        String sortOrder=MediaStore.Audio.Media.ARTIST + " ASC ";
        Cursor cursor=contentResolver.query(songUri,null,selection,null,sortOrder);
//        Log.d("ArtistFragment",cursor.toString()+" : "+ cursor.getCount());
        if (cursor!=null && cursor.getCount()>0) {
            while (cursor.moveToNext()) {
                String songData = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
                String songTitle = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
                String songAlbum = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM));
                String songArtist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
                long songDuration = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION));
//                String songComposer= cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.COMPOSER));

                // Save to audioList
                audioList.add(new SongsInfo(songTitle, songArtist, songDuration, songData, songAlbum));
            }
        }
        cursor.close();
        return audioList;
    }

//    public Bitmap getsongThumbnail(Uri uri){
//        MediaMetadataRetriever mediaMetadataRetriever=new MediaMetadataRetriever();
//        byte[] rawArt;
//        Bitmap art;
//        BitmapFactory.Options options=new BitmapFactory.Options();
//        mediaMetadataRetriever.setDataSource(getContext(),uri);
//        rawArt=mediaMetadataRetriever.getEmbeddedPicture();
//
//        if (rawArt != null) {
//            return BitmapFactory.decodeByteArray(rawArt, 0, rawArt.length, options);
//        } else {
//            return null;
//        }
//    }


}
