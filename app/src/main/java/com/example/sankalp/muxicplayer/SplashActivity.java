package com.example.sankalp.muxicplayer;

import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.example.sankalp.muxicplayer.fragments.SplashFragment;

import java.util.Locale;

public class SplashActivity extends AppCompatActivity implements TextToSpeech.OnInitListener {

    public static TextToSpeech tts;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        tts=new TextToSpeech(getApplicationContext(),this);
        if (savedInstanceState==null) {
            setContentView(R.layout.activity_splash);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.containerSplash,new SplashFragment())
                    .commit();
//            speakOut();
        }
    }

    @Override
    public void onInit(int status) {
//        if (status == TextToSpeech.SUCCESS) {
//            int result = tts.setLanguage(Locale.US);
//            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
//                Log.e("TTS", "This Language is not supported");
//            } else {
//                speakOut();
//            }
//        } else {
//            Log.e("TTS", "Initilization Failed!");
//        }
    }

    private void speakOut()
    {
        String text="Welcome, to Mighty Player!!";
        tts.speak(text,TextToSpeech.QUEUE_FLUSH,null);
    }
}
