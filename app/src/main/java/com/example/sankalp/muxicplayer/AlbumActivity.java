package com.example.sankalp.muxicplayer;

import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.ListViewCompat;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.sankalp.muxicplayer.adapters.AlbumArtistsPlayListAdapter;
import com.example.sankalp.muxicplayer.adapters.TracksListAdapter;
import com.example.sankalp.muxicplayer.data.SongsInfo;
import com.example.sankalp.muxicplayer.fragments.AlbumSongFragment;
import com.example.sankalp.muxicplayer.fragments.AlbumsFragment;
import com.example.sankalp.muxicplayer.fragments.ArtistSongFragment;
import com.example.sankalp.muxicplayer.fragments.ArtistsFragment;
import com.example.sankalp.muxicplayer.fragments.PlaylistSongFragment;
import com.example.sankalp.muxicplayer.fragments.PlaylistsFragments;
import com.example.sankalp.muxicplayer.mightyDB.MightyContract;
import com.example.sankalp.muxicplayer.mighty_async_tasks.SongsAsyncTask;

import java.util.Collections;
import java.util.List;

public class AlbumActivity extends AppCompatActivity {

    public static final String ALBUM_BUNDLE="album_bundle";
    public static final String ARTIST_BUNDLE="artist_bundle";
    public static final String PLAYLIST_BUNDLE="playlist_bundle";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album);

//        Log.d("AlbumActivityAlbum",getIntent().getStringExtra(AlbumsFragment.ALBUM_KEY));
        Bundle bundle=new Bundle();
        if (getIntent().hasExtra(PlaylistsFragments.PLAYLIST_KEY)) {

            String[] extras = getIntent().getStringArrayExtra(PlaylistsFragments.PLAYLIST_KEY);
            bundle.putStringArray(PLAYLIST_BUNDLE, extras);
            PlaylistSongFragment playlistSongFragment = new PlaylistSongFragment();
            playlistSongFragment.setArguments(bundle);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.album_container, playlistSongFragment)
                    .commit();

        } else if (getIntent().hasExtra(AlbumsFragment.ALBUM_KEY)) {

            bundle.putString(ALBUM_BUNDLE,getIntent().getStringExtra(AlbumsFragment.ALBUM_KEY));
            AlbumSongFragment albumSongFragment=new AlbumSongFragment();
            albumSongFragment.setArguments(bundle);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.album_container,albumSongFragment)
                    .commit();

        }else if (getIntent().hasExtra(ArtistsFragment.ARTIST_KEY)) {

            bundle.putString(ARTIST_BUNDLE,getIntent().getStringExtra(ArtistsFragment.ARTIST_KEY));
            ArtistSongFragment artistSongFragment=new ArtistSongFragment();
            artistSongFragment.setArguments(bundle);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.album_container,artistSongFragment)
                    .commit();
        }
    }
}
