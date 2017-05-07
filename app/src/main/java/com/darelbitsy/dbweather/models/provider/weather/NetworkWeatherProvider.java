package com.darelbitsy.dbweather.models.provider.weather;

import android.content.Context;
import android.content.Intent;

import com.darelbitsy.dbweather.models.api.adapters.WeatherRestAdapter;
import com.darelbitsy.dbweather.models.datatypes.weather.Weather;
import com.darelbitsy.dbweather.utils.helper.DatabaseOperation;
import com.darelbitsy.dbweather.utils.services.WeatherDatabaseService;
import com.darelbitsy.dbweather.utils.utility.weather.WeatherUtil;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Single;

import static com.darelbitsy.dbweather.utils.holder.ConstantHolder.IS_FROM_CITY_KEY;
import static com.darelbitsy.dbweather.utils.holder.ConstantHolder.PREFS_NAME;
import static com.darelbitsy.dbweather.utils.holder.ConstantHolder.WEATHER_DATA_KEY;

/**
 * Created by Darel Bitsy on 22/04/17.
 * Weather provider from network using DARK SKY API
 */

@Singleton
public class NetworkWeatherProvider implements IWeatherProvider {
    private final DatabaseOperation database;
    @Inject Context mApplicationContext;
    @Inject WeatherRestAdapter mWeatherRestAdapter;

    @Inject
    public NetworkWeatherProvider() {
        database = DatabaseOperation.getInstance(mApplicationContext);
    }

    @Override
    public Single<Weather> getWeather() {
        return io.reactivex.Single.fromCallable(() -> {
            final Double[] coordinates = WeatherUtil.getCoordinates(database);
            final Weather weather = mWeatherRestAdapter.getWeather(coordinates[0],
                    coordinates[1]);

            weather.setCityName(WeatherUtil.getLocationName(mApplicationContext,
                    coordinates[0],
                    coordinates[1]));

            final Intent intent = new Intent(mApplicationContext, WeatherDatabaseService.class);

            intent.putExtra(WEATHER_DATA_KEY,
                    weather);

            intent.putExtra(IS_FROM_CITY_KEY, false);

            mApplicationContext.startService(intent);

            return weather;
        });
    }

    @Override
    public Single<Weather> getWeatherForCity(final String cityName,
                                             final double latitude,
                                             final double longitude) {
        return Single.fromCallable(() -> {
                final Weather weather = mWeatherRestAdapter.getWeather(latitude, longitude);
                weather.setCityName(cityName);

                final Intent intent = new Intent(mApplicationContext, WeatherDatabaseService.class);
                intent.putExtra(WEATHER_DATA_KEY, weather);
                intent.putExtra(IS_FROM_CITY_KEY,
                        mApplicationContext
                                .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                                .getBoolean(IS_FROM_CITY_KEY, false));

                mApplicationContext.startService(intent);

                return weather;
        });
    }
}
