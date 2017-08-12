package com.example.sankalp.muxicplayer.fragments;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.sankalp.muxicplayer.MainActivity;
import com.example.sankalp.muxicplayer.R;
import com.example.sankalp.muxicplayer.adapters.TracksListAdapter;
import com.example.sankalp.muxicplayer.data.SongsInfo;
import com.example.sankalp.muxicplayer.listeners_and_interfaces.MightyOnClickListener;
import com.example.sankalp.muxicplayer.listeners_and_interfaces.RecycleViewTouchListener;
import com.example.sankalp.muxicplayer.loaders.PlayingQueueLoader;
import com.example.sankalp.muxicplayer.mightyDB.MightyContract;
import com.example.sankalp.muxicplayer.mightyDB.MightySongProvider;
import com.example.sankalp.muxicplayer.utils.Utilities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Created by sankalp on 3/31/2017.
 */
public class PlayingQueueFragment extends Fragment implements LoaderManager.LoaderCallbacks<List<SongsInfo>> {
    public RecyclerView recyclerView;
//    public MightySongProvider mightySongProvider;
    public List<SongsInfo> list= Collections.emptyList();
    private static final int QUEUE_LOADER=10;

    MainActivity mainActivity;
    TracksListAdapter adapter;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_playing_queue,container,false);
        recyclerView= (RecyclerView) view.findViewById(R.id.queue);
        mainActivity=new MainActivity();
        adapter=new TracksListAdapter(getContext(),new ArrayList<SongsInfo>());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.addOnItemTouchListener(new RecycleViewTouchListener(getContext(), recyclerView, new MightyOnClickListener() {
            @Override
            public void onClick(View view, int position) {
                String title=list.get(position).getSongTitle();
                String artist=list.get(position).getSongArtist();
                long trackDuration=list.get(position).getSongDuration();
                PlayScreenFragment.duration=trackDuration;
        mainActivity.playAudio(position,title,artist, Utilities.milliSecondsToTimer(list.get(position).getSongDuration()),list.get(position).getSongThumbnail());            }
}));

        return view;
    }
    public void shuffleSongs(Context context){
        list=loadSongsFromPlayingQueue(context);
        for (int i=list.size()-1;i>=0;i--) {
            Random random=new Random();
            int j=random.nextInt(i+1);
            SongsInfo info=list.get(i);
            list.set(i,list.get(j));
            list.set(j,info);
        }
//        list.notify();
//        Log.d("Shuffle 1:",String.valueOf(list.get(0).getSongTitle()));
        new MightySongProvider().addSongsToPlayingQueue(context,list);
        new MainActivity().playAudio(0,list.get(0).getSongTitle(),list.get(0).getSongArtist(),Utilities.milliSecondsToTimer(list.get(0).getSongDuration())
        ,list.get(0).getSongThumbnail());
        PlayScreenFragment.duration=list.get(0).getSongDuration();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        mightySongProvider=new MightySongProvider();
        list=loadSongsFromPlayingQueue(getContext());
        Log.d("PlayingQueue",String.valueOf(list.size()));

    }

    private List<SongsInfo> loadSongsFromPlayingQueue(Context context){
        List<SongsInfo> list=new ArrayList<>();
        Cursor cursor = context.getContentResolver().query(MightyContract.PlayingQueueEntry.CONTENT_URI
                ,null
                ,null
                ,null
                ,null);
        if (cursor!=null && cursor.moveToFirst()) {
            do{
                long id=cursor.getLong(cursor.getColumnIndex(MightyContract.PlayingQueueEntry._ID));
                String title=cursor.getString(cursor.getColumnIndex(MightyContract.PlayingQueueEntry.COLUMN_TITLE));
                String data=cursor.getString(cursor.getColumnIndex(MightyContract.PlayingQueueEntry.COLUMN_DATA));
                String album=cursor.getString(cursor.getColumnIndex(MightyContract.PlayingQueueEntry.COLUMN_ALBUM));
                String artist=cursor.getString(cursor.getColumnIndex(MightyContract.PlayingQueueEntry.COLUMN_ARTIST));
                long duration=Long.parseLong(cursor.getString(cursor.getColumnIndex(MightyContract.PlayingQueueEntry.COLUMN_DURATION)));
                list.add(new SongsInfo(id,title, artist, duration, data, album));
            }while (cursor.moveToNext());
        }
        if (cursor!=null) {
            cursor.close();
        }
        return list;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(QUEUE_LOADER,null,this);

    }

    @Override
    public Loader<List<SongsInfo>> onCreateLoader(int id, Bundle args) {
        return new PlayingQueueLoader(getContext());
    }

    @Override
    public void onLoadFinished(Loader<List<SongsInfo>> loader, List<SongsInfo> data) {
        adapter.addSongs(data);
    }

    @Override
    public void onLoaderReset(Loader<List<SongsInfo>> loader) {
        adapter.clearSongs();
    }
}
