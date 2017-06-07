package com.dbeginc.dbweather.models.provider;

import android.support.annotation.NonNull;

import com.dbeginc.dbweather.models.datatypes.news.Article;
import com.dbeginc.dbweather.models.datatypes.weather.Weather;

import java.util.List;

import io.reactivex.Single;

/**
 * Created by Darel Bitsy on 27/04/17.
 * Data Provider
 */

public interface IDataProvider {

    Single<List<Article>> getNewsFromApi();

    Single<List<Article>> getNewsFromDatabase();

    Single<Weather> getWeatherFromApi();

    Single<Weather> getWeatherFromDatabase();

    Single<Weather> getWeatherForCityFromApi(@NonNull final String cityName, final double latitude, final double longitude);

    Single<Weather> getWeatherForCityFromDatabase(@NonNull final String cityName, final double latitude, final double longitude);
}
