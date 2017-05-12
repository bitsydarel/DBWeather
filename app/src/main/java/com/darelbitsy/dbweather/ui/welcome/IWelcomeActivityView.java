package com.darelbitsy.dbweather.ui.welcome;

import android.content.Context;
import android.support.annotation.NonNull;

import com.darelbitsy.dbweather.models.datatypes.news.Article;
import com.darelbitsy.dbweather.models.datatypes.weather.WeatherData;

import java.util.List;

/**
 * Created by Darel Bitsy on 24/04/17.
 */

public interface IWelcomeActivityView {

    void addWeatherToWeatherActivityIntent(@NonNull final WeatherData weatherData);

    void addNewsToWeatherActivityIntent(@NonNull final List<Article> articles);

    Context getContext();

    void showWeatherErrorMessage();

    void showNewsErrorMessage();
}
