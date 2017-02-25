package com.darelbitsy.dbweather.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.darelbitsy.dbweather.R;
import com.darelbitsy.dbweather.helper.ConstantHolder;
import com.darelbitsy.dbweather.helper.api.GetNewsesHelper;
import com.darelbitsy.dbweather.helper.api.GetWeatherHelper;
import com.darelbitsy.dbweather.model.news.News;
import com.darelbitsy.dbweather.model.weather.Weather;

import java.util.ArrayList;

/**
 * Created by Darel Bitsy on 13/02/17.
 * Welcome screen and initializer
 */

public class WelcomeActivity extends Activity {
    private Weather mWeather;
    private ArrayList<News> mNewses;
    private Intent mIntent;

    private class GetWeather extends GetWeatherHelper {
        GetWeather(Activity activity) {
            super(activity);
        }

        @Override
        protected void onPostExecute(Weather weather) {
            mWeather = weather;
            new GetNewses(WelcomeActivity.this).execute();
        }
    }

    private class GetNewses extends GetNewsesHelper {
        GetNewses(Context context) {
            super(context);
        }

        @Override
        protected void onPostExecute(ArrayList<News> newses) {
            mNewses = new ArrayList<>(newses);
            mIntent = new Intent(WelcomeActivity.this, MainActivity.class);
            mIntent.putExtra(ConstantHolder.WEATHER_DATA_KEY, mWeather);
            mIntent.putParcelableArrayListExtra(ConstantHolder.NEWS_DATA_KEY, mNewses);
            startActivity(mIntent);
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome_activity);
        new GetWeather(this).execute();
    }
}
