package com.yadav.divya.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;

import com.yadav.divya.popularmovies.adapters.MovieAdapter;
import com.yadav.divya.popularmovies.data.MovieContract;

public class MovieFragment extends Fragment implements  LoaderManager.LoaderCallbacks<Cursor>{

    private static final int MOVIE_LOADER = 0;
    private MovieAdapter movieAdapter;
    static String POPULAR_FILTER = "popular";
    static String TOP_RATED_FILTER = "top_rated";
    static String FAVORITE_FILTER = "favorite";

    private static final String[] MOVIE_COLUMNS = {
            MovieContract.MovieEntry.TABLE_NAME + "." + MovieContract.MovieEntry._ID,
            MovieContract.MovieEntry.COLUMN_POSTER_PATH,
            MovieContract.MovieEntry.COLUMN_OVERVIEW,
            MovieContract.MovieEntry.COLUMN_RELEASE_DATE,
            MovieContract.MovieEntry.COLUMN_MOVIE_TITLE,
            MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE
    };

    static final int COL_MOVIE_ID = 0;
    static final int COL_POSTER_PATH = 1;
    static final int COL_OVERVIEW = 2;
    static final int COL_RELEASE_DATE = 3;
    static final int COL_TITLE = 4;
    static final int COL_VOTE_AVERAGE = 5;

    //Empty Constructor
    public MovieFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.popularmovies:
                callFetchMovieList(POPULAR_FILTER);
                return true;
            case R.id.highestrated:
                callFetchMovieList(TOP_RATED_FILTER);
                return true;
            case R.id.favorite:
                callFetchMovieList(FAVORITE_FILTER);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager)getContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        return activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        movieAdapter = new MovieAdapter(getActivity(), null, 0);

        View view = inflater.inflate(R.layout.fragment_movie, container, false);
        GridView gridview = (GridView) view.findViewById(R.id.gridview);
        gridview.setAdapter(movieAdapter);

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView parent, View v,
                                    int position, long id) {
                Cursor cursor = (Cursor) parent.getItemAtPosition(position);

                if (cursor != null) {
                    Intent intent = new Intent(getContext(), DetailActivity.class).setData(MovieContract.MovieEntry.buildMovieUri(cursor.getInt(COL_MOVIE_ID)));
                    startActivity(intent);
                }
            }
        });
        callFetchMovieList(POPULAR_FILTER);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(MOVIE_LOADER, null, this);
    }

    public void callFetchMovieList(String sort) {
        if (isOnline()) {
            FetchMovieList task = new FetchMovieList(getContext());
            task.execute(sort);
        } else {
            Toast.makeText(getContext(), R.string.no_internet, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getActivity(),
                MovieContract.MovieEntry.CONTENT_URI, MOVIE_COLUMNS, null, null, null);
    }

    @Override
    public void onLoadFinished(android.support.v4.content.Loader<Cursor> loader, Cursor data) {
        movieAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(android.support.v4.content.Loader<Cursor> loader) {
        movieAdapter.swapCursor(null);
    }
}
