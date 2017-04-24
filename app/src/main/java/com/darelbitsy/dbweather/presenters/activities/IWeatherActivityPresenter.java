package com.darelbitsy.dbweather.presenters.activities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.SparseArray;
import android.view.MenuItem;

import com.darelbitsy.dbweather.models.datatypes.geonames.GeoName;

import java.util.List;

/**
 * Created by Darel Bitsy on 22/04/17.
 */

public interface IWeatherActivityPresenter {

    void loadWeather();

    void loadNews();

    void loadWeatherForCity(@NonNull final String cityName,
                            final double latitude,
                            final double longitude);

    void loadUserCitiesMenu();

    void getWeather();

    void getNews();

    void getWeatherForCity(@NonNull final String cityName,
                              final double latitude,
                              final double longitude);

    void configureNewsMenu();

    void saveState(final Bundle save);
}
