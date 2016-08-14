package com.yadav.divya.popularmovies;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
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

    public DetailFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ListView trailersList;
        ListView reviewsList;

        Bundle arguments = getArguments();
        if (arguments == null) {
            return null;
        }
        mUri = arguments.getParcelable(DetailFragment.DETAIL_URI);
        movie_id = mUri.getPathSegments().get(1);

        trailerListAdapter = new TrailerAdapter(getActivity(), null, 0);
        reviewListAdapter = new ReviewAdapter(getActivity(), null, 0);

        view = inflater.inflate(R.layout.fragment_detail, container, false);

        trailersList = (ListView) view.findViewById(R.id.listview_trailer);
        if (trailersList != null) {
            trailersList.setAdapter(trailerListAdapter);
        }

        reviewsList = (ListView) view.findViewById(R.id.listview_reviews);
        if (reviewsList != null) {
            reviewsList.setAdapter(reviewListAdapter);
        }
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
                    ImageView image = (ImageView) view.findViewById(R.id.imageView);
                    Picasso.with(getContext())
                            .load("http://image.tmdb.org/t/p/w185/" + data.getString(MovieFragment.COL_POSTER_PATH))
                            .into(image);

                    TextView plot = (TextView) view.findViewById(R.id.overview);
                    plot.setText(data.getString(MovieFragment.COL_OVERVIEW));

                    TextView year = (TextView) view.findViewById(R.id.release_year);
                    String[] date = data.getString(MovieFragment.COL_RELEASE_DATE).split("-");
                    year.setText(date[0]);

                    TextView title = (TextView) view.findViewById(R.id.title);
                    title.setText(data.getString(MovieFragment.COL_TITLE));

                    TextView rating = (TextView) view.findViewById(R.id.ranking);
                    rating.setText(data.getString(MovieFragment.COL_VOTE_AVERAGE) + "/10");
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
