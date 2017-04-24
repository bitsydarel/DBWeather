package com.darelbitsy.dbweather.views.activities;

import android.os.Bundle;

import com.darelbitsy.dbweather.models.datatypes.geonames.GeoName;

import java.util.List;

/**
 * Created by Darel Bitsy on 22/04/17.
 * Interface representing the mainScreen of the
 */

public interface IWeatherActivityView<WEATHER, NEWS> {

    void requestWeatherUpdate();

    void requestNewsUpdate();

    void showWeather(final WEATHER weather);

    void showNews(final NEWS news);

    void showNetworkWeatherErrorMessage();

    void showNetworkNewsErrorMessage();

    void setupNavigationDrawerWithCities(final List<GeoName> listOfLocation);

    void setupNavigationDrawerWithNoCities();

    void setupNavigationDrawerMenu();

    void saveState(final Bundle bundle);
}
