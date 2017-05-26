package com.dbeginc.dbweather.ui.config;

import android.support.v4.util.Pair;

import com.crashlytics.android.Crashlytics;
import com.dbeginc.dbweather.models.provider.AppDataProvider;
import com.dbeginc.dbweather.models.provider.schedulers.RxSchedulersProvider;
import com.dbeginc.dbweather.utils.utility.weather.WeatherUtil;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import io.reactivex.disposables.CompositeDisposable;

/**
 * Created by Bitsy Darel on 26.05.17.
 * News Configuration View presenter
 */

class NewsConfigurationPresenter {
    private final INewsConfiguration view;
    private final CompositeDisposable subscriptions = new CompositeDisposable();
    private final RxSchedulersProvider schedulersProvider = RxSchedulersProvider.getInstance();
    @Inject AppDataProvider dataProvider;


    NewsConfigurationPresenter(@Nonnull final INewsConfiguration view, @Nonnull final AppDataProvider dataProvider) {
        this.view = view;
        this.dataProvider = dataProvider;
        subscriptions.add(
                view.getConfigChangeEvent().subscribe(this::saveConf, Crashlytics::logException)
        );
    }

    private void saveConf(@Nonnull final Pair<String, Pair<Integer, Integer>> configData) {
        subscriptions.add(dataProvider.saveNewsSourceConfiguration(configData.first, configData.second.first, configData.second.second)
                .subscribeOn(schedulersProvider.getDatabaseWorkScheduler())
                .observeOn(schedulersProvider.getUIScheduler())
                .unsubscribeOn(schedulersProvider.getDatabaseWorkScheduler())
                .subscribe(view::notifySuccessSavedConfig, error -> {
                    Crashlytics.logException(error);
                    view.notifyErrorWhileSavingConfig();
                }));
    }

    void loadNewsSourcesConfigs() {
        subscriptions.add(dataProvider.getNewsSources()
                .subscribe(view::showConfigItems, Crashlytics::logException));
    }

    void clearState() { subscriptions.clear(); }

    void prepareBackHome() {
        subscriptions.add(dataProvider.getNewsFromDatabase()
                .subscribeOn(schedulersProvider.getNewsScheduler())
                .observeOn(schedulersProvider.getUIScheduler())
                .unsubscribeOn(schedulersProvider.getNewsScheduler())
                .subscribe(view::addNewsToHomeIntent, Crashlytics::logException));

        subscriptions.add(dataProvider.getWeatherFromDatabase()
                .subscribeOn(schedulersProvider.getWeatherScheduler())
                .observeOn(schedulersProvider.getComputationThread())
                .map(weather -> WeatherUtil.parseWeather(weather, view.getContext()))
                .observeOn(schedulersProvider.getUIScheduler())
                .unsubscribeOn(schedulersProvider.getWeatherScheduler())
                .subscribe(view::addWeatherToHomeIntent, Crashlytics::logException));
    }
}
