package com.example.too1.blesensorreader.Gui;

import android.os.Bundle;
import android.preference.PreferenceActivity;

import com.example.too1.blesensorreader.R;

/**
 * Created by too1 on 07.07.2015.
 */
public class SettingsActivity extends PreferenceActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();
    }
}
