package com.yadav.divya.popularmovies.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yadav.divya.popularmovies.R;


public class ReviewAdapter extends CursorRecyclerViewAdapter<ReviewAdapter.ViewHolder> {
    Context mContext;

    public ReviewAdapter(Context context, Cursor c) {
        super(context, c);
        mContext = context;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView mAuthor;
        public TextView mReview;
        public ViewHolder(View view) {
            super(view);
            mAuthor = (TextView) view.findViewById(R.id.author);
            mReview = (TextView) view.findViewById(R.id.content);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.listview_review, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, Cursor cursor) {
        viewHolder.mAuthor.setText(cursor.getString(2));
        viewHolder.mReview.setText(cursor.getString(3));
    }
}
