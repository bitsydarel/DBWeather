package com.dbeginc.dbweather.ui.main.weather;

import android.support.annotation.NonNull;

import com.crashlytics.android.Crashlytics;
import com.dbeginc.dbweather.models.datatypes.geonames.GeoName;
import com.dbeginc.dbweather.models.datatypes.weather.Weather;
import com.dbeginc.dbweather.models.provider.AppDataProvider;
import com.dbeginc.dbweather.models.provider.schedulers.RxSchedulersProvider;
import com.dbeginc.dbweather.utils.utility.weather.WeatherUtil;

import org.threeten.bp.LocalDateTime;

import java.util.Locale;

import io.reactivex.Single;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

/**
 * Created by darel on 28.05.17.
 * Weather view presenter
 */

class WeatherPresenter {
    private final IWeatherView mainView;
    private final AppDataProvider dataProvider;
    private int lastWeatherApiCallTimeInMinutes;
    private final RxSchedulersProvider schedulersProvider = RxSchedulersProvider.getInstance();
    private final CompositeDisposable subscriptions = new CompositeDisposable();

    WeatherPresenter(@NonNull final IWeatherView weatherView, @NonNull final AppDataProvider dataProvider) {
        this.mainView = weatherView;
        this.dataProvider = dataProvider;
        subscriptions.add(
                mainView.getLocationUpdateEvent()
                        .subscribeOn(schedulersProvider.getWeatherScheduler())
                        .observeOn(schedulersProvider.getUIScheduler())
                        .subscribe(mainView::onLocationUpdate, Crashlytics::logException)
        );
    }

    void getWeatherForCity(@NonNull final GeoName location) {
        final String cityName = String.format(Locale.getDefault(),
            "%s, %s", location.getName(), location.getCountryName());
        final double latitude = location.getLatitude();
        final double longitude = location.getLongitude();

        subscriptions.add(
                dataProvider.isLocationInDatabase(cityName)
                        .subscribeOn(schedulersProvider.getDatabaseWorkScheduler())
                        .observeOn(schedulersProvider.getUIScheduler())
                        .unsubscribeOn(schedulersProvider.getDatabaseWorkScheduler())
                        .doOnError(error -> {
                            Crashlytics.logException(error);
                            if (mainView.isNetworkAvailable()) {
                                subscriptions.add(
                                        dataProvider.getWeatherForCityFromDatabase(cityName, latitude, longitude)
                                                .subscribeOn(schedulersProvider.getWeatherScheduler())
                                                .observeOn(schedulersProvider.getComputationThread())
                                                .unsubscribeOn(schedulersProvider.getWeatherScheduler())
                                                .map(weather -> WeatherUtil.parseWeather(weather, mainView.getAppContext()))
                                                .observeOn(schedulersProvider.getUIScheduler())
                                                .unsubscribeOn(schedulersProvider.getWeatherScheduler())
                                                .subscribe(mainView::showWeather, this::notifyError)
                                );
                            }
                        })
                        .doOnSuccess(isLocationThere -> {
                            Single<Weather> weatherSingle;
                            if (isLocationThere) {
                                if (mainView.isNetworkAvailable()) {
                                    weatherSingle = dataProvider.getWeatherForCityFromApi(cityName, latitude, longitude)
                                            .ambWith(dataProvider.getWeatherForCityFromDatabase(cityName, latitude, longitude));

                                } else { weatherSingle = dataProvider.getWeatherForCityFromDatabase(cityName, latitude, longitude); }

                            } else { weatherSingle = dataProvider.getWeatherForCityFromApi(cityName, latitude, longitude); }

                            subscriptions.add(
                                    weatherSingle.subscribeOn(schedulersProvider.getWeatherScheduler())
                                            .observeOn(schedulersProvider.getComputationThread())
                                            .unsubscribeOn(schedulersProvider.getWeatherScheduler())
                                            .map(weather -> WeatherUtil.parseWeather(weather, mainView.getAppContext()))
                                            .observeOn(schedulersProvider.getUIScheduler())
                                            .unsubscribeOn(schedulersProvider.getWeatherScheduler())
                                            .subscribe(mainView::showWeather, this::notifyError)
                            );
                        }).subscribe()
        );
    }

    void getWeather() {
        if (lastWeatherApiCallTimeInMinutes == 0 ||
                (LocalDateTime.now().getMinute() - lastWeatherApiCallTimeInMinutes) > 10) {

            subscriptions.add(
                    getWeatherApiSubscription()
            );

            lastWeatherApiCallTimeInMinutes = LocalDateTime.now().getMinute();

        } else { loadWeather(); }
    }

    private void loadWeather() {
        subscriptions.add(
                dataProvider.getWeatherFromDatabase()
                        .subscribeOn(schedulersProvider.getWeatherScheduler())
                        .observeOn(schedulersProvider.getComputationThread())
                        .unsubscribeOn(schedulersProvider.getWeatherScheduler())
                        .map(weather -> WeatherUtil.parseWeather(weather, mainView.getAppContext()))
                        .observeOn(schedulersProvider.getUIScheduler())
                        .unsubscribeOn(schedulersProvider.getWeatherScheduler())
                        .subscribe(mainView::showWeather, this::notifyError)
        );
    }

    void clearState() { subscriptions.clear(); }

    boolean isFirstRun() { return dataProvider.isFirstRun(); }

    boolean isCurrentWeatherFromGps() { return dataProvider.isCurrentWeatherFromGps(); }

    void setCurrentWeatherFromGps(final boolean value) { dataProvider.setCurrentWeatherFromGps(value); }

    void doWeSaveWeather(boolean value) { dataProvider.doWeSaveWeather(value); }

    void retryWeatherRequest() { subscriptions.add(getWeatherApiSubscription()); }

    void loadUserCities() {
        subscriptions.add(
                dataProvider.getUserCitiesFromDatabase()
                        .subscribeOn(schedulersProvider.getDatabaseWorkScheduler())
                        .observeOn(schedulersProvider.getUIScheduler())
                        .unsubscribeOn(schedulersProvider.getDatabaseWorkScheduler())
                        .subscribe(mainView::loadUserCities, this::notifyError)
        );
    }

    @NonNull
    CompositeDisposable getRxSubscriptions() { return subscriptions; }

    @NonNull
    private Disposable getWeatherApiSubscription() {
        return dataProvider.getWeatherFromApi()
                .subscribeOn(schedulersProvider.getWeatherScheduler())
                .observeOn(schedulersProvider.getComputationThread())
                .unsubscribeOn(schedulersProvider.getWeatherScheduler())
                .map(weather -> WeatherUtil.parseWeather(weather, mainView.getAppContext()))
                .observeOn(schedulersProvider.getUIScheduler())
                .unsubscribeOn(schedulersProvider.getWeatherScheduler())
                .subscribe(mainView::showWeather, this::notifyError);
    }

    private void notifyError(@NonNull final Throwable throwable) {
        Crashlytics.logException(throwable);
        mainView.showWeatherError();
    }

    void addToFavorite(@NonNull final GeoName location) {
        subscriptions.add(
                dataProvider.addLocationToDatabase(location)
                        .subscribeOn(schedulersProvider.getDatabaseWorkScheduler())
                        .observeOn(schedulersProvider.getUIScheduler())
                        .subscribe(mainView::notifySuccessfullAddedLocation, Crashlytics::logException)
        );
    }

    void isLocationInDatabase(@NonNull final String cityName, @NonNull final GeoName location) {
        subscriptions.add(
                dataProvider.isLocationInDatabase(cityName)
                        .subscribeOn(schedulersProvider.getDatabaseWorkScheduler())
                        .observeOn(schedulersProvider.getUIScheduler())
                        .unsubscribeOn(schedulersProvider.getDatabaseWorkScheduler())
                        .doOnSuccess(isInside -> {
                            if (!isInside) { mainView.addLocationToMenu(location); }
                        }).doOnError(Crashlytics::logException)
                        .subscribe()
        );
    }
}
