package com.example.sankalp.muxicplayer.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
//import android.widget.Toast;

import com.example.sankalp.muxicplayer.AlbumActivity;
import com.example.sankalp.muxicplayer.R;
import com.example.sankalp.muxicplayer.adapters.PlayListAdapter;
import com.example.sankalp.muxicplayer.data.PlaylistInfo;
import com.example.sankalp.muxicplayer.dialogs.CreatePlaylistDialog;
import com.example.sankalp.muxicplayer.listeners_and_interfaces.MightyOnClickListener;
import com.example.sankalp.muxicplayer.listeners_and_interfaces.RecycleViewTouchListener;
import com.example.sankalp.muxicplayer.loaders.PlaylistLoader;
import com.example.sankalp.muxicplayer.mighty_async_tasks.PlaylistAsyncTasks;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sankalp on 9/18/2016.
 */
    public class PlaylistsFragments extends Fragment implements LoaderManager.LoaderCallbacks<List<PlaylistInfo>>{

        Button playlistButton;
        public RecyclerView playlistRecyclerView;
    private static final int PLAYLIST_LOADER=0;
    public static final String PLAYLIST_KEY="playlist_key";
    private static final String LOG_TAG=PlaylistsFragments.class.getSimpleName();
     public PlayListAdapter adapter;


        @Nullable
        @Override
        public View onCreateView(final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable final Bundle savedInstanceState) {
            View view=inflater.inflate(R.layout.fragment_playlist,container,false);
            playlistButton= (Button) view.findViewById(R.id.btn_create_playlist);
            playlistRecyclerView= (RecyclerView) view.findViewById(R.id.playlist_recycler_view);
            adapter=new PlayListAdapter(getContext(),new ArrayList<PlaylistInfo>());
            playlistRecyclerView.setAdapter(adapter);
            playlistRecyclerView.setLayoutManager(new GridLayoutManager(getContext(),2));
            playlistRecyclerView.addOnItemTouchListener(new RecycleViewTouchListener(getContext(), playlistRecyclerView, new MightyOnClickListener() {
                @Override
                public void onClick(View view, int position) {
                    String playlist=PlaylistLoader.list.get(position).getPlaylistName();
                    String description=PlaylistLoader.list.get(position).getGetPlaylistDescription();
                    Intent intent=new Intent(getContext(), AlbumActivity.class);
                    intent.putExtra(PLAYLIST_KEY,new String[]{playlist,description,String.valueOf(position)});
                    startActivity(intent);
                }
            }));


            playlistButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    FragmentTransaction ft=getFragmentManager().beginTransaction();
                    CreatePlaylistDialog dialog=new CreatePlaylistDialog();
                    dialog.show(ft,"CreatePlaylistDialog");
                }
            });
            return view;
        }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 0:
//                Toast.makeText(getContext(),"play_next",Toast.LENGTH_LONG).show();
                break;
            case 1:
//                Toast.makeText(getContext(),"add_to_queue",Toast.LENGTH_LONG).show();
                break;
            case 2:
//                Toast.makeText(getContext(),"add_to_playlist",Toast.LENGTH_LONG).show();
                break;
            case 3:
//                Toast.makeText(getContext(),"share",Toast.LENGTH_LONG).show();
                break;
            case 4:
//                Toast.makeText(getContext(),"delete",Toast.LENGTH_LONG).show();
                break;
        }
        return super.onContextItemSelected(item);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(PLAYLIST_LOADER,null,this);
    }

    @Override
    public void onResume() {
        super.onResume();
    }



    @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
        }

    @Override
    public Loader<List<PlaylistInfo>> onCreateLoader(int id, Bundle args) {
        return new PlaylistLoader(getContext());
    }

    @Override
    public void onLoadFinished(Loader<List<PlaylistInfo>> loader, List<PlaylistInfo> data) {
       adapter.addPlaylists(data);
    }

    @Override
    public void onLoaderReset(Loader<List<PlaylistInfo>> loader) {
        adapter.clearPlaylists();
    }
}
