package com.yadav.divya.popularmovies.adapters;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.yadav.divya.popularmovies.R;


public class ReviewAdapter extends CursorAdapter {
    public ReviewAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public int getCount() {
        return super.getCount();
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.listview_review, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView author = (TextView) view.findViewById(R.id.author);
        TextView content = (TextView) view.findViewById(R.id.content);
        TextView url = (TextView) view.findViewById(R.id.url);

        author.setText(cursor.getString(2));
        content.setText(cursor.getString(3));
        url.setText(cursor.getString(4));
    }
}
