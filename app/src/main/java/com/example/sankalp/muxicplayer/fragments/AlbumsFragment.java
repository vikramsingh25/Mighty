package com.example.sankalp.muxicplayer.fragments;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
//import android.widget.Toast;

import com.example.sankalp.muxicplayer.AlbumActivity;
import com.example.sankalp.muxicplayer.R;
import com.example.sankalp.muxicplayer.adapters.AlbumListAdapter;
import com.example.sankalp.muxicplayer.adapters.TracksListAdapter;
import com.example.sankalp.muxicplayer.data.SongsInfo;
import com.example.sankalp.muxicplayer.listeners_and_interfaces.MightyOnClickListener;
import com.example.sankalp.muxicplayer.listeners_and_interfaces.RecycleViewTouchListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sankalp on 9/18/2016.
 */
public class AlbumsFragment extends Fragment {
    private RecyclerView albumRecyclerView;
    private AlbumListAdapter adapter;
    private Bundle songBundle;
    private List<SongsInfo> list;

    public static final String ALBUM_KEY="album_key";


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_albums,container,false);
        albumRecyclerView= (RecyclerView) view.findViewById(R.id.album_recycler_view);
        songBundle = new Bundle();
        list = new ArrayList<>();
        list=loadAudio();
//        Log.d("AlbumsFragmentLIST",String.valueOf(list.size()));
        adapter=new AlbumListAdapter(getContext(),list);
        albumRecyclerView.setAdapter(adapter);
        albumRecyclerView.setLayoutManager(new GridLayoutManager(getContext(),2));
        registerForContextMenu(albumRecyclerView);
        albumRecyclerView.addOnItemTouchListener(new RecycleViewTouchListener(getContext(), albumRecyclerView, new MightyOnClickListener() {
            @Override
            public void onClick(View view, int position) {

                String album=list.get(position).getSongAlbum();
//                Toast.makeText(getContext(),"clicked ALbum "+position,Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getContext(),AlbumActivity.class).putExtra(ALBUM_KEY,album));
            }
        }));
        return view;
    }

    public interface MightyOnLongClickListener {
        void onLongClick(int position);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case 0:
//                Toast.makeText(getContext(),"shuffle",Toast.LENGTH_LONG).show();
////                new AlbumSongFragment().shufffleSongs();
//                break;
//            case 1:
//                Toast.makeText(getContext(),"play_next",Toast.LENGTH_LONG).show();
//                break;
//            case 2:
//                Toast.makeText(getContext(),"add_to_queue",Toast.LENGTH_LONG).show();
//                break;
//            case 3:
//                Toast.makeText(getContext(),"add_to_playlist",Toast.LENGTH_LONG).show();
////                                    isPlaylistDialog=1;
////                                    showAddToPlaylistDialog();
//                break;
//            case 4:
//                Toast.makeText(getContext(),"add_to_favorites",Toast.LENGTH_LONG).show();
//                break;
//            case 5:
//                Toast.makeText(getContext(),"delete",Toast.LENGTH_LONG).show();
//                break;
//        }
//        return true;
        return false;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public List<SongsInfo> loadAudio(){
        List<SongsInfo> audioList=new ArrayList<>();
        ContentResolver contentResolver=getActivity().getContentResolver();
        Uri songUri= MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String groupby=MediaStore.Audio.Media.ALBUM;
        String[] projection=new String[]{MediaStore.Audio.Media.ALBUM,MediaStore.Audio.Media.ARTIST};
        String selection=MediaStore.Audio.Media.IS_MUSIC + "!=0 ) GROUP BY (" +groupby;
        String sortOrder=MediaStore.Audio.Media.ALBUM + " ASC ";
        Cursor cursor=contentResolver.query(songUri,projection,selection,null,sortOrder);
//        Log.d("AlbumsFragment",cursor.toString()+" : "+ cursor.getCount());
        if (cursor!=null && cursor.getCount()>0) {
            while (cursor.moveToNext()) {
                String songAlbum = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM));
                String songArtist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
                // Save to audioList
                audioList.add(new SongsInfo(songAlbum,songArtist));
            }
        }
        if (cursor!=null) {
            cursor.close();
        }
        return audioList;
    }


//    class AlbumRecycleViewTouchListener implements RecyclerView.OnItemTouchListener{
//
//        private GestureDetectorCompat gestureDetector;
//        private OnAlbumClickListener onAlbumClickListener;
//
//        //CONSTRUCTOR
//        public AlbumRecycleViewTouchListener(Context context,final RecyclerView recyclerView,final OnAlbumClickListener onTracksClickListener)
//        {
//
//            this.onAlbumClickListener=onTracksClickListener;
//
//            gestureDetector=new GestureDetectorCompat(context,new GestureDetector.SimpleOnGestureListener(){
//
//                @Override
//                public boolean onSingleTapUp(MotionEvent e) {
//                    return true;
//                }
//
//                @Override
//                public void onLongPress(MotionEvent e) {
//                    super.onLongPress(e);
////                    View child=recyclerView.findChildViewUnder(e.getX(),e.getY());
////                    if (child!=null && onAlbumClickListener!=null) {
////                        onAlbumClickListener.onLongClick(child,recyclerView.getChildPosition(child));
////                    }
//                }
//
//            });
//
//        }
//
//        @Override
//        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
//            View child=rv.findChildViewUnder(e.getX(),e.getY());
//            if (child!=null && onAlbumClickListener!=null && gestureDetector.onTouchEvent(e)) {
//                onAlbumClickListener.onClick(child,rv.getChildPosition(child));
//            }
//
//            return false;
//        }
//
//        @Override
//        public void onTouchEvent(RecyclerView rv, MotionEvent e) {
//
//        }
//
//        @Override
//        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
//
//        }
//
//
//    }
//
//
//    public interface OnAlbumClickListener{
//         void onClick(View view,int position);
//         void onLongClick(View view, int position);
//    }









//    public Bitmap getsongThumbnail(Uri uri){
//        MediaMetadataRetriever mediaMetadataRetriever=new MediaMetadataRetriever();
//        byte[] rawArt;
//        Bitmap art;
//        BitmapFactory.Options options=new BitmapFactory.Options();
//        mediaMetadataRetriever.setDataSource(getContext(),uri);
//        rawArt=mediaMetadataRetriever.getEmbeddedPicture();
//
//        if (rawArt != null) {
//            return BitmapFactory.decodeByteArray(rawArt, 0, rawArt.length, options);
//        } else {
//            return null;
//        }
//    }
//
//    public String getAlbumart(Context context, Long album_id) {
//        String path = null;
//        Cursor c = context.getContentResolver().query(
//                MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
//                new String[]{MediaStore.Audio.Albums.ALBUM_ART},
//                MediaStore.Audio.Albums._ID + "=?",
//                new String[]{Long.toString(album_id)},
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
}
