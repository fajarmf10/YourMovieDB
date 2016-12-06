package com.fajarmf.yourmoviedb.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by znaznazna on 12/5/2016.
 */

public class SPManagerFavMovies {
    private static final String PREFERENCE_NAME_FAV_MOVIES = "app_state_fav_movies";
    private static SPManagerFavMovies instance = null;
    private final SharedPreferences sharedPreferences;

    private SPManagerFavMovies(Context context) {
        sharedPreferences = context.getApplicationContext().getSharedPreferences(PREFERENCE_NAME_FAV_MOVIES, Context.MODE_PRIVATE);
    }

    public static synchronized SPManagerFavMovies getInstance(Context context) {
        if (instance == null)
            instance = new SPManagerFavMovies(context);
        return instance;
    }

    public boolean getBoolean(int key) {
        return sharedPreferences.getBoolean(String.valueOf(key), false);
    }

    public void putBoolean(int key, boolean value) {
        sharedPreferences.edit().putBoolean(String.valueOf(key), value).apply();
    }

    public void clear() {
        sharedPreferences.edit().clear().apply();
    }

}
