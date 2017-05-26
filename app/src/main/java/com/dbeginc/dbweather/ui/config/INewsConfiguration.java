package com.dbeginc.dbweather.ui.config;

import android.content.Context;
import android.support.v4.util.Pair;

import com.dbeginc.dbweather.models.datatypes.news.Article;
import com.dbeginc.dbweather.models.datatypes.weather.WeatherData;

import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;

import io.reactivex.subjects.PublishSubject;

/**
 * Created by Bitsy Darel on 26.05.17.
 * News Source Config View
 */

interface INewsConfiguration {

    void notifySuccessSavedConfig();

    void notifyErrorWhileSavingConfig();

    PublishSubject<Pair<String, Pair<Integer, Integer>>> getConfigChangeEvent();

    void showConfigItems(@Nonnull final Map<String, Pair<Integer,Integer>> configData);

    void addNewsToHomeIntent(@Nonnull final List<Article> articles);

    void addWeatherToHomeIntent(@Nonnull final WeatherData weatherData);

    Context getContext();
}
