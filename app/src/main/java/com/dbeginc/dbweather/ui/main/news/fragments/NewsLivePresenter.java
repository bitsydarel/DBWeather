package com.dbeginc.dbweather.ui.main.news.fragments;

import android.support.annotation.NonNull;

import com.crashlytics.android.Crashlytics;
import com.dbeginc.dbweather.models.provider.AppDataProvider;
import com.dbeginc.dbweather.models.provider.schedulers.RxSchedulersProvider;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.subjects.PublishSubject;

/**
 * Created by darel on 08.06.17.
 * Live View Presenter
 */

class NewsLivePresenter extends BaseLivePresenter {
    private final INewsLive liveView;
    private final AppDataProvider dataProvider;
    private final CompositeDisposable subscription = new CompositeDisposable();
    private final RxSchedulersProvider schedulersProvider = RxSchedulersProvider.getInstance();

    NewsLivePresenter(@NonNull final INewsLive liveView, final AppDataProvider mAppDataProvider, final PublishSubject<Boolean> updateLiveSourceDataEvent) {
        this.liveView = liveView;
        dataProvider = mAppDataProvider;
        subscribeToLiveTouchEvent();
        subscribeToDataUpdateEvent(updateLiveSourceDataEvent);
    }

    private void subscribeToLiveTouchEvent() {
        subscription.add(
                videoSelectedEvent.subscribeOn(schedulersProvider.getComputationThread())
                        .observeOn(schedulersProvider.getUIScheduler())
                        .subscribe(liveView::showLiveView, Crashlytics::logException)
        );
    }

    private void subscribeToDataUpdateEvent(PublishSubject<Boolean> updateLiveSourceDataEvent) {

        subscription.add(
                updateLiveSourceDataEvent.subscribeOn(schedulersProvider.getComputationThread())
                        .observeOn(schedulersProvider.getComputationThread())
                        .subscribe(isOn -> subscription.add(
                                dataProvider.getLiveSources()
                                        .subscribeOn(schedulersProvider.getDatabaseWorkScheduler())
                                        .observeOn(schedulersProvider.getUIScheduler())
                                        .subscribe(liveView::updateLiveData, Crashlytics::logException)
                        ), Crashlytics::logException)
        );
    }

    void sendLiveUrlToPlayer(@NonNull final String url) { liveVideoUpdateEvent.onNext(url); }

    void clearState() { subscription.dispose(); }

    CompositeDisposable getSubscriptions() { return subscription; }
}
