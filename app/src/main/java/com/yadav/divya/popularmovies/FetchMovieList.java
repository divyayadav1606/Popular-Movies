package com.yadav.divya.popularmovies;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;

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

public class FetchMovieList extends AsyncTask<String, Void, Void> {

    private final Context mContext;

    public FetchMovieList(Context context) {
        mContext = context;
    }

    private void getMovieListFromJson(String str) throws JSONException {
        JSONObject MovieJson = new JSONObject(str);
        JSONArray movieArray = MovieJson.getJSONArray("results");

        //bulk addition
        Vector<ContentValues> cVVector = new Vector<>(movieArray.length());

        for(int i = 0; i < movieArray.length(); i++) {
            JSONObject movieDetails = movieArray.getJSONObject(i);
            ContentValues movieValues = new ContentValues();

            movieValues.put(MovieContract.MovieEntry._ID, movieDetails.getString("id"));
            movieValues.put(MovieContract.MovieEntry.COLUMN_POSTER_PATH, movieDetails.getString("poster_path"));
            movieValues.put(MovieContract.MovieEntry.COLUMN_OVERVIEW, movieDetails.getString("overview"));
            movieValues.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, movieDetails.getString("release_date"));
            movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_TITLE, movieDetails.getString("title"));
            movieValues.put(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE, movieDetails.getString("vote_average"));
            cVVector.add(movieValues);
        }

        if ( cVVector.size() > 0 ) {
            ContentValues[] cvArray = new ContentValues[cVVector.size()];
            cVVector.toArray(cvArray);

            mContext.getContentResolver().bulkInsert(MovieContract.MovieEntry.CONTENT_URI, cvArray);
        }
    }

    @Override
    protected Void doInBackground(String... params) {
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        try {
            Uri builtUri = Uri.parse("http://api.themoviedb.org/3/movie/").buildUpon()
                    .appendPath(params[0])
                    .appendQueryParameter("api_key",BuildConfig.API_KEY).build();

            URL url = new URL(builtUri.toString());

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            InputStream inputStream = urlConnection.getInputStream();
            StringBuilder buffer = new StringBuilder();
            if (inputStream == null) {
                // Nothing to do.
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line).append("\n");
            }

            if (buffer.length() == 0) {
                return null;
            }

            String str = buffer.toString();
            try {
                getMovieListFromJson(str);
            }   catch (JSONException e) {
                return null;
            }
        } catch (IOException e) {
            return null;
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
        return null;
    }
}
