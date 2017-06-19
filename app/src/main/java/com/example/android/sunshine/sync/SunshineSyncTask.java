package com.example.android.sunshine.sync;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;

import com.example.android.sunshine.data.WeatherContract;
import com.example.android.sunshine.utilities.NetworkUtils;
import com.example.android.sunshine.utilities.OpenWeatherJsonUtils;

import java.net.URL;

/**
 * Created by sagar on 25/6/17.
 */

public class SunshineSyncTask {
    synchronized public static void syncWeather(Context context) {
        try {
            URL weatherUrl = NetworkUtils.buildUrl(context);
            String jsonResponse = NetworkUtils.getResponseFromHttpUrl(weatherUrl);
            ContentValues[] weatherValues = OpenWeatherJsonUtils.getWeatherContentValuesFromJson(context, jsonResponse);

            if (weatherValues != null && weatherValues.length !=0) {
                ContentResolver sunshineResolver = context.getContentResolver();
                sunshineResolver.delete(WeatherContract.WeatherEntry.CONTENT_URI, null,null);

                sunshineResolver.bulkInsert(WeatherContract.WeatherEntry.CONTENT_URI, weatherValues);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
