package com.dbeginc.dbweather.ui.intro;

import android.support.annotation.NonNull;

import com.crashlytics.android.Crashlytics;
import com.dbeginc.dbweather.models.provider.AppDataProvider;
import com.dbeginc.dbweather.models.provider.schedulers.RxSchedulersProvider;
import com.dbeginc.dbweather.utils.utility.weather.WeatherUtil;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.subjects.PublishSubject;

/**
 * Created by darel on 14.06.17.
 *
 * Intro Presenter View
 */

class IntroPresenter {

    private final IIntroView introView;
    private final AppDataProvider dataProvider;
    private final PublishSubject<Boolean> permissionEvent;
    private final PublishSubject<String> locationUpdateEvent;
    private final CompositeDisposable subscriptions = new CompositeDisposable();
    private final RxSchedulersProvider schedulersProvider = RxSchedulersProvider.getInstance();

    IntroPresenter(@NonNull final IIntroView introView, @NonNull final AppDataProvider dataProvider, @NonNull final PublishSubject<Boolean> permissionEvent, PublishSubject<String> locationUpdateEvent) {
        this.introView = introView;
        this.dataProvider = dataProvider;
        this.permissionEvent = permissionEvent;
        this.locationUpdateEvent = locationUpdateEvent;
        subscribeToPermissionEvent();
        subscribeToLocationEvent();
        initializeConfigurations();
    }

    void subscribeToPermissionEvent() {
        subscriptions.add(
                permissionEvent.subscribeOn(schedulersProvider.getComputationThread())
                        .observeOn(schedulersProvider.getUIScheduler())
                        .unsubscribeOn(schedulersProvider.getComputationThread())
                        .subscribe(introView::onPermissionEvent, Crashlytics::logException)
        );
    }

    void subscribeToLocationEvent() {
        subscriptions.add(
                locationUpdateEvent.subscribeOn(schedulersProvider.getComputationThread())
                .observeOn(schedulersProvider.getUIScheduler())
                .subscribe(introView::onLocationEvent, Crashlytics::logException)
        );
    }

    void getNews() {
        subscriptions.add(
                dataProvider.getNewsFromApi()
                        .subscribeOn(schedulersProvider.getNewsScheduler())
                        .observeOn(schedulersProvider.getUIScheduler())
                        .unsubscribeOn(schedulersProvider.getNewsScheduler())
                        .subscribe(introView::addNewsToData, this::onNewsError)
        );
    }

    private void onNewsError(final Throwable throwable) {
        Crashlytics.logException(throwable);
        introView.onNewsError();
    }

    void getWeather() {
        subscriptions.add(
                dataProvider.getWeatherFromApi()
                        .subscribeOn(schedulersProvider.getWeatherScheduler())
                        .observeOn(schedulersProvider.getComputationThread())
                        .unsubscribeOn(schedulersProvider.getWeatherScheduler())
                        .map(weather -> WeatherUtil.parseWeather(weather, introView.getContext()))
                        .observeOn(schedulersProvider.getUIScheduler())
                        .unsubscribeOn(schedulersProvider.getWeatherScheduler())
                        .subscribe(introView::addWeatherToData, this::onWeatherError)
        );
    }

    private void onWeatherError(final Throwable throwable) {
        Crashlytics.logException(throwable);
        introView.onWeatherError();
    }

    void initiateLiveSourcesTable() {
        dataProvider.initiateLiveSourcesTable();
    }

    private void initializeConfigurations() {
        dataProvider.setWeatherNotificationStatus(true);
        dataProvider.setNewsTranslationStatus(true);
    }

    void clearState() { subscriptions.clear(); }

    void setFromCurrentWeather() { dataProvider.setCurrentWeatherFromGps(true); }
}
