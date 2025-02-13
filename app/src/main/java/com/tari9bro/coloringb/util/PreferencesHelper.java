package com.tari9bro.coloringb.util;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import com.tari9bro.coloringb.R;

public final class PreferencesHelper {
    public PreferencesHelper(Activity activity) {
        this.activity = activity;
    }

    Activity activity;
    public void SaveString(String key, String value) {
        SharedPreferences preferences = activity.getSharedPreferences(activity.getString(R.string.app_link), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(key, value);
        editor.apply();
    }
    public String LoadString(String key) {
        SharedPreferences preferences = activity.getSharedPreferences(activity.getString(R.string.app_link), Context.MODE_PRIVATE);
        return preferences.getString(key, activity.getString(R.string.w6));

    }
    public void SaveInt(String key, int value) {
        SharedPreferences preferences = activity.getSharedPreferences(activity.getString(R.string.app_link), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(key, value);
        editor.apply();
    }
    public int LoadInt(String key) {
        SharedPreferences preferences = activity.getSharedPreferences(activity.getString(R.string.app_link), Context.MODE_PRIVATE);
        return preferences.getInt(key, 0);
    }
}

