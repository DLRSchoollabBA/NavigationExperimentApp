package com.skronawi.dlr_school_lab.navigation_english.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.skronawi.dlr_school_lab.navigation_english.Constants;

public class PreferencesManager {

    private final SharedPreferences sharedPreferences;
    private final Context context;

    public PreferencesManager(Context context) {
        sharedPreferences = context.getSharedPreferences(Constants.PREF_NAME,
                Context.MODE_PRIVATE);
        this.context = context;
    }

    public int getInt(String key) {
        return sharedPreferences.getInt(Constants.PREFS_PREFIX + "." + key, 0);
    }

    public void setInt(String key, int value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(Constants.PREFS_PREFIX + "." + key, value);
        editor.commit();
    }

    public void setString(String key, String value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(Constants.PREFS_PREFIX + "." + key, value);
        editor.commit();
    }

    public String getString(String key) {
        return sharedPreferences.getString(Constants.PREFS_PREFIX + "." + key, null);
    }

    public boolean useRealCoords() {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(
                "setting_real_coords", true);
    }
}
