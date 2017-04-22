package com.darelbitsy.dbweather.models.provider.weather;

import com.darelbitsy.dbweather.models.datatypes.geonames.GeoName;

import io.reactivex.Single;

/**
 * Created by Darel Bitsy on 22/04/17.
 */

interface WeatherProvider<T> {
    Single<T> getWeather();

    Single<T> getWeatherForCity(final String cityName,
                                final double latitude,
                                final double longitude);
}
