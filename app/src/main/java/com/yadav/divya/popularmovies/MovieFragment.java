package com.yadav.divya.popularmovies;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnMenuTabClickListener;
import com.yadav.divya.popularmovies.adapters.MovieAdapter;
import com.yadav.divya.popularmovies.data.MovieContract;
import com.yadav.divya.popularmovies.sync.MovieSyncAdapter;

public class MovieFragment extends Fragment implements  LoaderManager.LoaderCallbacks<Cursor>{

    private static final int POPULAR_MOVIE_LOADER = 0;
    private static final int TOPRATED_MOVIE_LOADER = 1;
    private static final int FAVORITE_MOVIE_LOADER = 2;
    private GridView mGridView;
    private MovieAdapter movieAdapter;
    private LoaderManager.LoaderCallbacks<Cursor> mCallbacks;
    private BottomBar mBottomBar;
    private int mPosition = GridView.INVALID_POSITION;
    private static final String SELECTED_KEY = "selected_position";

    public static final String[] MOVIE_COLUMNS = {
            MovieContract.MovieEntry.TABLE_NAME + "." + MovieContract.MovieEntry._ID,
            MovieContract.MovieEntry.COLUMN_POSTER_PATH,
            MovieContract.MovieEntry.COLUMN_OVERVIEW,
            MovieContract.MovieEntry.COLUMN_RELEASE_DATE,
            MovieContract.MovieEntry.COLUMN_MOVIE_TITLE,
            MovieContract.MovieEntry.COLUMN_BACK_DROP,
            MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE
    };

    public static final int COL_MOVIE_ID = 0;
    public static final int COL_POSTER_PATH = 1;
    public static final int COL_OVERVIEW = 2;
    public static final int COL_RELEASE_DATE = 3;
    public static final int COL_TITLE = 4;
    public static final int COL_BACKDROP = 5;
    public static final int COL_VOTE_AVERAGE = 6;

    public interface Callback {
          void onItemSelected(Uri dateUri);
    }

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
            case R.id.refresh:
                RefreshMovieList();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        movieAdapter = new MovieAdapter(getActivity(), null, 0);
        mCallbacks = this;

        View view = inflater.inflate(R.layout.fragment_movie, container, false);
        mGridView = (GridView) view.findViewById(R.id.gridview);
        mGridView.setAdapter(movieAdapter);

        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView parent, View v,
                                    int position, long id) {
                Cursor cursor = (Cursor) parent.getItemAtPosition(position);
                mPosition = position;

                if (cursor != null) {
                    ((Callback) getActivity())
                            .onItemSelected(MovieContract.MovieEntry.buildMovieUri(cursor.getInt(COL_MOVIE_ID)));
                }
            }
        });

        //Bottom Navigation bar
        mBottomBar = BottomBar.attach(getActivity(), savedInstanceState);
        mBottomBar.noTabletGoodness();
        mBottomBar.setItems(R.menu.bottombar_menu);
        mBottomBar.useDarkTheme();
        mBottomBar.setOnMenuTabClickListener(new OnMenuTabClickListener() {
            @Override
            public void onMenuTabSelected(@IdRes int menuItemId) {
                if (menuItemId == R.id.bottomBarPopular) {
                    getLoaderManager().restartLoader(POPULAR_MOVIE_LOADER, null, mCallbacks);
                } else if (menuItemId == R.id.bottomBarTopRated) {
                    getLoaderManager().restartLoader(TOPRATED_MOVIE_LOADER, null, mCallbacks);
                } else if (menuItemId == R.id.bottomBarFavorite) {
                    getLoaderManager().restartLoader(FAVORITE_MOVIE_LOADER, null, mCallbacks);
                }
            }

            @Override
            public void onMenuTabReSelected(@IdRes int menuItemId) {

            }

        });

        if (savedInstanceState != null && savedInstanceState.containsKey(SELECTED_KEY)) {
            mPosition = savedInstanceState.getInt(SELECTED_KEY);
        }

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (mPosition != GridView.INVALID_POSITION) {
            outState.putInt(SELECTED_KEY, mPosition);
        }
        super.onSaveInstanceState(outState);
        mBottomBar.onSaveInstanceState(outState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(POPULAR_MOVIE_LOADER, null, this);
    }

    public void RefreshMovieList() {
        MovieSyncAdapter.syncImmediately(getActivity());
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {

            case POPULAR_MOVIE_LOADER:
                return new CursorLoader(getActivity(),
                        MovieContract.PopularEntry.CONTENT_URI, null, null, null, null);

            case TOPRATED_MOVIE_LOADER:
                return new CursorLoader(getActivity(),
                        MovieContract.TopRatedentry.CONTENT_URI, null, null, null, null);

            case FAVORITE_MOVIE_LOADER:
                return new CursorLoader(getActivity(),
                        MovieContract.FavEntry.CONTENT_URI, null, null, null, null);
        }
        return null;
    }

    @Override
    public void onLoadFinished(android.support.v4.content.Loader<Cursor> loader, Cursor data) {
        movieAdapter.swapCursor(data);
        if (mPosition != GridView.INVALID_POSITION) {
            mGridView.smoothScrollToPosition(mPosition);
        }
    }

    @Override
    public void onLoaderReset(android.support.v4.content.Loader<Cursor> loader) {
        movieAdapter.swapCursor(null);
    }
}
