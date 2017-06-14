package com.dbeginc.dbweather.ui.main.config.fragments.newssource;

import android.support.annotation.NonNull;
import android.support.v4.util.Pair;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.dbeginc.dbweather.models.datatypes.news.Source;
import com.dbeginc.dbweather.models.provider.AppDataProvider;
import com.dbeginc.dbweather.models.provider.schedulers.RxSchedulersProvider;
import com.dbeginc.dbweather.utils.holder.ConstantHolder;

import java.util.Map;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.subjects.PublishSubject;

/**
 * Created by darel on 12.06.17.
 * News Source View Presenter
 */

class NewsSourcePresenter {

    private final NewsSourceView newsSourceView;
    private final AppDataProvider dataProvider;
    private final PublishSubject<Source> itemTouchEvent;
    private final RxSchedulersProvider schedulersProvider = RxSchedulersProvider.getInstance();
    private final CompositeDisposable subscriptions = new CompositeDisposable();

    NewsSourcePresenter(@NonNull final NewsSourceView newsSourceView, @NonNull final AppDataProvider dataProvider, PublishSubject<Source> itemTouchEvent) {
        this.newsSourceView = newsSourceView;
        this.dataProvider = dataProvider;
        this.itemTouchEvent = itemTouchEvent;
    }

    void getSources() {
        subscriptions.add(
                dataProvider.getNewsFeedSources().zipWith(dataProvider.getNewsSources(), (sources, stringPairMap) -> {
                    Log.i(ConstantHolder.TAG, "Source Data Size: " + sources.getSources().size());
                    for (final Map.Entry<String, Pair<Integer, Integer>> stringPairEntry : stringPairMap.entrySet()) {
                        for (final Source source : sources.getSources()) {
                            if (source.getId().equalsIgnoreCase(stringPairEntry.getKey())) {
                                source.isSubscribed.set(stringPairEntry.getValue().second != 0);
                                source.amountOfNews.set(stringPairEntry.getValue().first);
                            }
                        }
                    }
                    return sources;
                }).subscribeOn(schedulersProvider.getNewsScheduler())
                        .observeOn(schedulersProvider.getUIScheduler())
                        .subscribe(newsSourceView::loadSources, Crashlytics::logException)
        );
    }

    private void subscribeToSource(@NonNull final Source newsSource) {
        subscriptions.add(
                dataProvider.isNewsSourceInDatabase(newsSource.getId())
                .flatMapCompletable(isInDatabase -> {
                    if (isInDatabase) { return dataProvider.saveNewsSourceConfiguration(newsSource.getId(),
                            newsSource.amountOfNews.get() > 5 ? 5 : newsSource.amountOfNews.get(), 1); }
                    else { return dataProvider.addNewsSourceToDatabase(newsSource.getId()); }
                }).subscribeOn(schedulersProvider.getDatabaseWorkScheduler())
                        .observeOn(schedulersProvider.getUIScheduler())
                        .unsubscribeOn(schedulersProvider.getDatabaseWorkScheduler())
                        .subscribe(newsSourceView::notifyNewsSuccessfullySaved, this::notifyError)
        );
    }

    private void notifyError(Throwable throwable) {
        Crashlytics.logException(throwable);
        newsSourceView.notifyErrorWhileSaved();
    }

    private void unSubscribeToSource(@NonNull final Source newsSource) {
        subscriptions.add(
                dataProvider.saveNewsSourceConfiguration(newsSource.getId(),
                        newsSource.amountOfNews.get() > 5 ? 5 : newsSource.amountOfNews.get(), 0)
                        .subscribeOn(schedulersProvider.getDatabaseWorkScheduler())
                        .observeOn(schedulersProvider.getUIScheduler())
                        .unsubscribeOn(schedulersProvider.getDatabaseWorkScheduler())
                        .subscribe(newsSourceView::notifyNewsSuccessfullySaved, Crashlytics::logException)
        );
    }

    void subscribeToTouchEvent() {
        subscriptions.add(
                itemTouchEvent.subscribeOn(schedulersProvider.getComputationThread())
                .observeOn(schedulersProvider.getUIScheduler())
                .subscribe(source -> {
                    if (source.isSubscribed.get()) { subscribeToSource(source); }
                    else { unSubscribeToSource(source); }
                })
        );
    }

    void clearState() { subscriptions.clear(); }
}
