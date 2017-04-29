package com.darelbitsy.dbweather.dagger.components;

import com.darelbitsy.dbweather.dagger.modules.DBWeatherApplicationModule;
import com.darelbitsy.dbweather.dagger.modules.DatabaseModule;
import com.darelbitsy.dbweather.dagger.modules.NetworkModule;
import com.darelbitsy.dbweather.presenters.activities.NewsDialogPresenter;
import com.darelbitsy.dbweather.presenters.activities.WelcomeActivityPresenter;
import com.darelbitsy.dbweather.provider.AppDataProvider;
import com.darelbitsy.dbweather.provider.geoname.ILocationInfoProvider;
import com.darelbitsy.dbweather.provider.geoname.LocationSuggestionProvider;
import com.darelbitsy.dbweather.provider.translators.ITranslateProvider;
import com.darelbitsy.dbweather.views.activities.BaseActivity;
import com.darelbitsy.dbweather.views.activities.NewsDialogActivity;
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

    void inject(final ILocationInfoProvider iLocationInfoProvider);

    void inject(final LocationSuggestionProvider locationSuggestionProvider);

    void inject(final ITranslateProvider iTranslateProvider);

    void inject(final WelcomeActivityPresenter welcomeActivityPresenter);

    void inject(final NewsDialogActivity newsDialogActivity);

    void inject(final NewsDialogPresenter newsDialogPresenter);

    void inject(final AppDataProvider appDataProvider);

    void inject(final BaseActivity baseActivity);
}
