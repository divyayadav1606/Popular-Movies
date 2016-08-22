package com.yadav.divya.popularmovies.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SyncRequest;
import android.content.SyncResult;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.yadav.divya.popularmovies.BuildConfig;
import com.yadav.divya.popularmovies.R;
import com.yadav.divya.popularmovies.data.MovieContract;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Vector;

public class MovieSyncAdapter extends AbstractThreadedSyncAdapter {
    public static final int SYNC_INTERVAL = 3600;
    public static final int SYNC_FLEXTIME = SYNC_INTERVAL/3;

    static final int POPULAR_SORT = 1;
    static final int TOP_RATED_SORT = 2;

    public MovieSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
    }

    private void getMovieListFromJson(String str, Integer sort) throws JSONException {
        JSONObject MovieJson = new JSONObject(str);
        JSONArray movieArray = MovieJson.getJSONArray("results");

        //bulk addition
        Vector<ContentValues> cVVector = new Vector<>(movieArray.length());
        Vector<ContentValues> idVector = new Vector<>(movieArray.length());

        for(int i = 0; i < movieArray.length(); i++) {
            JSONObject movieDetails = movieArray.getJSONObject(i);
            ContentValues movieValues = new ContentValues();
            ContentValues idValues = new ContentValues();

            movieValues.put(MovieContract.MovieEntry._ID, movieDetails.getString("id"));
            movieValues.put(MovieContract.MovieEntry.COLUMN_POSTER_PATH, movieDetails.getString("poster_path"));
            movieValues.put(MovieContract.MovieEntry.COLUMN_OVERVIEW, movieDetails.getString("overview"));
            movieValues.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, movieDetails.getString("release_date"));
            movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_TITLE, movieDetails.getString("title"));
            movieValues.put(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE, movieDetails.getString("vote_average"));
            movieValues.put(MovieContract.MovieEntry.COLUMN_BACK_DROP, movieDetails.getString("backdrop_path"));

            switch (sort) {
                case POPULAR_SORT:
                    idValues.put(MovieContract.PopularEntry._ID, movieDetails.getString("id"));
                    break;
                case TOP_RATED_SORT:
                    idValues.put(MovieContract.TopRatedentry._ID, movieDetails.getString("id"));
                    break;
            }
            cVVector.add(movieValues);
            idVector.add(idValues);
        }

        if ( cVVector.size() > 0 ) {
            ContentValues[] cvArray = new ContentValues[cVVector.size()];
            cVVector.toArray(cvArray);

            getContext().getContentResolver().bulkInsert(MovieContract.MovieEntry.CONTENT_URI, cvArray);
        }

        if ( idVector.size() > 0 ) {
            ContentValues[] idArray = new ContentValues[idVector.size()];
            idVector.toArray(idArray);

            switch(sort) {
                case POPULAR_SORT:
                    getContext().getContentResolver().bulkInsert(MovieContract.PopularEntry.CONTENT_URI, idArray);
                    break;

                case TOP_RATED_SORT:
                    getContext().getContentResolver().bulkInsert(MovieContract.TopRatedentry.CONTENT_URI, idArray);
                    break;
            }
        }
    }

    private void getPopularMovies() {

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        try {
            Uri builtUri = Uri.parse("http://api.themoviedb.org/3/movie/popular").buildUpon()
                    .appendQueryParameter("api_key", BuildConfig.API_KEY).build();

            URL url = new URL(builtUri.toString());

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            InputStream inputStream = urlConnection.getInputStream();
            StringBuilder buffer = new StringBuilder();
            if (inputStream == null) {
                // Nothing to do.
                return;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line).append("\n");
            }

            if (buffer.length() == 0) {
                return;
            }

            String str = buffer.toString();
            Log.d("APP", str);
            //Empty Popular List
            getContext().getContentResolver().delete(MovieContract.PopularEntry.CONTENT_URI, null, null);
            try {
                getMovieListFromJson(str, POPULAR_SORT);
            }   catch (JSONException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException ignored) {
                }
            }
        }
    }

    private void getTopRatedMovies() {

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        try {
            Uri builtUri = Uri.parse("http://api.themoviedb.org/3/movie/top_rated").buildUpon()
                    .appendQueryParameter("api_key", BuildConfig.API_KEY).build();

            URL url = new URL(builtUri.toString());

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            InputStream inputStream = urlConnection.getInputStream();
            StringBuilder buffer = new StringBuilder();
            if (inputStream == null) {
                // Nothing to do.
                return;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line).append("\n");
            }

            if (buffer.length() == 0) {
                return;
            }

            String str = buffer.toString();
            Log.d("APP", str);
            //Empty Top Rated List
            getContext().getContentResolver().delete(MovieContract.TopRatedentry.CONTENT_URI, null, null);
            try {
                getMovieListFromJson(str, TOP_RATED_SORT);
            }   catch (JSONException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException ignored) {
                }
            }
        }
    }

    @Override
    public void onPerformSync(Account account,
                              Bundle extras,
                              String authority,
                              ContentProviderClient provider,
                              SyncResult syncResult) {
        getPopularMovies();
        getTopRatedMovies();
    }

    public static void configurePeriodicSync(Context context, int syncInterval, int flexTime) {
        Account account = getSyncAccount(context);
        String authority = context.getString(R.string.content_authority);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // we can enable inexact timers in our periodic sync
            SyncRequest request = new SyncRequest.Builder().
                    syncPeriodic(syncInterval, flexTime).
                    setSyncAdapter(account, authority).
                    setExtras(new Bundle()).build();
            ContentResolver.requestSync(request);
        } else {
            ContentResolver.addPeriodicSync(account,
                    authority, new Bundle(), syncInterval);
        }
    }

    public static void syncImmediately(Context context) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        ContentResolver.requestSync(getSyncAccount(context),
                context.getString(R.string.content_authority), bundle);
    }

    private static void onAccountCreated(Account newAccount, Context context) {
        MovieSyncAdapter.configurePeriodicSync(context, SYNC_INTERVAL, SYNC_FLEXTIME);
        ContentResolver.setSyncAutomatically(newAccount, context.getString(R.string.content_authority), true);
        syncImmediately(context);
    }

    public static Account getSyncAccount(Context context) {
        AccountManager accountManager =
                (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);

        Account newAccount = new Account(
                context.getString(R.string.app_name), context.getString(R.string.sync_account_type));

        // If the password doesn't exist, the account doesn't exist
        if ( null == accountManager.getPassword(newAccount) ) {

        /*
         * Add the account and account type, no password or user data
         * If successful, return the Account object, otherwise report an error.
         */
            if (!accountManager.addAccountExplicitly(newAccount, "", null)) {
                return null;
            }
            /*
             * If you don't set android:syncable="true" in
             * in your <provider> element in the manifest,
             * then call ContentResolver.setIsSyncable(account, AUTHORITY, 1)
             * here.
             */

            onAccountCreated(newAccount, context);
        }
        return newAccount;
    }

    public static void initializeSyncAdapter(Context context) {
        getSyncAccount(context);
    }
}
