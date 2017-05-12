package com.darelbitsy.dbweather.ui.introduction;

import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v4.util.Pair;

import com.darelbitsy.dbweather.models.datatypes.news.Article;
import com.darelbitsy.dbweather.models.datatypes.weather.WeatherData;
import com.darelbitsy.dbweather.models.provider.AppDataProvider;
import com.darelbitsy.dbweather.models.provider.schedulers.RxSchedulersProvider;
import com.darelbitsy.dbweather.utils.utility.weather.WeatherUtil;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;

import static com.darelbitsy.dbweather.utils.holder.ConstantHolder.NEWS_DATA_KEY;
import static com.darelbitsy.dbweather.utils.holder.ConstantHolder.WEATHER_INFO_KEY;

/**
 * Created by darel on 01.05.17.
 * IntroductionView Presenter
 */

public class IntroPresenter {

    private static final int LOCATION_PAGE_INDEX = 1;
    private static final int ACCOUNT_PAGE_INDEX = 2;
    static final int LAST_PAGE_INDEX = 3;
    static final String LOCATION_PAGE = "Location";
    static final String ACCOUNT_PAGE = "Account";

    private final IntroductionView view;
    private final AppDataProvider dataProvider;
    private final RxSchedulersProvider schedulersProvider = RxSchedulersProvider.newInstance();

    private int accountPermissionAttempt;
    private int locationPermissionAttempt;
    private int requieredPagesWatch;
    private boolean pagingStatus;
    private boolean doneGettingNewsData;
    private boolean doneGettingWeatherData;
    private final CompositeDisposable subscriptions = new CompositeDisposable();

    IntroPresenter(@NonNull final IntroductionView view,
                   @NonNull final AppDataProvider dataProvider) {

        this.view = view;
        this.dataProvider = dataProvider;
        locationPermissionAttempt = 1;
        accountPermissionAttempt = 1;
        pagingStatus = true;
    }

    private boolean getLocationStatus() {
        return dataProvider.getGpsPermissionStatus();
    }

    private boolean getAccountStatus() {
        return dataProvider.getAccountPermissionStatus();
    }

    void askPermission(@NonNull final String permission) {
        askLocationPermission(permission);
        askAccountPermission(permission);
    }

    private void askLocationPermission(@NonNull final String location) {
        if (location.equalsIgnoreCase(LOCATION_PAGE)) {
            view.askLocationPermission();
        }
    }

    private void askAccountPermission(@NonNull final String account) {
        if (account.equalsIgnoreCase(ACCOUNT_PAGE)) {
            view.askAccountPermission();
        }
    }

    Pair<String, Boolean> shouldAllowPaging(final int position) {
        if (position == LAST_PAGE_INDEX && handleLastPage()) {
            view.closeView();

        } else if (position == LOCATION_PAGE_INDEX) {
            requieredPagesWatch = requieredPagesWatch == 0 ? requieredPagesWatch + 1 : requieredPagesWatch;
            pagingStatus = dataProvider.getGpsPermissionStatus();
            return new Pair<>(LOCATION_PAGE, pagingStatus);

        } else if (position == ACCOUNT_PAGE_INDEX && accountPermissionAttempt == 1) {
            requieredPagesWatch = requieredPagesWatch == 1 ? requieredPagesWatch + 1 : requieredPagesWatch;
            pagingStatus = dataProvider.getAccountPermissionStatus();
            return new Pair<>(ACCOUNT_PAGE, pagingStatus);
        }

        pagingStatus = true;
        return new Pair<>("", pagingStatus);
    }

    boolean isPagingEnable() { return pagingStatus; }

    void setPagingStatus(final boolean pagingStatus) { this.pagingStatus = pagingStatus; }

    void shouldRetryPermissionRequest(@NonNull final String permissionName) {
        if (locationPermissionAttempt > 1 ||
                accountPermissionAttempt > 1) { view.retryPermissionRequest(permissionName); }
    }

    boolean canMoveToNext(final int currentPosition) {
        if (currentPosition == LOCATION_PAGE_INDEX && !getLocationStatus()) {
            locationPermissionAttempt++;
            view.askLocationPermission();
            return false;

        } else if (currentPosition == ACCOUNT_PAGE_INDEX && !getAccountStatus() && accountPermissionAttempt == 1) {
            accountPermissionAttempt++;
            view.askAccountPermission();
            return false;

        } else { return true; }
    }

    void getNews() {
        subscriptions.add(
                dataProvider.getNewsFromApi()
                .subscribeOn(schedulersProvider.getNewsScheduler())
                .observeOn(schedulersProvider.getUIScheduler())
                .subscribeWith(new NewsObserver())
        );
    }

    void getWeather() {
        subscriptions.add(
                dataProvider.getWeatherFromApi()
                        .subscribeOn(schedulersProvider.getWeatherScheduler())
                        .observeOn(schedulersProvider.getComputationThread())
                        .map(weather -> WeatherUtil.parseWeather(weather, view.getContext()))
                        .observeOn(schedulersProvider.getUIScheduler())
                        .subscribeWith(new WeatherObserver()));
    }

    void afterPermissionGranted(final int currentItem) {
        view.goToNextPage();
        if (currentItem != LOCATION_PAGE_INDEX) { view.handleLastPage(); }
    }

    boolean handleLastPage() {
        return doneGettingNewsData && doneGettingWeatherData && requieredPagesWatch == 2;
    }

    private class WeatherObserver extends DisposableSingleObserver<WeatherData> {

        @Override
        public void onSuccess(@NonNull final WeatherData weather) {
            view.getViewData().putExtra(WEATHER_INFO_KEY, weather);
            doneGettingWeatherData = true;
        }
        @Override
        public void onError(final Throwable throwable) {
            view.showWeatherErrorMessage();
        }
    }

    private class NewsObserver extends DisposableSingleObserver<List<Article>> {
        @Override
        public void onSuccess(@NonNull final List<Article> articles) {
            view.getViewData().putParcelableArrayListExtra(NEWS_DATA_KEY,
                    (ArrayList<? extends Parcelable>) articles);
            dataProvider.setNewsTranslationStatus(true);
            dataProvider.setWeatherNotificationStatus(true);
            doneGettingNewsData = true;
            pagingStatus = true;
            view.allowSwiping(pagingStatus);
        }
        @Override
        public void onError(final Throwable throwable) {
            view.showNewsErrorMessage();
        }
    }
}