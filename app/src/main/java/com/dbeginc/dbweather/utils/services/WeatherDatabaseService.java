package com.dbeginc.dbweather.utils.services;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;

import com.dbeginc.dbweather.models.datatypes.weather.Weather;
import com.dbeginc.dbweather.utils.helper.DatabaseOperation;
import com.dbeginc.dbweather.utils.holder.ConstantHolder;

import static com.dbeginc.dbweather.utils.holder.ConstantHolder.IS_FROM_CITY_KEY;
import static com.dbeginc.dbweather.utils.holder.ConstantHolder.SHOULD_WEATHER_BE_SAVED;

/**
 * Created by Darel Bitsy on 25/02/17.
 * Background service that help
 * to save weather data in the database
 */

public class WeatherDatabaseService extends IntentService {

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     */
    public WeatherDatabaseService() {
        super(WeatherDatabaseService.class.getSimpleName());
    }

    @Override
    protected void onHandleIntent(@Nullable final Intent intent) {
        final DatabaseOperation database = DatabaseOperation.getInstance(this);
        if (intent != null) {
            final Weather weather = intent.getParcelableExtra(ConstantHolder.WEATHER_DATA_KEY);
            if (weather != null) {
                if (intent.getBooleanExtra(SHOULD_WEATHER_BE_SAVED, false)) {
                    saveWeatherFromCitiesLocation(database, weather);

                } else if (intent.getBooleanExtra(IS_FROM_CITY_KEY, true)) {
                    saveWeatherForCurrentLocation(database, weather);
                }
            }
        }
    }

    private void saveWeatherFromCitiesLocation(final DatabaseOperation database,
                                               final Weather weather) {

        database.saveCurrentWeatherForCity(weather.getCityName(), weather.getCurrently());
        database.saveDailyWeatherForCity(weather.getCityName(), weather.getDaily().getData());
        database.saveHourlyWeatherForCity(weather.getCityName(), weather.getHourly().getData());
    }

    private void saveWeatherForCurrentLocation(final DatabaseOperation database, final Weather weather) {
        database.saveWeatherData(weather);
        database.saveCoordinates(weather.getLatitude(),
                weather.getLongitude());

        if (weather.getCurrently() != null) { database.saveCurrentWeather(weather.getCurrently()); }

        if (weather.getDaily() != null) { database.saveDailyWeather(weather.getDaily().getData()); }

        if (weather.getHourly() != null) { database.saveHourlyWeather(weather.getHourly().getData()); }

        if (weather.getMinutely() != null) {
            database.saveMinutelyWeather(weather.getMinutely()
                    .getData());
        }

        if (weather.getAlerts() != null) { database.saveAlerts(weather.getAlerts()); }
    }
}
