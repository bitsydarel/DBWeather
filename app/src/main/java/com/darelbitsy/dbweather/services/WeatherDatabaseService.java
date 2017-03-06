package com.darelbitsy.dbweather.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.darelbitsy.dbweather.adapters.DatabaseOperation;
import com.darelbitsy.dbweather.helper.ConstantHolder;
import com.darelbitsy.dbweather.helper.utility.WeatherUtil;
import com.darelbitsy.dbweather.model.weather.Weather;

/**
 * Created by Darel Bitsy on 25/02/17.
 * Background service that help
 * to save weather data in the database
 */

public class WeatherDatabaseService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        final DatabaseOperation database = new DatabaseOperation(this);

        Weather weather = intent.getParcelableExtra(ConstantHolder.WEATHER_DATA_KEY);
        if (weather != null) {
            database.saveWeatherData(weather);

            WeatherUtil.saveCoordinates(weather.getLatitude(),
                    weather.getLongitude(),
                    database);

            if (weather.getCurrently() != null) {
                database.saveCurrentWeather(weather.getCurrently());
            }

            if (weather.getDaily() != null) {
                database.saveDailyWeather(weather.getDaily()
                        .getData());
            }

            if (weather.getHourly() != null) {
                database.saveHourlyWeather(weather.getHourly()
                        .getData());
            }

            if (weather.getMinutely() != null) {
                database.saveMinutelyWeather(weather.getMinutely()
                        .getData());
            }

            if (weather.getAlerts() != null) {
                database.saveAlerts(weather.getAlerts());
            }
        }
        return START_NOT_STICKY;
    }
}
