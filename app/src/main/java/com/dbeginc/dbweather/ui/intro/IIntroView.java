package com.dbeginc.dbweather.ui.intro;

import android.content.Context;
import android.support.annotation.NonNull;

import com.dbeginc.dbweather.models.datatypes.news.Article;
import com.dbeginc.dbweather.models.datatypes.weather.WeatherData;

import java.util.List;

/**
 * Created by darel on 14.06.17.
 * Intro View layout
 */

interface IIntroView {

    void onPermissionEvent(final boolean isGranted);

    void addNewsToData(@NonNull final List<Article> articles);

    void addWeatherToData(@NonNull final WeatherData weather);

    Context getContext();

    void onLocationEvent(@NonNull final String locationEvent);

    void onNewsError();

    void onWeatherError();
}
