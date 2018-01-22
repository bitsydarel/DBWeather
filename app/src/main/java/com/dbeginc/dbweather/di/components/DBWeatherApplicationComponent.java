package com.dbeginc.dbweather.di.components;

import com.dbeginc.dbweather.base.BaseActivity;
import com.dbeginc.dbweather.base.BaseFragment;
import com.dbeginc.dbweather.config.managelocations.view.ManageLocationsActivity;
import com.dbeginc.dbweather.config.managesources.sourcedetail.SourceDetailActivity;
import com.dbeginc.dbweather.config.managesources.ManageSourcesActivity;
import com.dbeginc.dbweather.config.view.ConfigurationTabFragment;
import com.dbeginc.dbweather.di.modules.DBWeatherApplicationModule;
import com.dbeginc.dbweather.di.modules.DataModule;
import com.dbeginc.dbweather.di.modules.PresentationModule;
import com.dbeginc.dbweather.di.modules.UseCaseModule;
import com.dbeginc.dbweather.di.scopes.AppScope;
import com.dbeginc.dbweather.intro.chooselocation.view.ChooseLocationFragment;
import com.dbeginc.dbweather.intro.gpslocationfinder.view.GpsLocationFinderFragment;
import com.dbeginc.dbweather.intro.view.IntroActivity;
import com.dbeginc.dbweather.news.lives.LivesTabFragment;
import com.dbeginc.dbweather.news.lives.livedetail.LiveDetailActivity;
import com.dbeginc.dbweather.news.lives.page.alllives.AllLivesTabPageFragment;
import com.dbeginc.dbweather.news.lives.page.favorite.FavoriteLivesTabFragment;
import com.dbeginc.dbweather.news.newspaper.articledetail.ArticleDetailActivity;
import com.dbeginc.dbweather.news.newspaper.NewsPaperTabFragment;
import com.dbeginc.dbweather.news.NewsTabFragment;
import com.dbeginc.dbweather.splash.presenter.SplashPresenterImpl;
import com.dbeginc.dbweather.splash.view.SplashActivity;
import com.dbeginc.dbweather.utils.contentprovider.LocationSuggestionProvider;
import com.dbeginc.dbweather.weather.view.WeatherTabFragment;

import dagger.Component;

/**
 * Created by Darel Bitsy on 24/04/17.
 * DBWeather Application Component
 */
@AppScope
@Component(modules = {DBWeatherApplicationModule.class, DataModule.class, PresentationModule.class, UseCaseModule.class})
public interface DBWeatherApplicationComponent {

    void inject(final LocationSuggestionProvider locationSuggestionProvider);

    void inject(final SplashPresenterImpl welcomeActivityPresenter);

    void inject(final BaseFragment baseFragment);

    void inject(final IntroActivity introActivity);

    void inject(final BaseActivity baseActivity);

    void inject(final SplashActivity splashActivity);

    void inject(final WeatherTabFragment weatherTabFragment);

    void inject(final ChooseLocationFragment chooseLocationFragment);

    void inject(final GpsLocationFinderFragment gpsLocationFinderFragment);

    void inject(final NewsPaperTabFragment articlesTabFragment);

    void inject(final NewsTabFragment newsTabFragment);

    void inject(final AllLivesTabPageFragment livesPageFragment);

    void inject(final LivesTabFragment livesTabFragment);

    void inject(final FavoriteLivesTabFragment favoriteLivesTabFragment);

    void inject(final ConfigurationTabFragment configurationTabFragment);

    void inject(final LiveDetailActivity liveDetailActivity);

    void inject(final ManageLocationsActivity manageLocationsActivity);

    void inject(final ManageSourcesActivity manageSourcesActivity);

    void inject(final ArticleDetailActivity articleDetailActivity);

    void inject(final SourceDetailActivity sourceDetailActivity);
}
