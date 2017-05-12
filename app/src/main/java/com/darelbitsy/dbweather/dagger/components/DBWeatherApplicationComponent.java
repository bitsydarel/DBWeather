package com.darelbitsy.dbweather.dagger.components;

import com.darelbitsy.dbweather.GlideConfiguration;
import com.darelbitsy.dbweather.dagger.modules.DBWeatherApplicationModule;
import com.darelbitsy.dbweather.dagger.modules.DatabaseModule;
import com.darelbitsy.dbweather.dagger.modules.NetworkModule;
import com.darelbitsy.dbweather.ui.addlocation.AddLocationActivity;
import com.darelbitsy.dbweather.ui.newsdetails.NewsDialogPresenter;
import com.darelbitsy.dbweather.ui.welcome.WelcomeActivity;
import com.darelbitsy.dbweather.ui.welcome.WelcomeActivityPresenter;
import com.darelbitsy.dbweather.models.provider.AppDataProvider;
import com.darelbitsy.dbweather.models.provider.geoname.ILocationInfoProvider;
import com.darelbitsy.dbweather.models.provider.geoname.LocationSuggestionProvider;
import com.darelbitsy.dbweather.models.provider.translators.ITranslateProvider;
import com.darelbitsy.dbweather.ui.BaseActivity;
import com.darelbitsy.dbweather.ui.newsdetails.NewsDialogActivity;

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

    void inject(final AddLocationActivity addLocationActivity);

    void inject(final WelcomeActivity welcomeActivity);

    void inject(final GlideConfiguration glideConfiguration);
}
