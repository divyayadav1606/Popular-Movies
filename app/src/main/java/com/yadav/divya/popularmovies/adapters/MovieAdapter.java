package com.yadav.divya.popularmovies.adapters;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.yadav.divya.popularmovies.MovieFragment;
import com.yadav.divya.popularmovies.data.MovieContract;

public class MovieAdapter extends CursorAdapter {

    public MovieAdapter(Activity context, Cursor cur, int flags) {
        super(context, cur, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        ImageView imageView;

        imageView = new ImageView(context);
        imageView.setAdjustViewBounds(true);
        imageView.setPadding(5,5,5,5);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        return imageView;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        //Get the movie id for Popular/Top_Rated and Favorite
        // list and get the movie details from the Movie Table
        if (cursor == null)
            return;
        String whereClause = MovieContract.MovieEntry.TABLE_NAME + "." + MovieContract.MovieEntry._ID + " = ? ";
        Cursor retCursor = context.getContentResolver().query(
                MovieContract.MovieEntry.CONTENT_URI,
                MovieFragment.MOVIE_COLUMNS,
                whereClause,
                new String[]{cursor.getString(0)},
                null);

        if (retCursor.moveToFirst())
            Picasso.with(context).load("http://image.tmdb.org/t/p/w185"+retCursor.getString(1)).into((ImageView) view);
    }
}
