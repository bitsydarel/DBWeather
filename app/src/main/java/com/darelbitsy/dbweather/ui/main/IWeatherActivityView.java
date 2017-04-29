package com.darelbitsy.dbweather.ui.main;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.util.Pair;

import com.darelbitsy.dbweather.models.datatypes.geonames.GeoName;
import com.darelbitsy.dbweather.models.datatypes.news.Article;
import com.darelbitsy.dbweather.models.datatypes.weather.HourlyData;
import com.darelbitsy.dbweather.models.datatypes.weather.WeatherInfo;

import java.util.List;

/**
 * Created by Darel Bitsy on 22/04/17.
 * Interface representing the mainScreen of the
 */

public interface IWeatherActivityView {

    void showWeather(final Pair<List<WeatherInfo>, List<HourlyData>> weather);

    void showNews(final List<Article> news);

    void showNetworkWeatherErrorMessage();

    void showNetworkNewsErrorMessage();

    void setupNavigationDrawerWithCities(final List<GeoName> listOfLocation);

    void setupNavigationDrawerWithNoCities();

    void launchActivity(@NonNull final Intent intent);

    void saveState(final Bundle bundle);

    Context getAppContext();

    void showScreenshotAttempError();
}
