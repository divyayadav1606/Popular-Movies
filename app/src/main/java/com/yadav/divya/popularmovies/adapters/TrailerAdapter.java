package com.yadav.divya.popularmovies.adapters;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.yadav.divya.popularmovies.R;

public class TrailerAdapter extends CursorRecyclerViewAdapter<TrailerAdapter.ViewHolder> {
    Context mContext;

    public TrailerAdapter(Context context, Cursor c) {
        super(context, c);
        mContext = context;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView thumbnail;
        public ViewHolder(View view) {
            super(view);
            thumbnail = (ImageView) view.findViewById(R.id.thumbnail);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.listview_trailers, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, Cursor cursor) {
        final String trailer_source = cursor.getString(5);

        Picasso.with(mContext).load("http://img.youtube.com/vi/" + trailer_source + "/0.jpg").into(viewHolder.thumbnail);

        viewHolder.thumbnail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/watch?v=" + trailer_source));
                mContext.startActivity(intent);
            }
        });
    }
}
