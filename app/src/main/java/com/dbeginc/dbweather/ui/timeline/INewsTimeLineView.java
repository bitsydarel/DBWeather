package com.dbeginc.dbweather.ui.timeline;

import android.content.Context;

import com.dbeginc.dbweather.models.datatypes.news.Article;
import com.dbeginc.dbweather.models.datatypes.weather.WeatherData;

import java.util.List;

import javax.annotation.Nonnull;

/**
 * Created by Bitsy Darel on 15.05.17.
 */

interface INewsTimeLineView {
    void showNewsFeed(@Nonnull final List<Article> articles);

    void showDetails(@Nonnull final String url);

    void showError(final Throwable throwable);

    Context getContext();

    void addWeatherToHomeIntent(@Nonnull final WeatherData weatherData);
}
