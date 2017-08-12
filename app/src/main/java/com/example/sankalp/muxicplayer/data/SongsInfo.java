package com.example.sankalp.muxicplayer.data;

import android.graphics.Bitmap;
import android.support.v4.graphics.BitmapCompat;

import java.io.Serializable;

/**
 * Created by sankalp on 9/29/2016.
 */
public class SongsInfo implements Serializable {
    public String songTitle;

    public long getSondId() {
        return songId;
    }

    public int getLiked() {
        return liked;
    }

//    public void setSondId(long sondId) {
//        this.sondId = sondId;
//    }

    public long songId;

    public long getAlbumId() {
        return albumId;
    }

    public void setAlbumId(long albumId) {
        this.albumId = albumId;
    }

    public long albumId;
    public String songArtist;
    public long songDuration;
    //    public String songGenre;
    public String songData;
    public String songAlbum;
    public int liked;

    public int getIsCurrent() {
        return isCurrent;
    }

    public int isCurrent;
    public int noOfSongs;

    public void setSongThumbnail(String songThumbnail) {
        this.songThumbnail = songThumbnail;
    }

    public String songThumbnail;

    public SongsInfo(String songAlbum, String songArtist) {
        this.songAlbum = songAlbum;
        this.songArtist = songArtist;
    }

    public void setIsCurrent(int isCurrent) {
        this.isCurrent = isCurrent;
    }

    public void setLiked(int liked) {
        this.liked = liked;
    }

    public SongsInfo(String songTitle, String songArtist, long songDuration,
                     String songData, String songAlbum) {
        this.songTitle = songTitle;
        this.songArtist = songArtist;
        this.songDuration = songDuration;
        this.songData = songData;
        this.songAlbum = songAlbum;
    }
    public SongsInfo(long id,String songTitle, String songArtist, long songDuration,
                     String songData, String songAlbum) {
        this.songId=id;
        this.songTitle = songTitle;
        this.songArtist = songArtist;
        this.songDuration = songDuration;
        this.songData = songData;
        this.songAlbum = songAlbum;
    }

//    public SongsInfo(long songId, String songData, String songTitle, String songAlbum,
//                     String songArtist, long songDuration, int liked, String songThumbnail, int noOfSongs) {
//        this.songTitle = songTitle;
//        this.songId = songId;
//        this.liked = liked;
//        this.songArtist = songArtist;
//        this.songDuration = songDuration;
//        this.songData = songData;
//        this.songAlbum = songAlbum;
//        this.noOfSongs = noOfSongs;
//        this.songThumbnail = songThumbnail;
//    }

    public SongsInfo(long songId,long albumId,String songTitle, String songArtist, long songDuration,
                     String songData, String songAlbum, String songThumbnail) {
        this.songId = songId;
        this.albumId=albumId;
        this.songTitle = songTitle;
        this.songArtist = songArtist;
        this.songDuration = songDuration;
        this.songData = songData;
        this.songAlbum = songAlbum;
        this.songThumbnail = songThumbnail;
    }
    public SongsInfo(String songTitle, String songArtist, long songDuration,
                     String songData, String songAlbum, String songThumbnail) {
//        this.albumId=albumId;
        this.songTitle = songTitle;
        this.songArtist = songArtist;
        this.songDuration = songDuration;
        this.songData = songData;
        this.songAlbum = songAlbum;
        this.songThumbnail = songThumbnail;
    }


    public String getSongThumbnail() {
        return songThumbnail;
    }

//    public void setSongThumbnail(String songThumbnail) {
//        this.songThumbnail = songThumbnail;
//    }

    public String getSongAlbum() {
        return songAlbum;
    }

//    public void setSongAlbum(String songAlbum) {
//        this.songAlbum = songAlbum;
//    }

    public String getSongTitle() {
        return songTitle;
    }

//    public void setSongTitle(String songTitle) {
//        this.songTitle = songTitle;
//    }

    public String getSongArtist() {
        return songArtist;
    }

    public void setSongArtist(String songArtist) {
        this.songArtist = songArtist;
    }

    public long getSongDuration() {
        return songDuration;
    }

//    public void setSongDuration(long songDuration) {
//        this.songDuration = songDuration;
//    }

//    public String getSongGenre() {
//        return songGenre;
//    }

//    public void setSongGenre(String songGenre) {
//        this.songGenre = songGenre;
//    }

    public String getSongData() {
        return songData;
    }

//    public void setSongData(String songData) {
//        this.songData = songData;
//    }

}
