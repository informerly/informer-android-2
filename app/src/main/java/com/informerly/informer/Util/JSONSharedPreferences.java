package com.informerly.informer.Util;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.Map;

public class JSONSharedPreferences {
    private static final String PREFIX = "json";

    public static void saveJSONString(Context c, String prefName, String key, String jsonString) {
        SharedPreferences settings = c.getSharedPreferences(prefName, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(JSONSharedPreferences.PREFIX+key, jsonString);
        editor.commit();
    }

    public static void saveJSONObject(Context c, String prefName, String key, JSONObject object) {
        SharedPreferences settings = c.getSharedPreferences(prefName, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(JSONSharedPreferences.PREFIX+key, object.toString());
        editor.commit();
    }

    public static void saveJSONArray(Context c, String prefName, String key, JSONArray array) {
        SharedPreferences settings = c.getSharedPreferences(prefName, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(JSONSharedPreferences.PREFIX+key, array.toString());
        editor.commit();
    }

    public static String getJSONString(Context c, String prefName, String key) throws JSONException {
        SharedPreferences settings = c.getSharedPreferences(prefName, 0);
        return settings.getString(JSONSharedPreferences.PREFIX+key, "{}");
    }

    public static JSONObject getJSONObject(Context c, String prefName, String key) throws JSONException {
        SharedPreferences settings = c.getSharedPreferences(prefName, 0);
        return new JSONObject(settings.getString(JSONSharedPreferences.PREFIX+key, "{}"));
    }

    public static JSONArray getJSONArray(Context c, String prefName, String key) throws JSONException {
        SharedPreferences settings = c.getSharedPreferences(prefName, 0);
        return new JSONArray(settings.getString(JSONSharedPreferences.PREFIX+key, "[]"));
    }

    public static Map<String, ?> getAll(Context c, String prefName) throws JSONException {
        SharedPreferences settings = c.getSharedPreferences(prefName, 0);
        return settings.getAll();
    }

    public static void remove(Context c, String prefName, String key) {
        SharedPreferences settings = c.getSharedPreferences(prefName, 0);
        if (settings.contains(JSONSharedPreferences.PREFIX+key)) {
            SharedPreferences.Editor editor = settings.edit();
            editor.remove(JSONSharedPreferences.PREFIX+key);
            editor.commit();
        }
    }

    public static void removeAll(Context c, String prefName) throws JSONException {
        SharedPreferences settings = c.getSharedPreferences(prefName, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.clear();
        editor.commit();
    }

    public static int count(Context c, String prefName) throws JSONException {
        SharedPreferences settings = c.getSharedPreferences(prefName, 0);
        return settings.getAll().size();
    }
}