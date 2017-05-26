package com.dbeginc.dbweather.ui.welcome;

import android.content.Context;
import android.support.annotation.NonNull;

import com.dbeginc.dbweather.DBWeatherApplication;
import com.dbeginc.dbweather.models.datatypes.news.Article;
import com.dbeginc.dbweather.models.datatypes.weather.WeatherData;
import com.dbeginc.dbweather.models.provider.news.DatabaseNewsProvider;
import com.dbeginc.dbweather.models.provider.news.NetworkNewsProvider;
import com.dbeginc.dbweather.models.provider.schedulers.RxSchedulersProvider;
import com.dbeginc.dbweather.models.provider.weather.DatabaseWeatherProvider;
import com.dbeginc.dbweather.models.provider.weather.NetworkWeatherProvider;
import com.dbeginc.dbweather.utils.helper.DatabaseOperation;
import com.dbeginc.dbweather.utils.utility.weather.WeatherUtil;

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
        mSchedulersProvider = RxSchedulersProvider.getInstance();
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

    void populateNewsDatabase() {
        DatabaseOperation.getInstance(mApplicationContext).initiateNewsSourcesTable();
    }

    private class WeatherObserver extends DisposableSingleObserver<WeatherData> {

        @Override
        public void onSuccess(@NonNull final WeatherData weather) {
            mView.addWeatherToWeatherActivityIntent(weather);
        }

        @Override
        public void onError(final Throwable throwable) { mView.showWeatherErrorMessage(); }
    }

    private class NewsObserver extends DisposableSingleObserver<List<Article>> {
        @Override
        public void onSuccess(@NonNull final List<Article> articles) {
            mView.addNewsToWeatherActivityIntent(articles);
        }

        @Override
        public void onError(final Throwable throwable) {
            mView.showNewsErrorMessage();
        }
    }
}
