package com.darelbitsy.dbweather.models.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.darelbitsy.dbweather.models.helper.DatabaseOperation;
import com.darelbitsy.dbweather.models.holder.ConstantHolder;
import com.darelbitsy.dbweather.models.utility.weather.WeatherUtil;
import com.darelbitsy.dbweather.models.datatypes.weather.Weather;

import static com.darelbitsy.dbweather.models.holder.ConstantHolder.IS_FROM_CITY_KEY;

/**
 * Created by Darel Bitsy on 25/02/17.
 * Background service that help
 * to save weather data in the database
 */

public class WeatherDatabaseService extends Service {
    @Nullable
    @Override
    public IBinder onBind(final Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(final Intent intent, final int flags, final int startId) {
        final DatabaseOperation database = DatabaseOperation.newInstance(this);

        final Weather weather = intent.getParcelableExtra(ConstantHolder.WEATHER_DATA_KEY);

        if (weather != null) {

            if (!intent.getBooleanExtra(IS_FROM_CITY_KEY, false)) {
                saveWeatherForCurrentLocation(database, weather);

            } else {
                saveWeatherFromCitiesLocation(database, weather);
            }
        }

        stopSelf();
        return START_NOT_STICKY;
    }

    private void saveWeatherFromCitiesLocation(final DatabaseOperation database,
                                               final Weather weather) {

        database.saveCurrentWeatherForCity(weather.getCityName(), weather.getCurrently());
        database.saveDailyWeatherForCity(weather.getCityName(), weather.getDaily().getData());
        database.saveHourlyWeatherForCity(weather.getCityName(), weather.getHourly().getData());
    }

    private void saveWeatherForCurrentLocation(final DatabaseOperation database, final Weather weather) {
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
}
