package com.yadav.divya.popularmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.yadav.divya.popularmovies.data.MovieContract.MovieEntry;
import com.yadav.divya.popularmovies.data.MovieContract.ReviewsEntry;
import com.yadav.divya.popularmovies.data.MovieContract.Trailersentry;
import com.yadav.divya.popularmovies.data.MovieContract.PopularEntry;
import com.yadav.divya.popularmovies.data.MovieContract.TopRatedentry;
import com.yadav.divya.popularmovies.data.MovieContract.FavEntry;

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
                + MovieEntry.COLUMN_VOTE_AVERAGE + " TEXT NOT NULL, "
                + MovieEntry.COLUMN_BACK_DROP + " TEXT);";
        db.execSQL(SQL_CREATE_MOVIE_TABLE);

        String SQL_CREATE_REVIEWS_TABLE  = "create table "
                + ReviewsEntry.TABLE_NAME + "(" + ReviewsEntry._ID
                + " INTEGER, "
                + ReviewsEntry.COLUMN_MOVIE_ID
                + " INTEGER, "
                + ReviewsEntry.COLUMN_AUTHOR + " TEXT, "
                + ReviewsEntry.COLUMN_REVIEW_CONTENT + " TEXT, "
                + ReviewsEntry.COLUMN_REVIEW_URL + " TEXT, "
                + ReviewsEntry.COLUMN_REVIEW_ID + " TEXT PRIMARY KEY NOT NULL, "
                + "FOREIGN KEY (" + ReviewsEntry.COLUMN_MOVIE_ID + ") REFERENCES " +
                    MovieEntry.TABLE_NAME + " (" + MovieEntry._ID + "));";
        db.execSQL(SQL_CREATE_REVIEWS_TABLE);

        String SQL_CREATE_TRAILERS_TABLE  = "create table "
                + Trailersentry.TABLE_NAME + "(" + Trailersentry._ID
                + " INTEGER, "
                + Trailersentry.COLUMN_MOVIE_ID
                + " INTEGER, "
                + Trailersentry.COLUMN_TRAILER_NAME + " TEXT, "
                + Trailersentry.COLUMN_TRAILER_SIZE + " TEXT, "
                + Trailersentry.COLUMN_TRAILER_TYPE + " TEXT, "
                + Trailersentry.COLUMN_TRAILER_SOURCE + " TEXT PRIMARY KEY NOT NULL, "
                + "FOREIGN KEY (" + Trailersentry.COLUMN_MOVIE_ID + ") REFERENCES " +
                MovieEntry.TABLE_NAME + " (" + MovieEntry._ID + "));";
        db.execSQL(SQL_CREATE_TRAILERS_TABLE);

        String SQL_CREATE_POPULAR_TABLE  = "create table "
                + PopularEntry.TABLE_NAME + "(" + PopularEntry._ID
                + " INTEGER, "
                + "FOREIGN KEY (" + PopularEntry._ID + ") REFERENCES " +
                MovieEntry.TABLE_NAME + " (" + MovieEntry._ID + "));";
        db.execSQL(SQL_CREATE_POPULAR_TABLE);

        String SQL_CREATE_TOPRATED_TABLE  = "create table "
                + TopRatedentry.TABLE_NAME + "(" + TopRatedentry._ID
                + " INTEGER, "
                + "FOREIGN KEY (" + TopRatedentry._ID + ") REFERENCES " +
                MovieEntry.TABLE_NAME + " (" + MovieEntry._ID + "));";
        db.execSQL(SQL_CREATE_TOPRATED_TABLE);

        String SQL_CREATE_FAV_TABLE  = "create table "
                + FavEntry.TABLE_NAME + "(" + FavEntry._ID
                + " INTEGER, "
                + "FOREIGN KEY (" + FavEntry._ID + ") REFERENCES " +
                MovieEntry.TABLE_NAME + " (" + MovieEntry._ID + "));";
        db.execSQL(SQL_CREATE_FAV_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + MovieEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + ReviewsEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + Trailersentry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + PopularEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + TopRatedentry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + FavEntry.TABLE_NAME);
        onCreate(db);
    }
}
