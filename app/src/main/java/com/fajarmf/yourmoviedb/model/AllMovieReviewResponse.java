package com.fajarmf.yourmoviedb.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by znaznazna on 12/5/2016.
 */

public class AllMovieReviewResponse {
    int id;
    int page;
    private ArrayList<MovieReview> results;
    @SerializedName("total_pages")
    private long totalPages;
    @SerializedName("total_results")
    private long totalResults;

    public int getId() {
        return id;
    }

    public int getPage() {
        return page;
    }

    public ArrayList<MovieReview> getResults() {
        return results;
    }

    public long getTotalPages() {
        return totalPages;
    }

    public long getTotalResults() {
        return totalResults;
    }

}
