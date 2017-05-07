package com.darelbitsy.dbweather.ui.welcome;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.util.Pair;

import com.darelbitsy.dbweather.DBWeatherApplication;
import com.darelbitsy.dbweather.models.datatypes.news.Article;
import com.darelbitsy.dbweather.models.datatypes.weather.HourlyData;
import com.darelbitsy.dbweather.models.datatypes.weather.WeatherInfo;
import com.darelbitsy.dbweather.models.provider.news.DatabaseNewsProvider;
import com.darelbitsy.dbweather.models.provider.news.NetworkNewsProvider;
import com.darelbitsy.dbweather.models.provider.schedulers.RxSchedulersProvider;
import com.darelbitsy.dbweather.models.provider.weather.DatabaseWeatherProvider;
import com.darelbitsy.dbweather.models.provider.weather.NetworkWeatherProvider;
import com.darelbitsy.dbweather.utils.utility.weather.WeatherUtil;

import java.util.List;

import javax.inject.Inject;

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

    WelcomeActivityPresenter(@NonNull final Context applicationContext,
                             @NonNull final IWelcomeActivityView view) {

        DBWeatherApplication.getComponent()
                .inject(this);

        mApplicationContext = applicationContext;
        mView = view;
        mSchedulersProvider = RxSchedulersProvider.newInstance();
    }

    void loadWeather() {
        mDatabaseWeatherProvider.getWeather()
                .subscribeOn(mSchedulersProvider.getWeatherScheduler())
                .observeOn(mSchedulersProvider.getComputationThread())
                .map(weather -> WeatherUtil.parseWeather(weather, mView.getContext()))
                .observeOn(mSchedulersProvider.getUIScheduler())
                .subscribeWith(new WeatherObserver());
    }

    public void getWeather() {
        mNetworkWeatherProvider.getWeather()
                .subscribeOn(mSchedulersProvider.getWeatherScheduler())
                .observeOn(mSchedulersProvider.getComputationThread())
                .map(weather -> WeatherUtil.parseWeather(weather, mView.getContext()))
                .observeOn(mSchedulersProvider.getUIScheduler())
                .subscribeWith(new WeatherObserver());
    }

    void loadNews() {
        mDatabaseNewsProvider.getNews()
                .subscribeOn(mSchedulersProvider.getNewsScheduler())
                .observeOn(mSchedulersProvider.getUIScheduler())
                .subscribeWith(new NewsObserver());
    }

    public void getNews() {
        mNetworkNewsProvider.getNews()
                .subscribeOn(mSchedulersProvider.getNewsScheduler())
                .observeOn(mSchedulersProvider.getUIScheduler())
                .subscribeWith(new NewsObserver());
    }

    void clearData() {
        subscriptions.clear();
    }

    private class WeatherObserver extends DisposableSingleObserver<Pair<List<WeatherInfo>,List<HourlyData>>> {

        @Override
        public void onSuccess(@NonNull final Pair<List<WeatherInfo>,List<HourlyData>> weather) {
            mView.addWeatherToWeatherActivityIntent(weather.first);
            //TODO: PASS HOURLY INFO TOO
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
