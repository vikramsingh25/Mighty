package com.example.sankalp.muxicplayer;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.sankalp.muxicplayer.fragments.SettingsFragment;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        getFragmentManager().beginTransaction()
                .add(R.id.settings_container,new SettingsFragment())
                .commit();
    }
}
