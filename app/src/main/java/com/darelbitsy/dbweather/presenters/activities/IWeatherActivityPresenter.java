package com.darelbitsy.dbweather.presenters.activities;

import android.os.Bundle;
import android.support.annotation.NonNull;

import com.darelbitsy.dbweather.models.datatypes.geonames.GeoName;

/**
 * Created by Darel Bitsy on 22/04/17.
 * Interface representing the functionality
 * of the Weather activity
 */

public interface IWeatherActivityPresenter {

    void loadWeather();

    void loadNews();

    void loadWeatherForCity(@NonNull final String cityName,
                            final double latitude,
                            final double longitude);

    void loadUserCitiesMenu();

    void removeCityFromUserCities(@NonNull final GeoName location);

    void getWeather();

    void getNews();

    void getWeatherForCity(@NonNull final String cityName,
                              final double latitude,
                              final double longitude);
    
    void saveState(final Bundle save);

    void clearState();
}
