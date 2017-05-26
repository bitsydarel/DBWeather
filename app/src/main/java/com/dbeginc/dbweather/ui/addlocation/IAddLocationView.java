package com.dbeginc.dbweather.ui.addlocation;

import android.content.Context;

import com.dbeginc.dbweather.models.datatypes.geonames.GeoName;
import com.dbeginc.dbweather.models.datatypes.news.Article;
import com.dbeginc.dbweather.models.datatypes.weather.WeatherData;

import java.util.List;

import javax.annotation.Nonnull;

/**
 * Created by Bitsy Darel on 13.05.17.
 */

interface IAddLocationView {
    void saveWeatherForHomeButton(@Nonnull final WeatherData weatherData);

    void saveNewsForHomeButton(@Nonnull final List<Article> articles);

    void closeView();

    Context getContext();

    void onLocationAdded();

    void onLocationNotAdded();

    void displayFoundedLocation(final List<GeoName> locationList);
}
