package com.example.too1.blesensorreader.Gui;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import com.example.too1.blesensorreader.R;

/**
 * Created by too1 on 07.07.2015.
 */
public class SettingsFragment extends PreferenceFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.preferences);
    }
}
