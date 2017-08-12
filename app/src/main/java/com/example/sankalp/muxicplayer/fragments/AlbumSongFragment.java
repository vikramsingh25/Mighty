package com.example.sankalp.muxicplayer.fragments;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
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
import com.example.sankalp.muxicplayer.dialogs.AddToPlaylistDialog;
import com.example.sankalp.muxicplayer.dialogs.DeleteSongConfirmationDialog;
import com.example.sankalp.muxicplayer.listeners_and_interfaces.MightyOnClickListener;
import com.example.sankalp.muxicplayer.listeners_and_interfaces.RecycleViewTouchListener;
import com.example.sankalp.muxicplayer.mightyDB.MightyContract;
import com.example.sankalp.muxicplayer.mightyDB.MightySongProvider;
import com.example.sankalp.muxicplayer.utils.Utilities;


import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by sankalp on 1/3/2017.
 */
public class AlbumSongFragment extends Fragment {
    private ImageView albumThumbnail;
    private Button shuffleAlbum,queueAlbum;
    private TextView albumName,noOfSongs;
    private RecyclerView albumSongList;
    private AlbumArtistsPlayListAdapter adapter;
    public static int songPosition;
    private Bundle arguments;
    String album,albumArt;
//    public int lastCurrent;
//    private int cnt = 0;
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
            album = arguments.getString(AlbumActivity.ALBUM_BUNDLE);
        }
        albumName.setText(album);

        Uri uri= MightyContract.SongEntry.buildUriForAlbums(album);

//        Log.d("AlbumSongFragment",uri.toString());



        list=loadSongsForAlbum();
        noOfSongs.setText(list.size()+" Songs");
//        Log.d("AlbumSongFragment",String.valueOf(list.size()));
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
//                        Toast.makeText(getContext(),"song Clicked",Toast.LENGTH_SHORT).show();
//                        list.get(position).setIsCurrent(1);
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

        shuffleAlbum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                songProvider.addSongsToPlayingQueue(getContext(),list);
//                new MainActivity().playAudio(0
//                        ,list.get(0).getSongTitle()
//                        ,list.get(0).getSongArtist()
//                        ,Utilities.milliSecondsToTimer(list.get(0).getSongDuration())
//                ,list.get(0).getSongThumbnail());
//                PlayScreenFragment.duration=list.get(0).getSongDuration();
////                list.get(0).setIsCurrent(1);
                shuffleSongs(getContext()
                );
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

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        int pos=AlbumSongFragment.songPosition;
        SongsInfo selectedSong=list.get(pos);
        long songId=selectedSong.getSondId();
        String title=selectedSong.getSongTitle();
        String data=selectedSong.getSongData();
//        Log.d("delete",String.valueOf(pos));
        switch (item.getItemId()) {
            case 0:
//                                    Toast.makeText(getContext(),"play_next",Toast.LENGTH_LONG).show();
                new MightySongProvider().addSongToQueueNext(getContext(),selectedSong);
                break;
            case 1:
//                                    Toast.makeText(getContext(),"add_to_queue",Toast.LENGTH_LONG).show();
                new MightySongProvider().addSingleSongToQueue(getContext(),selectedSong);
                break;
            case 2:
                showAddToPlaylistDialog(pos);
                break;
            case 3:
                shareMusic(data);
                break;
            case 4:
                Bundle bundle=new Bundle();
                bundle.putString("name",title);
                bundle.putInt("position",pos);
                bundle.putLong("id",songId);
                FragmentTransaction ft=getFragmentManager().beginTransaction();
                DeleteSongConfirmationDialog deleteSongConfirmationDialog =new DeleteSongConfirmationDialog();
                deleteSongConfirmationDialog.setArguments(bundle);
                deleteSongConfirmationDialog.show(ft,"deleteSongConfirmationDialog");
                break;
        }
        return true;
    }

    public void showAddToPlaylistDialog(int songPosition){
        Bundle bundle=new Bundle();
        bundle.putInt("songPosition",songPosition);
        FragmentTransaction ft=getActivity().getSupportFragmentManager().beginTransaction();
        AddToPlaylistDialog dialog=new AddToPlaylistDialog();
        dialog.setArguments(bundle);
        dialog.show(ft,"AddToPlaylistDialog");
    }

    public void shareMusic(String songData){
        Log.d("TracksFragment",songData);
        Uri uri=Uri.parse("file://"+songData);
        Intent share=new Intent(Intent.ACTION_SEND);
        share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        share.setType("audio/mp3");
        share.putExtra(Intent.EXTRA_STREAM,uri);
        startActivity(Intent.createChooser(share,"Share Sound File"));
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

    private List<SongsInfo> loadSongsForAlbum()
    {
        List<SongsInfo> audioList=new ArrayList<>();
        Cursor cursor=getContext().getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                , null
                , MightyContract.SongEntry.COLUMN_ALBUM + " = ?"
                , new String[]{album}
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

//    @Override
//    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
//        super.onActivityCreated(savedInstanceState);
//        getLoaderManager().initLoader(ALBUM_LOADER, null, this);
//    }
//
//    @Override
//    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
//        return new CursorLoader(getContext(), MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
//                , null
//                , MightyContract.SongEntry.COLUMN_ALBUM + " = ?"
//                , new String[]{album.toString()}
//                , MightyContract.SongEntry.COLUMN_TITLE + " ASC");
//    }
//
//    @Override
//    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
//        adapter.swapCursor(cursor);
//    }
//
//    @Override
//    public void onLoaderReset(Loader<Cursor> loader) {
//        adapter.swapCursor(null);
//    }
}
