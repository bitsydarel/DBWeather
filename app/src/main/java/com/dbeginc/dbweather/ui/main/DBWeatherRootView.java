package com.dbeginc.dbweather.ui.main;

import android.support.annotation.NonNull;

import com.dbeginc.dbweather.models.datatypes.geonames.GeoName;
import com.dbeginc.dbweather.models.datatypes.news.Article;
import com.dbeginc.dbweather.models.datatypes.weather.WeatherData;

import java.util.List;

/**
 * Created by darel on 02.06.17.
 */

interface DBWeatherRootView {

    void updateWeather(@NonNull final WeatherData weatherData);

    void updateNews(@NonNull final List<Article> news);
}
