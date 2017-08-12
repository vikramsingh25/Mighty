package com.example.sankalp.muxicplayer.data;

/**
 * Created by sankalp on 12/13/2016.
 */
public class PlaylistInfo {
    public String playlistName;
    public String getPlaylistDescription;
    public long playlistId;
    public int songCount=0;

    public String getPlaylistName() {
        return playlistName;
    }

    public String getGetPlaylistDescription() {
        return getPlaylistDescription;
    }

    public long getPlaylistId() {
        return playlistId;
    }

    public int getSongCount() {
        return songCount;
    }

    public PlaylistInfo(long playlistId, String playlistName, String getPlaylistDescription){
        this.playlistName=playlistName;
        this.getPlaylistDescription=getPlaylistDescription;
        this.playlistId=playlistId;
//        this.songCount=songCount;
    }
//    public int playlistIconId;
}
