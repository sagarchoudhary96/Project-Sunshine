package com.example.android.sunshine.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.example.android.sunshine.utilities.SunshineDateUtils;

import static com.example.android.sunshine.data.WeatherContract.CONTENT_AUTHORITY;
import static com.example.android.sunshine.data.WeatherContract.PATH_WEATHER;
import static com.example.android.sunshine.data.WeatherContract.WeatherEntry;

/**
 * Created by sagar on 7/6/17.
 */

public class WeatherProvider extends ContentProvider {

    public static final int CODE_WEATHER = 100;
    public static final int CODE_WEATHER_WITH_DATE = 101;

    private static final UriMatcher sUriMatcher = buildUriMatcher();
    WeatherDbHelper mOpenHelper;

    public static UriMatcher buildUriMatcher() {
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        uriMatcher.addURI(CONTENT_AUTHORITY, PATH_WEATHER, CODE_WEATHER);
        uriMatcher.addURI(CONTENT_AUTHORITY, PATH_WEATHER + "/#", CODE_WEATHER_WITH_DATE);

        return uriMatcher;
    }


    @Override
    public boolean onCreate() {
        mOpenHelper = new WeatherDbHelper(getContext());
        return true;
    }

    @Override
    public int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int match = sUriMatcher.match(uri);
        switch (match) {
            case CODE_WEATHER:
                db.beginTransaction();
                int rowsInserted = 0;
                try {
                    for(ContentValues value : values) {
                        long weatherDate = value.getAsLong(WeatherEntry.COLUMN_DATE);
                        if(!SunshineDateUtils.isDateNormalized(weatherDate)) {
                            throw new IllegalArgumentException("Date must be normalized");
                        }

                        long _id = db.insert(WeatherEntry.TABLE_NAME, null, value);
                        if (_id!=-1) {
                            rowsInserted++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }

                if (rowsInserted > 0) {
                    getContext().getContentResolver().notifyChange(uri, null);
                }

                return rowsInserted;
            default:
                return super.bulkInsert(uri, values);

        }
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        final SQLiteDatabase db = mOpenHelper.getReadableDatabase();
        int match = sUriMatcher.match(uri);
        Cursor result = null;

        switch (match) {
            case CODE_WEATHER:
                result = db.query(WeatherEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case CODE_WEATHER_WITH_DATE:

                String normalizedUtcDateString = uri.getLastPathSegment();

                String[] selectionArguments = new String[]{normalizedUtcDateString};

                result = mOpenHelper.getReadableDatabase().query(
        /* Table we are going to query */
                        WeatherContract.WeatherEntry.TABLE_NAME,
                        projection,
                        WeatherContract.WeatherEntry.COLUMN_DATE + " = ? ",
                        selectionArguments,
                        null,
                        null,
                        sortOrder);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        result.setNotificationUri(getContext().getContentResolver(), uri);
        return result;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }
}
