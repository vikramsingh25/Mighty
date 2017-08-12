package com.example.sankalp.muxicplayer.mighty_async_tasks;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.example.sankalp.muxicplayer.data.PlaylistInfo;
import com.example.sankalp.muxicplayer.fragments.PlaylistsFragments;
import com.example.sankalp.muxicplayer.mightyDB.MightyContract;
import com.example.sankalp.muxicplayer.mightyDB.MightyDbHelper;
import com.example.sankalp.muxicplayer.mightyDB.MightySongProvider;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sankalp on 12/31/2016.
 */
public class PlaylistAsyncTasks extends AsyncTask<String,Void,List<PlaylistInfo>> {

    public Context mContext;
    private static final String LOG_TAG=PlaylistAsyncTasks.class.getSimpleName();
    public List<PlaylistInfo> playlistInfoList;

    public static boolean playlistAlreadyExist;
    public PlaylistAsyncTasks(Context context){

        playlistInfoList = new ArrayList<>();
        mContext=context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected List<PlaylistInfo> doInBackground(String[] params) {
        if (params.length==0) {
            return null;
        }
        String playlistName,playlistDescription;
        int songPosition,playlistPosition;
        MightySongProvider songProvider=new MightySongProvider();
        if (params.length==2) {
//            Log.d("PlaylistAsyncTasks","call addPlaylistToDatabase");


            playlistName = params[0];
            playlistDescription = params[1];

            songProvider.addPlaylistToDatabase(mContext,playlistName,playlistDescription);
        }
//        if (params.length>2) {
//            Log.d("PlaylistAsyncTasks","call songTOPlaylist");
//
//            playlistName = params[0];
//            playlistDescription = params[1];
//
//            songProvider.addPlaylistToDatabase(mContext,playlistName,playlistDescription);
//
//            songPosition=Integer.parseInt(params[2]);
//            playlistPosition=songProvider.getPlaylistCount(mContext);
//            songProvider.addSelectedSongToPlaylist(mContext, songPosition, playlistPosition);
////            playlistPosition=Integer.parseInt(params[3]);
//        }



//        loadPlaylists();
        return null;
    }

    @Override
    protected void onPostExecute(List<PlaylistInfo> list) {
        super.onPostExecute(list);
        if (playlistAlreadyExist) {
            Toast.makeText(mContext,"Playlist already exists",Toast.LENGTH_SHORT).show();
        }
//        new PlaylistsFragments().onCreateView(PlaylistsFragments.mInflater
//                ,PlaylistsFragments.mContainer
//                ,PlaylistsFragments.mSavedInstanceState);
    }


}
