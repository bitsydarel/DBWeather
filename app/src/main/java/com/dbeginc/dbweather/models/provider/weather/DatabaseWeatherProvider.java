package com.dbeginc.dbweather.models.provider.weather;

import android.content.Context;

import com.dbeginc.dbweather.utils.helper.DatabaseOperation;
import com.dbeginc.dbweather.models.datatypes.weather.Daily;
import com.dbeginc.dbweather.models.datatypes.weather.Hourly;
import com.dbeginc.dbweather.models.datatypes.weather.Weather;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Single;

/**
 * Created by Darel Bitsy on 22/04/17.
 * Weather provider from database
 */

@Singleton
public class DatabaseWeatherProvider implements IWeatherProvider {

    private final DatabaseOperation database;

    @Inject
    public DatabaseWeatherProvider(final Context context) {
        database = DatabaseOperation.getInstance(context.getApplicationContext());
    }

    @Override
    public Single<Weather> getWeather() {
        return Single.fromCallable(() -> {
            final Weather weather = database.getWeatherData();
            weather.setCurrently(database.getCurrentWeatherFromDatabase());

            weather.setDaily(new Daily());
            weather.getDaily().setData(database.getDailyWeatherFromDatabase());

            weather.setHourly(new Hourly());
            weather.getHourly().setData(database.getHourlyWeatherFromDatabase());

            weather.setAlerts(database.getAlerts());

            return weather;
        });
    }

    @Override
    public Single<Weather> getWeatherForCity(final String cityName,
                                             final double latitude,
                                             final double longitude) {
        return Single.fromCallable(() -> {
            final Weather weather = new Weather();
            weather.setCityName(cityName);
            weather.setCurrently(database.getCurrentlyWeatherForCity(cityName));

            final Daily daily = new Daily();
            daily.setData(database.getDailyWeatherForCity(cityName));
            weather.setDaily(daily);

            final Hourly hourly = new Hourly();
            hourly.setData(database.getHourlyWeatherForCity(cityName));
            weather.setHourly(hourly);

            return weather;
        });
    }
}
