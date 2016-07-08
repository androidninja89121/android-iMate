package com.tieyouin.Preference;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Morning on 11/24/2015.
 */

public class UserPreference {

    private static final String FILE_NAME = "imate.pref";

    private static UserPreference mInstance = null;

    public static UserPreference getInstance() {
        if(null == mInstance) {
            mInstance = new UserPreference();
        }

        return mInstance;
    }

    public void putSharedPreference(Context context, String key, String value) {
        SharedPreferences pref = context.getSharedPreferences(FILE_NAME,
                Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public void putSharedPreference(Context context, String key, boolean value) {
        SharedPreferences pref = context.getSharedPreferences(FILE_NAME,
                Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    public void putSharedPreference(Context context, String key, int value) {
        SharedPreferences pref = context.getSharedPreferences(FILE_NAME,
                Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt(key, value);
        editor.commit();
    }

    public void putSharedPreference(Context context, String key, long value) {
        SharedPreferences pref = context.getSharedPreferences(FILE_NAME,
                Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putLong(key, value);
        editor.commit();
    }

    public String getSharedPreference(Context context, String key,
                                      String defaultValue) {
        SharedPreferences pref = context.getSharedPreferences(FILE_NAME,
                Activity.MODE_PRIVATE);
        return pref.getString(key, defaultValue);
    }

    public int getSharedPreference(Context context, String key, int defaultValue) {
        SharedPreferences pref = context.getSharedPreferences(FILE_NAME,
                Activity.MODE_PRIVATE);
        return pref.getInt(key, defaultValue);
    }

    public long getSharedPreference(Context context, String key,
                                    long defaultValue) {
        SharedPreferences pref = context.getSharedPreferences(FILE_NAME,
                Activity.MODE_PRIVATE);
        return pref.getLong(key, defaultValue);
    }

    public boolean getSharedPreference(Context context, String key,
                                       boolean defaultValue) {
        SharedPreferences pref = context.getSharedPreferences(FILE_NAME,
                Activity.MODE_PRIVATE);
        return pref.getBoolean(key, defaultValue);
    }
}
