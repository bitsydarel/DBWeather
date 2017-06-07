package com.dbeginc.dbweather.ui.main;

import android.support.annotation.NonNull;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.dbeginc.dbweather.models.datatypes.news.Article;
import com.dbeginc.dbweather.models.datatypes.weather.WeatherData;
import com.dbeginc.dbweather.models.provider.AppDataProvider;
import com.dbeginc.dbweather.models.provider.schedulers.RxSchedulersProvider;
import com.dbeginc.dbweather.utils.holder.ConstantHolder;

import java.io.File;
import java.util.List;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.subjects.PublishSubject;

/**
 * Created by darel on 29.05.17.
 * DBWeatherRootView Presenter
 */

class DBWeatherPresenter {
    private final AppDataProvider dataProvider;
    private final DBWeatherRootView rootView;
    private final RxSchedulersProvider schedulersProvider = RxSchedulersProvider.getInstance();
    private final CompositeDisposable subscriptions = new CompositeDisposable();

    DBWeatherPresenter(@NonNull final DBWeatherRootView rootView, PublishSubject<WeatherData> weatherDataUpdateEvent, PublishSubject<List<Article>> newsUpdateEvent, @NonNull final AppDataProvider dataProvider) {
        this.dataProvider = dataProvider;
        this.rootView = rootView;
        subscribeToEvents(weatherDataUpdateEvent, newsUpdateEvent);
    }

    void subscribeToEvents(@NonNull final PublishSubject<WeatherData> weatherEvent, @NonNull final PublishSubject<List<Article>> newsEvent) {
        subscriptions.add(
                weatherEvent.subscribeOn(schedulersProvider.getWeatherScheduler())
                        .observeOn(schedulersProvider.getUIScheduler())
                        .unsubscribeOn(schedulersProvider.getWeatherScheduler())
                        .subscribe(rootView::updateWeather, Crashlytics::logException)
        );

        subscriptions.add(
                newsEvent.subscribeOn(schedulersProvider.getNewsScheduler())
                        .observeOn(schedulersProvider.getUIScheduler())
                        .unsubscribeOn(schedulersProvider.getNewsScheduler())
                        .subscribe(rootView::updateNews, Crashlytics::logException)
        );
    }

    boolean isFirstRun() { return dataProvider.isFirstRun(); }

    void setFirstRun(boolean value) { dataProvider.setFirstRun(value); }

    void clearState(@NonNull final File dir) {
        cleanCache(dir);
        dataProvider.doWeSaveWeather(false);
        dataProvider.setCurrentWeatherFromGps(true);
        subscriptions.clear();
    }

    private void cleanCache(@NonNull final File dir) {
        if (dir.isDirectory()) {
            for (final File file : dir.listFiles()) {
                Log.i(ConstantHolder.TAG, "Is File Cache Cleared on exit: "
                        + file.delete());
            }
        }
    }

    boolean getGpsPermissionStatus() {
        return dataProvider.getGpsPermissionStatus();
    }
}
