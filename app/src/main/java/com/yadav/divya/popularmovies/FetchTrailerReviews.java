package com.yadav.divya.popularmovies;

import android.content.ContentValues;
import android.content.Context;
import android.database.DatabaseUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

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

public class FetchTrailerReviews extends AsyncTask <String, Void, Void> {

    private final Context mContext;

    public FetchTrailerReviews(Context context) {
        mContext = context;
    }

    private void getTrailersReviewsFromJson(String str) throws JSONException {
        JSONObject MovieJson = new JSONObject(str);
        String movie_id = MovieJson.getString("id");
        Log.d("APP", movie_id);
        JSONArray movieArrayReviews = MovieJson.getJSONObject("reviews").getJSONArray("results");
        JSONArray movieArrayTrailers = MovieJson.getJSONObject("trailers").getJSONArray("youtube");

        //bulk addition of Reviews
        Vector<ContentValues> cVVector = new Vector<>(movieArrayReviews.length());

        for(int i = 0; i < movieArrayReviews.length(); i++) {
            JSONObject movieReviews = movieArrayReviews.getJSONObject(i);
            ContentValues reviewValues = new ContentValues();

            reviewValues.put(MovieContract.ReviewsEntry.COLUMN_MOVIE_ID, movie_id);
            reviewValues.put(MovieContract.ReviewsEntry.COLUMN_REVIEW_ID, movieReviews.getString("id"));
            reviewValues.put(MovieContract.ReviewsEntry.COLUMN_AUTHOR, movieReviews.getString("author"));
            reviewValues.put(MovieContract.ReviewsEntry.COLUMN_REVIEW_CONTENT, DatabaseUtils.sqlEscapeString(movieReviews.getString("content")));
            reviewValues.put(MovieContract.ReviewsEntry.COLUMN_REVIEW_URL, movieReviews.getString("url"));
            cVVector.add(reviewValues);
        }

        if ( cVVector.size() > 0 ) {
            ContentValues[] cvArray = new ContentValues[cVVector.size()];
            cVVector.toArray(cvArray);
            mContext.getContentResolver().bulkInsert(MovieContract.ReviewsEntry.CONTENT_URI, cvArray);
        }

        //Bulk add trailers
        cVVector = new Vector<>(movieArrayTrailers.length());

        for(int i = 0; i < movieArrayTrailers.length(); i++) {
            JSONObject movieTrailers = movieArrayTrailers.getJSONObject(i);
            ContentValues trailersValues = new ContentValues();

            trailersValues.put(MovieContract.Trailersentry.COLUMN_MOVIE_ID, movie_id);
            trailersValues.put(MovieContract.Trailersentry.COLUMN_TRAILER_NAME, movieTrailers.getString("name"));
            trailersValues.put(MovieContract.Trailersentry.COLUMN_TRAILER_SIZE, movieTrailers.getString("size"));
            trailersValues.put(MovieContract.Trailersentry.COLUMN_TRAILER_SOURCE, movieTrailers.getString("source"));
            trailersValues.put(MovieContract.Trailersentry.COLUMN_TRAILER_TYPE, movieTrailers.getString("type"));

            cVVector.add(trailersValues);
        }

        if ( cVVector.size() > 0 ) {
            ContentValues[] cvArray = new ContentValues[cVVector.size()];
            cVVector.toArray(cvArray);
            mContext.getContentResolver().bulkInsert(MovieContract.Trailersentry.CONTENT_URI, cvArray);
        }
    }

    @Override
    protected Void doInBackground(String... params) {
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        try {
            Uri builtUri = Uri.parse("http://api.themoviedb.org/3/movie/").buildUpon()
                    .appendPath(params[0])
                    .appendQueryParameter("api_key",BuildConfig.API_KEY)
                    .appendQueryParameter("append_to_response", "trailers,reviews").build();

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
            Log.d("APP", str);
            try {
                getTrailersReviewsFromJson(str);
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
