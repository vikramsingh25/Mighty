package com.example.sankalp.muxicplayer.mightyDB;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;
import android.util.Log;

/**
 * Created by sankalp on 12/27/2016.
 */
public class MightyContract {

    public static final String CONTENT_AUTHORITY="com.example.sankalp.muxicplayer";
    public static final Uri BASE_CONTENT_URI= Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_SONG="song";
    public static final String PATH_PLAYLIST="playlist";
    public static final String PATH_PLAYLIST_SONG="playlist_song_relation";
    public static final String PATH_PLAYING_QUEUE="playing_queue";

    public static final class PlayingQueueEntry implements BaseColumns {
        public static final Uri CONTENT_URI=BASE_CONTENT_URI.buildUpon().appendPath(PATH_PLAYING_QUEUE).build();
        public static final String CONTENT_TYPE=ContentResolver.CURSOR_DIR_BASE_TYPE + "/"
                + CONTENT_AUTHORITY + "/" + PATH_PLAYING_QUEUE;
        public static final String CONTENT_ITEM_TYPE=ContentResolver.CURSOR_ITEM_BASE_TYPE+ "/"
                + CONTENT_AUTHORITY + "/" + PATH_PLAYING_QUEUE;

        public static final String TABLE_NAME="queue";
        public static final String COLUMN_DATA="data";
        public static final String COLUMN_TITLE="title";
        public static final String COLUMN_ALBUM="album";
        public static final String COLUMN_ARTIST="artist";
        public static final String COLUMN_ALBUM_ART="albumArt";
        public static final String COLUMN_DURATION="duration";
        public static final String COLUMN_IS_CURRENT="current";
        public static final String COLUMN_LIKE="liked";

        public static Uri buildQueueUri(long _id) {
            return ContentUris.withAppendedId(CONTENT_URI, _id);
        }
    }

    public static final class SongEntry implements BaseColumns{

        public static final Uri CONTENT_URI=BASE_CONTENT_URI.buildUpon().appendPath(PATH_SONG).build();

        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" +
                CONTENT_AUTHORITY + "/" + PATH_SONG;

        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" +
                CONTENT_AUTHORITY + "/" + PATH_SONG;

        public static final String TABLE_NAME="song";
        public static final String COLUMN_DATA="data";
        public static final String COLUMN_TITLE="title";
        public static final String COLUMN_ALBUM="album";
        public static final String COLUMN_ALBUM_ID="album_id";
        public static final String COLUMN_ARTIST="artist";
        public static final String COLUMN_ALBUM_ART="albumArt";
        public static final String COLUMN_DURATION="duration";
        public static final String COLUMN_IS_CURRENT="current";
        public static final String COLUMN_LIKE="liked";

        public static Uri buildUriForAlbums(String albumName){
            return CONTENT_URI.buildUpon().appendQueryParameter(COLUMN_ALBUM,albumName).build();
        }

        public static Uri buildUriForArtists(String artistName){
            return CONTENT_URI.buildUpon().appendQueryParameter(COLUMN_ARTIST,artistName).build();
        }

        public static Uri buildUriForSearches(String searchQuery){
            return CONTENT_URI.buildUpon().appendQueryParameter("SearchQuery",searchQuery).build();
        }

        public static Uri buildSongUri(long _id) {
            return ContentUris.withAppendedId(CONTENT_URI, _id);
        }

        public static String getAlbumNameFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }

        public static String getArtistNameFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }

        public static String getSearchQueryFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }
    }

    public static final class PlaylistEntry implements BaseColumns{

        public static final Uri CONTENT_URI=BASE_CONTENT_URI.buildUpon().appendPath(PATH_PLAYLIST).build();

        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" +
                CONTENT_AUTHORITY + "/" + PATH_PLAYLIST;

        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" +
                CONTENT_AUTHORITY + "/" + PATH_PLAYLIST;

        public static final String TABLE_NAME="playlist";
        public static final String COLUMN_PLAYLIST_NAME="playlist_name";
        public static final String COLUMN_DESCRIPTION="description";
        public static final String COLUMN_MODIFICATION_TIME="modified";

        public static Uri buildUriForPlaylists(String  playlistId){
            return CONTENT_URI.buildUpon().appendQueryParameter("PLAYLIST_ID",playlistId).build();
        }

        public static Uri buildPlaylistUri(long _id) {
            return ContentUris.withAppendedId(CONTENT_URI, _id);
        }

        public static String getPlaylistIdFromUri(Uri uri){
            Log.d("contract",uri.toString());
            return uri.getPathSegments().get(1);
        }
    }

    public static final class PlaylistSongEntry implements BaseColumns{

        public static final Uri CONTENT_URI=BASE_CONTENT_URI.buildUpon().appendPath(PATH_PLAYLIST_SONG).build();

        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" +
                CONTENT_AUTHORITY + "/" + PATH_PLAYLIST_SONG;

        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" +
                CONTENT_AUTHORITY + "/" + PATH_PLAYLIST_SONG;

        public static final String TABLE_NAME="playlist_song_relation";
        public static final String COLUMN_PLAYLIST_ID="playlist_id";
        public static final String COLUMN_SONG_ID="song_id";

        public static Uri buildPlaylistSongUri(long _id) {
            return ContentUris.withAppendedId(CONTENT_URI, _id);
        }
    }

}
