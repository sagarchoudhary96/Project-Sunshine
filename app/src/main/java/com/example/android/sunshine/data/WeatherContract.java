package com.example.android.sunshine.data;

import android.provider.BaseColumns;

/**
 * Created by sagar on 4/6/17.
 */

public class WeatherContract {

    private WeatherContract() {

    }

    public static final class WeatherEntry implements BaseColumns {
        public static final String TABLE_NAME = "weather";
        public static  final String COLUMN_DATE = "date";
        public static final String COLUMN_WEATHER_ID = "weather_id";
        public static final String COLUMN_MIN_TEMP = "min";
        public static final String COLUMN_MAX_TEMP = "max";
        public static final String COLUMN_HUMIDITY = "humidity";
        public static final String COLUMN_PRESSURE = "pressure";
        public static final String COLUMN_WIND_SPEED = "wind";
        public static final String COLUMN_DEGREES = "degrees";

    }
}
