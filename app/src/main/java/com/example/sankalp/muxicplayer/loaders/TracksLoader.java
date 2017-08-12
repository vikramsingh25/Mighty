package com.example.sankalp.muxicplayer.loaders;

import android.content.ContentResolver;
//import android.content.ContentValues;
import android.content.ContentValues;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.util.Log;

//import com.example.sankalp.muxicplayer.data.PlaylistInfo;
import com.example.sankalp.muxicplayer.data.SongsInfo;
import com.example.sankalp.muxicplayer.mightyDB.MightyContract;
import com.example.sankalp.muxicplayer.mighty_async_tasks.SongsAsyncTask;
//import com.example.sankalp.muxicplayer.mightyDB.MightyContract;
//import com.example.sankalp.muxicplayer.mighty_async_tasks.SongsAsyncTask;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 * Created by sankalp on 2/24/2017.
 */
public class TracksLoader extends AsyncTaskLoader<List<SongsInfo>> {

    TracksObserver tracksObserver;
    public List<SongsInfo> list,cache;
    int i=0;
    private Vector vector=new Vector();

    public TracksLoader(Context context) {
        super(context);
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        if (cache!=null) {
            deliverResult(cache);
        }
        if (tracksObserver==null) {
            tracksObserver=new TracksObserver(this,new Handler());
            getContext().getContentResolver().registerContentObserver(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                    , true
                    , tracksObserver);
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
        if (tracksObserver!=null) {
            getContext().getContentResolver().unregisterContentObserver(tracksObserver);
            tracksObserver=null;
        }
    }

    @Override
    public List<SongsInfo> loadInBackground() {
        Log.d("TracksFragment","loadInBackground");
        list=new ArrayList<>();
        {
            list.clear();
            vector.clear();
            //            cache.clear();
        }
        ContentResolver contentResolver=getContext().getContentResolver();
        Uri songUri= MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String selection=MediaStore.Audio.Media.IS_MUSIC + "!=0";
        String sortOrder=MediaStore.Audio.Media.TITLE + " ASC";
        Cursor cursor=contentResolver.query(songUri,null,selection,null,sortOrder);
        Log.d("TracksFragment",cursor.toString()+" : "+ cursor.getCount());
        if (cursor!=null && cursor.getCount()>0) {
            while (cursor.moveToNext()) {
                long id=cursor.getInt(cursor.getColumnIndex(MightyContract.SongEntry._ID));
                String songData = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
                String songTitle = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
                String songAlbum = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM));
                String songArtist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
                long albumId= cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID));
                long songDuration = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION));
//                String albumArt=cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM_ART));
//                String albumArt=getAlbumart(getContext(),albumId);
//                addSongAttrToVector(songData,songTitle,songAlbum,songArtist,0,albumArt,String.valueOf(songDuration));

                addSongAttrToVector(songData,songTitle,songAlbum,String.valueOf(albumId),songArtist,0,null,String.valueOf(songDuration));
                // Save to audioList
//                list.add(new SongsInfo(songTitle, songArtist, songDuration, songData, songAlbum,albumArt));
                list.add(new SongsInfo(id,albumId,songTitle, songArtist, songDuration, songData, songAlbum,null));
            }
        }
        new SongsAsyncTask(getContext()).execute(vector);
        if (cursor!=null) {
            cursor.close();
        }
        return list;
    }


    public void addSongAttrToVector(String songData, String songTitle, String songAlbum,String albumId, String songArtist, int liked, String albumArt, String songDuration){
        ContentValues contentValues=new ContentValues();
        contentValues.put(MightyContract.SongEntry._ID,i++);
        contentValues.put(MightyContract.SongEntry.COLUMN_DATA,songData);
        contentValues.put(MightyContract.SongEntry.COLUMN_TITLE,songTitle);
        contentValues.put(MightyContract.SongEntry.COLUMN_ALBUM,songAlbum);
        contentValues.put(MightyContract.SongEntry.COLUMN_ALBUM_ID,albumId);
        contentValues.put(MightyContract.SongEntry.COLUMN_ARTIST,songArtist);
        contentValues.put(MightyContract.SongEntry.COLUMN_LIKE,liked);
//        contentValues.put(MightyContract.SongEntry.COLUMN_IS_CURRENT,current);
        contentValues.put(MightyContract.SongEntry.COLUMN_ALBUM_ART,albumArt);
        contentValues.put(MightyContract.SongEntry.COLUMN_DURATION,songDuration);
        vector.add(contentValues);
    }

    private static class TracksObserver extends ContentObserver {
        /**
         * Creates a content observer.
         *
         */
        private Loader loader;
        public TracksObserver(Loader loader,Handler handler) {
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
