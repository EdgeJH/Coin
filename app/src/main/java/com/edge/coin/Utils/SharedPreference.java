package com.edge.coin.Utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.edge.coin.UpbitPackage.TradeCoin;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import static android.content.Context.MODE_PRIVATE;

public class SharedPreference {

    // Avoid magic numbers.


    public SharedPreference() {
        super();
    }

    private final static String PREF_NAME = "coin.pref";
    private final static String COOKIE_NAME = "coin.pref";



    public void put(Context context, String key, String value) {
        SharedPreferences pref = context.getSharedPreferences(PREF_NAME,
                MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();

        editor.putString(key, value);
        editor.apply();
    }

    public void put(Context context, String key, long value) {
        SharedPreferences pref = context.getSharedPreferences(PREF_NAME,
                MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();

        editor.putLong(key, value);
        editor.apply();
    }
    public void put(Context context, String key, HashSet<String> value){
        SharedPreferences pref = context.getSharedPreferences(COOKIE_NAME,MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putStringSet(key,value);
        editor.apply();
    }
    public void put(Context context, String key, boolean value) {
        SharedPreferences pref = context.getSharedPreferences(PREF_NAME,
                MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();

        editor.putBoolean(key, value);
        editor.apply();
    }

    public void put(Context context, String key, Object value){
        SharedPreferences pref = context.getSharedPreferences(PREF_NAME,
                MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        Gson gson = new Gson();
        String json = gson.toJson(value);
        editor.putString(key, json);
        editor.apply();
    }

    public void put(Context context, String key, int value) {
        SharedPreferences pref = context.getSharedPreferences(PREF_NAME,
                MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();

        editor.putInt(key, value);
        editor.apply();
    }
    public void remove(Context context){
        SharedPreferences pref = context.getSharedPreferences(PREF_NAME,MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.clear();
        editor.apply();
    }
    public void removeCookie(Context context){
        SharedPreferences pref = context.getSharedPreferences(COOKIE_NAME,MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.clear();
        editor.apply();
    }

    public <T> T getValue(Context context, String key, Class<T> classOfT){
        SharedPreferences pref = context.getSharedPreferences(PREF_NAME,
                MODE_PRIVATE);
        String json = pref.getString(key, "");
        Gson gson =new Gson();
        T object  = gson.fromJson(json,classOfT);
        try {
            return object;
        } catch (Exception e) {
            return null;
        }
    }
    public String getValue(Context context, String key, String dftValue) {
        SharedPreferences pref = context.getSharedPreferences(PREF_NAME,
                MODE_PRIVATE);

        try {
            return pref.getString(key, dftValue);
        } catch (Exception e) {
            return dftValue;
        }

    }
    public String getValueToken(Context context, String key, String dftValue) {
        SharedPreferences pref = context.getSharedPreferences(PREF_NAME,
                MODE_PRIVATE);

        try {
            return pref.getString(key, dftValue);
        } catch (Exception e) {
            return dftValue;
        }

    }
    public Set<String> getValue(Context context, String key, HashSet<String> value){
        SharedPreferences pref = context.getSharedPreferences(COOKIE_NAME,MODE_PRIVATE);
        try {
            return pref.getStringSet(key,value);
        }catch (Exception e){
            return value;
        }
    }
    public int getValue(Context context, String key, int dftValue) {
        SharedPreferences pref = context.getSharedPreferences(PREF_NAME,
                MODE_PRIVATE);

        try {
            return pref.getInt(key, dftValue);
        } catch (Exception e) {
            return dftValue;
        }

    }
    public long getValue(Context context, String key, long dftValue) {
        SharedPreferences pref = context.getSharedPreferences(PREF_NAME,
                MODE_PRIVATE);

        try {
            return pref.getLong(key, dftValue);
        } catch (Exception e) {
            return dftValue;
        }

    }

    public boolean getValue(Context context , String key, boolean dftValue) {
        SharedPreferences pref = context.getSharedPreferences(PREF_NAME,
                MODE_PRIVATE);

        try {
            return pref.getBoolean(key, dftValue);
        } catch (Exception e) {
            return dftValue;
        }
    }

    public void storeCoinList(Context context, String key, ArrayList countries) {

        SharedPreferences settings;
        SharedPreferences.Editor editor;
        settings = context.getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        editor = settings.edit();
        Gson gson = new Gson();
        String jsonFavorites = gson.toJson(countries);
        editor.putString(key, jsonFavorites);
        editor.apply();
    }

    public ArrayList<TradeCoin> getCoinList(Context context, String key) {
        SharedPreferences sharedPrefs = context.getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPrefs.getString(key, null);
        ArrayList<TradeCoin> arrayList = gson.fromJson(json, new TypeToken<ArrayList<TradeCoin>>() {}.getType());
        return arrayList;
    }


}