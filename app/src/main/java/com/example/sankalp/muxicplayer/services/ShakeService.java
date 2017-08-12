package com.example.sankalp.muxicplayer.services;

import android.app.IntentService;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.example.sankalp.muxicplayer.fragments.CurrentSongFragment;

import java.io.File;
import java.io.IOException;

import edu.cmu.pocketsphinx.Assets;
import edu.cmu.pocketsphinx.Hypothesis;
import edu.cmu.pocketsphinx.RecognitionListener;
import edu.cmu.pocketsphinx.SpeechRecognizer;

import static edu.cmu.pocketsphinx.SpeechRecognizerSetup.defaultSetup;

/**
 * Created by sankalp on 5/11/2017.
 */
public class ShakeService extends IntentService implements SensorEventListener,RecognitionListener {

    private static final float SHAKE_THRESHOLD_GRAVITY = 2.7F;
    private static final int SHAKE_SLOP_TIME_MS = 500;
    private static final int SHAKE_COUNT_RESET_TIME_MS = 3000;

    private OnShakeListener mListener;
    private long mShakeTimestamp;
    private int mShakeCount;

    private SensorManager mSensorManager;
    private Sensor mAccelerometer;

    private final IBinder iBinder = new LocalBinder();

    private SpeechRecognizer recognizer;
    private static final String MIGHTY_SEARCH= "mighty";

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     */
    public ShakeService() {
        super("ShakeService");
    }

    public void setOnShakeListener(OnShakeListener listener) {
        this.mListener = listener;
    }

    @Override
    public void onBeginningOfSpeech() {
        if ( MightyPlayerService.mediaPlayer!=null) {
            MightyPlayerService.mediaPlayer.setVolume(0.1f,0.1f);
        }
    }

    @Override
    public void onEndOfSpeech() {
        recognizer.stop();
    }

    @Override
    public void onPartialResult(Hypothesis hypothesis) {
        if (hypothesis == null)
            return;
        String text = hypothesis.getHypstr();
        Toast.makeText(ShakeService.this, "onPartialResult with hypo "+text, Toast.LENGTH_SHORT).show();
//        Log.d("PocketSphinxActivity","onPartialResult with hypo "+text);
        recognizer.stop();
    }

    @Override
    public void onResult(Hypothesis hypothesis) {
        if (hypothesis != null) {
            String text = hypothesis.getHypstr();
            Log.d("PocketSphinxActivity","onResult with hypo "+text);
            Toast.makeText(ShakeService.this, "onResult with hypo "+text, Toast.LENGTH_SHORT).show();

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
            if ( MightyPlayerService.mediaPlayer!=null) {
                MightyPlayerService.mediaPlayer.setVolume(1.0f,1.0f);
            }
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
        recognizer.cancel();
    }

    public interface OnShakeListener {
        public void onShake(int count);
    }

    @Override
    public void onCreate() {

        Toast.makeText(this,
                "Service Started", Toast.LENGTH_SHORT).show();
        new AsyncTask<Void, Void, Exception>() {
            @Override
            protected Exception doInBackground(Void... params) {
                Log.d("PocketSphinxActivity","AsyncTask");

                try {
                    Assets assets = new Assets(ShakeService.this);
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
                    Toast.makeText(ShakeService.this,"Failed to init recognizer " + result,Toast.LENGTH_LONG).show();
                } else {
//                    switchSearch(KWS_SEARCH);
//                    recognizer.startListening(DIGITS_SEARCH,5000);
//                    recognizer.stop();
//                    recognizer.startListening(MIGHTY_SEARCH,3000);
                }
            }
        }.execute();
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
                recognizer.stop();
                recognizer.startListening(MIGHTY_SEARCH,3000);
//                a++;
//                MainActivity.textView.setText(""+a);
//                promptSpeechInput();
            }
        });
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        recognizer.shutdown();
        if (mSensorManager != null) {
            mSensorManager.unregisterListener(this,mAccelerometer);
            mSensorManager = null;
        }
        stopSelf();
        super.onDestroy();
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

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return iBinder;
    }

    @Override
    protected void onHandleIntent(Intent intent) {

    }

    public class LocalBinder extends Binder {
        public ShakeService getService() {
//            Log.d("MightyPlayerService ","in getService()");
            return ShakeService.this;
        }
    }
}
