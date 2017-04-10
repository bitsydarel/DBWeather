package com.darelbitsy.dbweather.controller.api.adapters.helper;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.darelbitsy.dbweather.adapters.database.DatabaseOperation;
import com.darelbitsy.dbweather.controller.api.adapters.network.WeatherAdapter;
import com.darelbitsy.dbweather.helper.holder.ConstantHolder;
import com.darelbitsy.dbweather.helper.services.WeatherDatabaseService;
import com.darelbitsy.dbweather.helper.utility.weather.WeatherUtil;
import com.darelbitsy.dbweather.model.weather.Daily;
import com.darelbitsy.dbweather.model.weather.Hourly;
import com.darelbitsy.dbweather.model.weather.Weather;

import io.reactivex.Single;

/**
 * Created by Darel Bitsy on 20/02/17.
 * This class help get Weather in background
 * Subclass will on override the onPostExecution
 * to choose of to display the data
 */

public class GetWeatherHelper {
    private final WeatherAdapter mWeatherAdapter;
    private final Context mContext;
    private static GetWeatherHelper singletonGetWeatherHelper;

    public static GetWeatherHelper newInstance(final Context context) {
        if (singletonGetWeatherHelper == null) {
            singletonGetWeatherHelper = new GetWeatherHelper(context.getApplicationContext());
        }
        return singletonGetWeatherHelper;
    }

    private GetWeatherHelper(final Context context) {
        mWeatherAdapter = new WeatherAdapter(context);
        mContext = context;
    }

    public Single<Weather> getObservableWeatherForCityFromApi(final String cityName,
                                                              final double latitude,
                                                              final  double longitude) {
        return Single.create(emitter -> {
            try {
                final Weather weather = mWeatherAdapter.getWeather(latitude, longitude);
                weather.setCityName(cityName);

                if (!emitter.isDisposed()) { emitter.onSuccess(weather); }

            } catch (final Exception e) { if (!emitter.isDisposed()) { emitter.onError(e); } }
        });
    }

    public Single<Weather> getObservableWeatherFromApi(final DatabaseOperation database) {

        return io.reactivex.Single.create(emitter -> {
            try {
                final Double[] coordinates = WeatherUtil.getCoordinates(database);
                final Weather weather = mWeatherAdapter.getWeather(coordinates[0],
                        coordinates[1]);

                weather.setCityName(WeatherUtil.getLocationName(mContext,
                        coordinates[0],
                        coordinates[1]));

                final Intent intent = new Intent(mContext, WeatherDatabaseService.class);
                intent.putExtra(ConstantHolder.WEATHER_DATA_KEY, weather);
                mContext.startService(intent);

                if (!emitter.isDisposed()) { emitter.onSuccess(weather); }

            } catch (final Exception e) { if (!emitter.isDisposed()) { emitter.onError(e); } }
        });
    }

    public Single<Weather> getObservableWeatherFromDatabase(final DatabaseOperation database) {
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
}