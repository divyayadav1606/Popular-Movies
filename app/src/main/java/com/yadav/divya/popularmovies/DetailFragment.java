package com.yadav.divya.popularmovies;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.yadav.divya.popularmovies.adapters.ReviewAdapter;
import com.yadav.divya.popularmovies.adapters.TrailerAdapter;
import com.yadav.divya.popularmovies.data.MovieContract;

public class DetailFragment extends Fragment implements  LoaderManager.LoaderCallbacks<Cursor> {

    private static final int DETAIL_LOADER = 0;
    private static final int TRAILER_LOADER = 1;
    private static final int REVIEW_LOADER = 2;
    private String movie_id;
    private TrailerAdapter trailerListAdapter;
    private ReviewAdapter reviewListAdapter;
    View view = null;
    static final String DETAIL_URI = "URI";
    private Uri mUri;

    public DetailFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        RecyclerView trailersList;
        RecyclerView reviewsList;

        Bundle arguments = getArguments();
        if (arguments == null) {
            return null;
        }
        mUri = arguments.getParcelable(DetailFragment.DETAIL_URI);
        movie_id = mUri.getPathSegments().get(1);

        trailerListAdapter = new TrailerAdapter(getActivity(), null);
        reviewListAdapter = new ReviewAdapter(getActivity(), null);

        view = inflater.inflate(R.layout.fragment_detail, container, false);

        trailersList = (RecyclerView) view.findViewById(R.id.listview_trailer);
        if (trailersList != null) {
            trailersList.setAdapter(trailerListAdapter);
            trailersList.setLayoutManager(new LinearLayoutManager(getActivity()));
        }

        reviewsList = (RecyclerView) view.findViewById(R.id.listview_reviews);
        if (reviewsList != null) {
            reviewsList.setAdapter(reviewListAdapter);
            reviewsList.setLayoutManager(new LinearLayoutManager(getActivity()));

        }

        view.findViewById(R.id.favbutton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContentValues values = new ContentValues();
                values.put(MovieContract.FavEntry._ID, movie_id);
                getContext().getContentResolver().insert(MovieContract.FavEntry.CONTENT_URI, values);
            }
        });
        FetchTrailerReviews task = new FetchTrailerReviews(getContext());
        task.execute(movie_id);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(DETAIL_LOADER, null, this);
        getLoaderManager().initLoader(REVIEW_LOADER, null, this);
        getLoaderManager().initLoader(TRAILER_LOADER, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (mUri == null) {
            return null;
        }

        switch(id) {
            case DETAIL_LOADER: {
                return new CursorLoader(
                        getActivity(),
                        mUri,
                        null,
                        null ,
                        null,
                        null);
            }

            case REVIEW_LOADER: {
                return new CursorLoader(
                        getActivity(),
                        MovieContract.ReviewsEntry.buildMovieUri(Integer.parseInt(movie_id)),
                        null,
                        null,
                        null,
                        null
                );
            }

            case TRAILER_LOADER: {
                return new CursorLoader(
                        getActivity(),
                        MovieContract.Trailersentry.buildMovieUri(Integer.parseInt(movie_id)),
                        null,
                        null,
                        null,
                        null
                );
            }

            default:
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (!data.moveToFirst()) {
            return;
        }

            switch (loader.getId()) {
                case DETAIL_LOADER: {
                    ImageView image = (ImageView) view.findViewById(R.id.movie_poster);
                    Picasso.with(getContext())
                            .load("http://image.tmdb.org/t/p/w185/" + data.getString(MovieFragment.COL_POSTER_PATH))
                            .into(image);

                    TextView title = (TextView) view.findViewById(R.id.movie_title);
                    title.setText(data.getString(MovieFragment.COL_TITLE));

                    TextView plot = (TextView) view.findViewById(R.id.overview);
                    plot.setText(data.getString(MovieFragment.COL_OVERVIEW));

                    TextView year = (TextView) view.findViewById(R.id.release_year);
                    String[] date = data.getString(MovieFragment.COL_RELEASE_DATE).split("-");
                    year.setText("Released: " + date[0]);

                    ImageView backdrop = (ImageView) view.findViewById(R.id.back_drop);
                    Picasso.with(getContext())
                            .load("http://image.tmdb.org/t/p/w185/" + data.getString(MovieFragment.COL_BACKDROP))
                            .into(backdrop);

                    TextView rating = (TextView) view.findViewById(R.id.ranking);
                    rating.setText("TmDb Rating: " + data.getString(MovieFragment.COL_VOTE_AVERAGE) + "/10");
                    break;
                }

                case REVIEW_LOADER: {
                    Log.d("APP", String.valueOf(data.getCount()));
                    reviewListAdapter.swapCursor(data);
                    break;
                }

                case TRAILER_LOADER: {
                    Log.d("APP", String.valueOf(data.getCount()));
                    trailerListAdapter.swapCursor(data);
                    break;
                }

                default: {

                }
            }
        }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        reviewListAdapter.swapCursor(null);
        trailerListAdapter.swapCursor(null);
    }

}
