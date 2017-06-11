package com.dbeginc.dbweather.ui.main;

import android.support.annotation.NonNull;
import android.support.v4.util.Pair;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.dbeginc.dbweather.models.datatypes.news.Article;
import com.dbeginc.dbweather.models.datatypes.news.LiveNews;
import com.dbeginc.dbweather.models.datatypes.weather.WeatherData;
import com.dbeginc.dbweather.models.provider.AppDataProvider;
import com.dbeginc.dbweather.models.provider.schedulers.RxSchedulersProvider;
import com.google.firebase.database.DataSnapshot;

import java.io.File;
import java.util.List;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.subjects.PublishSubject;

import static com.dbeginc.dbweather.utils.holder.ConstantHolder.TAG;

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
                Log.i(TAG, "Is File Cache Cleared on exit: "
                        + file.delete());
            }
        }
    }

    boolean getGpsPermissionStatus() {
        return dataProvider.getGpsPermissionStatus();
    }

    void getNews() {
        subscriptions.add(
                dataProvider.getNewsFromApi()
                        .subscribeOn(schedulersProvider.getNewsScheduler())
                        .observeOn(schedulersProvider.getUIScheduler())
                        .subscribe(rootView::updateNews, Crashlytics::logException)
        );
    }

    void addNewLiveSource(@NonNull final DataSnapshot dataSnapshot) {
        final Pair<String, String> liveSource = new Pair<>(dataSnapshot.getKey(), dataSnapshot.getValue(String.class));

        subscriptions.add(
                dataProvider.isLiveInDatabase(liveSource.first)
                        .flatMapCompletable(isIn -> {
                            final LiveNews liveNews = new LiveNews();
                            liveNews.liveSource.set(liveSource.first);
                            liveNews.liveUrl.set(liveSource.second);
                            return dataProvider.refreshLiveData(liveNews, isIn);
                        }).subscribe(() -> Crashlytics.log(liveSource.first + " Updated successfully"), Crashlytics::logException)
        );
    }

    void removeLiveSource(final DataSnapshot dataSnapshot) {
        final LiveNews liveNews = new LiveNews();
        liveNews.liveSource.set(dataSnapshot.getKey());
        liveNews.liveUrl.set(dataSnapshot.getValue(String.class));
        subscriptions.add(
                dataProvider.removeLiveSource(liveNews)
                        .subscribeOn(schedulersProvider.getDatabaseWorkScheduler())
                        .observeOn(schedulersProvider.getDatabaseWorkScheduler())
                        .subscribe(() -> Log.i(TAG, liveNews.liveSource.get() + " Remove from Database"),
                                Crashlytics::logException)
        );
    }
}
