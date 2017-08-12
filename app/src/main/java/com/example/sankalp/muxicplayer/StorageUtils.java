package com.example.sankalp.muxicplayer;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.sankalp.muxicplayer.data.SongsInfo;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Created by sankalp on 10/2/2016.
 */
public class StorageUtils {
    private final String STORAGE = "com.example.sankalp.muxicplayer.STORAGE";
    private SharedPreferences preferences;
    private Context context;

    public StorageUtils(Context context){
        this.context=context;
    }

    public void storeAudio(ArrayList<SongsInfo> arrayList){
         preferences= context.getSharedPreferences(STORAGE,Context.MODE_PRIVATE);
         SharedPreferences.Editor editor=preferences.edit();
         Gson gson=new Gson();
         String json=gson.toJson(arrayList);
         editor.putString("audioArrayList",json);
         editor.apply();
     }

    public ArrayList<SongsInfo> loadAudio(){
        preferences=context.getSharedPreferences(STORAGE,Context.MODE_PRIVATE);
        Gson gson=new Gson();
        String json=preferences.getString("audioArrayList",null);
        Type type= new TypeToken<ArrayList<SongsInfo>>(){

        }.getType();
        return gson.fromJson(json,type);
    }

    public void storeAudioIndex(int index) {
        preferences = context.getSharedPreferences(STORAGE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("audioIndex", index);
        editor.apply();
    }

    public int loadAudioIndex() {
        preferences = context.getSharedPreferences(STORAGE, Context.MODE_PRIVATE);
        return preferences.getInt("audioIndex", -1);//return -1 if no data found
    }

    public void clearCachedAudioPlaylist() {
        preferences = context.getSharedPreferences(STORAGE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.commit();
    }

}
