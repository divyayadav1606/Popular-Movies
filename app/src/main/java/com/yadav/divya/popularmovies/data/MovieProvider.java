package com.yadav.divya.popularmovies.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;

public class MovieProvider extends ContentProvider {
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private MovieDbHelper mOpenHelper;

    static final int MOVIES = 100;
    static final int MOVIE_WITH_ID = 101;
    static final int REVIEWS = 200;
    static final int REVIEWS_WITH_ID = 201;
    static final int TRAILERS = 300;
    static final int TRAILERS_WITH_ID = 301;
    static final int POPULAR = 400;
    static final int TOPRATED = 500;
    static final int FAVORITE = 600;

    public MovieProvider() {
    }

    static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        matcher.addURI(MovieContract.CONTENT_AUTHORITY, MovieContract.PATH_MOVIES, MOVIES);
        matcher.addURI(MovieContract.CONTENT_AUTHORITY, MovieContract.PATH_MOVIES + "/#", MOVIE_WITH_ID);
        matcher.addURI(MovieContract.CONTENT_AUTHORITY, MovieContract.PATH_REVIEWS, REVIEWS);
        matcher.addURI(MovieContract.CONTENT_AUTHORITY, MovieContract.PATH_REVIEWS + "/#", REVIEWS_WITH_ID);
        matcher.addURI(MovieContract.CONTENT_AUTHORITY, MovieContract.PATH_TRAILERS, TRAILERS);
        matcher.addURI(MovieContract.CONTENT_AUTHORITY, MovieContract.PATH_TRAILERS + "/#", TRAILERS_WITH_ID);
        matcher.addURI(MovieContract.CONTENT_AUTHORITY, MovieContract.PATH_POPULAR, POPULAR);
        matcher.addURI(MovieContract.CONTENT_AUTHORITY, MovieContract.PATH_TOPRATED, TOPRATED);
        matcher.addURI(MovieContract.CONTENT_AUTHORITY, MovieContract.PATH_FAV, FAVORITE);
        return matcher;
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int rowsDeleted;

        if ( null == selection )
            selection = "1";

        switch(sUriMatcher.match(uri)) {
            case MOVIES : {
                rowsDeleted = db.delete(MovieContract.MovieEntry.TABLE_NAME, selection, selectionArgs);
                break;
            }

            case REVIEWS: {
                rowsDeleted = db.delete(MovieContract.ReviewsEntry.TABLE_NAME, selection, selectionArgs);
                break;
            }

            case TRAILERS: {
                rowsDeleted = db.delete(MovieContract.Trailersentry.TABLE_NAME, selection, selectionArgs);
                break;
            }

            case POPULAR: {
                rowsDeleted = db.delete(MovieContract.PopularEntry.TABLE_NAME, selection, selectionArgs);
                break;
            }

            case TOPRATED: {
                rowsDeleted = db.delete(MovieContract.TopRatedentry.TABLE_NAME, selection, selectionArgs);
                break;
            }

            case FAVORITE: {
                rowsDeleted = db.delete(MovieContract.FavEntry.TABLE_NAME, selection, selectionArgs);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public String getType(@NonNull Uri uri) {
        switch(sUriMatcher.match(uri)){
            case MOVIE_WITH_ID:
                return MovieContract.MovieEntry.CONTENT_ITEM_TYPE;
            case MOVIES:
                return MovieContract.MovieEntry.CONTENT_TYPE;
            case REVIEWS:
                return MovieContract.ReviewsEntry.CONTENT_TYPE;
            case REVIEWS_WITH_ID:
                return MovieContract.ReviewsEntry.CONTENT_ITEM_TYPE;
            case TRAILERS:
                return MovieContract.Trailersentry.CONTENT_TYPE;
            case TRAILERS_WITH_ID:
                return MovieContract.Trailersentry.CONTENT_ITEM_TYPE;
            case POPULAR:
                return MovieContract.PopularEntry.CONTENT_TYPE;
            case TOPRATED:
                return MovieContract.TopRatedentry.CONTENT_TYPE;
            case FAVORITE:
                return MovieContract.FavEntry.CONTENT_TYPE;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        Uri returnUri;
        long _id;

        switch (sUriMatcher.match(uri)) {
            case MOVIES: {
                _id = db.insert(MovieContract.MovieEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = MovieContract.MovieEntry.buildMovieUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }

            case REVIEWS: {
                _id = db.insert(MovieContract.ReviewsEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = MovieContract.ReviewsEntry.buildMovieUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }

            case TRAILERS: {
                _id = db.insert(MovieContract.Trailersentry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = MovieContract.Trailersentry.buildMovieUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }

            case POPULAR: {
                _id = db.insert(MovieContract.PopularEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = MovieContract.PopularEntry.buildMovieUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }

            case TOPRATED: {
                _id = db.insert(MovieContract.TopRatedentry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = MovieContract.TopRatedentry.buildMovieUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }

            case FAVORITE: {
                _id = db.insert(MovieContract.FavEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = MovieContract.FavEntry.buildMovieUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new MovieDbHelper(getContext());
        return false;
    }

    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        Cursor retCursor;

        switch (sUriMatcher.match(uri)) {
            case MOVIES: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        MovieContract.MovieEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            }
            case MOVIE_WITH_ID: {
                selection = MovieContract.MovieEntry.TABLE_NAME + "." + MovieContract.MovieEntry._ID + " = ? ";
                retCursor = mOpenHelper.getReadableDatabase().query(
                        MovieContract.MovieEntry.TABLE_NAME,
                        projection,
                        selection,
                        new String[]{uri.getPathSegments().get(1)},
                        null,
                        null,
                        sortOrder);
                break;
            }
            case REVIEWS: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        MovieContract.ReviewsEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            }
            case REVIEWS_WITH_ID: {
                selection = MovieContract.ReviewsEntry.TABLE_NAME + "." + MovieContract.ReviewsEntry.COLUMN_MOVIE_ID + " = ? ";
                retCursor = mOpenHelper.getReadableDatabase().query(
                        MovieContract.ReviewsEntry.TABLE_NAME,
                        projection,
                        selection,
                        new String[]{uri.getPathSegments().get(1)},
                        null,
                        null,
                        sortOrder);
                break;
            }
            case TRAILERS: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        MovieContract.Trailersentry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            }
            case TRAILERS_WITH_ID: {
                selection = MovieContract.Trailersentry.TABLE_NAME + "." + MovieContract.Trailersentry.COLUMN_MOVIE_ID + " = ? ";
                retCursor = mOpenHelper.getReadableDatabase().query(
                        MovieContract.Trailersentry.TABLE_NAME,
                        projection,
                        selection,
                        new String[]{uri.getPathSegments().get(1)},
                        null,
                        null,
                        sortOrder);
                break;
            }

            case POPULAR: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        MovieContract.PopularEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            }
            case TOPRATED: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        MovieContract.TopRatedentry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            }
            case FAVORITE: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        MovieContract.FavEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            }

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int rowsUpdated;

        switch(sUriMatcher.match(uri)) {
            case MOVIES: {
                rowsUpdated = db.update(MovieContract.MovieEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            }

            case REVIEWS: {
                rowsUpdated = db.update(MovieContract.ReviewsEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            }

            case TRAILERS: {
                rowsUpdated = db.update(MovieContract.Trailersentry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            }

            case POPULAR: {
                rowsUpdated = db.update(MovieContract.PopularEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            }

            case TOPRATED: {
                rowsUpdated = db.update(MovieContract.TopRatedentry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            }

            case FAVORITE: {
                rowsUpdated = db.update(MovieContract.FavEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            }

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

    @Override
    public int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int returnCount = 0;
        Cursor cursor = null;

        switch (sUriMatcher.match(uri)) {
            case MOVIES: {
                db.beginTransaction();

                try {
                    for (ContentValues value : values) {

                        String whereClause = MovieContract.MovieEntry._ID + " = ?";

                        cursor = this.query(
                                MovieContract.MovieEntry.CONTENT_URI,
                                new String[]{MovieContract.MovieEntry._ID},
                                whereClause,
                                new String[]{value.get("_id").toString()},
                                null);

                        if (cursor == null)
                            return 0;

                        if (!cursor.moveToFirst()) {
                            long _id = db.insert(MovieContract.MovieEntry.TABLE_NAME, null, value);
                            if (_id != -1) {
                                returnCount++;
                            }
                        } else {//Update the movie information
                            db.update(MovieContract.MovieEntry.TABLE_NAME, value, whereClause, new String[]{value.get("_id").toString()});
                        }
                        cursor.close();
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            }

            case REVIEWS: {
                db.beginTransaction();
                try {
                    for (ContentValues value : values) {
                        String whereClause = MovieContract.ReviewsEntry._ID + " = ?";

                        cursor = this.query(
                                MovieContract.ReviewsEntry.CONTENT_URI,
                                new String[]{MovieContract.ReviewsEntry._ID},
                                whereClause,
                                new String[]{value.get("review_id").toString()},
                                null);

                        if (cursor == null)
                            return 0;

                        if (!cursor.moveToFirst()) {
                            long _id = db.insert(MovieContract.ReviewsEntry.TABLE_NAME, null, value);
                            if (_id != -1) {
                                returnCount++;
                            }
                        }
                        cursor.close();
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return  returnCount;
            }

            case TRAILERS: {
                db.beginTransaction();
                try {
                    for (ContentValues value : values) {
                        String whereClause = MovieContract.Trailersentry.COLUMN_TRAILER_SOURCE + " = ?";

                        cursor = this.query(
                                MovieContract.Trailersentry.CONTENT_URI,
                                new String[]{MovieContract.Trailersentry.COLUMN_TRAILER_SOURCE},
                                whereClause,
                                new String[]{value.get("source").toString()},
                                null);

                        if (cursor == null)
                            return 0;

                        if (!cursor.moveToFirst()) {
                            long _id = db.insert(MovieContract.Trailersentry.TABLE_NAME, null, value);
                            if (_id != -1) {
                                returnCount++;
                            }
                        }
                        cursor.close();
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            }

            case POPULAR: {
                db.beginTransaction();
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(MovieContract.PopularEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            }

            case TOPRATED: {
                db.beginTransaction();
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(MovieContract.TopRatedentry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            }

            case FAVORITE: {
                db.beginTransaction();
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(MovieContract.FavEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            }

            default:
                return super.bulkInsert(uri, values);
        }
    }

    public void shutdown() {
        mOpenHelper.close();
        super.shutdown();
    }
}
