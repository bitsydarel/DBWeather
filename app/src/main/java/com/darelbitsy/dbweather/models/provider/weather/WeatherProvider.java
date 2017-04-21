package com.darelbitsy.dbweather.models.provider.weather;

import com.darelbitsy.dbweather.models.datatypes.weather.WeatherInfo;

import io.reactivex.Single;

/**
 * Created by Darel Bitsy on 21/04/17.
 */

public abstract class WeatherProvider {

    abstract Single<WeatherInfo> getCurrentWeatherInfo();
    abstract void saveCurrentWeather(final WeatherInfo weatherInfo);
}
