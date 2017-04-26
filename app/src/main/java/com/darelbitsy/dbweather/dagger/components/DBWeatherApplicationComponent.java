package com.darelbitsy.dbweather.dagger.components;

import android.content.Context;
import android.content.SharedPreferences;

import com.darelbitsy.dbweather.dagger.modules.DBWeatherApplicationModule;
import com.darelbitsy.dbweather.dagger.modules.DatabaseModule;
import com.darelbitsy.dbweather.dagger.modules.NetworkModule;
import com.darelbitsy.dbweather.provider.news.DatabaseNewsProvider;
import com.darelbitsy.dbweather.provider.news.NetworkNewsProvider;
import com.darelbitsy.dbweather.provider.weather.NetworkWeatherProvider;
import com.darelbitsy.dbweather.views.activities.WeatherActivity;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by Darel Bitsy on 24/04/17.
 * DBWeather Application Component
 */

@Singleton
@Component(modules = {DBWeatherApplicationModule.class, NetworkModule.class, DatabaseModule.class})
public interface DBWeatherApplicationComponent {

    void inject(final WeatherActivity activity);
}
