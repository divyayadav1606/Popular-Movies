package com.yadav.divya.popularmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.yadav.divya.popularmovies.data.MovieContract.MovieEntry;
import com.yadav.divya.popularmovies.data.MovieContract.ReviewsEntry;
import com.yadav.divya.popularmovies.data.MovieContract.Trailersentry;

public class MovieDbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;

    static final String DATABASE_NAME = "movies.db";

    public MovieDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String SQL_CREATE_MOVIE_TABLE  = "create table "
                + MovieEntry.TABLE_NAME + "(" + MovieEntry._ID
                + " INTEGER PRIMARY KEY, "
                + MovieEntry.COLUMN_POSTER_PATH + " TEXT NOT NULL, "
                + MovieEntry.COLUMN_OVERVIEW + " TEXT NOT NULL, "
                + MovieEntry.COLUMN_RELEASE_DATE + " TEXT NOT NULL, "
                + MovieEntry.COLUMN_MOVIE_TITLE + " TEXT NOT NULL, "
                + MovieEntry.COLUMN_VOTE_AVERAGE + " TEXT NOT NULL);";
        db.execSQL(SQL_CREATE_MOVIE_TABLE);

        String SQL_CREATE_REVIEWS_TABLE  = "create table "
                + MovieContract.ReviewsEntry.TABLE_NAME + "(" + ReviewsEntry._ID
                + " INTEGER, "
                + ReviewsEntry.COLUMN_MOVIE_ID
                + " INTEGER, "
                + ReviewsEntry.COLUMN_AUTHOR + " TEXT NOT NULL, "
                + ReviewsEntry.COLUMN_REVIEW_CONTENT + " TEXT NOT NULL, "
                + ReviewsEntry.COLUMN_REVIEW_URL + " TEXT NOT NULL, "
                + ReviewsEntry.COLUMN_REVIEW_ID + " TEXT PRIMARY KEY NOT NULL, "
                + "FOREIGN KEY (" + ReviewsEntry.COLUMN_MOVIE_ID + ") REFERENCES " +
                    MovieEntry.TABLE_NAME + " (" + MovieEntry._ID + "));";
        db.execSQL(SQL_CREATE_REVIEWS_TABLE);

        String SQL_CREATE_TRAILERS_TABLE  = "create table "
                + MovieContract.Trailersentry.TABLE_NAME + "(" + Trailersentry._ID
                + " INTEGER, "
                + Trailersentry.COLUMN_MOVIE_ID
                + " INTEGER, "
                + Trailersentry.COLUMN_TRAILER_NAME + " TEXT NOT NULL, "
                + Trailersentry.COLUMN_TRAILER_SIZE + " TEXT NOT NULL, "
                + Trailersentry.COLUMN_TRAILER_TYPE + " TEXT NOT NULL, "
                + Trailersentry.COLUMN_TRAILER_SOURCE + " TEXT PRIMARY KEY NOT NULL, "
                + "FOREIGN KEY (" + Trailersentry.COLUMN_MOVIE_ID + ") REFERENCES " +
                MovieEntry.TABLE_NAME + " (" + MovieEntry._ID + "));";
        db.execSQL(SQL_CREATE_TRAILERS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + MovieEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + ReviewsEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + Trailersentry.TABLE_NAME);
        onCreate(db);
    }
}
