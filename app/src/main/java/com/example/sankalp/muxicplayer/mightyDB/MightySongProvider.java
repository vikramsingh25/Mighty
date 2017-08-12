package com.example.sankalp.muxicplayer.mightyDB;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.database.sqlite.SQLiteStatement;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.example.sankalp.muxicplayer.data.SongsInfo;
import com.example.sankalp.muxicplayer.mighty_async_tasks.PlaylistAsyncTasks;
import com.example.sankalp.muxicplayer.services.MightyPlayerService;
import com.example.sankalp.muxicplayer.utils.Utilities;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sankalp on 12/29/2016.
 */
public class MightySongProvider extends ContentProvider {

    public static final int SONG=100;
    public static final int PLAYLIST=101;
    public static final int PLAYLIST_SONG=102;
    public static final int SONG_FOR_ALBUM=103;
    public static final int SONG_FOR_PLAYLIST=104;
    public static final int SONG_FOR_ARTISTS=105;
    public static final int SEARCHES=106;
    public static final int PLAYING_QUEUE=107;

    private MightyDbHelper mightyDbHelper;

    private static final String searchSelection= MightyContract.SongEntry.TABLE_NAME
            +"."+ MightyContract.SongEntry.COLUMN_TITLE + "=? OR "
            + MightyContract.SongEntry.COLUMN_ALBUM + "=? OR "
            + MightyContract.SongEntry.COLUMN_ARTIST + "=?";

    private static final String artistSelection= MightyContract.SongEntry.TABLE_NAME
            +"."+ MightyContract.SongEntry.COLUMN_ARTIST+ "=?";

    private static final String albumSelection= MightyContract.SongEntry.TABLE_NAME
            +"."+ MightyContract.SongEntry.COLUMN_ALBUM + "=?";

    private static final UriMatcher mUriMatcher=buildUriMatcher();
    private static final SQLiteQueryBuilder songsForAlbumArtistsQueryBuilder;
    static{
        songsForAlbumArtistsQueryBuilder=new SQLiteQueryBuilder();
    }

    private static final String playlistSelection= MightyContract.PlaylistEntry.TABLE_NAME
            +"."+ MightyContract.PlaylistEntry._ID+ "=?";

    static UriMatcher buildUriMatcher(){
        final UriMatcher uriMatcher=new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(MightyContract.CONTENT_AUTHORITY, MightyContract.PATH_SONG,SONG);
        uriMatcher.addURI(MightyContract.CONTENT_AUTHORITY, MightyContract.PATH_PLAYLIST,PLAYLIST);
        uriMatcher.addURI(MightyContract.CONTENT_AUTHORITY,MightyContract.PATH_PLAYLIST_SONG,PLAYLIST_SONG);
        uriMatcher.addURI(MightyContract.CONTENT_AUTHORITY, MightyContract.PATH_SONG + "/*",SONG_FOR_ALBUM);
        uriMatcher.addURI(MightyContract.CONTENT_AUTHORITY, MightyContract.PATH_PLAYLIST+ "/#",SONG_FOR_PLAYLIST);
        uriMatcher.addURI(MightyContract.CONTENT_AUTHORITY, MightyContract.PATH_SONG + "/*",SONG_FOR_ARTISTS);
        uriMatcher.addURI(MightyContract.CONTENT_AUTHORITY, MightyContract.PATH_SONG + "/*",SEARCHES);
        uriMatcher.addURI(MightyContract.CONTENT_AUTHORITY, MightyContract.PATH_PLAYING_QUEUE,PLAYING_QUEUE);
        return uriMatcher;
    }

    @Override
    public boolean onCreate() {
        mightyDbHelper=new MightyDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        Cursor returnCursor;
//        Log.d("MightySongProvider",String .valueOf(uri));
        switch (mUriMatcher.match(uri)) {
            case SONG:
                returnCursor=getSongs(projection,selection,selectionArgs,sortOrder);
//                Log.d("MightySongProvider",projection+":"+selection+":"+selectionArgs[0]+":"+sortOrder);
                break;
            case PLAYLIST:
                returnCursor=getPlaylists(projection,selection,selectionArgs,sortOrder);
//                Log.d("MightySongProvider","getPlaylist is Called");
                break;

            case PLAYING_QUEUE:
                returnCursor=getPlayingQueueSongs(projection,selection,selectionArgs,sortOrder);
                break;

            case SONG_FOR_ALBUM:
                returnCursor=getSongsForAlbums(uri,projection,selection,selectionArgs,sortOrder);
//                Log.d("AlbumSongFragmentALBUM","getSongsForAlbums is Called");
                break;

            case SONG_FOR_ARTISTS:
                returnCursor=getSongsForArtists(uri,projection,selection,selectionArgs,sortOrder);
//                Log.d("MightySongProvider","getSongsForArtists is Called");
                break;
            case SONG_FOR_PLAYLIST:
                returnCursor=getSongsForPlaylists(uri,projection,selection,selectionArgs,sortOrder);
//                Log.d("MightySongProvider","getSongsForPlaylist is Called");
                break;

            case SEARCHES:
                returnCursor=getResultsForSeaches(uri,projection,selection,selectionArgs,sortOrder);
//                Log.d("MightySongProvider","getSearch is Called");
                break;

            default:
                throw new UnsupportedOperationException("Unknown Uri: " + uri);
        }
        return returnCursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {

        final int match = mUriMatcher.match(uri);
        switch (match) {

            case PLAYLIST:
                return MightyContract.PlaylistEntry.CONTENT_TYPE;
            case PLAYING_QUEUE:
                return MightyContract.PlayingQueueEntry.CONTENT_TYPE;
            case SONG:
                return MightyContract.SongEntry.CONTENT_TYPE;
            case SONG_FOR_ALBUM:
                return MightyContract.SongEntry.CONTENT_TYPE;
            case SONG_FOR_ARTISTS:
                return MightyContract.SongEntry.CONTENT_TYPE;
            case SONG_FOR_PLAYLIST:
                return MightyContract.SongEntry.CONTENT_TYPE;
            case SEARCHES:
                return MightyContract.SongEntry.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown Uri: " + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {

        final SQLiteDatabase mightyWritableDatabase=mightyDbHelper.getWritableDatabase();
        final int match=mUriMatcher.match(uri);
        Uri returnUri;
        switch (match) {
            case SONG: {
                if (values.containsKey(MightyContract.SongEntry.COLUMN_DURATION)) {
                    String finalTimerString;
                    finalTimerString = Utilities.milliSecondsToTimer(values.getAsLong(MightyContract.SongEntry.COLUMN_DURATION));
                    values.put(MightyContract.SongEntry.COLUMN_DURATION, finalTimerString);
                }
                long _id = mightyWritableDatabase.insert(MightyContract.SongEntry.TABLE_NAME, null, values);
                if (_id > 0) {
                    returnUri = MightyContract.SongEntry.buildSongUri(_id);
                } else {
                    throw new SQLException("Failed to insert row into " + uri);
                }
                break;
            }
            case PLAYLIST: {
                long _id = mightyWritableDatabase.insert(MightyContract.PlaylistEntry.TABLE_NAME, null, values);
                if (_id > 0) {
                    returnUri = MightyContract.PlaylistEntry.buildPlaylistUri(_id);
                } else {
                    throw new SQLException("Failed to insert row into " + uri);
                }
                break;
            }
            case PLAYLIST_SONG: {
                long _id = mightyWritableDatabase.insert(MightyContract.PlaylistSongEntry.TABLE_NAME, null, values);
                if (_id > 0) {
                    returnUri = MightyContract.PlaylistSongEntry.buildPlaylistSongUri(_id);
                } else {
                    throw new SQLException("Failed to insert row into " + uri);
                }
                break;
            }
            case PLAYING_QUEUE: {
                long _id = mightyWritableDatabase.insert(MightyContract.PlayingQueueEntry.TABLE_NAME, null, values);
                if (_id > 0) {
                    returnUri = MightyContract.PlayingQueueEntry.buildQueueUri(_id);
                } else {
                    throw new SQLException("Failed to insert row into " + uri);
                }
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri,null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase mightyWritableDatabase=mightyDbHelper.getWritableDatabase();
        final int match=mUriMatcher.match(uri);
        int rowsDeleted;
        if (selection==null) {
            selection="1";
        }
        switch (match) {
            case SONG:
                rowsDeleted = mightyWritableDatabase.delete(MightyContract.SongEntry.TABLE_NAME, selection, selectionArgs);
                break;

            case PLAYLIST:
                rowsDeleted = mightyWritableDatabase.delete(MightyContract.PlaylistEntry.TABLE_NAME, selection, selectionArgs);
                break;

            case PLAYLIST_SONG:
                rowsDeleted= mightyWritableDatabase.delete(MightyContract.PlaylistSongEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case PLAYING_QUEUE:
                rowsDeleted=mightyWritableDatabase.delete(MightyContract.PlayingQueueEntry.TABLE_NAME,selection,selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowsDeleted > 0) {
            getContext().getContentResolver().notifyChange(uri,null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase mightyWritableDatabase=mightyDbHelper.getWritableDatabase();
        final int match=mUriMatcher.match(uri);
        int rowsUpdated;
        switch (match) {
            case SONG:
                rowsUpdated = mightyWritableDatabase.update(MightyContract.SongEntry.TABLE_NAME,values, selection, selectionArgs);
                break;

            case PLAYLIST:
                rowsUpdated= mightyWritableDatabase.update(MightyContract.PlaylistEntry.TABLE_NAME,values, selection, selectionArgs);
                break;

            case PLAYLIST_SONG:
                rowsUpdated= mightyWritableDatabase.update(MightyContract.PlaylistSongEntry.TABLE_NAME,values, selection, selectionArgs);
                break;
            case PLAYING_QUEUE:
                rowsUpdated=mightyWritableDatabase.update(MightyContract.PlayingQueueEntry.TABLE_NAME,values,selection,selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowsUpdated > 0) {
            getContext().getContentResolver().notifyChange(uri,null);
        }
        return rowsUpdated;
    }
//    static int i=0;
    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase mightyWritableDatabase=mightyDbHelper.getWritableDatabase();
        final int match=mUriMatcher.match(uri);
        switch (match) {
            case SONG:
                int returnedSongs=0;
                mightyWritableDatabase.beginTransaction();
                try{
                    for (ContentValues contentValues : values) {
//                        i++;
                        if (contentValues.containsKey(MightyContract.SongEntry.COLUMN_DURATION )) {
//                            String finalTimerString;
//                            finalTimerString = Utilities.milliSecondsToTimer(contentValues.getAsLong(MightyContract.SongEntry.COLUMN_DURATION));
//                            contentValues.put(MightyContract.SongEntry.COLUMN_DURATION, finalTimerString);
                            String string=String.valueOf(contentValues.getAsLong(MightyContract.SongEntry.COLUMN_DURATION));
                            contentValues.put(MightyContract.SongEntry.COLUMN_DURATION, string);
                        }

                        long _id = mightyWritableDatabase.insert(MightyContract.SongEntry.TABLE_NAME, null, contentValues);
                        if (_id != -1) {
                            returnedSongs++;
                        }
                    }
//                    Log.d("BulkInsert",String.valueOf(i));
                    mightyWritableDatabase.setTransactionSuccessful();
                    return returnedSongs;
                }finally {
                    mightyWritableDatabase.endTransaction();
                }
            case PLAYING_QUEUE:
                int returnedQueueSongs=0;
                mightyWritableDatabase.beginTransaction();
                try{
                    for (ContentValues contentValues : values) {
                        if (contentValues.containsKey(MightyContract.PlayingQueueEntry.COLUMN_DURATION)) {
                            String finalTimerString;
                            finalTimerString= Utilities.milliSecondsToTimer(contentValues.getAsLong(MightyContract.PlayingQueueEntry.COLUMN_DURATION));
                            contentValues.put(MightyContract.PlayingQueueEntry.COLUMN_DURATION,finalTimerString);
                        }
                        long _id=mightyWritableDatabase.insert(MightyContract.PlayingQueueEntry.TABLE_NAME,null,contentValues);
                        if (_id != -1) {
                            returnedQueueSongs++;
                        }
                    }
                    mightyWritableDatabase.setTransactionSuccessful();
                    return returnedQueueSongs;
                }finally {
                    mightyWritableDatabase.endTransaction();
                }
            default:
                return super.bulkInsert(uri, values);
        }
    }

    public void addSongsToPlayingQueue(Context context, List<SongsInfo> list){

        MightyDbHelper dbHelper=null;
        if (context != null) {
            dbHelper = new MightyDbHelper(context);
        } else {
            dbHelper=new MightyDbHelper(getContext());
        }
        SQLiteDatabase writableDatabase=dbHelper.getWritableDatabase();
        int delete=writableDatabase.delete(MightyContract.PlayingQueueEntry.TABLE_NAME,null,null);
        Log.d("Shuffle 1:",String.valueOf(delete));
        writableDatabase.close();

        String sql="INSERT INTO " +
                MightyContract.PlayingQueueEntry.TABLE_NAME +
                " values(?,?,?,?,?,?,?,?,?)";

        SQLiteDatabase readableDatabase=dbHelper.getReadableDatabase();
        SQLiteStatement statement=readableDatabase.compileStatement(sql);
        readableDatabase.beginTransaction();
        for (int i=0;i<list.size();i++) {
            statement.clearBindings();
            statement.bindLong(1,list.get(i).getSondId());
            statement.bindString(2,list.get(i).getSongData());
            statement.bindString(3,list.get(i).getSongTitle());
            statement.bindString(4,list.get(i).getSongAlbum());
            statement.bindString(5,list.get(i).getSongArtist());
//            String time=Utilities.milliSecondsToTimer(list.get(i).getSongDuration());
            statement.bindString(6,String.valueOf(list.get(i).getSongDuration()));
            statement.bindLong(7,list.get(i).getLiked());
            statement.bindLong(8,list.get(i).getIsCurrent());
            statement.bindNull(9);
            statement.execute();
            Log.d("Shuffle 1:",String.valueOf(i));
        }
        readableDatabase.setTransactionSuccessful();
        readableDatabase.endTransaction();
        readableDatabase.close();
    }

    public void addSongsToQueue(Context context, List<SongsInfo> list){

        MightyDbHelper dbHelper=new MightyDbHelper(context);
        int queueSize=getFromQueue(context);

        String sql="INSERT INTO " +
                MightyContract.PlayingQueueEntry.TABLE_NAME +
                " values(?,?,?,?,?,?,?,?,?)";

        SQLiteDatabase readableDatabase=dbHelper.getReadableDatabase();
        SQLiteStatement statement=readableDatabase.compileStatement(sql);
        readableDatabase.beginTransaction();
        for (int i=0;i<list.size();i++) {
            statement.clearBindings();
            statement.bindLong(1,(queueSize+list.get(i).getSondId()));
            statement.bindString(2,list.get(i).getSongData());
            statement.bindString(3,list.get(i).getSongTitle());
            statement.bindString(4,list.get(i).getSongAlbum());
            statement.bindString(5,list.get(i).getSongArtist());
//            String time=Utilities.milliSecondsToTimer(list.get(i).getSongDuration());
            statement.bindString(6,String.valueOf(list.get(i).getSongDuration()));
            statement.bindLong(7,list.get(i).getLiked());
            statement.bindLong(8,list.get(i).getIsCurrent());
            statement.bindNull(9);
            statement.execute();
        }
        readableDatabase.setTransactionSuccessful();
        readableDatabase.endTransaction();
        readableDatabase.close();

        MightyPlayerService.audioList.addAll(list);
    }

    public void addSingleSongToQueue(Context context,SongsInfo song){

        int queueSize=getFromQueue(context);
        ContentValues contentValues=new ContentValues();
        contentValues.put(MightyContract.PlayingQueueEntry._ID,(queueSize+song.getSondId()));
        contentValues.put(MightyContract.PlayingQueueEntry.COLUMN_DATA,song.getSongData());
        contentValues.put(MightyContract.PlayingQueueEntry.COLUMN_TITLE,song.getSongTitle());
        contentValues.put(MightyContract.PlayingQueueEntry.COLUMN_ALBUM,song.getSongAlbum());
        contentValues.put(MightyContract.PlayingQueueEntry.COLUMN_ARTIST,song.getSongArtist());
        contentValues.put(MightyContract.PlayingQueueEntry.COLUMN_DURATION,song.getSongDuration());
        contentValues.put(MightyContract.PlayingQueueEntry.COLUMN_LIKE,song.getLiked());
        contentValues.put(MightyContract.PlayingQueueEntry.COLUMN_IS_CURRENT,song.getIsCurrent());
        contentValues.put(MightyContract.PlayingQueueEntry.COLUMN_ALBUM_ART,song.getSongThumbnail());

        context.getContentResolver().insert(MightyContract.PlayingQueueEntry.CONTENT_URI,contentValues);
        getFromQueue(context);

//        List<SongsInfo> list=new ArrayList<>();
//        list.add(song);
        if (MightyPlayerService.audioList != null) {
            MightyPlayerService.audioList.add(song);
        } else {
            MightyPlayerService.audioList=new ArrayList<>();
            MightyPlayerService.audioList.add(song);
        }

    }

    public void addSongToQueueNext(Context context,SongsInfo song){
        int queueSize=getFromQueue(context);
        ContentValues contentValues=new ContentValues();
        contentValues.put(MightyContract.PlayingQueueEntry._ID,(queueSize+song.getSondId()));
        contentValues.put(MightyContract.PlayingQueueEntry.COLUMN_DATA,song.getSongData());
        contentValues.put(MightyContract.PlayingQueueEntry.COLUMN_TITLE,song.getSongTitle());
        contentValues.put(MightyContract.PlayingQueueEntry.COLUMN_ALBUM,song.getSongAlbum());
        contentValues.put(MightyContract.PlayingQueueEntry.COLUMN_ARTIST,song.getSongArtist());
        contentValues.put(MightyContract.PlayingQueueEntry.COLUMN_DURATION,song.getSongDuration());
        contentValues.put(MightyContract.PlayingQueueEntry.COLUMN_LIKE,song.getLiked());
        contentValues.put(MightyContract.PlayingQueueEntry.COLUMN_IS_CURRENT,song.getIsCurrent());
        contentValues.put(MightyContract.PlayingQueueEntry.COLUMN_ALBUM_ART,song.getSongThumbnail());

        context.getContentResolver().insert(MightyContract.PlayingQueueEntry.CONTENT_URI,contentValues);
        getFromQueue(context);
        if (MightyPlayerService.audioList != null) {
            MightyPlayerService.audioList.add(MightyPlayerService.audioIndex + 1, song);
        } else {
            MightyPlayerService.audioList=new ArrayList<>();
            MightyPlayerService.audioList.add(MightyPlayerService.audioIndex + 1, song);
        }
    }

    public int getFromQueue(Context context){
        int count=0;
        Cursor cursor=context.getContentResolver().query(MightyContract.PlayingQueueEntry.CONTENT_URI
                ,null,null,null,null,null);
        if (cursor!=null&&cursor.moveToFirst()) {
            count=cursor.getCount();
            Log.d("QUEUE",String.valueOf(cursor.getCount()));
        }
        if (cursor!=null) {
            cursor.close();
        }
        return count;
    }

    public Cursor getPlayingQueueSongs(String[] projection,String selection,String[] selectionArgs,String sortOrder){
        return mightyDbHelper.getReadableDatabase()
                .query(MightyContract.PlayingQueueEntry.TABLE_NAME
                        ,projection
                        ,selection
                        ,selectionArgs
                        ,null
                        ,null
                        ,sortOrder);
    }

    public Cursor getSongs(String[] projection, String selection, String[] selectionArgs, String sortOrder){
        return mightyDbHelper.getReadableDatabase()
                .query(MightyContract.SongEntry.TABLE_NAME
                        ,projection
                        ,selection
                        ,selectionArgs
                        ,null
                        ,null
                        ,sortOrder);
    }

    public Cursor getPlaylists(String[] projection,String selection,String[] selectionArgs,String sortOrder){
        return mightyDbHelper.getReadableDatabase()
                .query(MightyContract.PlaylistEntry.TABLE_NAME
                        ,projection
                        ,selection
                        ,selectionArgs
                        ,null
                        ,null
                        ,sortOrder);
    }

    public Cursor getSongsForAlbums(Uri uri,String[] projection,String selection,String[] selectionArgs,String sortOrder){

        String albumName= MightyContract.SongEntry.getAlbumNameFromUri(uri);
        return songsForAlbumArtistsQueryBuilder.query(mightyDbHelper.getReadableDatabase()
                ,projection
                ,albumSelection
                ,new String[]{albumName}
                ,null
                ,null
                ,sortOrder);

    }

    public Cursor getSongsForArtists(Uri uri,String[] projection,String selection,String[] selectionArgs,String sortOrder){

        String artistName= MightyContract.SongEntry.getArtistNameFromUri(uri);
        return songsForAlbumArtistsQueryBuilder.query(mightyDbHelper.getReadableDatabase()
                ,projection
                ,artistSelection
                ,new String[]{artistName}
                ,null
                ,null
                ,sortOrder);

    }

    public Cursor getSongsForPlaylists(Uri uri,String[] projection,String selection,String[] selectionArgs,String sortOrder){

        String playlistId= MightyContract.PlaylistEntry.getPlaylistIdFromUri(uri);
        Log.d("getSongsForPlaylists",playlistId);
//        String sql="SELECT * FROM " +
//                MightyContract.SongEntry.TABLE_NAME +
//                " JOIN " +
//                MightyContract.PlaylistSongEntry.TABLE_NAME +
//                " ON " +
//                MightyContract.SongEntry.TABLE_NAME + "." + MightyContract.SongEntry._ID +
//                "=" +
//                MightyContract.PlaylistSongEntry.TABLE_NAME + "." + MightyContract.PlaylistSongEntry.COLUMN_SONG_ID +
//                " AND " +
//                MightyContract.PlaylistSongEntry.TABLE_NAME + "." + MightyContract.PlaylistSongEntry.COLUMN_PLAYLIST_ID +
//                " = ? ";

        //OR


        String sql="SELECT * FROM " + MightyContract.SongEntry.TABLE_NAME
                + " WHERE "
                + MightyContract.SongEntry.TABLE_NAME + "." + MightyContract.SongEntry._ID + " IN ("
                + " SELECT " + MightyContract.PlaylistSongEntry.TABLE_NAME + "." + MightyContract.PlaylistSongEntry.COLUMN_SONG_ID + " FROM "
                + MightyContract.PlaylistSongEntry.TABLE_NAME
                + " WHERE "
                + MightyContract.PlaylistSongEntry.TABLE_NAME + "." + MightyContract.PlaylistSongEntry.COLUMN_PLAYLIST_ID + " = ?" + " )"
                + " ORDER BY "
                + sortOrder;

        Log.d("getSongsForPlaylists",sql);
        Cursor cursor=mightyDbHelper.getReadableDatabase().rawQuery(sql,new String[]{playlistId});
        Log.d("getSongsForPlaylists",String.valueOf(cursor.getCount()));
        return cursor;

    }

    public Cursor getResultsForSeaches(Uri uri,String[] projection,String selection,String[] selectionArgs,String sortOrder){

        String searchedFor= MightyContract.SongEntry.getSearchQueryFromUri(uri);
        return songsForAlbumArtistsQueryBuilder.query(mightyDbHelper.getReadableDatabase()
                ,projection
                ,searchSelection
                ,new String[]{searchedFor}
                ,null
                ,null
                ,sortOrder);

    }

    public void addSelectedSongToPlaylist(Context context,int songPosition,int playlistPosition){
        MightyDbHelper dbHelper=new MightyDbHelper(context);
        Cursor cursor=dbHelper.getReadableDatabase().query(MightyContract.PlaylistSongEntry.TABLE_NAME
                ,null
                ,MightyContract.PlaylistSongEntry.COLUMN_PLAYLIST_ID + " =? AND "+MightyContract.PlaylistSongEntry.COLUMN_SONG_ID+ " =?"
                ,new String[]{String.valueOf(playlistPosition),String.valueOf(songPosition)}
                ,null
                ,null
                ,null);
        if (cursor!=null && cursor.moveToFirst()) {

            Toast.makeText(context,"song already exists", Toast.LENGTH_SHORT).show();
            cursor.close();
            dbHelper.close();
            return;
        }

        ContentValues contentValues = new ContentValues();
        contentValues.put(MightyContract.PlaylistSongEntry.COLUMN_PLAYLIST_ID,playlistPosition);
        contentValues.put(MightyContract.PlaylistSongEntry.COLUMN_SONG_ID,songPosition);
        long id=dbHelper.getWritableDatabase().insert(MightyContract.PlaylistSongEntry.TABLE_NAME,null,contentValues);
        if (id > 0) {
            Toast.makeText(context,"song added successfully to playlist", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(context,"no song added", Toast.LENGTH_LONG).show();
        }
        dbHelper.close();
    }

    public long addPlaylistToDatabase(Context context,String playlistName,String playlistDescription){

        long playlistId=0;

        Cursor playlistCursor=context.getContentResolver().query(MightyContract.PlaylistEntry.CONTENT_URI
                , new String[]{MightyContract.PlaylistEntry._ID}
                , MightyContract.PlaylistEntry.COLUMN_PLAYLIST_NAME + " = ?"
                , new String[]{playlistName}
                , null);

        if (playlistCursor.moveToFirst()) {
            PlaylistAsyncTasks.playlistAlreadyExist=true;
        } else {
            PlaylistAsyncTasks.playlistAlreadyExist=false;
            ContentValues playlistValues=new ContentValues();
            playlistValues.put(MightyContract.PlaylistEntry.COLUMN_PLAYLIST_NAME,playlistName);
            playlistValues.put(MightyContract.PlaylistEntry.COLUMN_DESCRIPTION,playlistDescription);

            Uri insertedUri=context.getContentResolver().insert(MightyContract.PlaylistEntry.CONTENT_URI,playlistValues);
            playlistId= ContentUris.parseId(insertedUri);

//            Log.d(LOG_TAG,insertedUri.toString());
        }
        playlistCursor.close();
        return playlistId;
    }

    public int getPlaylistCount(Context context){
        int count=0;
        Cursor cursor=context.getContentResolver().query(MightyContract.PlaylistEntry.CONTENT_URI
                ,null,null,null,null,null);
        if (cursor!=null&&cursor.moveToFirst()) {
            count=cursor.getCount();
            Log.d("QUEUE",String.valueOf(cursor.getCount()));
        }
        if (cursor!=null) {
            cursor.close();
        }
        return count;
    }

    public int deleteSong(Context context,String title,long id){

        int delete=0;
        MightyDbHelper dbHelper=new MightyDbHelper(context);
        final SQLiteDatabase mightyWritableDatabase=dbHelper.getWritableDatabase();
        mightyWritableDatabase.beginTransaction();
        try {
            String selection= MediaStore.Audio.Media.IS_MUSIC + "!=? AND " + MediaStore.Audio.Media.TITLE + " =? " ;
            delete=context.getContentResolver().delete(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,selection,new String[]{"0",title});
            int songDelete=mightyWritableDatabase.delete(MightyContract.SongEntry.TABLE_NAME, MightyContract.SongEntry.COLUMN_TITLE+"=?",new String[]{title});
            int queueDelete=mightyWritableDatabase.delete(MightyContract.PlayingQueueEntry.TABLE_NAME, MightyContract.PlayingQueueEntry.COLUMN_TITLE+"=?",new String[]{title});
            int playlistDelete=mightyWritableDatabase.delete(MightyContract.PlaylistSongEntry.TABLE_NAME, MightyContract.PlaylistSongEntry.COLUMN_SONG_ID+"=?",new String[]{String.valueOf(id)});
            Log.d("Delete",String.valueOf(delete)+":"+String.valueOf(songDelete)+":"+String.valueOf(queueDelete)+":"+String.valueOf(playlistDelete));
        }finally {
            mightyWritableDatabase.endTransaction();
        }

        return delete;
    }
}
