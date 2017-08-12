package com.example.sankalp.muxicplayer.mighty_async_tasks;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.util.Log;

import com.example.sankalp.muxicplayer.data.PlaylistInfo;
import com.example.sankalp.muxicplayer.data.SongsInfo;
import com.example.sankalp.muxicplayer.mightyDB.MightyContract;
import com.example.sankalp.muxicplayer.mightyDB.MightyDbHelper;
import com.example.sankalp.muxicplayer.utils.Utilities;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.Vector;

/**
 * Created by sankalp on 1/1/2017.
 */
public class SongsAsyncTask extends AsyncTask<Vector,Void,List<SongsInfo>> {

    public Context mContext;
    private List<SongsInfo> songsInfoList;
    private static final String LOG_TAG=SongsAsyncTask.class.getSimpleName();

    public SongsAsyncTask(Context context){
        songsInfoList=new ArrayList<>();
        mContext=context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        int rowsDeleted=mContext.getContentResolver().delete(MightyContract.SongEntry.CONTENT_URI,null,null);
        Log.d("TracksFragmentDelete",String.valueOf(rowsDeleted));
    }

    @Override
    protected List<SongsInfo> doInBackground(Vector... vector) {
        if (vector.length==0) {
            return null;
        }
        addSongsToDatabase(vector[0]);
        return null;
    }

    public void addSongsToDatabase(Vector songVector){

        Log.d("SongAsyncTask",String.valueOf(songVector.size()));
        if (songVector.size() > 0) {
            ContentValues[] songArray=new ContentValues[songVector.size()];
            songVector.toArray(songArray);
            int rowsInserted=mContext.getContentResolver().bulkInsert(MightyContract.SongEntry.CONTENT_URI,songArray);
//            Log.d("SongAsyncTask",String.valueOf(rowsInserted));
        }
    }
}
