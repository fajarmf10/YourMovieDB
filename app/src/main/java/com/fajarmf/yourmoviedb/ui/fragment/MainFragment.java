package com.fajarmf.yourmoviedb.ui.fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.fajarmf.yourmoviedb.R;
import com.fajarmf.yourmoviedb.app.AppController;
import com.fajarmf.yourmoviedb.data.MovieContract;
import com.fajarmf.yourmoviedb.model.AllMovieResponse;
import com.fajarmf.yourmoviedb.model.Language;
import com.fajarmf.yourmoviedb.model.Movie;
import com.fajarmf.yourmoviedb.network.DataManager;
import com.fajarmf.yourmoviedb.network.DataRequester;
import com.fajarmf.yourmoviedb.ui.activity.MainActivity;
import com.fajarmf.yourmoviedb.ui.adapter.MovieGridAdapter;
import com.fajarmf.yourmoviedb.ui.settings.SettingsActivity;
import com.fajarmf.yourmoviedb.utils.Constants;
import com.fajarmf.yourmoviedb.utils.NetworkUtils;
import com.fajarmf.yourmoviedb.utils.ProgressBarUtil;
import com.google.gson.Gson;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;

import static com.fajarmf.yourmoviedb.ui.activity.MovieDetailActivity.DETAIL_FRAGMENT_TAG;
import static com.fajarmf.yourmoviedb.utils.Constants.ARG_MOVIE_DETAIL;


/**
 * Created by znaznazna on 12/5/2016.
 */

public class MainFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = MainFragment.class.getSimpleName();
    public static final String SAVE_ALL_MOVIES_LIST = "ALL_MOVIES_LIST";
    public static final String SAVE_MOVIE_FILTER_SORT = "MOVIE_FILTER_SORT";

    @BindView(R.id.my_recycler_view)
    RecyclerView mRecyclerView;
    @BindView(R.id.network_retry_full_linearlayout)
    LinearLayout mNoNetworkRetryLayout;
    @BindView(R.id.button_retry)
    Button retryButton;
    @BindString(R.string.top_rated_movies)
    String topRatedMovies;
    @BindString(R.string.most_popular_movies)
    String mostPopularMovies;
    @BindString(R.string.my_favorite_movies)
    String myFavoriteMovies;
    @BindString(R.string.no_internet_connection)
    String mNoInternetCon;
    @BindString(R.string.reached_fav_end)
    String mReachedEndFav;
    @BindString(R.string.unable_to_reach_server)
    String mUnableToReachServer;

    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private ArrayList<Movie> mDatasetList;
    private DataManager mDataMan;
    private ProgressBarUtil mProgressBar;
    private ActionBar mActionBar;

    private SharedPreferences pref;
    private String mMovieFilterSort;
    private int mPage = 1;
    private MainActivity mActivity;
    private boolean mTwoPane = false;

    private static final String[] FAV_MOVIE_COLUMNS = {
            MovieContract.MovieEntry.TABLE_NAME + "." + MovieContract.MovieEntry._ID,
            MovieContract.MovieEntry.COLUMN_MOVIE_ID,
            MovieContract.MovieEntry.COLUMN_TITLE,
            MovieContract.MovieEntry.COLUMN_POSTER_IMAGE,
            MovieContract.MovieEntry.COLUMN_OVERVIEW,
            MovieContract.MovieEntry.COLUMN_AVERAGE_RATING,
            MovieContract.MovieEntry.COLUMN_RELEASE_DATE,
            MovieContract.MovieEntry.COLUMN_BACKDROP_IMAGE
    };

    static final int COL_MOVIE_ROW_ID = 0;
    static final int COL_MOVIE_CONDITION_ID = 1;
    static final int COL_TITLE = 2;
    static final int COL_POSTER_IMAGE = 3;
    static final int COL_OVERVIEW = 4;
    static final int COL_AVERAGE_RATING = 5;
    static final int COL_RELEASE_DATE = 6;
    static final int COL_BACKDROP_IMAGE = 7;


    public static MainFragment newInstance() {
        MainFragment fragment = new MainFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public MainFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            Bundle arg = getArguments();
        }

        mActivity = (MainActivity) getActivity();
        AppController app = ((AppController) mActivity.getApplication());
        mDataMan = app.getDataManager();

        mProgressBar = new ProgressBarUtil(mActivity);
        pref = PreferenceManager.getDefaultSharedPreferences(mActivity);

        mActionBar = ((AppCompatActivity) mActivity).getSupportActionBar();
        mActionBar.setTitle(mostPopularMovies);

        if (savedInstanceState == null || !savedInstanceState.containsKey(SAVE_ALL_MOVIES_LIST) ||
                !savedInstanceState.containsKey(SAVE_MOVIE_FILTER_SORT)) {
            Log.d(TAG, "savedInstanceState is null");
            mDatasetList = new ArrayList<Movie>();
            mMovieFilterSort = Constants.MOST_POPULAR;
        } else {
            mDatasetList = savedInstanceState.getParcelableArrayList(SAVE_ALL_MOVIES_LIST);
            mMovieFilterSort = savedInstanceState.getString(SAVE_MOVIE_FILTER_SORT);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        ButterKnife.bind(this, view);

        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new GridLayoutManager(mActivity, 2);

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            mLayoutManager = new GridLayoutManager(mActivity, 2);
        } else {
            mLayoutManager = new GridLayoutManager(mActivity, 3);
        }

        mRecyclerView.setLayoutManager(mLayoutManager);

        if (getActivity().findViewById(R.id.detail_container) != null) {
            mTwoPane = true;
        }

        mAdapter = new MovieGridAdapter(mActivity, mDatasetList, mTwoPane);
        mRecyclerView.setAdapter(mAdapter);

        retryButton.setOnClickListener(this);

        mRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (dy > 0) {
                    int visibleItemCount = recyclerView.getChildCount();
                    int totalItemCount = recyclerView.getLayoutManager().getItemCount();
                    int pastVisibleItem =
                            ((GridLayoutManager) recyclerView.getLayoutManager()).findFirstVisibleItemPosition();
                    if ((visibleItemCount + pastVisibleItem) >= totalItemCount) {
                        if (!mMovieFilterSort.equals(Constants.FAVORITE)) {

                            if (NetworkUtils.isOnline(mActivity)) {
                                mNoNetworkRetryLayout.setVisibility(View.GONE);
                                mPage++;
                                fetchMovies(false);
                            } else {
                                if (mDatasetList.isEmpty()) {
                                    NetworkUtils.showSnackbar(mRecyclerView, mNoInternetCon);
                                    mNoNetworkRetryLayout.setVisibility(View.VISIBLE);
                                }
                            }

                        } else {
                            NetworkUtils.showSnackbar(mRecyclerView, mReachedEndFav);
                        }
                    }
                }
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        String movieFilterSort = pref.getString(getString(R.string.sort_by_key), getString(R.string.sort_by_default));
        setActionBarTitle(movieFilterSort);
        if (!mMovieFilterSort.equalsIgnoreCase(movieFilterSort) || mDatasetList.isEmpty()) {
            mMovieFilterSort = movieFilterSort;
            fetchMovies(true);
        }

    }

    private void setActionBarTitle(String movieFilterSort) {
        if (movieFilterSort.equals(Constants.HIGHEST_RATED)) {
            mActionBar.setTitle(topRatedMovies);
        } else if (movieFilterSort.equals(Constants.MOST_POPULAR)) {
            mActionBar.setTitle(mostPopularMovies);
        } else if (movieFilterSort.equals(Constants.FAVORITE)) {
            mActionBar.setTitle(myFavoriteMovies);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_retry:
                if (NetworkUtils.isOnline(mActivity)) {
                    mNoNetworkRetryLayout.setVisibility(View.GONE);
                    fetchMovies(false);
                } else {
                    if (mDatasetList.isEmpty()) {
                        NetworkUtils.showSnackbar(mRecyclerView, mNoInternetCon);
                        mNoNetworkRetryLayout.setVisibility(View.VISIBLE);
                    }
                }

                break;
        }
    }

    private void fetchMovies(boolean refresh) {
        if (refresh) {
            mDatasetList.clear();
            mAdapter.notifyDataSetChanged();
            mPage = 1;
        }
        if (mMovieFilterSort.equals(Constants.FAVORITE)) {
            new LoadFavoriteMoviesTask().execute();
        } else {
            mProgressBar.show();
            Log.v(TAG, "Calling : get top rated/popular movies api according to filter");
            mDataMan.getMovies(
                    new WeakReference<DataRequester>(mMoviesRequester), mMovieFilterSort,
                    mPage, Language.LANGUAGE_EN.getValue(), TAG);

        }

    }

    private DataRequester mMoviesRequester = new DataRequester() {

        @Override
        public void onFailure(Throwable error) {
            if (!isAdded()) {
                return;
            }

            mProgressBar.hide();
            Log.v(TAG, "Failure : movies onFailure");

            mDatasetList.clear();
            mAdapter.notifyDataSetChanged();
            NetworkUtils.showSnackbar(mRecyclerView, mUnableToReachServer);
            mNoNetworkRetryLayout.setVisibility(View.VISIBLE);
        }

        @Override
        public void onSuccess(Object respObj) {
            if (!isAdded()) {
                return;
            }

            mProgressBar.hide();
            if (mNoNetworkRetryLayout.getVisibility() == View.VISIBLE) {
                mNoNetworkRetryLayout.setVisibility(View.GONE);
            }
            Log.v(TAG, "Success : movies data : " + new Gson().toJson(respObj).toString());
            AllMovieResponse response = (AllMovieResponse) respObj;

            if (response != null && response.getResults() != null && response.getResults().size() > 0) {
                List<Movie> movieList = response.getResults();
                for (Movie movie : movieList) {
                    mDatasetList.add(movie);
                }
                mAdapter.notifyDataSetChanged();

                if (mTwoPane) {
                    Log.d(TAG, "Tablet mode as mTwoPane : " + mTwoPane);

                    Bundle bundle = new Bundle();
                    bundle.putParcelable(ARG_MOVIE_DETAIL, mDatasetList.get(0));

                    addDetailFragmentForTwoPane(bundle);
                }
            }
        }
    };

    public void addDetailFragmentForTwoPane(Bundle bundle) {
        DetailFragment detailFragment = DetailFragment.newInstance(bundle);
        FragmentManager fragmentManager = mActivity.getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .setCustomAnimations(android.support.design.R.anim.abc_grow_fade_in_from_bottom,
                        android.support.design.R.anim.abc_shrink_fade_out_from_bottom)
                .replace(R.id.detail_container, detailFragment, DETAIL_FRAGMENT_TAG)
                .commit();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(mActivity, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private class LoadFavoriteMoviesTask extends AsyncTask<Void, Integer, Integer> {

        @Override
        protected Integer doInBackground(Void... params) {
            mDatasetList.clear();
            Uri favoriteMovieUri = MovieContract.MovieEntry.CONTENT_URI;
            Cursor favMovieCursor = mActivity.getContentResolver().query(
                    favoriteMovieUri,
                    FAV_MOVIE_COLUMNS,
                    null,
                    null,
                    null);

            if (favMovieCursor.moveToFirst()) {
                do {
                    Movie movie = new Movie(favMovieCursor.getInt(COL_MOVIE_CONDITION_ID),
                            favMovieCursor.getString(COL_TITLE),
                            favMovieCursor.getString(COL_POSTER_IMAGE),
                            favMovieCursor.getString(COL_OVERVIEW),
                            favMovieCursor.getDouble(COL_AVERAGE_RATING),
                            favMovieCursor.getString(COL_RELEASE_DATE),
                            favMovieCursor.getString(COL_BACKDROP_IMAGE)
                    );
                    mDatasetList.add(movie);
                } while (favMovieCursor.moveToNext());

            }

            favMovieCursor.close();
            return mDatasetList.size();
        }

        @Override
        protected void onPostExecute(Integer size) {
            super.onPostExecute(size);
            Log.d(TAG, "Favorite movie size : " + size);
            mAdapter.notifyDataSetChanged();
            if (size < 1) {
                NetworkUtils.showSnackbar(mRecyclerView, getResources().getString(R.string.no_favorite_movie));
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mDatasetList != null && !mDatasetList.isEmpty()) {
            outState.putParcelableArrayList(SAVE_ALL_MOVIES_LIST, mDatasetList);
        }
        outState.putString(SAVE_MOVIE_FILTER_SORT, mMovieFilterSort);
    }

}
