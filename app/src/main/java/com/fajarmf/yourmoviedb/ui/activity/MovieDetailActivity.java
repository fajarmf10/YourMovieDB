package com.fajarmf.yourmoviedb.ui.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import com.fajarmf.yourmoviedb.R;
import com.fajarmf.yourmoviedb.ui.fragment.DetailFragment;

/**
 * Created by znaznazna on 12/5/2016.
 */

public class MovieDetailActivity extends AppCompatActivity {

    private static final String TAG = MovieDetailActivity.class.getSimpleName();
    public static final String DETAIL_FRAGMENT_TAG = "DFTAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        if (savedInstanceState != null) {
            return;
        } else {
            addDetailFragment();
        }
    }

    public void addDetailFragment() {
        if (!isFinishing()) {
            DetailFragment detailFragment = DetailFragment.newInstance(getIntent().getExtras());

            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.detail_container, detailFragment, DETAIL_FRAGMENT_TAG).commit();
        }
    }
}
