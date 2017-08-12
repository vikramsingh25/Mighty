package com.example.sankalp.muxicplayer.adapters;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.sankalp.muxicplayer.R;
import com.example.sankalp.muxicplayer.data.PlaylistInfo;
import com.example.sankalp.muxicplayer.mightyDB.MightyContract;
import com.example.sankalp.muxicplayer.mightyDB.MightyDbHelper;

import java.util.List;

/**
 * Created by sankalp on 2/26/2017.
 */
public class AddToPlaylistAdaper extends BaseAdapter{

    Context context;
    TextView playListTitle,playListSongCount;
    List<PlaylistInfo> list;

    public AddToPlaylistAdaper(Context context, List<PlaylistInfo> list){
        this.context=context;
        this.list=list;
    }

//    public void addSinglePlaylist(PlaylistInfo item){
//        list.add(item);
//        notifyDataSetChanged();
//    }
//    public void addPlaylists(List<PlaylistInfo> items){
//        list.clear();
//        list.addAll(items);
//        notifyDataSetChanged();
//    }
//    public void clearPlaylists(){
//        list.clear();
//    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater= (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view=inflater.inflate(R.layout.song_list_item,parent,false);
        PlaylistInfo current=list.get(position);
        playListTitle = (TextView) view.findViewById(R.id.list_song_title);
        playListTitle.setText(current.getPlaylistName());
        playListSongCount = (TextView) view.findViewById(R.id.list_song_artist);
        String count=String.valueOf(getSongCountForPlaylists(position));
        playListSongCount.setText(count + " songs");
        return view;
    }

    public int getSongCountForPlaylists(int playlistPosition){
        int songCount=0;
        String selection= MightyContract.PlaylistSongEntry.COLUMN_PLAYLIST_ID + " =? ";
        String[] selectionArgs=new String[]{String.valueOf(playlistPosition)};
        String[] columns=new String[]{MightyContract.PlaylistSongEntry.COLUMN_SONG_ID};

//        Cursor cursor=context.getContentResolver().query(MightyContract.PlaylistSongEntry.CONTENT_URI
//        ,columns
//        ,selection
//        ,selectionArgs
//        ,null);

        MightyDbHelper dbHelper=new MightyDbHelper(context);
        Cursor cursor=dbHelper.getReadableDatabase().query(MightyContract.PlaylistSongEntry.TABLE_NAME
        ,columns
        ,selection
        ,selectionArgs
        ,null
        ,null
        ,null);

        if (cursor != null && cursor.moveToFirst()) {
            songCount = cursor.getCount();
        } else {
            songCount=0;
        }
        if (cursor!=null) {
            cursor.close();
        }
        dbHelper.close();
        return songCount;
    }

}
