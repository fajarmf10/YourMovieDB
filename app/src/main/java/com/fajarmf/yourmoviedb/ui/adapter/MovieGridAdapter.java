package com.fajarmf.yourmoviedb.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.fajarmf.yourmoviedb.R;
import com.fajarmf.yourmoviedb.model.Movie;
import com.fajarmf.yourmoviedb.ui.activity.MainActivity;
import com.fajarmf.yourmoviedb.ui.activity.MovieDetailActivity;
import com.fajarmf.yourmoviedb.ui.fragment.DetailFragment;
import com.fajarmf.yourmoviedb.utils.PabloPicasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.fajarmf.yourmoviedb.ui.activity.MovieDetailActivity.DETAIL_FRAGMENT_TAG;
import static com.fajarmf.yourmoviedb.utils.Constants.ARG_MOVIE_DETAIL;

/**
 * Created by znaznazna on 12/5/2016.
 */

public class MovieGridAdapter  extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static Context mContext;
    private static List<Movie> mDatasetList;
    private int lastAnimatedItemPosition = -1;
    private boolean mTwoPane;

    public MovieGridAdapter(Context context, List<Movie> datasetList, boolean twoPane) {
        mContext = context;
        mDatasetList = datasetList;
        mTwoPane = twoPane;
    }

    public class MovieViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        @BindView(R.id.tv_title)
        TextView mTVTitle;
        @BindView(R.id.card_view)
        CardView mCardView;
        @BindView(R.id.iv_thumbnail)
        ImageView mIVThumbNail;
        @BindView(R.id.tv_rating)
        TextView mTVRating;


        public MovieViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            this.mIVThumbNail.setOnClickListener(this);
            view.setOnClickListener(this);
            view.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int itemPosition = getAdapterPosition();
            if (itemPosition != RecyclerView.NO_POSITION) {
                Movie movie = mDatasetList.get(itemPosition);

                switch (view.getId()) {
                    case R.id.iv_thumbnail:

                        Bundle bundle = new Bundle();
                        bundle.putParcelable(ARG_MOVIE_DETAIL, movie);

                        if (mTwoPane) {
                            addDetailFragmentForTwoPane(bundle);
                        } else {
                            Intent movieDetailIntent = new Intent(mContext, MovieDetailActivity.class);
                            movieDetailIntent.putExtras(bundle);
                            mContext.startActivity(movieDetailIntent);
                        }
                        break;

                    default:
                        Toast.makeText(mContext, "You clicked at position " + itemPosition + " on movie thumbnail : " +
                                movie.getTitle(), Toast.LENGTH_SHORT).show();
                }
            }

        }

        public void addDetailFragmentForTwoPane(Bundle bundle) {
            DetailFragment detailFragment = DetailFragment.newInstance(bundle);
            FragmentManager fragmentManager = ((MainActivity) mContext).getSupportFragmentManager();
            fragmentManager.beginTransaction().setCustomAnimations(android.support.design.R.anim.abc_grow_fade_in_from_bottom, android.support.design.R.anim.abc_shrink_fade_out_from_bottom).replace(R.id.detail_container, detailFragment, DETAIL_FRAGMENT_TAG).commit();
        }

        @Override
        public boolean onLongClick(View v) {
            int itemPosition = getAdapterPosition();
            return true;
        }
    }

    @Override
    public MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.movie_item_view, parent, false);
        return new MovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        MovieViewHolder cusHolder = (MovieViewHolder) holder;
        cusHolder.mTVTitle.setText(mDatasetList.get(position).getTitle());
        cusHolder.mTVRating.setText(String.valueOf(mDatasetList.get(position).getVoteAverage()));
        String completePosterPath = mDatasetList.get(position).getPosterPath();
        PabloPicasso.with(mContext).load(completePosterPath).
                placeholder(R.drawable.placeholder)
                .error(R.drawable.placeholder)
                .into(cusHolder.mIVThumbNail);
        cusHolder.mIVThumbNail.setVisibility(View.VISIBLE);
        setEnterAnimation(cusHolder.mCardView, position);
    }

    @Override
    public int getItemCount() {
        return mDatasetList.size();
    }

    private void setEnterAnimation(View viewToAnimate, int position) {
        if (position > lastAnimatedItemPosition) {
            Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.translate_up);
            viewToAnimate.startAnimation(animation);
            lastAnimatedItemPosition = position;
        }
    }

    @Override
    public void onViewDetachedFromWindow(final RecyclerView.ViewHolder holder) {
        ((MovieViewHolder) holder).mCardView.clearAnimation();
    }
}
