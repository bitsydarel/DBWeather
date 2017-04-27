package com.darelbitsy.dbweather.provider;

import android.support.annotation.NonNull;

import com.darelbitsy.dbweather.DBWeatherApplication;
import com.darelbitsy.dbweather.models.datatypes.news.Article;
import com.darelbitsy.dbweather.models.datatypes.weather.Weather;
import com.darelbitsy.dbweather.provider.news.DatabaseNewsProvider;
import com.darelbitsy.dbweather.provider.news.NetworkNewsProvider;
import com.darelbitsy.dbweather.provider.weather.DatabaseWeatherProvider;
import com.darelbitsy.dbweather.provider.weather.NetworkWeatherProvider;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Single;

/**
 * Created by Darel Bitsy on 27/04/17.
 */

public class DataProvider implements IDataProvider {
    @Inject
    DatabaseWeatherProvider mDatabaseWeatherProvider;
    @Inject
    NetworkWeatherProvider mNetworkWeatherProvider;
    @Inject
    DatabaseNewsProvider mDatabaseNewsProvider;
    @Inject
    NetworkNewsProvider mNetworkNewsProvider;

    public DataProvider() {
        DBWeatherApplication.getComponent()
                .inject(this);
    }

    @Override
    public Single<List<Article>> getNewsFromApi() {
        return null;
    }

    @Override
    public Single<List<Article>> getNewsFromDatabase() {
        return mDatabaseNewsProvider.getNews();
    }

    @Override
    public Single<Weather> getWeatherFromApi() {
        return mNetworkWeatherProvider.getWeather();
    }

    @Override
    public Single<Weather> getWeatherFromDatabase() {
        return mDatabaseWeatherProvider.getWeather();
    }

    @Override
    public Single<Weather> getWeatherForCityFromApi(@NonNull final String cityName, final double latitude, final double longitude) {
        return mNetworkWeatherProvider.getWeatherForCity(cityName, latitude, longitude);
    }

    @Override
    public Single<Weather> getWeatherForCityFromDatabase(@NonNull final String cityName, final double latitude, final double longitude) {
        return mDatabaseWeatherProvider.getWeatherForCity(cityName, latitude, longitude);
    }
}
