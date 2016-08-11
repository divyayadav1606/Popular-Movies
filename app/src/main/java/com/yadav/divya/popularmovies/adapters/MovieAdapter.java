package com.yadav.divya.popularmovies.adapters;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

public class MovieAdapter extends CursorAdapter {

    public MovieAdapter(Activity context, Cursor cur, int flags) {
        super(context, cur, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        ImageView imageView;

        imageView = new ImageView(context);
        imageView.setAdjustViewBounds(true);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        return imageView;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        Picasso.with(context).load("http://image.tmdb.org/t/p/w185"+cursor.getString(1)).into((ImageView) view);
    }
}
