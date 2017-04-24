package com.darelbitsy.dbweather.provider.weather;

import android.content.Context;
import android.content.Intent;

import com.darelbitsy.dbweather.models.api.adapters.network.WeatherRestAdapter;
import com.darelbitsy.dbweather.models.datatypes.weather.Weather;
import com.darelbitsy.dbweather.extensions.helper.DatabaseOperation;
import com.darelbitsy.dbweather.extensions.services.WeatherDatabaseService;
import com.darelbitsy.dbweather.extensions.utility.weather.WeatherUtil;

import io.reactivex.Single;

import static com.darelbitsy.dbweather.extensions.holder.ConstantHolder.IS_FROM_CITY_KEY;
import static com.darelbitsy.dbweather.extensions.holder.ConstantHolder.PREFS_NAME;
import static com.darelbitsy.dbweather.extensions.holder.ConstantHolder.WEATHER_DATA_KEY;

/**
 * Created by Darel Bitsy on 22/04/17.
 * Weather provider from network using DARK SKY API
 */

public class NetworkWeatherProvider implements IWeatherProvider<Weather> {
    private final Context mApplicationContext;
    private final DatabaseOperation database;
    private final WeatherRestAdapter mWeatherRestAdapter;

    public NetworkWeatherProvider(final Context context) {
        mApplicationContext = context.getApplicationContext();
        database = DatabaseOperation.newInstance(mApplicationContext);
        mWeatherRestAdapter = WeatherRestAdapter.newInstance(mApplicationContext);
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
