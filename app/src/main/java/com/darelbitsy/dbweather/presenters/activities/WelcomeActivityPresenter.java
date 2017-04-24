package com.darelbitsy.dbweather.presenters.activities;

import android.content.Context;
import android.support.annotation.NonNull;

import com.darelbitsy.dbweather.extensions.utility.weather.WeatherUtil;
import com.darelbitsy.dbweather.models.datatypes.news.Article;
import com.darelbitsy.dbweather.models.datatypes.weather.Weather;
import com.darelbitsy.dbweather.provider.news.DatabaseNewsProvider;
import com.darelbitsy.dbweather.provider.news.NetworkNewsProvider;
import com.darelbitsy.dbweather.provider.schedulers.RxSchedulersProvider;
import com.darelbitsy.dbweather.provider.weather.DatabaseWeatherProvider;
import com.darelbitsy.dbweather.provider.weather.NetworkWeatherProvider;
import com.darelbitsy.dbweather.views.activities.IWelcomeActivityView;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;

/**
 * Created by Darel Bitsy on 24/04/17.
 */

public class WelcomeActivityPresenter {
    private final DatabaseWeatherProvider mDatabaseWeatherProvider;
    private final NetworkWeatherProvider mNetworkWeatherProvider;
    private final DatabaseNewsProvider mDatabaseNewsProvider;
    private final NetworkNewsProvider mNetworkNewsProvider;
    private final RxSchedulersProvider mSchedulersProvider;
    private final CompositeDisposable subscriptions = new CompositeDisposable();
    private final Context mApplicationContext;
    private final IWelcomeActivityView mView;

    public WelcomeActivityPresenter(@NonNull final Context applicationContext,
                                    @NonNull final IWelcomeActivityView view) {
        mApplicationContext = applicationContext;

        mDatabaseWeatherProvider = new DatabaseWeatherProvider(mApplicationContext);
        mDatabaseNewsProvider = new DatabaseNewsProvider(mApplicationContext);

        mNetworkWeatherProvider = new NetworkWeatherProvider(mApplicationContext);
        mNetworkNewsProvider = new NetworkNewsProvider(mApplicationContext);

        mSchedulersProvider = RxSchedulersProvider.newInstance();

        mView = view;
    }

    public void loadWeather() {
        mDatabaseWeatherProvider.getWeather()
                .subscribeOn(mSchedulersProvider.getWeatherScheduler())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new WeatherObserver());
    }

    public void getWeather() {
        mNetworkWeatherProvider.getWeather()
                .subscribeOn(mSchedulersProvider.getWeatherScheduler())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new WeatherObserver());
    }

    public void loadNews() {
        mDatabaseNewsProvider.getNews()
                .subscribeOn(mSchedulersProvider.getNewsScheduler())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new NewsObserver());
    }

    public void getNews() {
        mNetworkNewsProvider.getNews()
                .subscribeOn(mSchedulersProvider.getNewsScheduler())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new NewsObserver());
    }

    public void clearData() {
        subscriptions.clear();
    }

    private class WeatherObserver extends DisposableSingleObserver<Weather> {
        @Override
        public void onSuccess(@NonNull final Weather weather) {
            mView.addWeatherToWeatherActivityIntent(WeatherUtil
                    .parseWeather(weather, mApplicationContext));
        }

        @Override
        public void onError(final Throwable throwable) {

        }
    }

    private class NewsObserver extends DisposableSingleObserver<List<Article>> {
        @Override
        public void onSuccess(@NonNull final List<Article> articles) {
            mView.addNewsToWeatherActivityIntent(articles);
        }

        @Override
        public void onError(final Throwable throwable) {

        }
    }
}
