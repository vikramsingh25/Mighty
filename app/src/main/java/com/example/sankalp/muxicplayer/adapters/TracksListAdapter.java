package com.example.sankalp.muxicplayer.adapters;

import android.content.Context;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.sankalp.muxicplayer.MainActivity;
import com.example.sankalp.muxicplayer.R;
import com.example.sankalp.muxicplayer.fragments.PlayScreenFragment;
import com.example.sankalp.muxicplayer.fragments.PlayingQueueFragment;
import com.example.sankalp.muxicplayer.fragments.TracksFragment;
import com.example.sankalp.muxicplayer.listeners_and_interfaces.MightyOnLongClickListener;
import com.example.sankalp.muxicplayer.mightyDB.MightySongProvider;
import com.example.sankalp.muxicplayer.utils.StorageUtils;
import com.example.sankalp.muxicplayer.utils.Utilities;
import com.example.sankalp.muxicplayer.data.SongsInfo;

import java.text.StringCharacterIterator;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Created by sankalp on 9/29/2016.
 */
public class TracksListAdapter extends RecyclerView.Adapter<TracksListAdapter.TrackViewHolder> {

    private LayoutInflater inflater;
    private Context context;
//    static int i=0;
    List<SongsInfo> trackInfoList = Collections.emptyList();

    public TracksListAdapter(Context context, List<SongsInfo> trackInfoList) {
        this.trackInfoList =trackInfoList;
        this.context=context;
//        getThumbnails();
    }
//    private  void getThumbnails(){
//        for (int i=0;i<trackInfoList.size();i++) {
//            trackInfoList.get(i).setSongThumbnail(getAlbumart(trackInfoList.get(i).getAlbumId()));
//        }
//    }
    public TracksListAdapter(){}


    public void addSingleSong(SongsInfo item){
        trackInfoList.add(item);
        notifyItemInserted(trackInfoList.size()-1);
    }
    public void removeSingleSong(int position){

        if (trackInfoList!=null) {
            trackInfoList.remove(position);
        }
        notifyDataSetChanged();
    }
    public void removeSongs(List<SongsInfo> items){
        trackInfoList.removeAll(items);
        notifyItemRangeRemoved(trackInfoList.size()-1,items.size()-1);
    }
    public void addSongs(List<SongsInfo> items){
        trackInfoList.clear();
        trackInfoList.addAll(items);
//        Log.d("RangeInserted",String.valueOf(trackInfoList.size()-1)+":"+String.valueOf(items.size()-1));
//        notifyDataSetChanged();
        notifyItemRangeInserted(trackInfoList.size()-1,items.size()-1);
//        notifyItemRangeInserted(0,data.size()-1);
    }
    public void clearSongs(){
        trackInfoList.clear();
    }

    @Override
    public TrackViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//        Log.d("TrackList","onCreateViewHolder: "+String.valueOf(i));
        inflater= LayoutInflater.from(context);
        View view=inflater.inflate(R.layout.song_list_item,parent,false);
        TrackViewHolder viewHolder=new TrackViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder( final TrackViewHolder holder, int position) {
        Log.d("TrackList","onBindViewHolder");
        final SongsInfo currentSongListItem= trackInfoList.get(position);

//        String albumArt=getAlbumart(currentSongListItem.getAlbumId());
//            if (albumArt!=null) {
//                holder.songThumbnail.setImageBitmap(BitmapFactory.decodeFile(albumArt));
//            }
        holder.songTitle.setText(currentSongListItem.songTitle);
        holder.songArtist.setText(currentSongListItem.songArtist);
        holder.songDuration.setText(Utilities.milliSecondsToTimer(currentSongListItem.songDuration));
        holder.setOnTracksClickListener(new TracksFragment.MightyOnLongClickListener() {

            @Override
            public void onLongClick(int position) {
                TracksFragment.songPosition=position;
            }

        });
    }


    public String getAlbumart(long albumId) {
        String path = null;
        Cursor c = context.getContentResolver().query(
                MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Audio.Albums.ALBUM_ART},
                MediaStore.Audio.Albums._ID+ "=?",
                new String[]{Long.toString(albumId)},
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
//        Log.d("AlbumListAdapter list", trackInfoList.size()+"");
        return trackInfoList.size();
    }



    class TrackViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener,View.OnCreateContextMenuListener {
        ImageView songThumbnail;
        TextView songTitle,songArtist,songDuration;
        TracksFragment.MightyOnLongClickListener mightyOnLongClickListener;

        public TrackViewHolder(View itemView) {
            super(itemView);
            songThumbnail= (ImageView) itemView.findViewById(R.id.list_song_thumbnail);
            songTitle= (TextView) itemView.findViewById(R.id.list_song_title);
            songArtist= (TextView) itemView.findViewById(R.id.list_song_artist);
            songDuration= (TextView) itemView.findViewById(R.id.list_song_duration);
            itemView.setOnLongClickListener(this);
            itemView.setOnCreateContextMenuListener(this);
        }

        public void setOnTracksClickListener(TracksFragment.MightyOnLongClickListener tracksLongClickListener){
            this.mightyOnLongClickListener =tracksLongClickListener;
        }

        @Override
        public boolean onLongClick(View v) {
            this.mightyOnLongClickListener.onLongClick(getLayoutPosition());
            return false;
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            menu.setHeaderTitle("Select an option");
            menu.add(0,0,0,"Play Next");
            menu.add(0,1,0,"Add to Queue");
            menu.add(0,2,0,"Add to Playlist");
            menu.add(0,3,0,"Share");
            menu.add(0,4,0,"delete");
        }
    }
}
