package com.darelbitsy.dbweather.models.provider.weather;

import android.content.Context;
import android.util.Log;

import com.darelbitsy.dbweather.models.datatypes.weather.Daily;
import com.darelbitsy.dbweather.models.datatypes.weather.Hourly;
import com.darelbitsy.dbweather.models.datatypes.weather.Weather;
import com.darelbitsy.dbweather.extensions.helper.DatabaseOperation;
import com.darelbitsy.dbweather.extensions.holder.ConstantHolder;

import io.reactivex.Single;

/**
 * Created by Darel Bitsy on 22/04/17.
 * Weather provider from database
 */

public class DatabaseWeatherProvider implements IWeatherProvider<Weather> {

    private final DatabaseOperation database;

    public DatabaseWeatherProvider(final Context context) {
        database = DatabaseOperation.newInstance(context.getApplicationContext());
    }

    @Override
    public Single<Weather> getWeather() {
        return Single.create(emitter -> {
            try {
                final Weather weather = database.getWeatherData();
                weather.setCurrently(database.getCurrentWeatherFromDatabase());

                weather.setDaily(new Daily());
                weather.getDaily().setData(database.getDailyWeatherFromDatabase());

                weather.setHourly(new Hourly());
                weather.getHourly().setData(database.getHourlyWeatherFromDatabase());

                weather.setAlerts(database.getAlerts());

                if (!emitter.isDisposed()) { emitter.onSuccess(weather); }

            } catch (final Exception e) {
                Log.i(ConstantHolder.TAG, "Error from getObservableWeatherFromDatabase: "
                        + e.getMessage());
                if (!emitter.isDisposed()) { emitter.onError(e); }
            }
        });
    }

    @Override
    public Single<Weather> getWeatherForCity(final String cityName,
                                             final double latitude,
                                             final double longitude) {
        return Single.create(emitter -> {
            try {
                final Weather weather = new Weather();
                weather.setCityName(cityName);
                weather.setCurrently(database.getCurrentlyWeatherForCity(cityName));

                final Daily daily = new Daily();
                daily.setData(database.getDailyWeatherForCity(cityName));
                weather.setDaily(daily);

                final Hourly hourly = new Hourly();
                hourly.setData(database.getHourlyWeatherForCity(cityName));
                weather.setHourly(hourly);

                if (!emitter.isDisposed()) { emitter.onSuccess(weather); }

            } catch (final Exception e) { if (!emitter.isDisposed()) { emitter.onError(e); } }
        });
    }
}
