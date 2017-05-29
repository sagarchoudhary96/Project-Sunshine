/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.sunshine;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.android.sunshine.data.SunshinePreferences;
import com.example.android.sunshine.utilities.NetworkUtils;
import com.example.android.sunshine.utilities.OpenWeatherJsonUtils;

import java.net.URL;

public class MainActivity extends AppCompatActivity implements ForecastAdapter.ForecastAdapterOnClickHandler, LoaderManager.LoaderCallbacks<String[]>{

    private static final String TAG = MainActivity.class.getSimpleName();

    /*
    * This number will uniquely identify our Loader and is chosen arbitrarily. You can change this
    * to any number you like, as long as you use the same variable name.
    */
    private static final int WEATHER_SEARCH_LOADER = 1;

    private RecyclerView mRecyclerView;
    private ForecastAdapter mForecastAdapter;

    private TextView mErrorMessageDisplay;

    private ProgressBar mLoadingIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forecast);

        /*
         * Using findViewById, we get a reference to our RecyclerView from xml.
         */
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview);

        /* This TextView is used to display errors and will be hidden if there are no errors */
        mErrorMessageDisplay = (TextView) findViewById(R.id.errorDisplay);

        /*a LinearLayoutManager with VERTICAL orientation and
         * shouldReverseLayout == false
         */
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        // Set the layoutManager on mRecyclerView
        mRecyclerView.setLayoutManager(linearLayoutManager);

        /* Used setHasFixedSize(true) on mRecyclerView to designate that all
         * items in the list will have the same size
         */
        mRecyclerView.setHasFixedSize(true);

        // new ForecastAdapter
        mForecastAdapter = new ForecastAdapter(this);

        //  set mRecyclerView adapter to mForecastAdapter
        mRecyclerView.setAdapter(mForecastAdapter);
        /*
         * The ProgressBar that will indicate to the user that we are loading data. It will be
         * hidden when no data is loading.
         *
         * Please note: This so called "ProgressBar" isn't a bar by default. It is more of a
         * circle. We didn't make the rules (or the names of Views), we just follow them.
         */
        mLoadingIndicator = (ProgressBar) findViewById(R.id.progressDisplay);

        Bundle bundle = new Bundle();

        getSupportLoaderManager().initLoader(WEATHER_SEARCH_LOADER, bundle, MainActivity.this);
    }


    @Override
    public Loader<String[]> onCreateLoader(int id, Bundle args) {
        return new AsyncTaskLoader<String[]>(this) {

            String[] mWeatherData = null;


            @Override
            protected void onStartLoading() {
                if (mWeatherData != null) {
                    deliverResult(mWeatherData);
                }
                else {
                    mLoadingIndicator.setVisibility(View.VISIBLE);
                    forceLoad();
                }
            }

            @Override
            public String[] loadInBackground() {
                String location = SunshinePreferences.getPreferredWeatherLocation(MainActivity.this);
                URL weatherRequestUrl = NetworkUtils.buildUrl(location);

                try {
                    String jsonWeatherResponse = NetworkUtils
                            .getResponseFromHttpUrl(weatherRequestUrl);

                    return OpenWeatherJsonUtils
                            .getSimpleWeatherStringsFromJson(MainActivity.this, jsonWeatherResponse);

                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }

            }

            @Override
            public void deliverResult(String[] data) {
                mWeatherData = data;
                super.deliverResult(data);
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<String[]> loader, String[] weatherData) {
        mLoadingIndicator.setVisibility(View.INVISIBLE);
        if (weatherData != null) {
            showWeatherDataView();
            mForecastAdapter.setWeatherData(weatherData);
        } else {
            showErrorMessage();
        }
    }

    @Override
    public void onLoaderReset(Loader<String[]> loader) {

    }

    /**
     * This method is overridden by our MainActivity class in order to handle RecyclerView item
     * clicks.
     *
     * @param weatherForDay The weather for the day that was clicked
     */
    @Override
    public void OnItemClick(String weatherForDay) {
        Context context = this;
        Class destinationClass = DetailActivity.class;
        Intent intentToStartDetailActivity = new Intent(context, destinationClass);
        intentToStartDetailActivity.putExtra(Intent.EXTRA_TEXT, weatherForDay);
        startActivity(intentToStartDetailActivity);
    }

    /**
     * This method will make the View for the weather data visible and
     * hide the error message.
     * <p>
     * Since it is okay to redundantly set the visibility of a View, we don't
     * need to check whether each view is currently visible or invisible.
     */
    private void showWeatherDataView() {
        /* First, make sure the error is invisible */
        mErrorMessageDisplay.setVisibility(View.INVISIBLE);
        /* Then, make sure the weather data is visible */
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    /**
     * This method will make the error message visible and hide the weather
     * View.
     * <p>
     * Since it is okay to redundantly set the visibility of a View, we don't
     * need to check whether each view is currently visible or invisible.
     */
    private void showErrorMessage() {
        /* First, hide the currently visible data */
        mRecyclerView.setVisibility(View.INVISIBLE);
        /* Then, show the error */
        mErrorMessageDisplay.setVisibility(View.VISIBLE);
    }
    /**
     * This method uses the URI scheme for showing a location found on a
     * map. This super-handy intent is detailed in the "Common Intents"
     * page of Android's developer site:
     *
     * @see "http://developer.android.com/guide/components/intents-common.html#Maps"
     *
     * Hint: Hold Command on Mac or Control on Windows and click that link
     * to automagically open the Common Intents page
     */
    private void openLocationInMap() {
        String addressString = "1600 Ampitheatre Parkway, CA";
        Uri geoLocation = Uri.parse("geo:0,0?q=" + addressString);

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(geoLocation);

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        } else {
            Log.d(TAG, "Couldn't call " + geoLocation.toString()
                    + ", no receiving apps installed!");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        /* Use AppCompatActivity's method getMenuInflater to get a handle on the menu inflater */
        MenuInflater inflater = getMenuInflater();
        /* Use the inflater's inflate method to inflate our menu layout to this menu */
        inflater.inflate(R.menu.forecast, menu);
        /* Return true so that the menu is displayed in the Toolbar */
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_refresh) {
            mForecastAdapter.setWeatherData(null);
            getSupportLoaderManager().restartLoader(WEATHER_SEARCH_LOADER, null, this);
            return true;
        }

        if (id == R.id.action_map) {
            openLocationInMap();
            return true;
        }

        if (id == R.id.action_settings) {
            Intent startSettingsActivity = new Intent(this, SettingsActivity.class);
            startActivity(startSettingsActivity);
            return true;
        }


        return super.onOptionsItemSelected(item);
    }
}