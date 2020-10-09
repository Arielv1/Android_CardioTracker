package com.example.cardiotracker.utilities;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Set;

import static android.content.Context.MODE_PRIVATE;

public class MySP {

    private SharedPreferences prefs;
    private static MySP mySPInstance;

    public MySP(Context context) {
        prefs = context.getSharedPreferences("MY_SP", MODE_PRIVATE);
    }

    public static MySP init(Context context) {
        if (mySPInstance == null)
            mySPInstance = new MySP(context);
        return mySPInstance;
    }
    public static MySP getInstance() {
        return mySPInstance;
    }

    public void putString(String key, String value) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public String getString(String key, String defaultValue) {
        return prefs.getString(key, defaultValue);
    }

    public void putBoolean(String key, boolean value) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public boolean getBoolean(String key, boolean defaultValue) {
        return prefs.getBoolean(key, defaultValue);
    }

    public void putInteger(String key, int value) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(key, value);
        editor.apply();
    }

    public int getInteger(String key, int defaultValue) {
        return prefs.getInt(key, defaultValue);
    }

    public void putDouble(String key, double value){
        SharedPreferences.Editor editor = prefs.edit();
        editor.putLong(key, Double.doubleToRawLongBits(value));
        editor.apply();
    }

    public double getDouble(String key, double defaultValue) {
        return Double.longBitsToDouble(prefs.getLong(key, Double.doubleToLongBits(defaultValue)));
    }

    public void putFloat(String key, float value) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putFloat(key, value);
        editor.apply();
    }

    public float getFloat(String key, float defaultValue) {
        return prefs.getFloat(key, defaultValue);
    }

    public void putLong(String key, long value) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putLong(key, value);
        editor.apply();
    }

    public long getLong(String key, long defaultValue) {
        return prefs.getLong(key, defaultValue);
    }

    public void putSetStrings(String key, Set<String> value){
        SharedPreferences.Editor editor = prefs.edit();
        editor.putStringSet(key, value);
        editor.apply();
    }

    public Set<String> getSetStrings(String key, Set<String> defaultValue) {
        return  prefs.getStringSet(key, defaultValue);
    }
}