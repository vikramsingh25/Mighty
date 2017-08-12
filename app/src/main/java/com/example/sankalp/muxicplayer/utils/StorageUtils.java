package com.example.sankalp.muxicplayer.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.util.Log;

import com.example.sankalp.muxicplayer.data.SongsInfo;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by sankalp on 10/2/2016.
 */
public class StorageUtils {
    public static final String STORAGE = "com.example.sankalp.muxicplayer.STORAGE";
//    private final String CURRENT_STORAGE = "com.example.sankalp.muxicplayer.CURRENT_STORAGE";
    private SharedPreferences preferences=null;
    private Context mContext;

    public StorageUtils(Context context){
        mContext=context;
    }

//    public void storeAudio(List<SongsInfo> arrayList){
//        if (mContext != null) {
//            Log.d("Storage Utils","Context is not null");
//            preferences = mContext.getSharedPreferences(STORAGE,Context.MODE_PRIVATE);
//            SharedPreferences.Editor editor = preferences.edit();
//            Gson gson = new Gson();
//            String json = gson.toJson(arrayList);
//            editor.putString("audioArrayList", json);
//            editor.apply();
//        } else {
//            Log.d("Storage utils","Context is null");
//        }
//    }
//
//    public List<SongsInfo> loadAudio(){
//        preferences=mContext.getSharedPreferences(STORAGE,Context.MODE_PRIVATE);
//        Gson gson=new Gson();
//        String json=preferences.getString("audioArrayList",null);
//        Type type= new TypeToken<ArrayList<SongsInfo>>(){
//
//        }.getType();
//        return gson.fromJson(json,type);
//    }

//    public void storeCurrentSongInfo(int index,String name,String album,String artist,String data,long duration){
//        preferences=mContext.getSharedPreferences(CURRENT_STORAGE,Context.MODE_PRIVATE);
//        SharedPreferences.Editor editor=preferences.edit();
//        editor.putInt("index",index);
//        editor.putString("name",name);
//        editor.putString("album",album);
//        editor.putString("artist",artist);
//        editor.putString("data",data);
//        editor.putLong("duration",duration);
//        editor.apply();
//    }
//
//    public String[] loadCurrentSongInfo(){
//        preferences=mContext.getSharedPreferences(CURRENT_STORAGE,Context.MODE_PRIVATE);
//        String[] info=new String[]{
//                String.valueOf(preferences.getInt("index",-1)),
//                preferences.getString("name",""),
//                preferences.getString("album",""),
//                preferences.getString("artist",""),
//                preferences.getString("data",""),
//                String.valueOf(preferences.getLong("duration",-1))
//        };
//        return info;
//    }

//    public void storeAlbumArt(final List<SongsInfo> list) {
//        preferences = mContext.getSharedPreferences(STORAGE, Context.MODE_PRIVATE);
//        final SharedPreferences.Editor editor = preferences.edit();
//        final String art[]=new String[list.size()];
//        new AsyncTask<Long,Void,String>(){
//            @Override
//            protected String doInBackground(Long... params) {
//                for (int i=0;i<list.size();i++) {
//                    art[i]=getAlbumart(list.get(i).getAlbumId());
//                    list.get(i).setSongThumbnail(art[i]);
//                }
//                Set<String> set=new HashSet<>(Arrays.asList(art));
//                editor.putStringSet("art",set);
//                return null;
//            }
//        }.execute();
//        editor.apply();
//    }

//    public String loadAlbumArt(List<SongsInfo> list,int position){
//        preferences = mContext.getSharedPreferences(STORAGE, Context.MODE_PRIVATE);
//        Set<String> set=preferences.getStringSet("art",null);
//        String art[]=new String[list.size()];
//        if (set!=null) {
//            set.toArray(art);
//        }
//        return art[position];
//    }

//    public String getAlbumart(long albumId) {
//        String path = null;
//        Cursor c = mContext.getContentResolver().query(
//                MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
//                new String[]{MediaStore.Audio.Albums.ALBUM_ART},
//                MediaStore.Audio.Albums._ID+ "=?",
//                new String[]{Long.toString(albumId)},
//                null);
//
//        if (c != null)
//        {
//            if (c.moveToFirst())
//            {
//                path = c.getString(0);
//            }
//            c.close();
//
//        }
//
//        return path;
//    }

    public void storeAudioIndex(int index) {
        preferences = mContext.getSharedPreferences(STORAGE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("audioIndex", index);
        editor.apply();
    }
//    public void storeLongClickedAudio(int position){
//        preferences=mContext.getSharedPreferences(STORAGE,Context.MODE_PRIVATE);
//        SharedPreferences.Editor editor=preferences.edit();
//        editor.putInt("songIndexForMenu",position);
//    }
//    public int loadLongClickedAudio(){
//        preferences=mContext.getSharedPreferences(STORAGE,Context.MODE_PRIVATE);
//        return preferences.getInt("songIndexForMenu",-1);
//    }

    public int loadAudioIndex() {
        preferences = mContext.getSharedPreferences(STORAGE, Context.MODE_PRIVATE);
        return preferences.getInt("audioIndex", -1);//return -1 if no data found
    }

    public void clearCachedAudioPlaylist() {
        preferences = mContext.getSharedPreferences(STORAGE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.apply();
    }

}
