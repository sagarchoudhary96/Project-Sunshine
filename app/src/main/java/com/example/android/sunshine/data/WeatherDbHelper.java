package com.example.android.sunshine.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static com.example.android.sunshine.data.WeatherContract.*;

/**
 * Created by sagar on 4/6/17.
 */

public class WeatherDbHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "weather.db";

    private static final int DATABASE_VERSION = 1;

    public WeatherDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_WEATHER_TABLE = "CREATE TABLE " + WeatherEntry.TABLE_NAME + " (" +
                WeatherEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                WeatherEntry.COLUMN_DATE + " INTEGER, " +
                WeatherEntry.COLUMN_WEATHER_ID + " INTEGER, " +
                WeatherEntry.COLUMN_DEGREES + " REAL, " +
                WeatherEntry.COLUMN_HUMIDITY + " REAL, " +
                WeatherEntry.COLUMN_MAX_TEMP + " REAL, " +
                WeatherEntry.COLUMN_MIN_TEMP + " REAL, " +
                WeatherEntry.COLUMN_PRESSURE + " REAL, " +
                WeatherEntry.COLUMN_WIND_SPEED + " REAL" + ");";
        db.execSQL(SQL_CREATE_WEATHER_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

}
