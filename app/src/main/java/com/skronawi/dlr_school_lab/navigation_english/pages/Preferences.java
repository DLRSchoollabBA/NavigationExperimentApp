package com.skronawi.dlr_school_lab.navigation_english.pages;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.PreferenceActivity;

import com.skronawi.dlr_school_lab.navigation_english.R;

public class Preferences extends PreferenceActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }

    @Override
    public void onResume() {
        super.onResume();
        getActionBar().setTitle(getResources().getString(R.string.settings));
        getActionBar().setBackgroundDrawable(new ColorDrawable(Color.BLACK));
    }
}