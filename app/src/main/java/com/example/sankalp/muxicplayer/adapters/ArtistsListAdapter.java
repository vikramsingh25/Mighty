package com.example.sankalp.muxicplayer.adapters;

import android.content.Context;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.provider.MediaStore;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.sankalp.muxicplayer.R;
import com.example.sankalp.muxicplayer.data.SongsInfo;
import com.example.sankalp.muxicplayer.listeners_and_interfaces.MightyOnLongClickListener;

import java.util.Collections;
import java.util.List;

/**
 * Created by sankalp on 9/29/2016.
 */
public class ArtistsListAdapter extends RecyclerView.Adapter<ArtistsListAdapter.ArtistViewHolder> {

    private LayoutInflater inflater;
    Context context;
    List<SongsInfo> artistInfoList = Collections.emptyList();

    public ArtistsListAdapter(Context context, List<SongsInfo> artistInfoList) {
        this.artistInfoList = artistInfoList;
        this.context=context;
    }
    @Override
    public ArtistViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        inflater= LayoutInflater.from(context);
        View view=inflater.inflate(R.layout.song_grid_item,parent,false);
        ArtistViewHolder viewHolder=new ArtistViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ArtistViewHolder holder, int position) {
        SongsInfo currentSongListItem= artistInfoList.get(position);
//        Log.d("TrackListAdapter pos", position+"");
        holder.artistTitle.setText(currentSongListItem.songArtist);
        if (getAlbumart(currentSongListItem.getSongArtist())!=null) {
            holder.songThumbnail.setImageBitmap(BitmapFactory.decodeFile(getAlbumart(currentSongListItem.getSongArtist())));
        }
        holder.setOnArtistLongClickListener(new MightyOnLongClickListener() {
            @Override
            public void onLongClick(int position) {

            }
        });
    }


    public String getAlbumart(String artistName) {
        String path = null;
        Cursor c = context.getContentResolver().query(
                MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Audio.Albums.ALBUM_ART},
                MediaStore.Audio.Artists.ARTIST+ "=?",
                new String[]{artistName},
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
//        Log.d("ArtistsListAdapter list", artistInfoList.size()+"");
        return artistInfoList.size();
    }

    class ArtistViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener,View.OnLongClickListener {
        ImageView songThumbnail;
        TextView artistTitle, songCount;
        MightyOnLongClickListener mightyOnLongClickListener;

        public ArtistViewHolder(View itemView) {
            super(itemView);
            songThumbnail= (ImageView) itemView.findViewById(R.id.grid_song_thumbnail);
            songThumbnail.setOnLongClickListener(this);
            artistTitle= (TextView) itemView.findViewById(R.id.grid_song_title);
            songCount = (TextView) itemView.findViewById(R.id.grid_song_artist);
            songCount.setVisibility(View.GONE);
            itemView.setOnCreateContextMenuListener(this);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
//            menu.setHeaderTitle("Select an option");
//            menu.add(2,0,2,"Play Next");
//            menu.add(2,1,2,"Add to Queue");
//            menu.add(2,2,2,"Add to Playlist");
//            menu.add(2,3,2,"Share");
//            menu.add(2,4,2,"delete");
        }

        public void setOnArtistLongClickListener(MightyOnLongClickListener longClickListener){
            this.mightyOnLongClickListener=longClickListener;
        }
        @Override
        public boolean onLongClick(View v) {
            this.mightyOnLongClickListener.onLongClick(getLayoutPosition());
            return false;
        }
    }
}
