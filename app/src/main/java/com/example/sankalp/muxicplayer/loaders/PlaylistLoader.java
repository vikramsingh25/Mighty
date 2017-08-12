package com.example.sankalp.muxicplayer.loaders;

import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.os.Handler;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.util.Log;

import com.example.sankalp.muxicplayer.data.PlaylistInfo;
import com.example.sankalp.muxicplayer.mightyDB.MightyContract;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sankalp on 2/24/2017.
 */
public class PlaylistLoader extends AsyncTaskLoader<List<PlaylistInfo>> {

    PlaylistObserver playlistObserver;
    public static List<PlaylistInfo> list,cache;

    public PlaylistLoader(Context context) {
        super(context);
    }


    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        if (cache!=null) {
            deliverResult(cache);
        }
        if (playlistObserver==null) {
            playlistObserver=new PlaylistObserver(this,new Handler());
            getContext().getContentResolver().registerContentObserver(MightyContract.PlaylistEntry.CONTENT_URI
                        , true
                        , playlistObserver);
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
    public void deliverResult(List<PlaylistInfo> data) {
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
        if (playlistObserver!=null) {
            getContext().getContentResolver().unregisterContentObserver(playlistObserver);
            playlistObserver=null;
        }
    }

    @Override
    public List<PlaylistInfo> loadInBackground() {

        list=new ArrayList<>();
        Cursor cursor=getContext().getContentResolver().query(MightyContract.PlaylistEntry.CONTENT_URI
                ,null
                ,null
                ,null
                , MightyContract.PlaylistEntry.COLUMN_MODIFICATION_TIME + " ASC");
        if (cursor!=null && cursor.getCount()>0) {
            while (cursor.moveToNext()) {
                if (isLoadInBackgroundCanceled()) {
                    return list;
                }
                long playListId = cursor.getLong(cursor.getColumnIndex(MightyContract.PlaylistEntry._ID));
                String playListName = cursor.getString(cursor.getColumnIndex(MightyContract.PlaylistEntry.COLUMN_PLAYLIST_NAME));
                String playListDescription= cursor.getString(cursor.getColumnIndex(MightyContract.PlaylistEntry.COLUMN_DESCRIPTION));

                // Save to audioList
                list.add(new PlaylistInfo(playListId,playListName,playListDescription));
            }
        }
        if (cursor!=null) {
            cursor.close();
        }
        return list;
    }

    private static class PlaylistObserver extends ContentObserver{
        /**
         * Creates a content observer.
         *
         * @param handler The handler to run {@link #onChange} on, or null if none.
         */
        private Loader loader;
        public PlaylistObserver(Loader loader,Handler handler) {
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
