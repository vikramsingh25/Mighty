package com.example.sankalp.muxicplayer;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.IBinder;
import android.speech.RecognizerIntent;
import android.support.annotation.NonNull;
import android.support.multidex.MultiDex;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
//import android.widget.Toast;

import com.example.sankalp.muxicplayer.fragments.CurrentSongFragment;
import com.example.sankalp.muxicplayer.services.MightyPlayerService;
import com.example.sankalp.muxicplayer.services.ShakeService;
import com.example.sankalp.muxicplayer.utils.SlidingTabLayout;
import com.example.sankalp.muxicplayer.utils.StorageUtils;
import com.example.sankalp.muxicplayer.data.SongsInfo;
import com.example.sankalp.muxicplayer.fragments.AlbumsFragment;
import com.example.sankalp.muxicplayer.fragments.ArtistsFragment;
import com.example.sankalp.muxicplayer.fragments.NavigationFragment;
import com.example.sankalp.muxicplayer.fragments.PlaylistsFragments;
import com.example.sankalp.muxicplayer.fragments.TracksFragment;
import com.example.sankalp.muxicplayer.utils.Utilities;

import java.util.ArrayList;
import java.util.Locale;


public class MainActivity extends AppCompatActivity {

    private Toolbar mMainToolbar;
    public MightyPlayerService playerService;
    public static boolean isPlayerServiceBound = false;
    private SlidingTabLayout mTabLayout;
    private static FrameLayout containerOfFragment;
    private static final String MY_PREFS = "com.example.sankalp.muxicplayer.STORAGE";
    private ViewPager mViewPager;
    public static Context mContext;
    private final int REQ_CODE_SPEECH_INPUT = 100;

//    private SensorManager mSensorManager;
//    private Sensor mAccelerometer;
//    private ShakeDetector mShakeDetector;

    //List of available Audio files
//    private List<SongsInfo> audioList;

    public ShakeService shakeService;
    public boolean isShakeServiceBound = false;

    private String title,artist,duration,thumbnail;

    private int audioIndex = -1;
    private SongsInfo activeAudio; //an object of the currently playing audio
    private static final int READ_EXT_CONTENT_REQUEST_CODE = 200;
    private static final int WRITE_EXT_CONTENT_REQUEST_CODE = 300;
    private static final int RECORD_AUDIO_REQUEST_CODE = 400;

    public static final String Broadcast_PLAY_NEW_AUDIO = "com.example.sankalp.muxicplayer.playNewAudio";


    public ServiceConnection playerServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
//            Log.d("MA playerServiceConnection","in onServiceConnected()");
            MightyPlayerService.LocalBinder binder = (MightyPlayerService.LocalBinder) service;
            playerService = binder.getService();
            isPlayerServiceBound = true;
//            Toast.makeText(mContext, "isPlayerServiceBound : " + isPlayerServiceBound, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
//            Log.d("MAIN ACTIVITY","onServiceDisconnected()");
            isPlayerServiceBound = false;
        }
    };


//    public ServiceConnection shakeServiceConnection = new ServiceConnection() {
//        @Override
//        public void onServiceConnected(ComponentName name, IBinder service) {
////            Log.d("MA serviceConnection","in onServiceConnected()");
//            ShakeService.LocalBinder binder = (ShakeService.LocalBinder) service;
//            shakeService = binder.getService();
//            isShakeServiceBound= true;
//            Toast.makeText(mContext, "isServiceBound : " + isShakeServiceBound, Toast.LENGTH_SHORT).show();
//        }
//
//        @Override
//        public void onServiceDisconnected(ComponentName name) {
////            Log.d("MAIN ACTIVITY","onServiceDisconnected()");
//            isShakeServiceBound= false;
//        }
//    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (Build.VERSION.SDK_INT < 21) {
//            Log.d("MAIN ACTIVITY", "Version < 21");
            MultiDex.install(this);
        }
        super.onCreate(savedInstanceState);
        mContext=getApplicationContext();

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                //change this to show a dialog box explaining why we need this permission, and try to re-request the permission
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        READ_EXT_CONTENT_REQUEST_CODE);
            }
        }
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        WRITE_EXT_CONTENT_REQUEST_CODE);
            }
        }
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.RECORD_AUDIO)) {
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.RECORD_AUDIO},
                        RECORD_AUDIO_REQUEST_CODE);
            }
        }

//        audioList = new ArrayList<>();

        setContentView(R.layout.activity_main);

        mMainToolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(mMainToolbar);

        NavigationFragment navigationFragment = (NavigationFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_drawer_fragment);
        navigationFragment.setUp((DrawerLayout) findViewById(R.id.nav_drawer_layout), mMainToolbar);


        mTabLayout = (SlidingTabLayout) findViewById(R.id.tabLayout);
        mViewPager = (ViewPager) findViewById(R.id.pager);
        containerOfFragment = (FrameLayout) findViewById(R.id.current_song_fragment_container);
//        if (containerOfFragment!=null) {
//            containerOfFragment.setVisibility(View.VISIBLE);
//        }

//        containerOfFragment= (FrameLayout) findViewById(R.id.current_song_fragment_container);

        getSupportFragmentManager().beginTransaction()
                .add(R.id.current_song_fragment_container, new CurrentSongFragment())
                .commit();
        if (isPlayerServiceBound) {
            CurrentSongFragment.currentPlaybackStatus = MightyPlayerService.PlaybackStatus.PLAYING;
            if (MainActivity.containerOfFragment != null) {
                MainActivity.containerOfFragment.setVisibility(View.VISIBLE);
            }
        }
    }

    public void setPlayBar(String title, String artist, String thumbnail) {
//        Log.d("MainActivity set",title+":"+artist);

        CurrentSongFragment.currentSongTitle.setText(title);
        CurrentSongFragment.currentSongArtist.setText(artist);
        if (thumbnail!=null) {
            CurrentSongFragment.currentSongThumbnail.setImageBitmap(BitmapFactory.decodeFile(thumbnail));
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {

            mViewPager.setAdapter(new PagerAdapter(getSupportFragmentManager()));
            mTabLayout.setViewPager(mViewPager);

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case READ_EXT_CONTENT_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //Permission granted do the work now
                    mViewPager.setAdapter(new PagerAdapter(getSupportFragmentManager()));
                    mTabLayout.setViewPager(mViewPager);
                } else {
                    //Permission denied
                }
        }
    }

    public void playAudio(int audioIndex, String title, String artist, String duration, String thumbnail) {
//        Log.d("MainActivity playAudio",String.valueOf(audioIndex));
//        mContext = context;
        this.title=title;
        this.artist=artist;
        this.duration=duration;
        this.thumbnail=thumbnail;
        CurrentSongFragment.currentPlaybackStatus = MightyPlayerService.PlaybackStatus.PLAYING;
        if (MainActivity.containerOfFragment != null) {
            MainActivity.containerOfFragment.setVisibility(View.VISIBLE);
        }
        setPlayBar(title, artist,thumbnail);
        new PlayingScreenActivity().setSongCredentials(title,artist,duration,thumbnail);
        //Check is service is active
        if (!isPlayerServiceBound) {
            //Store Serializable audioList to SharedPreferences
            try {
                StorageUtils storage = new StorageUtils(mContext);
                storage.storeAudioIndex(audioIndex);
//                ContentValues values=new ContentValues();
//                values.put(MightyContract.SongEntry.COLUMN_IS_CURRENT,1);
//
//                int updatedRow=context.getContentResolver().update(MightyContract.SongEntry.CONTENT_URI,values
//                        , MightyContract.SongEntry._ID + " = ?",new String[]{String.valueOf(audioIndex)});
//                Log.d("MainActivity playAudio",String.valueOf(updatedRow));
//                Log.d("PLAY_AUDIO "," IF PART");
                Intent playerIntent = new Intent(mContext, MightyPlayerService.class);
                mContext.startService(playerIntent);
//                Log.d("PLAY_AUDIO IF PART","after start service()");
                mContext.bindService(playerIntent, playerServiceConnection, Context.BIND_AUTO_CREATE);
//                new PlayScreenFragment().updateProgressBar();
//                isPlayerServiceBound=true;
//                Log.d("PLAY_AUDIO IF PART","after bindService()");

            } catch (NullPointerException e) {
//                Log.d("PLAY_AUDIO IF PART","NULL OCCURRED");
                e.printStackTrace();
            }
        } else {
            try {
//                currentSongFragment.setPlaybackStatus(MightyPlayerService.PlaybackStatus.PLAYING);
                CurrentSongFragment.play_pause.setImageResource(R.drawable.pause_light);
//                Log.d("PLAY_AUDIO "," ELSE PART");
                //Store the new audioIndex to SharedPreferences
                StorageUtils storage = new StorageUtils(mContext);
                storage.storeAudioIndex(audioIndex);
//                storeAudioIndex(audioIndex);

                //Service is active
                //Send a broadcast to the service -> PLAY_NEW_AUDIO
                Intent broadcastIntent = new Intent(Broadcast_PLAY_NEW_AUDIO);
                mContext.sendBroadcast(broadcastIntent);
//                new PlayScreenFragment().updateProgressBar();

            } catch (NullPointerException e) {
//                Log.d("PLAYAUDIO ELSE PART","NULL OCCURRED");
                e.printStackTrace();
            }

        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("playerServiceState", isPlayerServiceBound);
        outState.putBoolean("shakeServiceState", isShakeServiceBound);
    }


    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        isPlayerServiceBound = savedInstanceState.getBoolean("playerServiceState");
        isShakeServiceBound=savedInstanceState.getBoolean("shakeServiceState");
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
//        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        mSensorManager.unregisterListener(mShakeDetector);
//        SplashActivity.tts.stop();
//        SplashActivity.tts.shutdown();
//        if (isShakeServiceBound) {
//
//            if (shakeServiceConnection != null) {
//                unbindService(shakeServiceConnection);
//                isShakeServiceBound = false;
//            }
//
//            if (shakeService != null) {
//                shakeService.stopSelf();
//            }
//
//        }

        if (isPlayerServiceBound) {

            if (playerServiceConnection != null) {
                mContext.unbindService(playerServiceConnection);
                isPlayerServiceBound = false;
            }

            if (playerService != null) {
                playerService.stopSelf();
            }

        }

    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    public String searchString;
    SearchView search;
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);

//        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
//            SearchManager manager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
//                search = (SearchView) menu.findItem(R.id.search).getActionView();
//            Log.d("Search",String.valueOf(search));
//            search.setSearchableInfo(manager.getSearchableInfo(getComponentName()));
//            search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//                @Override
//                public boolean onQueryTextSubmit(String query) {
//                    searchString=query;
//                    return false;
//                }
//
//                @Override
//                public boolean onQueryTextChange(String newText) {
//                    searchString=newText;
//                    return false;
//                }
//            });
//        }
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

//        if (id==R.id.actionShuffle) {
//            new TracksFragment().shuffleSongs(this);
//        }
//        if (id == R.id.actionSearch) {
//            Toast.makeText(getApplicationContext(), "search", Toast.LENGTH_SHORT).show();
//        }
//        if (id == R.id.actionVoice) {
////            promptSpeechInput();
////            Toast.makeText(getApplicationContext(), "voice", Toast.LENGTH_SHORT).show();
//        }
        return super.onOptionsItemSelected(item);
    }


//    private void promptSpeechInput() {
//        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
//        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
//        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
//        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, R.string.speech_prompt);
////        if (MightyPlayerService.mediaPlayer.isPlaying()) {
////            MightyPlayerService.mediaPlayer.setVolume(0.1f,0.1f);
////        }
//        try {
//            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
//        } catch (ActivityNotFoundException e) {
//            Toast.makeText(getApplicationContext(),
//                    getString(R.string.speech_not_supported),
//                    Toast.LENGTH_SHORT).show();
//        }
//    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        switch (requestCode) {
//            case REQ_CODE_SPEECH_INPUT:{
//                if (resultCode == RESULT_OK &&data != null) {
//                    ArrayList<String> result = data
//                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
//                    switch (result.get(0)) {
//                        case "play":
//                        case "play now":
//                        case "play it":
//                        case "resume":
//                            if (CurrentSongFragment.currentPlaybackStatus==MightyPlayerService.PlaybackStatus.PAUSED) {
//                                MightyPlayerService.transportControls.play();
//                            }
//                            break;
//                        case "pause":
//                        case "pause song":
//                        case "pause now":
//                        case "pause it":
//                            if (CurrentSongFragment.currentPlaybackStatus==MightyPlayerService.PlaybackStatus.PLAYING) {
//                                MightyPlayerService.transportControls.pause();
//                            }
//                            break;
//                        case "play next":
//                        case "next":
//                        case "next song":
//                        case "play next song":
//                            MightyPlayerService.transportControls.skipToNext();
//                            break;
//                        case "play previous":
//                        case "previous":
//                        case "previous song":
//                        case "play previous song":
//                            MightyPlayerService.transportControls.skipToPrevious();
//                            break;
//                        case "stop":
//                        case "stop it":
//                        case "stop playing":
//                            if (CurrentSongFragment.currentPlaybackStatus==MightyPlayerService.PlaybackStatus.PLAYING) {
//                                MightyPlayerService.transportControls.stop();
//                            }
//                            break;
//                        case "shuffle":
//                        case "shuffle all":
//                        case "shuffle songs":
//                            Toast.makeText(getApplicationContext(), "shuffle all", Toast.LENGTH_SHORT).show();
//                            break;
//                        default:
//                            String[] words=new Utilities().splitString(result.get(0));
//                            if (words[0].equalsIgnoreCase("play")) {
//                                if (words[1].equalsIgnoreCase("song")) {
//                                    searchForSongAndPlay(words[2]);
//                                } else if (words[1].equalsIgnoreCase("album") || words[1].equalsIgnoreCase("artist") || words[1].equalsIgnoreCase("playlist")) {
//                                    searchForAlbumAndPlay(words[2]);
//                                } else {
//                                    searchForSongAndPlay(words[1]);
//                                }
//                            }
//
//                            if (words[0].equalsIgnoreCase("shuffle")) {
//                                if (words[1].equalsIgnoreCase("album") || words[1].equalsIgnoreCase("artist") || words[1].equalsIgnoreCase("playlist")) {
//                                    searchForAlbumAndPlay(words[2]);
//                                } else {
//                                    searchForAlbumAndPlay(words[1]);
//                                }
//                            }
//                    }
//                    Log.d("Speech",result.get(0));
//                }
//                break;
//            }
//        }
//    }

    public void searchForAlbumAndPlay(String album){

        Log.d("Speech",album);
    }

    public void searchForSongAndPlay(String song){
        Log.d("Speech",song);
    }

//    @Override
//    public void onTrackClicked(int position, String title, String artist) {
//        playAudio(position,title,artist);
//    }

//    public List<SongsInfo> loadSongs(String[] projection, String selection, String[] selectionArgs, String sortOrder) {
//
//        List<SongsInfo> songsInfoList = new ArrayList<>();
//        MightyDbHelper dbHelper = new MightyDbHelper(mContext);
//        SQLiteDatabase db = dbHelper.getReadableDatabase();
//
//        if (mContext == null) {
////            Log.d("SongsAsyncTask","loadSong context is null");
//        }
//
//        Cursor cursor = db.query(MightyContract.SongEntry.TABLE_NAME
//                , projection
//                , selection
//                , selectionArgs
//                , null
//                , null
//                , sortOrder);
////        Cursor cursor=mContext.getContentResolver().query(MightyContract.SongEntry.CONTENT_URI
////                ,projection
////                ,selection
////                ,selectionArgs
////                , sortOrder);
//        if (cursor != null && cursor.getCount() > 0) {
//            int noOfSongs = cursor.getCount();
//            while (cursor.moveToNext()) {
//                long songId = cursor.getLong(cursor.getColumnIndex(MightyContract.PlaylistEntry._ID));
//                String songData = cursor.getString(cursor.getColumnIndex(MightyContract.SongEntry.COLUMN_DATA));
//                String songTitle = cursor.getString(cursor.getColumnIndex(MightyContract.SongEntry.COLUMN_TITLE));
//                String songAlbum = cursor.getString(cursor.getColumnIndex(MightyContract.SongEntry.COLUMN_ALBUM));
//                String songArtist = cursor.getString(cursor.getColumnIndex(MightyContract.SongEntry.COLUMN_ARTIST));
//                long songDuration = cursor.getLong(cursor.getColumnIndex(MightyContract.SongEntry.COLUMN_DURATION));
//                int liked = cursor.getInt(cursor.getColumnIndex(MightyContract.SongEntry.COLUMN_LIKE));
//                String albumArt = cursor.getString(cursor.getColumnIndex(MightyContract.SongEntry.COLUMN_ALBUM_ART));
//
//                // Save to audioList
//                songsInfoList.add(new SongsInfo(songId, songData, songTitle, songAlbum, songArtist, songDuration, liked, albumArt, noOfSongs));
//            }
//            cursor.close();
//        }
//        return songsInfoList;
//    }


    public class PagerAdapter extends FragmentStatePagerAdapter {

        public PagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new TracksFragment();
                case 1:
                    return new AlbumsFragment();
                case 2:
                    return new ArtistsFragment();
                case 3:
                    return new PlaylistsFragments();
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return 4;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Tracks";
                case 1:
                    return "Albums";
                case 2:
                    return "Artists";
                case 3:
                    return "Playlists";
                default:
                    return null;
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        // Add the following line to register the Session Manager Listener onResume
//        mSensorManager.registerListener(mShakeDetector, mAccelerometer,	SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    public void onPause() {
        // Add the following line to unregister the Sensor Manager onPause
//        mSensorManager.unregisterListener(mShakeDetector);
        super.onPause();
    }

}
