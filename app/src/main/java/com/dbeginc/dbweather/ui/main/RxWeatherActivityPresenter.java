package com.dbeginc.dbweather.ui.main;

import android.support.annotation.NonNull;
import android.support.v4.util.Pair;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.dbeginc.dbweather.models.datatypes.geonames.GeoName;
import com.dbeginc.dbweather.models.datatypes.news.Article;
import com.dbeginc.dbweather.models.datatypes.weather.WeatherData;
import com.dbeginc.dbweather.models.provider.AppDataProvider;
import com.dbeginc.dbweather.models.provider.repository.IUserCitiesRepository;
import com.dbeginc.dbweather.models.provider.schedulers.ISchedulersProvider;
import com.dbeginc.dbweather.models.provider.schedulers.RxSchedulersProvider;
import com.dbeginc.dbweather.utils.holder.ConstantHolder;
import com.dbeginc.dbweather.utils.utility.weather.WeatherUtil;

import org.threeten.bp.LocalDateTime;

import java.io.File;
import java.util.List;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;

/**
 * Created by Darel Bitsy on 22/04/17.
 * MainView Presenter with Rx
 */

public class RxWeatherActivityPresenter {

    private IWeatherActivityView mMainView;
    private int lastWeatherApiCallTimeInMinutes;
    private int lastNewsApiCallTimeInMinutes;
    private final ISchedulersProvider mSchedulersProvider;
    private final IUserCitiesRepository mUserCitiesRepository;
    private final AppDataProvider mDataProvider;
    private final CompositeDisposable rxSubscriptions = new CompositeDisposable();

    RxWeatherActivityPresenter(
            final IUserCitiesRepository userCitiesRepository,
            final IWeatherActivityView mainView,
            final AppDataProvider dataProvider) {

        mUserCitiesRepository = userCitiesRepository;

        mMainView = mainView;

        mDataProvider = dataProvider;

        mSchedulersProvider = RxSchedulersProvider.getInstance();
    }

    void configureView() {
        loadWeather();
        loadNews();
    }

    void loadWeather() {
        rxSubscriptions.add(mDataProvider.getWeatherFromDatabase()
                .subscribeOn(mSchedulersProvider.getWeatherScheduler())
                .observeOn(mSchedulersProvider.getComputationThread())
                .unsubscribeOn(mSchedulersProvider.getWeatherScheduler())
                .map(weather -> WeatherUtil.parseWeather(weather, mMainView.getContext()))
                .observeOn(mSchedulersProvider.getUIScheduler())
                .unsubscribeOn(mSchedulersProvider.getWeatherScheduler())
                .subscribeWith(new WeatherObserver()));
    }

    void clearState(final File file) {
        rxSubscriptions.clear();
        cleanCache(file);
        mMainView = null;
    }

    public void getWeather() {
        if (lastWeatherApiCallTimeInMinutes == 0 ||
                (LocalDateTime.now().getMinute() - lastWeatherApiCallTimeInMinutes) > 10) {

            rxSubscriptions.add(mDataProvider.getWeatherFromApi()
                    .subscribeOn(mSchedulersProvider.getWeatherScheduler())
                    .observeOn(mSchedulersProvider.getComputationThread())
                    .unsubscribeOn(mSchedulersProvider.getWeatherScheduler())
                    .map(weather -> WeatherUtil.parseWeather(weather, mMainView.getContext()))
                    .observeOn(mSchedulersProvider.getUIScheduler())
                    .unsubscribeOn(mSchedulersProvider.getWeatherScheduler())
                    .subscribeWith(new WeatherObserver()));

            lastWeatherApiCallTimeInMinutes = LocalDateTime.now().getMinute();

        } else { loadWeather(); }
    }

    void getWeatherForCity(@NonNull final String cityName,
                           final double latitude,
                           final double longitude) {

        rxSubscriptions.add(
                mDataProvider.getWeatherForCityFromApi(cityName, latitude, longitude)
                        .ambWith(mDataProvider.getWeatherForCityFromDatabase(cityName, latitude, longitude))
                        .subscribeOn(mSchedulersProvider.getWeatherScheduler())
                        .observeOn(mSchedulersProvider.getComputationThread())
                        .unsubscribeOn(mSchedulersProvider.getWeatherScheduler())
                        .map(weather -> WeatherUtil.parseWeather(weather, mMainView.getContext()))
                        .observeOn(mSchedulersProvider.getUIScheduler())
                        .unsubscribeOn(mSchedulersProvider.getWeatherScheduler())
                        .subscribeWith(new WeatherObserver())
        );
    }

    void loadWeatherForCity(@NonNull final String cityName,
                            final double latitude,
                            final double longitude) {

        rxSubscriptions.add(mDataProvider.getWeatherForCityFromDatabase(cityName, latitude, longitude)
                .subscribeOn(mSchedulersProvider.getWeatherScheduler())
                .observeOn(mSchedulersProvider.getComputationThread())
                .unsubscribeOn(mSchedulersProvider.getWeatherScheduler())
                .map(weather -> WeatherUtil.parseWeather(weather, mMainView.getContext()))
                .observeOn(mSchedulersProvider.getUIScheduler())
                .unsubscribeOn(mSchedulersProvider.getWeatherScheduler())
                .subscribeWith(new WeatherObserver()));
    }

    void loadUserCitiesMenu() {

        rxSubscriptions.add(mUserCitiesRepository.getUserCities()
                .subscribeOn(mSchedulersProvider.getDatabaseWorkScheduler())
                .observeOn(mSchedulersProvider.getUIScheduler())
                .unsubscribeOn(mSchedulersProvider.getDatabaseWorkScheduler())
                .subscribeWith(new DisposableSingleObserver<List<GeoName>>() {
                    @Override
                    public void onSuccess(@NonNull final List<GeoName> userCities) {
                        if (userCities.isEmpty()) { mMainView.setupNavigationDrawerWithNoCities(); }
                        else { mMainView.setupNavigationDrawerWithCities(userCities); }
                    }
                    @Override
                    public void onError(final Throwable throwable) {
                        mMainView.setupNavigationDrawerWithNoCities();
                        Crashlytics.logException(throwable);
                    }
                }));
    }

    void removeCityFromUserCities(@NonNull final GeoName location) {
        mUserCitiesRepository.removeCity(location);
    }

    void loadNews() {
        rxSubscriptions.add(mDataProvider.getNewsFromDatabase()
                .subscribeOn(mSchedulersProvider.getNewsScheduler())
                .observeOn(mSchedulersProvider.getUIScheduler())
                .unsubscribeOn(mSchedulersProvider.getNewsScheduler())
                .subscribeWith(new NewsObserver()));
    }

    public void getNews() {
        if (lastNewsApiCallTimeInMinutes == 0 ||
                (LocalDateTime.now().getMinute() - lastNewsApiCallTimeInMinutes) > 10) {

            rxSubscriptions.add(mDataProvider.getNewsFromApi()
                    .subscribeOn(mSchedulersProvider.getNewsScheduler())
                    .observeOn(mSchedulersProvider.getUIScheduler())
                    .unsubscribeOn(mSchedulersProvider.getNewsScheduler())
                    .subscribeWith(new NewsObserver()));

            lastNewsApiCallTimeInMinutes = LocalDateTime.now().getMinute();
        } else { loadNews(); }
    }

    boolean isFirstRun() {
        return mDataProvider.isFirstRun();
    }

    void setFirstRun(final boolean isFirstRun) {
        mDataProvider.setFirstRun(isFirstRun);
    }

    boolean didUserSelectedCityFromDrawer() { return mDataProvider.didUserSelectedCityFromDrawer(); }

    void userSelectedCityFromDrawer(final boolean isFromCity) { mDataProvider.userSelectedCityFromDrawer(isFromCity); }

    Pair<String, double[]> getSelectedUserCity() {
        return mDataProvider.getSelectedUserCity("Unknown");
    }

    boolean getWeatherNotificationStatus() {
        return mDataProvider.getWeatherNotificationStatus();
    }

    void setWeatherNotificationStatus(final boolean isOn) {
        mDataProvider.setWeatherNotificationStatus(isOn);
    }

    boolean getNewsTranslationStatus() {
        return mDataProvider.getNewsTranslationStatus();
    }

    void setNewsTranslationStatus(final boolean isOn) {
        mDataProvider.setNewsTranslationStatus(isOn);
    }

    boolean getWritePermissionStatus() {
        return mDataProvider.getWritePermissionStatus();
    }

    void setSelectedUserCity(@NonNull final String locationSelected,
                             final double latitude,
                             final double longitude) {

        mDataProvider.setSelectedUserCity(locationSelected, latitude, longitude);
    }

    private void cleanCache(@NonNull final File dir) {
        if (dir.isDirectory()) {
            for (final File file : dir.listFiles()) {
                Log.i(ConstantHolder.TAG, "Is File Cache Cleared on exit: "
                        + file.delete());
            }
        }
    }

    void unSubscribeToUpdate() {
        rxSubscriptions.clear();
    }

    void retryWeatherRequest() {
        // TODO: Implement weather retry method
    }

    void retryNewsRequest() {
        // TODO: Implement news retry method
    }

    private class NewsObserver extends DisposableSingleObserver<List<Article>> {
        @Override
        public void onSuccess(@NonNull final List<Article> articles) {
            if (!articles.isEmpty()) { mMainView.showNews(articles); }
            else { mMainView.showNetworkNewsErrorMessage(); }
        }

        @Override
        public void onError(final Throwable throwable) {
            mMainView.showNetworkNewsErrorMessage();
            Crashlytics.logException(throwable);
        }
    }

    private class WeatherObserver extends DisposableSingleObserver<WeatherData> {

        @Override
        public void onSuccess(@NonNull final WeatherData weather) {
            if (!weather.getWeatherInfoList().isEmpty() && !weather.getHourlyWeatherList().isEmpty()) {
                mMainView.showWeather(weather);

            } else { mMainView.showNetworkWeatherErrorMessage(); }
        }

        @Override
        public void onError(final Throwable throwable) {
            mMainView.showNetworkWeatherErrorMessage();
            Crashlytics.logException(throwable);
        }
    }

    CompositeDisposable getRxSubscriptions() {
        return rxSubscriptions;
    }

    ISchedulersProvider getSchedulersProvider() {
        return mSchedulersProvider;
    }

    void setMainView(final IWeatherActivityView view) {
        if (mMainView == null) { mMainView = view; }
    }

}
