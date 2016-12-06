package com.fajarmf.yourmoviedb.data;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

/**
 * Created by znaznazna on 12/5/2016.
 */
import com.fajarmf.yourmoviedb.data.MovieContract.MovieEntry;
import com.fajarmf.yourmoviedb.interfaces.DBUpdateListener;
import com.fajarmf.yourmoviedb.model.Movie;

public class UpdateFavouriteMovieDBTask extends AsyncTask<Void, Void, Void> {

    private static final String TAG = UpdateFavouriteMovieDBTask.class.getSimpleName();

    private Context mContext;
    private Movie mMovie;
    private DBUpdateListener mDBUpdateListener;

    public static final int ADDED_TO_FAVORITE = 1;
    public static final int REMOVED_FROM_FAVORITE = 2;

    public UpdateFavouriteMovieDBTask(Context context, Movie movie, DBUpdateListener updateListener) {
        mDBUpdateListener = updateListener;
        mContext = context;
        mMovie = movie;
    }

    @Override
    protected Void doInBackground(Void... params) {
        deleteOrSaveFavoriteMovie();
        return null;
    }

    private void deleteOrSaveFavoriteMovie() {

        Log.d(TAG, MovieContract.MovieEntry.CONTENT_URI.getAuthority());
        Cursor favMovieCursor = mContext.getContentResolver().query(
                MovieContract.MovieEntry.CONTENT_URI,
                new String[]{MovieContract.MovieEntry.COLUMN_MOVIE_ID},
                MovieContract.MovieEntry.COLUMN_MOVIE_ID + " = ?",
                new String[]{String.valueOf(mMovie.getId())},
                null);

        if (favMovieCursor.moveToFirst()) {
            int rowDeleted = mContext.getContentResolver().delete(MovieContract.MovieEntry.CONTENT_URI,
                    MovieContract.MovieEntry.COLUMN_MOVIE_ID + " = ?",
                    new String[]{String.valueOf(mMovie.getId())});

            if (rowDeleted > 0) {
                mDBUpdateListener.onSuccess(REMOVED_FROM_FAVORITE);
            } else {
                mDBUpdateListener.onFailure();
            }
        }
        else {
            ContentValues values = new ContentValues();

            values.put(MovieEntry.COLUMN_MOVIE_ID, mMovie.getId());
            values.put(MovieEntry.COLUMN_TITLE, mMovie.getTitle());
            values.put(MovieEntry.COLUMN_POSTER_IMAGE, mMovie.getPosterPath());
            values.put(MovieEntry.COLUMN_OVERVIEW, mMovie.getOverview());
            values.put(MovieEntry.COLUMN_AVERAGE_RATING, mMovie.getVoteAverage());
            values.put(MovieEntry.COLUMN_RELEASE_DATE, mMovie.getReleaseDate());
            values.put(MovieEntry.COLUMN_BACKDROP_IMAGE, mMovie.getBackdropPath());

            Uri insertedUri = mContext.getContentResolver().insert(MovieContract.MovieEntry.CONTENT_URI, values);

            long movieRowId = ContentUris.parseId(insertedUri);

            if (movieRowId > 0) {
                mDBUpdateListener.onSuccess(ADDED_TO_FAVORITE);
            } else {
                mDBUpdateListener.onFailure();
            }
        }
        favMovieCursor.close();
    }
}
