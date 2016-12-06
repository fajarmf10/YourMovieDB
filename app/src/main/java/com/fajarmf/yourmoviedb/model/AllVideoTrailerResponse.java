package com.fajarmf.yourmoviedb.model;

import java.util.ArrayList;

/**
 * Created by znaznazna on 12/5/2016.
 */

public class AllVideoTrailerResponse {
    private int id;
    private ArrayList<VideoTrailer> results;

    public int getId() {
        return id;
    }

    public ArrayList<VideoTrailer> getResults() {
        return results;
    }

}
