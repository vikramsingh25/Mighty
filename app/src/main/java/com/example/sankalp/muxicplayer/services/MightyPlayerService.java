package com.example.sankalp.muxicplayer.services;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.session.MediaSessionManager;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.Loader;
import android.support.v7.app.NotificationCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import com.example.sankalp.muxicplayer.MainActivity;
import com.example.sankalp.muxicplayer.PlayingScreenActivity;
import com.example.sankalp.muxicplayer.R;
import com.example.sankalp.muxicplayer.fragments.CurrentSongFragment;
import com.example.sankalp.muxicplayer.fragments.PlayScreenFragment;
import com.example.sankalp.muxicplayer.mightyDB.MightyContract;
import com.example.sankalp.muxicplayer.utils.StorageUtils;
import com.example.sankalp.muxicplayer.data.SongsInfo;
import com.example.sankalp.muxicplayer.utils.Utilities;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import edu.cmu.pocketsphinx.Assets;
import edu.cmu.pocketsphinx.Hypothesis;
import edu.cmu.pocketsphinx.RecognitionListener;
import edu.cmu.pocketsphinx.SpeechRecognizer;

import static edu.cmu.pocketsphinx.SpeechRecognizerSetup.defaultSetup;

/**
 * Created by sankalp on 9/25/2016.
 */
public class MightyPlayerService extends Service implements MediaPlayer.OnSeekCompleteListener,
        MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener,
        MediaPlayer.OnInfoListener, MediaPlayer.OnBufferingUpdateListener,
        AudioManager.OnAudioFocusChangeListener,SensorEventListener,RecognitionListener {



    private static final float SHAKE_THRESHOLD_GRAVITY = 2.7F;
    private static final int SHAKE_SLOP_TIME_MS = 500;
    private static final int SHAKE_COUNT_RESET_TIME_MS = 3000;

    private OnShakeListener mListener;
    private long mShakeTimestamp;
    private int mShakeCount;

    private SensorManager mSensorManager;
    private Sensor mAccelerometer;

    private final IBinder iBinder = new LocalBinder();

    public static MediaPlayer mediaPlayer;
    private String mediaFilePath;   //path to media file

    private int resumePosition;
    private AudioManager audioManager;

    //List of available Audio files

    public static List<SongsInfo> audioList;
    public static int audioIndex = -1;
    private SongsInfo activeAudio; //an object of the currently playing audio

    //Handle incoming phone calls
    private boolean ongoingCall = false;
    private PhoneStateListener phoneStateListener;
    private TelephonyManager telephonyManager;

    public static final String ACTION_PLAY = "com.example.sankalp.muxicplayer.ACTION_PLAY";
    public static final String ACTION_PAUSE = "com.example.sankalp.muxicplayer.ACTION_PAUSE";
    public static final String ACTION_PREVIOUS = "com.example.sankalp.muxicplayer.ACTION_PREVIOUS";
    public static final String ACTION_NEXT = "com.example.sankalp.muxicplayer.ACTION_NEXT";
    public static final String ACTION_STOP = "com.example.sankalp.muxicplayer.ACTION_STOP";

    //MediaSession
    private MediaSessionManager mediaSessionManager;
    private MediaSessionCompat mediaSession;
    public static MediaControllerCompat.TransportControls transportControls;

    //AudioPlayer notification ID
    private static final int NOTIFICATION_ID = 101;
    private static final int NOTIFICATION_REQUEST_CODE = 0;


    private SpeechRecognizer recognizer;
    private static final String MIGHTY_SEARCH= "mighty";
    private boolean voice;
    SharedPreferences sharedPreferences;


    public void setOnShakeListener(OnShakeListener listener) {
        this.mListener = listener;
    }

    @Override
    public void onBeginningOfSpeech() {
    }

    @Override
    public void onEndOfSpeech() {
        mediaPlayer.setVolume(1.0f,1.0f);
//        transportControls.play();
        recognizer.stop();
    }

    @Override
    public void onPartialResult(Hypothesis hypothesis) {
        if (hypothesis == null)
            return;
        String text = hypothesis.getHypstr();
        Toast.makeText(MightyPlayerService.this, "onPartialResult with hypo "+text, Toast.LENGTH_SHORT).show();
//        Log.d("PocketSphinxActivity","onPartialResult with hypo "+text);
        recognizer.stop();
    }

    @Override
    public void onResult(Hypothesis hypothesis) {
        if (hypothesis != null) {
            String text = hypothesis.getHypstr();
//            Log.d("PocketSphinxActivity","onResult with hypo "+text);
            Toast.makeText(MightyPlayerService.this, "onResult with hypo "+text, Toast.LENGTH_LONG).show();

            switch (text) {
                case "next":
                    MightyPlayerService.transportControls.skipToNext();
                    break;
                case "previous":
                    MightyPlayerService.transportControls.skipToPrevious();
                    break;
                case "pause":
                    if (CurrentSongFragment.currentPlaybackStatus == MightyPlayerService.PlaybackStatus.PLAYING){
                        MightyPlayerService.transportControls.pause();
                        CurrentSongFragment.currentPlaybackStatus= MightyPlayerService.PlaybackStatus.PAUSED;
                    }
                    break;
                case "play":
                    if (CurrentSongFragment.currentPlaybackStatus == MightyPlayerService.PlaybackStatus.PAUSED){
                        MightyPlayerService.transportControls.play();
                        CurrentSongFragment.currentPlaybackStatus= MightyPlayerService.PlaybackStatus.PLAYING;
                    }
                    break;
                case "stop":
                    MightyPlayerService.transportControls.stop();
                    break;

            }

//            Toast.makeText(ShakeService.this, "hypo best score "+hypothesis.getBestScore(), Toast.LENGTH_SHORT).show();
            hypothesis.delete();
//            if ( MightyPlayerService.mediaPlayer!=null) {
//                MightyPlayerService.mediaPlayer.setVolume(1.0f,1.0f);
//            }
//            if (hypothesis!=null) {
//                hypothesis.delete();
//            }
//            hypothesis.delete();
            onTimeout();
        }
    }

    @Override
    public void onError(Exception e) {
        e.printStackTrace();
    }

    @Override
    public void onTimeout() {
        if (!audioManager.isWiredHeadsetOn()) {
//            transportControls.play();
            mediaPlayer.setVolume(1.0f,1.0f);
        }
        recognizer.cancel();
    }

    public interface OnShakeListener {
        public void onShake(int count);
    }


    private void initMediaPlayer() {
//        Log.d("MightyPlayerService","in initMediaPlayer()");
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnCompletionListener(this);
        mediaPlayer.setOnBufferingUpdateListener(this);
        mediaPlayer.setOnSeekCompleteListener(this);
        mediaPlayer.setOnPreparedListener(this);
        mediaPlayer.setOnInfoListener(this);
        mediaPlayer.setOnErrorListener(this);
        mediaPlayer.setOnSeekCompleteListener(this);

        mediaPlayer.reset();    //so that media player is not pointing to another data source

        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            mediaPlayer.setDataSource(activeAudio.getSongData());
        } catch (IOException e) {
            e.printStackTrace();
            stopSelf();
        } catch (IllegalStateException e) {
            e.printStackTrace();
            stopSelf();
        }
        mediaPlayer.prepareAsync();
    }

    private void initMediaSession() throws RemoteException {
//        Log.d("MightyPlayerService","in initMediaSession()");
        if (mediaSessionManager != null) return;
        mediaSessionManager = (MediaSessionManager) getSystemService(Context.MEDIA_SESSION_SERVICE);
        // Create a new MediaSession
        mediaSession = new MediaSessionCompat(getApplicationContext(), "AudioPlayer");
        //Get MediaSessions transport controls
        transportControls = mediaSession.getController(

        ).getTransportControls();
        //set MediaSession -> ready to receive media commands
        mediaSession.setActive(true);
        //indicate that the MediaSession handles transport control commands
        // through its MediaSessionCompat.Callback.
        mediaSession.setFlags(MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);
        //Set mediaSession's MetaData
        updateMetaData();
        // Attach Callback to receive MediaSession updates
        mediaSession.setCallback(new MediaSessionCompat.Callback() {
            @Override
            public void onPlay() {
                super.onPlay();
                resumeMedia();
                buildNotification(PlaybackStatus.PLAYING);
            }

            @Override
            public void onPause() {
                super.onPause();
                pauseMedia();
                buildNotification(PlaybackStatus.PAUSED);
            }

            @Override
            public void onSkipToNext() {
                super.onSkipToNext();
                skipToNext();
                updateMetaData();
                buildNotification(PlaybackStatus.PLAYING);
            }

            @Override
            public void onSkipToPrevious() {
                super.onSkipToPrevious();
                skipToPrevious();
                updateMetaData();
                buildNotification(PlaybackStatus.PLAYING);
            }

            @Override
            public void onStop() {
                super.onStop();
                removeNotification();
                //Stop the service
                stopSelf();
            }

            @Override
            public void onSeekTo(long pos) {
                super.onSeekTo(pos);
            }
        });
    }

    private void updateMetaData() {
//        Log.d("MightyPlayerService","in updateMetaData()");

        Bitmap albumArt = BitmapFactory.decodeResource(getResources(),
                R.drawable.ic_back); //replace with medias albumArt
        mediaSession.setMetadata(new MediaMetadataCompat.Builder()
                .putBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART, albumArt)
                .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, activeAudio.getSongArtist())
                .putString(MediaMetadataCompat.METADATA_KEY_ALBUM, activeAudio.getSongAlbum())
                .putString(MediaMetadataCompat.METADATA_KEY_TITLE, activeAudio.getSongTitle())
                .build());
    }

    private void skipToNext() {
//        Log.d("MightyPlayerService","in skipToNext()");
        if (audioIndex == audioList.size() - 1) {
            //if last in playlist
            audioIndex = 0;
            activeAudio = audioList.get(audioIndex);
        } else {
            //get next in playlist
            activeAudio = audioList.get(++audioIndex);
        }
        //Update stored index
        new StorageUtils(this).storeAudioIndex(audioIndex);
        new MainActivity().setPlayBar(activeAudio.getSongTitle(),activeAudio.getSongArtist(),null);

        new PlayingScreenActivity().setSongCredentials(activeAudio.getSongTitle()
                ,activeAudio.getSongArtist()
                ,Utilities.milliSecondsToTimer(activeAudio.getSongDuration())
        ,null);
        PlayScreenFragment.duration=audioList.get(audioIndex).getSongDuration();
//        new PlayScreenFragment().setSongCredentials(activeAudio.getSongTitle()
//                ,activeAudio.getSongArtist()
//                , Utilities.milliSecondsToTimer(activeAudio.getSongDuration()));
        stopMedia();
        //reset mediaPlayer
        mediaPlayer.reset();
        initMediaPlayer();
    }

    private void skipToPrevious() {
//        Log.d("MightyPlayerService","in skipToPrevious()");
        if (audioIndex == 0) {
            //if first in playlist
            //set index to the last of audioList
            audioIndex = audioList.size() - 1;
            activeAudio = audioList.get(audioIndex);
        } else {
            //get previous in playlist
            activeAudio = audioList.get(--audioIndex);
        }

        new MainActivity().setPlayBar(activeAudio.getSongTitle(),activeAudio.getSongArtist(),null);

        new PlayingScreenActivity().setSongCredentials(activeAudio.getSongTitle()
                ,activeAudio.getSongArtist()
                ,Utilities.milliSecondsToTimer(activeAudio.getSongDuration())
        ,null);
        PlayScreenFragment.duration=audioList.get(audioIndex).getSongDuration();
//        new PlayScreenFragment().setSongCredentials(activeAudio.getSongTitle()
//                ,activeAudio.getSongArtist()
//                , Utilities.milliSecondsToTimer(activeAudio.getSongDuration()));

        //Update stored index
        new StorageUtils(this).storeAudioIndex(audioIndex);

        stopMedia();
        //reset mediaPlayer
        mediaPlayer.reset();
        initMediaPlayer();
    }

    @Override
    public void onCreate() {

        super.onCreate();
//        Log.d("MightyPlayerService","in onCreate()");
        // Perform one-time setup procedures
        sharedPreferences=MightyPlayerService.this.getSharedPreferences(StorageUtils.STORAGE,MODE_PRIVATE);
//        SharedPreferences.Editor editor=sharedPreferences.edit();

        // Manage incoming phone calls during playback.
        // Pause MediaPlayer on incoming call,
        // Resume on hangup.
        callStateListener();
//        Log.d("MightyPlayerService","in onCreate() after callStateListener");
        //ACTION_AUDIO_BECOMING_NOISY -- change in audio outputs -- BroadcastReceiver
        registerBecomingNoisyReceiver();
//        Log.d("MightyPlayerService","in onCreate() after registerBecomingNoisyReceiver");
        //Listen for new Audio to play -- BroadcastReceiver
        register_playNewAudio();
//        Log.d("MightyPlayerService","in onCreate() after register_playNewAudio");

//        SharedPreferences sharedPreference= PreferenceManager.getDefaultSharedPreferences(MightyPlayerService.this);

//        voice = sharedPreference.getBoolean("Voice_on_off",true);
//        if (voice) {
//            initRecognizer();
//            manageShake();
//        }
            initRecognizer();
            manageShake();

//        if ( MightyPlayerService.mediaPlayer!=null) {
//            MightyPlayerService.mediaPlayer.setVolume(1.0f,1.0f);
//        }
    }

    private void initRecognizer() {
        new AsyncTask<Void, Void, Exception>() {
            @Override
            protected Exception doInBackground(Void... params) {
                Log.d("PocketSphinxActivity","AsyncTask");

                try {
                    Assets assets = new Assets(MightyPlayerService.this);
                    File assetDir = assets.syncAssets();
                    setupRecognizer(assetDir);
                } catch (IOException e) {
                    return e;
                }
                return null;
            }

            @Override
            protected void onPostExecute(Exception result) {
                if (result != null) {
                    Toast.makeText(MightyPlayerService.this, "Failed to init recognizer " + result, Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(MightyPlayerService.this,"Ready for your commands",Toast.LENGTH_LONG).show();
                }
            }
        }.execute();
    }

    private void manageShake(){
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        boolean accelSupported=  mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_GAME);
        if (!accelSupported) {
            // on accelerometer on this device
            mSensorManager.unregisterListener(this,mAccelerometer);
        }
        setOnShakeListener(new OnShakeListener() {
            //            int a=0;
            @Override
            public void onShake(int count) {
                Toast.makeText(MightyPlayerService.this,"Listening...",Toast.LENGTH_SHORT).show();
                if (recognizer!=null) {
                    recognizer.stop();
                    if (!audioManager.isWiredHeadsetOn()) {
//                        transportControls.pause();
                        mediaPlayer.setVolume(0.0f,0.0f);
                    }
                    recognizer.startListening(MIGHTY_SEARCH,3000);
                }
//                a++;
//                MainActivity.textView.setText(""+a);
//                promptSpeechInput();
            }

        });
    }

    private void playMedia() {
//        Log.d("MightyPlayerService","in playMedia()");
        if (!mediaPlayer.isPlaying()) {
            mediaPlayer.start();
        }
    }

    private void pauseMedia() {
//        Log.d("MightyPlayerService","in pauseMedia()");
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            resumePosition = mediaPlayer.getCurrentPosition();
        }
    }

    private void stopMedia() {
//        Log.d("MightyPlayerService","in stopMedia()");
        if (mediaPlayer == null) {
            return;
        }
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
        }
    }

    private void resumeMedia() {
//        Log.d("MightyPlayerService","in resumeMedia()");
        if (!mediaPlayer.isPlaying()) {
            mediaPlayer.seekTo(resumePosition);
            mediaPlayer.start();
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
//        Log.d("MightyPlayerService","in onBind()");
        return iBinder;
    }

    @Override
    public void onSeekComplete(MediaPlayer mp) {
//        Log.d("MightyPlayerService","in onSeekComplete()");
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
//        Log.d("MightyPlayerService","in onPrepared()");
//        Invoked when the media source is ready for playback.
        playMedia();
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
//        Log.d("MightyPlayerService","in onCompletion()");
//        Invoked when playback of a media source has completed.
        if (PlayScreenFragment.isRepeat) {
            audioIndex=--audioIndex;
        }
        skipToNext();
        buildNotification(PlaybackStatus.PLAYING);
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
//        Log.d("MightyPlayerService","in onError()");
        //Invoked when there has been an error during an asynchronous operation
        switch (what) {
            case MediaPlayer.MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK:
//                Log.d("MediaPlayer Error", "MEDIA ERROR NOT VALID FOR PROGRESSIVE PLAYBACK " + extra);
                break;
            case MediaPlayer.MEDIA_ERROR_SERVER_DIED:
//                Log.d("MediaPlayer Error", "MEDIA ERROR SERVER DIED" + extra);
                break;
            case MediaPlayer.MEDIA_ERROR_UNKNOWN:
//                Log.d("MediaPlayer Error", "MEDIA ERROR UNKNOWN " + extra);
        }
        return false;
    }

    @Override
    public boolean onInfo(MediaPlayer mp, int what, int extra) {
//        Log.d("MightyPlayerService","in onInfo()");
        return false;
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
//        Log.d("MightyPlayerService","in onBufferingUpdate()");
    }

    @Override
    public void onAudioFocusChange(int focusChange) {
        Log.d("MightyPlayerService","in onAudioFocusChange()");
//         Invoked when the audio focus of the system is updated
        switch (focusChange) {

            case AudioManager.AUDIOFOCUS_GAIN:
//                resume media player
                if (mediaPlayer == null) {
                    initMediaPlayer();
                } else if (!mediaPlayer.isPlaying()) {
                    mediaPlayer.start();
                    mediaPlayer.setVolume(1.0f, 1.0f);
                }
                break;

            case AudioManager.AUDIOFOCUS_LOSS:
                // Lost focus for an unbounded amount of time: stop playback and release media player
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.stop();
                }
                mediaPlayer.release();
                mediaPlayer = null;
                break;

            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                // Lost focus for a short time, but we have to stop
                // playback. We don't release the media player because playback
                // is likely to resume
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                }
                break;

            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                // Lost focus for a short time, but it's ok to keep playing
                // at an attenuated level
                if (mediaPlayer.isPlaying()) mediaPlayer.setVolume(0.1f, 0.1f);
                break;
        }

    }

    private boolean requestAudioFocus() {
//        Log.d("MightyPlayerService","in requestAudioFocus()");
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        int result = audioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
        if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            return true;    //focus gained
        }
        return false;
    }

    private boolean removeAudioFocus() {
//        Log.d("MightyPlayerService","in removeAudioFocus()");
        return AudioManager.AUDIOFOCUS_REQUEST_GRANTED == audioManager.abandonAudioFocus(this);
    }

    //The system calls this method when an activity, requests the service be started

    private List<SongsInfo> loadSongsFromPlayingQueue(){
        List<SongsInfo> list=new ArrayList<>();
        Cursor cursor = getContentResolver().query(MightyContract.PlayingQueueEntry.CONTENT_URI
        ,null
        ,null
        ,null
        ,null);
        if (cursor!=null && cursor.moveToFirst()) {
            do{
                long id=cursor.getLong(cursor.getColumnIndex(MightyContract.PlayingQueueEntry.COLUMN_TITLE));
                String title=cursor.getString(cursor.getColumnIndex(MightyContract.PlayingQueueEntry.COLUMN_TITLE));
                String data=cursor.getString(cursor.getColumnIndex(MightyContract.PlayingQueueEntry.COLUMN_DATA));
                String album=cursor.getString(cursor.getColumnIndex(MightyContract.PlayingQueueEntry.COLUMN_ALBUM));
                String artist=cursor.getString(cursor.getColumnIndex(MightyContract.PlayingQueueEntry.COLUMN_ARTIST));
                long duration=Long.parseLong(cursor.getString(cursor.getColumnIndex(MightyContract.PlayingQueueEntry.COLUMN_DURATION)));
                list.add(new SongsInfo(id,title, artist, duration, data, album));
            }while (cursor.moveToNext());
        }
        if (cursor!=null) {
            cursor.close();
        }
        return list;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

//        Log.d("MightyPlayerService","in onStartCommand()");
        try {
            //Load data from SharedPreferences
//            audioList=new SongsAsyncTask(MainActivity.mContext).execute();

            audioList=loadSongsFromPlayingQueue();
            Log.d("Shuffle 1 service:",String.valueOf(audioList.get(0).getSongTitle()));
            StorageUtils storage = new StorageUtils(MainActivity.mContext);
            audioIndex=storage.loadAudioIndex();
//            Log.d("MightyPlayerService",String.valueOf(audioIndex)+" : "+String.valueOf(audioList.size()));

//            Cursor cursor=getApplication().getContentResolver().query(MightyContract.SongEntry.CONTENT_URI
//                    , new String[]{MightyContract.SongEntry._ID}
//                    , MightyContract.SongEntry.COLUMN_IS_CURRENT + "=?"
//                    ,new String[]{"1"}
//                    ,null);
//
//            if (cursor!=null && cursor.getCount()>0) {
//                audioIndex = cursor.getInt(cursor.getColumnIndex(MightyContract.SongEntry._ID));
//            }
            if (audioIndex != -1 && audioIndex < audioList.size()) {
                //index is in a valid range
                activeAudio = audioList.get(audioIndex);
            } else {
                stopSelf();
            }
//            cursor.close();

        } catch (NullPointerException e) {
            e.printStackTrace();
            stopSelf();
        }
        if (!requestAudioFocus()) {
            stopSelf();
        }

        if (mediaSessionManager == null) {
            try {
                if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP) {
                    initMediaSession();
                }
                initMediaPlayer();
            } catch (RemoteException e) {
                e.printStackTrace();
                stopSelf();
            }
            buildNotification(PlaybackStatus.PLAYING);
        }
        //Handle Intent action from MediaSession.TransportControls
        handleIncomingAction(intent);



        return super.onStartCommand(intent, flags, startId);
    }

    private void setupRecognizer(File assetsDir) throws IOException {
        Log.d("PocketSphinxActivity","setupRecognizer");

        recognizer = defaultSetup()
                .setAcousticModel(new File(assetsDir, "en-us-ptm"))
                .setDictionary(new File(assetsDir, "cmudict-en-us.dict"))
                .setKeywordThreshold(1e-45f)
                .setBoolean("-allphone_ci", true)
                .getRecognizer();
        recognizer.addListener(this);

        File mightyGrammar = new File(assetsDir, "mighty.gram");
        recognizer.addGrammarSearch(MIGHTY_SEARCH, mightyGrammar);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (mListener != null) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];

            float gX = x / SensorManager.GRAVITY_EARTH;
            float gY = y / SensorManager.GRAVITY_EARTH;
            float gZ = z / SensorManager.GRAVITY_EARTH;

            // gForce will be close to 1 when there is no movement.
            float gForce = (float) Math.sqrt(gX * gX + gY * gY + gZ * gZ);

            if (gForce > SHAKE_THRESHOLD_GRAVITY) {
                final long now = System.currentTimeMillis();
                // ignore shake events too close to each other (500ms)
                if (mShakeTimestamp + SHAKE_SLOP_TIME_MS > now) {
                    return;
                }

                // reset the shake count after 3 seconds of no shakes
                if (mShakeTimestamp + SHAKE_COUNT_RESET_TIME_MS < now) {
                    mShakeCount = 0;
                }

                mShakeTimestamp = now;
                mShakeCount++;

                mListener.onShake(mShakeCount);
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }



    @Override
    public void onDestroy() {
//        Log.d("MightyPlayerService","in onDestroy()");

        recognizer.shutdown();
        if (mSensorManager != null) {
            mSensorManager.unregisterListener(this,mAccelerometer);
            mSensorManager = null;
        }
        stopSelf();

        super.onDestroy();
        if (mediaPlayer != null) {
            stopMedia();
            mediaPlayer.release();
        }
        removeAudioFocus();
        //Disable the PhoneStateListener
        if (phoneStateListener != null) {
            telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_NONE);
        }

        removeNotification();

        //unregister BroadcastReceivers
        unregisterReceiver(becomingNoisyReceiver);
        unregisterReceiver(playNewAudio);

        //clear cached playlist
        new StorageUtils(this).clearCachedAudioPlaylist();
    }

    private BroadcastReceiver playNewAudio = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
//            Log.d("MightyPlayerService","in playnewAudio onReceive()");
            audioList=loadSongsFromPlayingQueue();
//            Log.d("Shuffle 1 service:",String.valueOf(audioList.get(0).getSongTitle()));
            StorageUtils storage = new StorageUtils(MainActivity.mContext);
//            if (!voice) {
//
//            }
            audioIndex=storage.loadAudioIndex();
//            Log.d("MightyPlayerService",String.valueOf(audioIndex)+" : "+String.valueOf(audioList.size()));
            if (audioIndex != -1 && audioIndex < audioList.size()) {
                //index is in a valid range
                activeAudio = audioList.get(audioIndex);
            } else {
                stopSelf();
            }
            stopMedia();
            mediaPlayer.reset();
            initMediaPlayer();
            updateMetaData();
            buildNotification(PlaybackStatus.PLAYING);

        }


    };

    private void register_playNewAudio() {
//        Log.d("MightyPlayerService","in register_playNewAudio()");
        //Register playNewMedia receiver
        IntentFilter filter = new IntentFilter(MainActivity.Broadcast_PLAY_NEW_AUDIO);
        registerReceiver(playNewAudio, filter);
    }

    private BroadcastReceiver becomingNoisyReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
//            Log.d("MightyPlayerService","in becomingNoisyReceiver onReceive()");
            pauseMedia();
            CurrentSongFragment.play_pause.setImageResource(R.drawable.play);
            CurrentSongFragment.currentPlaybackStatus = MightyPlayerService.PlaybackStatus.PAUSED;
            buildNotification(PlaybackStatus.PAUSED);
        }

    };

    //Done
    private void registerBecomingNoisyReceiver() {
//        Log.d("MightyPlayerService","in registerBecomingNoisyReceiver()");
        //register after getting audio focus
        IntentFilter intentFilter = new IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY);
        registerReceiver(becomingNoisyReceiver, intentFilter);
    }

    //Handle incoming phone calls
    //Done
    private void callStateListener() {
//        Log.d("MightyPlayerService","in callStateListener()");
        telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        phoneStateListener = new PhoneStateListener() {
            @Override
            public void onCallStateChanged(int state, String incomingNumber) {
                super.onCallStateChanged(state, incomingNumber);
                switch (state) {
                    //if at least one call exists or the phone is ringing
                    //pause the MediaPlayer
                    case TelephonyManager.CALL_STATE_OFFHOOK:
                    case TelephonyManager.CALL_STATE_RINGING:
                        if (mediaPlayer != null) {
                            pauseMedia();
                            ongoingCall = true;
                        }
                        break;

                    case TelephonyManager.CALL_STATE_IDLE:
                        // Phone idle. Start playing.
                        if (mediaPlayer != null) {
                            if (ongoingCall) {
                                ongoingCall = false;
                                resumeMedia();
                            }
                        }
                        break;
                }
            }
        };
        // Register the listener with the telephony manager
        // Listen for changes to the device call state.
        telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
    }


    public enum PlaybackStatus {
        PLAYING,
        PAUSED
    }

    private void buildNotification(PlaybackStatus playbackStatus){

//        Bundle bundle=new Bundle();
//        bundle.putString("CurrentSongTitle",activeAudio.getSongTitle());
//        bundle.putString("CurrentSongArtist",activeAudio.getSongArtist());
//        CurrentSongFragment currentSongFragment=new CurrentSongFragment();
//        currentSongFragment.setArguments(bundle);

//        Log.d("MightyPlayerService","in buildNotification()");
        int notificationAction=android.R.drawable.ic_media_pause;//needs to be initialized
        PendingIntent play_pauseAction=null;

        //Build a new notification according to the current state of the MediaPlayer
        if (playbackStatus==PlaybackStatus.PLAYING) {
            notificationAction=android.R.drawable.ic_media_pause;
            //create the pause action
            play_pauseAction=playbackAction(1);
        } else if (playbackStatus==PlaybackStatus.PAUSED) {
            notificationAction=android.R.drawable.ic_media_play;
            //create the play action
            play_pauseAction=playbackAction(0);
        }

        Bitmap largeIcon = BitmapFactory.decodeResource(getResources(),
                R.drawable.notif_icon);
        // Create a new Notification
        NotificationCompat.Builder notificationBuilder;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            notificationBuilder = (NotificationCompat.Builder) new NotificationCompat.Builder(this)
                    .setShowWhen(false)
                    .setStyle(new NotificationCompat.MediaStyle()
                            .setMediaSession(mediaSession.getSessionToken())
                            .setShowActionsInCompactView(0, 1, 2))
                    .setColor(getResources().getColor(R.color.app_color))
                    .setLargeIcon(largeIcon)
//                    .setAutoCancel(true)
                    .setSmallIcon(R.drawable.play)
                    .setContentText(activeAudio.getSongArtist())
                    .setContentTitle(activeAudio.getSongTitle())
                    .setContentInfo(activeAudio.getSongAlbum())
                    .addAction(android.R.drawable.ic_media_previous, "previous", playbackAction(3))
                    .addAction(notificationAction, "pause", play_pauseAction)
                    .addAction(android.R.drawable.ic_media_next, "next", playbackAction(2));



        } else {
            notificationBuilder=(NotificationCompat.Builder) new NotificationCompat.Builder(this)
                    .setShowWhen(false)
                    .setStyle(new NotificationCompat.MediaStyle()
                            .setShowActionsInCompactView(0,1,2))
                    .setColor(getResources().getColor(R.color.app_color))
                    .setLargeIcon(largeIcon)
                    .setSmallIcon(R.drawable.play)
                    .setContentText(activeAudio.getSongArtist())
                    .setContentTitle(activeAudio.getSongTitle())
                    .setContentInfo(activeAudio.getSongAlbum())
                    .addAction(android.R.drawable.ic_media_previous,"previous",playbackAction(3))
                    .addAction(notificationAction,"pause",play_pauseAction)
                    .addAction(android.R.drawable.ic_media_next,"next",playbackAction(2));

        }
        Intent resultIntent=new Intent(MightyPlayerService.this,MainActivity.class);
//        TaskStackBuilder stackBuilder=TaskStackBuilder.create(MainActivity.mContext);
//        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent=PendingIntent.getActivity(MightyPlayerService.this,NOTIFICATION_REQUEST_CODE,resultIntent,0);
        notificationBuilder.setContentIntent(resultPendingIntent);
        ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).notify(NOTIFICATION_ID, notificationBuilder.build());
        startForeground(NOTIFICATION_ID,notificationBuilder.build());

        }

        private void removeNotification() {
//            Log.d("MightyPlayerService","in removeNotification()");
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.cancel(NOTIFICATION_ID);
        }

    @Nullable
    private PendingIntent playbackAction(int actionNumber){
//        Log.d("MightyPlayerService","in playbackAction()");
        Intent playbackAction=new Intent(this,MightyPlayerService.class);
        switch (actionNumber) {
            case 0:
                playbackAction.setAction(ACTION_PLAY);
                return PendingIntent.getService(this,actionNumber,playbackAction,0);
            case 1:
                playbackAction.setAction(ACTION_PAUSE);
                return PendingIntent.getService(this,actionNumber,playbackAction,0);
            case 2:
                playbackAction.setAction(ACTION_NEXT);
                return PendingIntent.getService(this,actionNumber,playbackAction,0);
            case 3:
                playbackAction.setAction(ACTION_PREVIOUS);
                return PendingIntent.getService(this,actionNumber,playbackAction,0);
        }
        return null;
    }

    private void handleIncomingAction(Intent playbackAction) {
//        Log.d("MightyPlayerService","in handleIncomingAction()");
        if (playbackAction == null || playbackAction.getAction() == null) {
            return;
        }

        String action=playbackAction.getAction();
        if (action.equalsIgnoreCase(ACTION_PLAY)) {
            transportControls.play();
            CurrentSongFragment.play_pause.setImageResource(R.drawable.pause);
            CurrentSongFragment.currentPlaybackStatus=PlaybackStatus.PLAYING;
        } else if (action.equalsIgnoreCase(ACTION_PAUSE)) {
            transportControls.pause();
            CurrentSongFragment.play_pause.setImageResource(R.drawable.play);
            CurrentSongFragment.currentPlaybackStatus=PlaybackStatus.PAUSED;
        } else if (action.equalsIgnoreCase(ACTION_NEXT)) {
            transportControls.skipToNext();
            CurrentSongFragment.play_pause.setImageResource(R.drawable.pause);
            CurrentSongFragment.currentPlaybackStatus=PlaybackStatus.PLAYING;
        } else if (action.equalsIgnoreCase(ACTION_PREVIOUS)) {
            transportControls.skipToPrevious();
            CurrentSongFragment.play_pause.setImageResource(R.drawable.pause);
            CurrentSongFragment.currentPlaybackStatus=PlaybackStatus.PLAYING;
        } else if (action.equalsIgnoreCase(ACTION_STOP)) {
            transportControls.stop();
        }
    }

    public class LocalBinder extends Binder {
        public MightyPlayerService getService() {
//            Log.d("MightyPlayerService ","in getService()");
            return MightyPlayerService.this;
        }
    }

}
