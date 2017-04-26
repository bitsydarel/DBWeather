package com.darelbitsy.dbweather.provider.weather;

import com.darelbitsy.dbweather.models.datatypes.weather.Weather;

import io.reactivex.Single;

/**
 * Created by Darel Bitsy on 22/04/17.
 */

interface IWeatherProvider {
    Single<Weather> getWeather();

    Single<Weather> getWeatherForCity(final String cityName,
                                final double latitude,
                                final double longitude);
}
