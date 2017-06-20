package com.dbeginc.dbweather.dagger.components;

import com.dbeginc.dbweather.GlideConfiguration;
import com.dbeginc.dbweather.dagger.modules.DBWeatherApplicationModule;
import com.dbeginc.dbweather.dagger.modules.DatabaseModule;
import com.dbeginc.dbweather.dagger.modules.NetworkModule;
import com.dbeginc.dbweather.models.provider.AppDataProvider;
import com.dbeginc.dbweather.models.provider.geoname.ILocationInfoProvider;
import com.dbeginc.dbweather.models.provider.geoname.LocationSuggestionProvider;
import com.dbeginc.dbweather.models.provider.translators.ITranslateProvider;
import com.dbeginc.dbweather.ui.BaseActivity;
import com.dbeginc.dbweather.ui.BaseFragment;
import com.dbeginc.dbweather.ui.main.news.live.BaseLivePresenter;
import com.dbeginc.dbweather.ui.welcome.WelcomeActivity;
import com.dbeginc.dbweather.ui.welcome.WelcomeActivityPresenter;
import com.dbeginc.dbweather.utils.services.NewsSyncJobScheduler;
import com.dbeginc.dbweather.utils.services.NewsSyncService;
import com.dbeginc.dbweather.utils.services.WeatherSyncJobScheduler;
import com.dbeginc.dbweather.utils.services.WeatherSyncService;

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

    void inject(final AppDataProvider appDataProvider);

    void inject(final BaseActivity baseActivity);

    void inject(final WelcomeActivity welcomeActivity);

    void inject(final GlideConfiguration glideConfiguration);

    void inject(final NewsSyncJobScheduler newsSyncJobScheduler);

    void inject(final NewsSyncService newsSyncService);

    void inject(final WeatherSyncService weatherSyncService);

    void inject(final WeatherSyncJobScheduler weatherSyncJobScheduler);

    void inject(final BaseFragment baseFragment);

    void inject(final BaseLivePresenter baseLivePresenter);
}
