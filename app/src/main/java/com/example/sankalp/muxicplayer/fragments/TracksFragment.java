package com.example.sankalp.muxicplayer.fragments;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
//import android.widget.Toast;

import com.example.sankalp.muxicplayer.MainActivity;
import com.example.sankalp.muxicplayer.R;
import com.example.sankalp.muxicplayer.adapters.TracksListAdapter;
import com.example.sankalp.muxicplayer.data.SongsInfo;
import com.example.sankalp.muxicplayer.dialogs.AddToPlaylistDialog;
import com.example.sankalp.muxicplayer.dialogs.DeleteSongConfirmationDialog;
import com.example.sankalp.muxicplayer.listeners_and_interfaces.MightyOnClickListener;
import com.example.sankalp.muxicplayer.listeners_and_interfaces.RecycleViewTouchListener;
//import com.example.sankalp.muxicplayer.loaders.TracksLoader;
import com.example.sankalp.muxicplayer.loaders.TracksLoader;
import com.example.sankalp.muxicplayer.mightyDB.MightyContract;
import com.example.sankalp.muxicplayer.mightyDB.MightyDbHelper;
import com.example.sankalp.muxicplayer.mightyDB.MightySongProvider;
import com.example.sankalp.muxicplayer.mighty_async_tasks.PlayingQueueAsyncTask;
import com.example.sankalp.muxicplayer.mighty_async_tasks.SongsAsyncTask;
import com.example.sankalp.muxicplayer.utils.StorageUtils;
import com.example.sankalp.muxicplayer.utils.Utilities;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Vector;

/**
 * Created by sankalp on 9/18/2016.
 */
public class TracksFragment extends Fragment implements LoaderManager.LoaderCallbacks<List<SongsInfo>>,SearchView.OnQueryTextListener{
    private RecyclerView trackRecyclerView;
    public static TracksListAdapter adapter;
    public static int songPosition;
//    public static int isPlaylistDialog=0;
//    public static boolean confirmDelete=false;
    private static final int TRACKS_LOADER=0;
    private MainActivity mainActivity;
    private MightySongProvider songProvider;
//    private Vector vector=new Vector();
    private List<SongsInfo> audioList;
//    String[] art;
//    SharedPreferences sharedPreferences;


//    public OnSongClickedListener onSongClickedListener;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        View view=inflater.inflate(R.layout.fragment_tracks,container,false);
        trackRecyclerView= (RecyclerView) view.findViewById(R.id.track_recycler_view);
        adapter=new TracksListAdapter(getContext(),new ArrayList<SongsInfo>());
//        adapter=new TracksListAdapter(getContext(),audioList);
        trackRecyclerView.setAdapter(adapter);
        trackRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        trackRecyclerView.addOnItemTouchListener(new RecycleViewTouchListener(getContext(), trackRecyclerView, new MightyOnClickListener() {
            @Override
            public void onClick(View view, int position) {
//                Toast.makeText(getContext(),"clicked song "+position,Toast.LENGTH_SHORT).show();
                String title=audioList.get(position).getSongTitle();
                String artist=audioList.get(position).getSongArtist();
                String albymArt=audioList.get(position).getSongThumbnail();
                Log.d("TracksFragment","art: "+albymArt);
                long trackDuration=audioList.get(position).getSongDuration();
                PlayScreenFragment.duration=trackDuration;
//                audioList.get(position).setIsCurrent(1);
//                Log.d("TracksFragment art","art: "+sharedPreferences.getString("art["+position+"]",art[position]));

                songProvider.addSongsToPlayingQueue(getContext(),audioList);
                songProvider.getFromQueue(getContext());
                mainActivity.playAudio(position,title,artist,Utilities.milliSecondsToTimer(audioList.get(position).getSongDuration()),albymArt);
//                onSongClickedListener.onTrackClicked(position,title,artist);

            }
        }));
        return view;
    }

//    public void shuffleSongs(Context context){
//        for (int i=audioList.size()-1;i>=0;i--) {
//            Random random=new Random();
//            int j=random.nextInt(i+1);
//            SongsInfo info=audioList.get(i);
//            audioList.set(i,audioList.get(j));
//            audioList.set(j,info);
//        }
////        Log.d("Shuffle 1:",String.valueOf(list.get(0).getSongTitle()));
//        new MightySongProvider().addSongsToPlayingQueue(context,audioList);
//        new MainActivity().playAudio(0,audioList.get(0).getSongTitle(),audioList.get(0).getSongArtist(),Utilities.milliSecondsToTimer(audioList.get(0).getSongDuration())
//                ,audioList.get(0).getSongThumbnail());
//        PlayScreenFragment.duration=audioList.get(0).getSongDuration();
//    }

    public interface MightyOnLongClickListener {
        void onLongClick(int position);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        int pos=TracksFragment.songPosition;
        SongsInfo selectedSong=audioList.get(pos);
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



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        audioList=new ArrayList<>();
        mainActivity = new MainActivity();
        songProvider=new MightySongProvider();
//        sharedPreferences = getContext().getSharedPreferences(StorageUtils.STORAGE,Context.MODE_PRIVATE);
        getContext().getContentResolver().delete(MightyContract.SongEntry.CONTENT_URI,null,null);
//        Log.d("TracksFragment",String.valueOf(rowsDeleted));
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(TRACKS_LOADER,null,this);
//        Log.d("TracksFragment","onActivityCreated: "+audioList.size());

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        if (MainActivity.isPlayerServiceBound) {
//            if (mainActivity.playerServiceConnection != null) {
//                getContext().unbindService(mainActivity.playerServiceConnection);
//                mainActivity.playerService.stopSelf();
//                MainActivity.isPlayerServiceBound=false;
//            }
//        }
    }


    @Override
    public Loader<List<SongsInfo>> onCreateLoader(int id, Bundle args) {

        Log.d("TracksFragment","OnCreateLoader");
        return new TracksLoader(getContext());
//        return null;
    }

    @Override
    public void onLoadFinished(Loader<List<SongsInfo>> loader, List<SongsInfo> data) {
        Log.d("TracksFragment","onLoadFinished");
        adapter.addSongs(data);
        audioList.clear();
        audioList=loadAudio();
        Log.d("TracksFragment",String.valueOf(audioList.size()));
    }

    public String getAlbumart(long albumId) {
        String path = null;
        Cursor c = getContext().getContentResolver().query(
                MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Audio.Albums.ALBUM_ART},
                MediaStore.Audio.Albums._ID+ "=?",
                new String[]{Long.toString(albumId)},
                null);

        if (c != null)
        {
            if (c.moveToFirst())
            {
                path = c.getString(0);
            }
            c.close();

        }

        return path;
    }

    @Override
    public void onLoaderReset(Loader<List<SongsInfo>> loader) {
        Log.d("TracksFragment","onLoaderReset");

        audioList.clear();
//        audioList=loadAudio();
        adapter.clearSongs();
    }

    public List<SongsInfo> loadAudio(){

        Cursor cursor=getContext().getContentResolver().query(MightyContract.SongEntry.CONTENT_URI
        ,null
        ,null
        ,null
        ,null);
        if (cursor!=null && cursor.getCount()>0) {
            while (cursor.moveToNext()) {
                long id=cursor.getInt(cursor.getColumnIndex(MightyContract.SongEntry._ID));
//                Log.d("abcd",String.valueOf(id));
                String songData = cursor.getString(cursor.getColumnIndex(MightyContract.SongEntry.COLUMN_DATA));
                String songTitle = cursor.getString(cursor.getColumnIndex(MightyContract.SongEntry.COLUMN_TITLE));
                String songAlbum = cursor.getString(cursor.getColumnIndex(MightyContract.SongEntry.COLUMN_ALBUM));
                String songArtist = cursor.getString(cursor.getColumnIndex(MightyContract.SongEntry.COLUMN_ARTIST));
                long songDuration = Long.parseLong(cursor.getString(cursor.getColumnIndex(MightyContract.SongEntry.COLUMN_DURATION)));
                long albumId = Long.parseLong(cursor.getString(cursor.getColumnIndex(MightyContract.SongEntry.COLUMN_ALBUM_ID)));
//                addSongAttrToVector(songData,songTitle,songAlbum,songArtist,0,null,String.valueOf(songDuration));
                // Save to audioList
                audioList.add(new SongsInfo(id,albumId,songTitle, songArtist, songDuration, songData, songAlbum,null));
            }
        }
        if (cursor!=null) {
            cursor.close();
        }
        return audioList;
    }


    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }
}
