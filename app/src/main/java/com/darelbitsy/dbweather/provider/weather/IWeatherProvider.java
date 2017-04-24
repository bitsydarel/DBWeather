package com.darelbitsy.dbweather.provider.weather;

import io.reactivex.Single;

/**
 * Created by Darel Bitsy on 22/04/17.
 */

interface IWeatherProvider<T> {
    Single<T> getWeather();

    Single<T> getWeatherForCity(final String cityName,
                                final double latitude,
                                final double longitude);
}
