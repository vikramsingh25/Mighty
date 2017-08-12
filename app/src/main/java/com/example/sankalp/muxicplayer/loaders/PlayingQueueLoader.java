package com.example.sankalp.muxicplayer.loaders;

import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;

import com.example.sankalp.muxicplayer.data.PlaylistInfo;
import com.example.sankalp.muxicplayer.data.SongsInfo;
import com.example.sankalp.muxicplayer.mightyDB.MightyContract;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sankalp on 2/26/2017.
 */
public class PlayingQueueLoader extends AsyncTaskLoader<List<SongsInfo>> {
    QueueObserver queueObserver;
    public List<SongsInfo> list,cache;

    public PlayingQueueLoader(Context context) {
        super(context);
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        if (cache!=null) {
            deliverResult(cache);
        }
        if (queueObserver==null) {
            queueObserver=new QueueObserver(this,new Handler());
            getContext().getContentResolver().registerContentObserver(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                    , true
                    , queueObserver);
        }
        if (takeContentChanged() || cache==null) {
            forceLoad();
        }
    }

    @Override
    protected void onStopLoading() {
        super.onStopLoading();
        cancelLoad();
    }

    @Override
    public void deliverResult(List<SongsInfo> data) {
        if (isReset()) {
            return;
        }
        cache=data;
        if (isStarted()) {
            super.deliverResult(data);
        }
    }

    @Override
    protected void onReset() {
        super.onReset();
        if (queueObserver!=null) {
            getContext().getContentResolver().unregisterContentObserver(queueObserver);
            queueObserver=null;
        }
    }

    @Override
    public List<SongsInfo> loadInBackground() {
        List<SongsInfo> list=new ArrayList<>();
        Cursor cursor = getContext().getContentResolver().query(MightyContract.PlayingQueueEntry.CONTENT_URI
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

    private static class QueueObserver extends ContentObserver {
        /**
         * Creates a content observer.
         *
         */
        private Loader loader;
        public QueueObserver(Loader loader,Handler handler) {
            super(handler);
            this.loader=loader;
        }

        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            loader.onContentChanged();
        }
    }
}
