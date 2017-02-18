package com.darelbitsy.dbweather.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.darelbitsy.dbweather.R;
import com.darelbitsy.dbweather.adapters.DatabaseOperation;
import com.darelbitsy.dbweather.helper.GetWeatherData;
import com.darelbitsy.dbweather.helper.WeatherCallHelper;

/**
 * Created by Darel Bitsy on 13/02/17.
 */

public class WelcomeActivity extends Activity {
    public static final String WEATHER_DATA_KEY = "weather_api_key";
    public static final String NEWS_DATA_KEY = "news_data_key";

    private GetWeatherData mWeatherData = new GetWeatherData(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome_activity);
        mWeatherData.execute();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent intent = new Intent(this, MainActivity1.class);
        intent.putExtra(WEATHER_DATA_KEY, mWeatherData.getWeatherApi());
        startActivity(intent);
    }
}
