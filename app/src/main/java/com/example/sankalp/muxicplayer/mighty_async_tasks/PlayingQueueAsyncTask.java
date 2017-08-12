package com.example.sankalp.muxicplayer.mighty_async_tasks;

import android.content.ContentValues;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.example.sankalp.muxicplayer.data.SongsInfo;
import com.example.sankalp.muxicplayer.mightyDB.MightyContract;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 * Created by sankalp on 2/26/2017.
 */
public class PlayingQueueAsyncTask extends AsyncTask<List<ContentValues>,Void,List<SongsInfo>> {
    public Context mContext;
//    private List<SongsInfo> playingQueueInfoList;
    private static final String LOG_TAG=PlayingQueueAsyncTask.class.getSimpleName();

    public PlayingQueueAsyncTask(Context context){
//        playingQueueInfoList=new ArrayList<>();
        mContext=context;
    }


    public void addSongsToPlayingQueue(List<ContentValues> list){
        if (list.size()>0) {
            ContentValues[] contentValues=new ContentValues[list.size()];
            contentValues=list.toArray(contentValues);
            int rowsInserted=mContext.getContentResolver().bulkInsert(MightyContract.PlayingQueueEntry.CONTENT_URI,contentValues);
            Log.d("PlayingQueueAsyncTask",String.valueOf(rowsInserted));
        }

    }

    @Override
    protected List<SongsInfo> doInBackground(List<ContentValues>... lists) {
        if (lists.length!=0) {
            addSongsToPlayingQueue(lists[0]);
        }
        return null;
    }
}
