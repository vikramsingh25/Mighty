package com.example.sankalp.muxicplayer.adapters;

import android.content.Context;

import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import android.widget.ImageView;
import android.widget.TextView;

import com.example.sankalp.muxicplayer.R;
import com.example.sankalp.muxicplayer.data.SongsInfo;
import com.example.sankalp.muxicplayer.fragments.AlbumSongFragment;
import com.example.sankalp.muxicplayer.fragments.PlaylistSongFragment;
import com.example.sankalp.muxicplayer.fragments.TracksFragment;
import com.example.sankalp.muxicplayer.listeners_and_interfaces.MightyOnLongClickListener;
import com.example.sankalp.muxicplayer.utils.Utilities;

import java.util.List;

/**
 * Created by sankalp on 1/3/2017.
 */
public class AlbumArtistsPlayListAdapter extends RecyclerView.Adapter<AlbumArtistsPlayListAdapter.AlbumArtistsPlayListViewHolder> {

    private LayoutInflater inflater;
    List<SongsInfo> list;
    Context context;

    public AlbumArtistsPlayListAdapter(Context context,List<SongsInfo> list) {
        this.list=list;
        this.context=context;
    }


//    @Override
//    public int getCount() {
//        return list.size();
//    }
//
//    @Override
//    public Object getItem(int position) {
//        return list.get(position);
//    }

    @Override
    public AlbumArtistsPlayListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        inflater= LayoutInflater.from(context);
        View view=inflater.inflate(R.layout.song_list_item,parent,false);
        AlbumArtistsPlayListViewHolder viewHolder=new AlbumArtistsPlayListViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(AlbumArtistsPlayListViewHolder holder, int position) {
        SongsInfo currentSongListItem= list.get(position);
//        Log.d("TrackListAdapter pos", position+"");
        holder.songTitle.setText(currentSongListItem.songTitle);
        holder.songArtist.setText(currentSongListItem.songArtist);
//        if (isPlaylist) {
//            holder.songDuration.setText(String.valueOf(currentSongListItem.songDuration));
//        } else {
//            holder.songDuration.setText(Utilities.milliSecondsToTimer(currentSongListItem.songDuration));
//        }
        holder.songDuration.setText(Utilities.milliSecondsToTimer(currentSongListItem.songDuration));

        holder.setOnAlbumSongClickListener(new MightyOnLongClickListener() {

            @Override
            public void onLongClick(int position) {
                AlbumSongFragment.songPosition=position;
                PlaylistSongFragment.songPosition=position;
            }

        });
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

//    @Override
//    public View getView(int position, View convertView, ViewGroup parent) {
//        View view = LayoutInflater.from(context).inflate(R.layout.song_list_item, parent, false);
//        AlbumArtistsPlayListViewHolder viewHolder=new AlbumArtistsPlayListViewHolder(view);
//
//        SongsInfo songsInfo=list.get(position);
//
//        String title=songsInfo.getSongTitle();
//        viewHolder.songTitle.setText(title);
////
//        String artist=songsInfo.getSongArtist();
//        viewHolder.songArtist.setText(artist);
//
//        long duration=songsInfo.getSongDuration();
//        viewHolder.songDuration.setText(Utilities.milliSecondsToTimer(duration));
//
//        viewHolder.setOnAlbumSongClickListener(new MightyOnLongClickListener() {
//            @Override
//            public void onLongClick(int position) {
//
//            }
//        });
//
//        return view;
//    }

    class AlbumArtistsPlayListViewHolder extends RecyclerView.ViewHolder  implements View.OnLongClickListener,View.OnCreateContextMenuListener {
        ImageView songThumbnail;
        TextView songTitle,songArtist,songDuration;
        MightyOnLongClickListener longClickListener;

        public AlbumArtistsPlayListViewHolder(View itemView) {
            super(itemView);
            songThumbnail= (ImageView) itemView.findViewById(R.id.list_song_thumbnail);
            songThumbnail.setOnLongClickListener(this);
            songTitle= (TextView) itemView.findViewById(R.id.list_song_title);
            songArtist= (TextView) itemView.findViewById(R.id.list_song_artist);
            songDuration= (TextView) itemView.findViewById(R.id.list_song_duration);
            itemView.setOnLongClickListener(this);
            itemView.setOnCreateContextMenuListener(this);
        }

        public void setOnAlbumSongClickListener(MightyOnLongClickListener albumLongClickListener){
            this.longClickListener =albumLongClickListener;
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            menu.setHeaderTitle("Select an option");
            menu.add(1,0,0,"Play Next");
            menu.add(1,1,0,"Add to Queue");
            menu.add(1,2,0,"Add to Playlist");
            menu.add(1,3,0,"Share");
            menu.add(1,4,0,"Delete");
        }

        @Override
        public boolean onLongClick(View v) {
            this.longClickListener.onLongClick(getLayoutPosition());
            return false;
        }
    }
}
