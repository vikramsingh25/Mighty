package com.example.sankalp.muxicplayer.fragments;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.SwitchPreference;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.sankalp.muxicplayer.R;

/**
 * Created by sankalp on 12/29/2016.
 */
public class SettingsFragment extends PreferenceFragment implements Preference.OnPreferenceChangeListener {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
//        bindPreferenceSummaryToValue(findPreference("Voice_on_off"));
    }

    private void bindPreferenceSummaryToValue(Preference preference) {
        preference.setOnPreferenceChangeListener(this);
        onPreferenceChange(preference,
                PreferenceManager.getDefaultSharedPreferences(preference.getContext())
                        .getBoolean(preference.getKey(),true));
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
//        String value=newValue.toString();
//        if (preference instanceof SwitchPreference) {
//            if (value.equalsIgnoreCase("true")) {
//                preference.setSummary("Enabled");
//            } else {
//                preference.setSummary("Disabled");
//            }
//        }
        return false;
    }
}
