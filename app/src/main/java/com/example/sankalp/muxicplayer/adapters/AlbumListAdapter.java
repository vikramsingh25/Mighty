package com.example.sankalp.muxicplayer.adapters;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.provider.MediaStore;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
//import android.widget.Toast;

import com.example.sankalp.muxicplayer.AlbumActivity;
import com.example.sankalp.muxicplayer.R;
import com.example.sankalp.muxicplayer.data.SongsInfo;
import com.example.sankalp.muxicplayer.fragments.AlbumsFragment;
import com.example.sankalp.muxicplayer.listeners_and_interfaces.MightyOnLongClickListener;

import java.util.Collections;
import java.util.List;

/**
 * Created by sankalp on 9/29/2016.
 */
public class AlbumListAdapter extends RecyclerView.Adapter<AlbumListAdapter.AlbumViewHolder> {

    private LayoutInflater inflater;
    Context context;
    List<SongsInfo> albumInfoList= Collections.emptyList();

    public AlbumListAdapter(Context context, List<SongsInfo> albumInfoList) {
        this.albumInfoList=albumInfoList;
        this.context=context;
    }
    @Override
    public AlbumViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        inflater= LayoutInflater.from(context);
        View view=inflater.inflate(R.layout.song_grid_item,parent,false);
        AlbumViewHolder viewHolder=new AlbumViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(AlbumViewHolder holder, int position) {
        SongsInfo currentSongListItem=albumInfoList.get(position);
//        Log.d("AlbumListAdapter pos", position+"");
        holder.albumTitle.setText(currentSongListItem.songAlbum);
        holder.albumArtist.setText(currentSongListItem.songArtist);
        if (getAlbumart(currentSongListItem.getSongAlbum())!=null) {
            holder.albumThumbnail.setImageBitmap(BitmapFactory.decodeFile(getAlbumart(currentSongListItem.getSongAlbum())));
        }
        holder.setOnAlbumClickListener(new AlbumsFragment.MightyOnLongClickListener() {
            @Override
            public void onLongClick(int position) {

            }
        });
    }

    public String getAlbumart(String albumName) {
        String path = null;
        Cursor c = context.getContentResolver().query(
                MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Audio.Albums.ALBUM_ART},
                MediaStore.Audio.Albums.ALBUM+ "=?",
                new String[]{albumName},
                null);

        if (c != null)
        {
            if (c.moveToFirst())
            {
                path = c.getString(0);
            }
            c.close();

        }

        return path;
    }


    @Override
    public int getItemCount() {
//        Log.d("AlbumListAdapter list",albumInfoList.size()+"");
        return albumInfoList.size();
    }

    class AlbumViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener,View.OnCreateContextMenuListener {
        ImageView albumThumbnail;

        TextView albumTitle, albumArtist;
        AlbumsFragment.MightyOnLongClickListener longClickListener;
//        ImageButton menuButton;

        public AlbumViewHolder(View itemView) {
            super(itemView);
            albumThumbnail = (ImageView) itemView.findViewById(R.id.grid_song_thumbnail);
            albumThumbnail.setOnLongClickListener(this);
//            albumThumbnail.setOnCreateContextMenuListener(this);

            albumTitle = (TextView) itemView.findViewById(R.id.grid_song_title);
            albumArtist = (TextView) itemView.findViewById(R.id.grid_song_artist);
            itemView.setOnLongClickListener(this);
            itemView.setOnCreateContextMenuListener(this);
        }

        public void setOnAlbumClickListener(AlbumsFragment.MightyOnLongClickListener albumLongClickListener){
            this.longClickListener =albumLongClickListener;
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
//            menu.setHeaderTitle("Select an option");
//            menu.add(1,0,1,"Shuffle");
//            menu.add(1,1,1,"Play Next");
//            menu.add(1,2,1,"Add to Queue");
//            menu.add(1,3,1,"Add to Playlist");
//            menu.add(1,4,1,"Add to Favorites");
//            menu.add(1,5,1,"Delete");
        }

        @Override
        public boolean onLongClick(View v) {
            this.longClickListener.onLongClick(getLayoutPosition());
            return false;
        }
    }

}
