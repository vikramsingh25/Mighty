package com.example.sankalp.muxicplayer.mightyDB;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by sankalp on 12/27/2016.
 */
public class MightyDbHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME ="Mighty.db";
    public static final int DB_VERSION =9;

    public MightyDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d("Sqlite","onCreate");
        final String SQL_CREATE_SONG_TABLE="CREATE TABLE "+ MightyContract.SongEntry.TABLE_NAME + " ( " +
                MightyContract.SongEntry._ID + " INTEGER PRIMARY KEY, " +
                MightyContract.SongEntry.COLUMN_DATA + " VARCHAR(255) NOT NULL, " +
                MightyContract.SongEntry.COLUMN_TITLE + " VARCHAR(255) NOT NULL, " +
                MightyContract.SongEntry.COLUMN_ALBUM + " VARCHAR(255) NOT NULL, " +
                MightyContract.SongEntry.COLUMN_ALBUM_ID + " VARCHAR(255) NOT NULL, " +
                MightyContract.SongEntry.COLUMN_ARTIST + " VARCHAR(255) NOT NULL, " +
                MightyContract.SongEntry.COLUMN_DURATION + " VARCHAR(10) NOT NULL, " +
                MightyContract.SongEntry.COLUMN_LIKE + " INT(1) DEFAULT 0, " +
//                MightyContract.SongEntry.COLUMN_IS_CURRENT + " INT(1) DEFAULT 0, " +
//                MightyContract.SongEntry.COLUMN_PLAYLIST_ID+ " INTEGER FOREIGN KEY REFERENCES ("+ MightyContract.PlaylistEntry._ID +"),"+
                MightyContract.SongEntry.COLUMN_ALBUM_ART+ " VARCHAR(10) DEFAULT NULL" +
                ")";
        db.execSQL(SQL_CREATE_SONG_TABLE);

        final String SQL_CREATE_PLAYLIST_TABLE="CREATE TABLE "+ MightyContract.PlaylistEntry.TABLE_NAME + " ( " +
                MightyContract.PlaylistEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                MightyContract.PlaylistEntry.COLUMN_PLAYLIST_NAME + " VARCHAR(255) UNIQUE, " +
                MightyContract.PlaylistEntry.COLUMN_MODIFICATION_TIME + " TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, "+
                MightyContract.PlaylistEntry.COLUMN_DESCRIPTION + " VARCHAR(255) DEFAULT 'no description specified' " +
//                MightyContract.PlaylistEntry.COLUMN_NO_OF_SONGS+ " INTEGER DEFAULT 0, " +
                ")";
        db.execSQL(SQL_CREATE_PLAYLIST_TABLE);

        final String SQL_CREATE_PLAYLIST_SONG_TABLE="CREATE TABLE "+ MightyContract.PlaylistSongEntry.TABLE_NAME + " ( " +
                MightyContract.PlaylistSongEntry.COLUMN_PLAYLIST_ID+ " INTEGER," +
                MightyContract.PlaylistSongEntry.COLUMN_SONG_ID+ " INTEGER," +
                "FOREIGN KEY(" + MightyContract.PlaylistSongEntry.COLUMN_PLAYLIST_ID + ") REFERENCES " + MightyContract.PlaylistEntry.TABLE_NAME + "("+ MightyContract.PlaylistEntry._ID + "),"+
                "FOREIGN KEY(" + MightyContract.PlaylistSongEntry.COLUMN_SONG_ID+ ") REFERENCES " + MightyContract.SongEntry.TABLE_NAME + "("+ MightyContract.SongEntry._ID + ")"+
                ")";
        db.execSQL(SQL_CREATE_PLAYLIST_SONG_TABLE);

        final String SQL_CREATE_TABLE_PLAYING_QUEUE="CREATE TABLE " + MightyContract.PlayingQueueEntry.TABLE_NAME + " ( " +
                MightyContract.PlayingQueueEntry._ID + " INTEGER NOT NULL, " +
                MightyContract.PlayingQueueEntry.COLUMN_DATA + " VARCHAR(255) NOT NULL, " +
                MightyContract.PlayingQueueEntry.COLUMN_TITLE + " VARCHAR(255) NOT NULL, " +
                MightyContract.PlayingQueueEntry.COLUMN_ALBUM + " VARCHAR(255) NOT NULL, " +
                MightyContract.PlayingQueueEntry.COLUMN_ARTIST + " VARCHAR(255) NOT NULL, " +
                MightyContract.PlayingQueueEntry.COLUMN_DURATION + " VARCHAR(10) NOT NULL, " +
                MightyContract.PlayingQueueEntry.COLUMN_LIKE + " INT(1) DEFAULT 0, " +
                MightyContract.PlayingQueueEntry.COLUMN_IS_CURRENT + " INT(1) DEFAULT 0, " +
                MightyContract.PlayingQueueEntry.COLUMN_ALBUM_ART+ " VARCHAR(10) DEFAULT NULL" +
                ")";
        db.execSQL(SQL_CREATE_TABLE_PLAYING_QUEUE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+ MightyContract.SongEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS "+ MightyContract.PlaylistEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS "+ MightyContract.PlaylistSongEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS "+ MightyContract.PlayingQueueEntry.TABLE_NAME);
        onCreate(db);
    }
}
