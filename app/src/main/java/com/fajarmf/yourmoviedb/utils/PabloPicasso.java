package com.fajarmf.yourmoviedb.utils;

import android.content.Context;

import com.squareup.picasso.Picasso;

/**
 * Created by znaznazna on 12/5/2016.
 */

public class PabloPicasso {
    private static Picasso instance;

    public static Picasso with(Context context) {
        if (instance == null) {
            instance = new Picasso.Builder(context.getApplicationContext()).build();
        }
        return instance;
    }

    private PabloPicasso() {
        throw new AssertionError("No instances.");
    }
}
