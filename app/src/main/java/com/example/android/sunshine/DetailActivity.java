package com.example.android.sunshine;

import android.content.Intent;
import android.support.v4.app.ShareCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class DetailActivity extends AppCompatActivity {

    private static final String FORECAST_SHARE_HASHTAG = " #SunshineApp";

    private String mForecast;
    private TextView mWeatherDisplay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        mWeatherDisplay = (TextView) findViewById(R.id.tv_display_weather);
        Intent receivedIntent = getIntent();

        if (receivedIntent != null) {
            if (receivedIntent.hasExtra(Intent.EXTRA_TEXT)) {
                mForecast = receivedIntent.getStringExtra(Intent.EXTRA_TEXT);
                mWeatherDisplay.setText(mForecast);
            }
        }
    }

    private Intent createShareIntent() {
        Intent shareForecastIntent = ShareCompat.IntentBuilder.from(this)
                .setType("text/plain")
                .setText(FORECAST_SHARE_HASHTAG + mForecast)
                .getIntent();

        return shareForecastIntent;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.detail, menu);
        MenuItem shareItem = menu.findItem(R.id.action_share);
        shareItem.setIntent(createShareIntent());
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            Intent startSettingsActivity = new Intent(this, SettingsActivity.class);
            startActivity(startSettingsActivity);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
