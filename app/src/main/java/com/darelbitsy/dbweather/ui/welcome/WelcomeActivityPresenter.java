package com.darelbitsy.dbweather.ui.welcome;

import android.content.Context;
import android.support.annotation.NonNull;

import com.darelbitsy.dbweather.DBWeatherApplication;
import com.darelbitsy.dbweather.utils.utility.weather.WeatherUtil;
import com.darelbitsy.dbweather.models.datatypes.news.Article;
import com.darelbitsy.dbweather.models.datatypes.weather.Weather;
import com.darelbitsy.dbweather.models.provider.news.DatabaseNewsProvider;
import com.darelbitsy.dbweather.models.provider.news.NetworkNewsProvider;
import com.darelbitsy.dbweather.models.provider.schedulers.RxSchedulersProvider;
import com.darelbitsy.dbweather.models.provider.weather.DatabaseWeatherProvider;
import com.darelbitsy.dbweather.models.provider.weather.NetworkWeatherProvider;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Scheduler;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;

/**
 * Created by Darel Bitsy on 24/04/17.
 * Welcome Acy
 */

public class WelcomeActivityPresenter {
    @Inject DatabaseWeatherProvider mDatabaseWeatherProvider;
    @Inject NetworkWeatherProvider mNetworkWeatherProvider;
    @Inject DatabaseNewsProvider mDatabaseNewsProvider;
    @Inject NetworkNewsProvider mNetworkNewsProvider;
    @Inject Context mApplicationContext;

    private final RxSchedulersProvider mSchedulersProvider;
    private final CompositeDisposable subscriptions = new CompositeDisposable();
    private final IWelcomeActivityView mView;
    private Scheduler mObserveOnScheduler;

    public WelcomeActivityPresenter(@NonNull final Context applicationContext,
                                    @NonNull final IWelcomeActivityView view,
                                    @NonNull final Scheduler observeOnScheduler) {

        DBWeatherApplication.getComponent()
                .inject(this);

        mApplicationContext = applicationContext;
        mView = view;
        mObserveOnScheduler = observeOnScheduler;
        mSchedulersProvider = RxSchedulersProvider.newInstance();
    }

    public void loadWeather() {
        mDatabaseWeatherProvider.getWeather()
                .subscribeOn(mSchedulersProvider.getWeatherScheduler())
                .observeOn(mObserveOnScheduler)
                .subscribeWith(new WeatherObserver());
    }

    public void getWeather() {
        mNetworkWeatherProvider.getWeather()
                .subscribeOn(mSchedulersProvider.getWeatherScheduler())
                .observeOn(mObserveOnScheduler)
                .subscribeWith(new WeatherObserver());
    }

    public void loadNews() {
        mDatabaseNewsProvider.getNews()
                .subscribeOn(mSchedulersProvider.getNewsScheduler())
                .observeOn(mObserveOnScheduler)
                .subscribeWith(new NewsObserver());
    }

    public void getNews() {
        mNetworkNewsProvider.getNews()
                .subscribeOn(mSchedulersProvider.getNewsScheduler())
                .observeOn(mObserveOnScheduler)
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
