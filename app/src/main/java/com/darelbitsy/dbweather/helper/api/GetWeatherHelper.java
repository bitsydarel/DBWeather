package com.darelbitsy.dbweather.helper.api;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;

import com.darelbitsy.dbweather.adapters.DatabaseOperation;
import com.darelbitsy.dbweather.adapters.FeedDataInForeground;
import com.darelbitsy.dbweather.controller.api.adapters.WeatherAdapter;
import com.darelbitsy.dbweather.helper.AlarmConfigHelper;
import com.darelbitsy.dbweather.helper.ConstantHolder;
import com.darelbitsy.dbweather.helper.utility.AppUtil;
import com.darelbitsy.dbweather.helper.utility.WeatherUtil;
import com.darelbitsy.dbweather.model.weather.Weather;
import com.darelbitsy.dbweather.services.KillCheckerService;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;

import static com.darelbitsy.dbweather.helper.ConstantHolder.IS_ALARM_ON;
import static com.darelbitsy.dbweather.helper.ConstantHolder.TAG;

/**
 * Created by Darel Bitsy on 20/02/17.
 * This class help get Weather in background
 * Subclass will on override the onPostExecution
 * to choose of to display the data
 */

public class GetWeatherHelper extends AsyncTask<Object, Void, Weather> {
    private static WeatherAdapter mWeatherAdapter;
    private static DatabaseOperation mDatabase;
    private Activity mActivity;
    private Context mContext;

    public GetWeatherHelper(Context context) {

        if (mDatabase == null) {
            mDatabase = new DatabaseOperation(context);
        }
        mContext = context;

    }

    public GetWeatherHelper(Activity activity) {
        if (mDatabase == null) {
            mDatabase = new DatabaseOperation(activity);
        }
        mContext = activity;
        mActivity = activity;
    }

    @Override
    protected Weather doInBackground(Object[] params) {
        Weather weather = new Weather();

        if (mWeatherAdapter == null) { mWeatherAdapter = new WeatherAdapter(); }

        if (!AppUtil.isAlarmSet(mContext)) {
            ExecutorService executorService;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                executorService = new ForkJoinPool();
                executorService.submit(new AlarmConfigHelper(mContext)::setClothingNotificationAlarm);
                Log.i(TAG, "Setted the alarm from MainActivity");
                executorService.submit(() -> FeedDataInForeground.setNextSync(mContext));
                Log.i(TAG, "Setted the hourly sync from MainActivity");

            } else {
                executorService = Executors.newCachedThreadPool();
                executorService.submit(new AlarmConfigHelper(mContext)::setClothingNotificationAlarm);
                Log.i(TAG, "Setted the alarm from MainActivity");
                executorService.submit(() -> FeedDataInForeground.setNextSync(mContext));
                Log.i(TAG, "Setted the hourly sync from MainActivity");
            }

            mContext.startService(new Intent(mContext, KillCheckerService.class));
            executorService.shutdown();

        }

        mContext.getSharedPreferences(DatabaseOperation.PREFS_NAME, mContext.MODE_PRIVATE)
                .edit()
                .putBoolean(IS_ALARM_ON, true)
                .apply();

        if (AppUtil.isNetworkAvailable(mContext)) {
            try {

                Double[] coordinates = WeatherUtil.getCoordinates(mDatabase);

                weather = mWeatherAdapter.getWeather(coordinates[0], coordinates[1]);

                if (mActivity != null) {
                    weather.setCityName(WeatherUtil.getLocationName(mActivity,
                            coordinates[0],
                            coordinates[1]));

                } else {
                    weather.setCityName(WeatherUtil.getLocationName(mContext,
                            coordinates[0],
                            coordinates[1]));

                }

            } catch (IOException e) {
                Log.i(ConstantHolder.TAG, "error fetching the weather: "+e.getMessage());
            }

            mDatabase.saveWeatherData(weather);
            WeatherUtil.saveCoordinates(weather.getLatitude(), weather.getLongitude(), mDatabase);

            if (weather.getCurrently() != null) {
                mDatabase.saveCurrentWeather(weather.getCurrently());
            }
            if (weather.getDaily() != null) {
                mDatabase.saveDailyWeather(weather.getDaily().getData());
            }
            if (weather.getHourly() != null) {
                mDatabase.saveHourlyWeather(weather.getHourly().getData());
            }
            if (weather.getMinutely() != null) {
                mDatabase.saveMinutelyWeather(weather.getMinutely().getData());
            }
            if (weather.getAlerts() != null) {
                mDatabase.saveAlerts(weather.getAlerts());
            }

        } else {
            weather = mDatabase.getWeatherData();

        }

        return weather;
    }
}
