package com.yadav.divya.popularmovies.adapters;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.yadav.divya.popularmovies.R;

public class TrailerAdapter extends CursorAdapter {
    public TrailerAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.listview_trailers, parent, false);
    }

    @Override
    public void bindView(View view, final Context context, Cursor cursor) {
        ImageView thumbnail = (ImageView) view.findViewById(R.id.thumbnail);
        String trailer_name = cursor.getString(2);
        final String trailer_source = cursor.getString(5);

        TextView name = (TextView) view.findViewById(R.id.name);
        name.setText(trailer_name);
        Picasso.with(context).load("http://img.youtube.com/vi/" + trailer_source + "/0.jpg").into(thumbnail);

        thumbnail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/watch?v=" + trailer_source));
                context.startActivity(intent);
            }
        });
    }
}
