package com.example.sankalp.muxicplayer.adapters;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.sankalp.muxicplayer.R;
import com.example.sankalp.muxicplayer.data.PlaylistInfo;
import com.example.sankalp.muxicplayer.data.SongsInfo;
import com.example.sankalp.muxicplayer.fragments.TracksFragment;
import com.example.sankalp.muxicplayer.listeners_and_interfaces.MightyOnLongClickListener;
import com.example.sankalp.muxicplayer.mightyDB.MightyContract;
import com.example.sankalp.muxicplayer.mightyDB.MightyDbHelper;

import java.util.Collections;
import java.util.List;

/**
 * Created by sankalp on 10/7/2016.
 */
    public class PlayListAdapter extends RecyclerView.Adapter<PlayListAdapter.MyViewHolder> {

        private LayoutInflater inflater;
        private Context context;
        List<PlaylistInfo> data= Collections.emptyList();

        public PlayListAdapter(Context context, List<PlaylistInfo> data) {
            this.context=context;
            this.data=data;
        }

    public void addSinglePlaylist(PlaylistInfo item){
        data.add(item);
        notifyItemInserted(data.size()-1);
    }
    public void addPlaylists(List<PlaylistInfo> items){
        data.clear();
        data.addAll(items);
        notifyItemRangeInserted(data.size()-1,items.size()-1);
//        notifyItemRangeInserted(0,data.size()-1);
    }
    public void clearPlaylists(){
        data.clear();
    }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            inflater = LayoutInflater.from(context);
            View view;
            view = inflater.inflate(R.layout.song_grid_item, parent, false);
            MyViewHolder holder = new MyViewHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            PlaylistInfo current = data.get(position);
            holder.playListTitle.setText(current.getPlaylistName());
            String count=String.valueOf(getSongCountForPlaylists(position));
            holder.playListSongCount.setText(count + " songs");
            holder.setOnPlaylistLongClickListener(new MightyOnLongClickListener() {
                @Override
                public void onLongClick(int position) {

                }
            });
//            holder.imageBtn.setImageResource(current.playlistIconId);
        }

    public int getSongCountForPlaylists(int playlistPosition){
        int songCount=0;
        String selection= MightyContract.PlaylistSongEntry.COLUMN_PLAYLIST_ID + " =? ";
        String[] selectionArgs=new String[]{String.valueOf(playlistPosition)};
        String[] columns=new String[]{MightyContract.PlaylistSongEntry.COLUMN_SONG_ID};
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
        cursor.close();
        dbHelper.close();
        return songCount;
    }

    @Override
        public int getItemCount() {
            return data.size();
        }


    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener,View.OnLongClickListener {
            ImageView playListThumbnail;
            TextView playListTitle,playListSongCount;
            MightyOnLongClickListener mightyOnLongClickListener;

            public MyViewHolder(View itemView) {
                super(itemView);
                    playListThumbnail = (ImageView) itemView.findViewById(R.id.grid_song_thumbnail);
                    playListThumbnail.setOnLongClickListener(this);
                    playListTitle = (TextView) itemView.findViewById(R.id.grid_song_title);
                    playListSongCount = (TextView) itemView.findViewById(R.id.grid_song_artist);
                itemView.setOnLongClickListener(this);
                itemView.setOnCreateContextMenuListener(this);
            }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
//            menu.setHeaderTitle("Select an option");
//            menu.add(3,0,3,"Play Next");
//            menu.add(3,1,3,"Add to Queue");
//            menu.add(3,2,3,"Add to Playlist");
//            menu.add(3,3,3,"Share");
//            menu.add(3,4,3,"delete");
        }

        public void setOnPlaylistLongClickListener(MightyOnLongClickListener longClickListener){
            this.mightyOnLongClickListener=longClickListener;
        }
        @Override
        public boolean onLongClick(View v) {
            this.mightyOnLongClickListener.onLongClick(getLayoutPosition());
            return false;
        }
    }

    }
