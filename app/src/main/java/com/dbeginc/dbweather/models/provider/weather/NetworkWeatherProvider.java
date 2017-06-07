package com.dbeginc.dbweather.models.provider.weather;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.dbeginc.dbweather.models.api.adapters.WeatherRestAdapter;
import com.dbeginc.dbweather.models.datatypes.weather.Weather;
import com.dbeginc.dbweather.utils.helper.DatabaseOperation;
import com.dbeginc.dbweather.utils.services.WeatherDatabaseService;
import com.dbeginc.dbweather.utils.utility.weather.WeatherUtil;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Single;

import static com.dbeginc.dbweather.utils.holder.ConstantHolder.IS_FROM_CITY_KEY;
import static com.dbeginc.dbweather.utils.holder.ConstantHolder.SHOULD_WEATHER_BE_SAVED;
import static com.dbeginc.dbweather.utils.holder.ConstantHolder.WEATHER_DATA_KEY;

/**
 * Created by Darel Bitsy on 22/04/17.
 * Weather provider from network using DARK SKY API
 */

@Singleton
public class NetworkWeatherProvider implements IWeatherProvider {
    private final DatabaseOperation database;
    @Inject Context mApplicationContext;
    @Inject SharedPreferences sharedPreferences;
    @Inject WeatherRestAdapter mWeatherRestAdapter;

    @Inject
    public NetworkWeatherProvider() {
        database = DatabaseOperation.getInstance(mApplicationContext);
    }

    @Override
    public Single<Weather> getWeather() {
        return Single.fromCallable(() -> {
            final Double[] coordinates = WeatherUtil.getCoordinates(database);
            final Weather weather = mWeatherRestAdapter.getWeather(coordinates[0],
                    coordinates[1]);

            weather.setCityName(WeatherUtil.getLocationName(mApplicationContext,
                    coordinates[0],
                    coordinates[1]));

            final Intent intent = new Intent(mApplicationContext, WeatherDatabaseService.class);

            intent.putExtra(WEATHER_DATA_KEY,
                    weather);

            intent.putExtra(SHOULD_WEATHER_BE_SAVED, false);

            mApplicationContext.startService(intent);

            return weather;
        });
    }

    @Override
    public Single<Weather> getWeatherForCity(final String cityName, final double latitude, final double longitude) {
        return Single.fromCallable(() -> {
            final Weather weather = mWeatherRestAdapter.getWeather(latitude, longitude);
            weather.setCityName(cityName);

            final Intent intent = new Intent(mApplicationContext, WeatherDatabaseService.class);
            intent.putExtra(WEATHER_DATA_KEY, weather);

            if (sharedPreferences.getBoolean(SHOULD_WEATHER_BE_SAVED, false)) {
                intent.putExtra(SHOULD_WEATHER_BE_SAVED, true);
            } else { intent.putExtra(SHOULD_WEATHER_BE_SAVED, false); }

            intent.putExtra(IS_FROM_CITY_KEY, sharedPreferences.getBoolean(IS_FROM_CITY_KEY, false));

            mApplicationContext.startService(intent);

            return weather;
        });
    }
}
