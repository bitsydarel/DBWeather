package com.dbeginc.dbweather.ui.main.news.fragments;

import android.support.annotation.NonNull;

import com.crashlytics.android.Crashlytics;
import com.dbeginc.dbweather.models.provider.schedulers.RxSchedulersProvider;

import io.reactivex.disposables.Disposable;

/**
 * Created by darel on 07.06.17.
 * News Live Presenter
 */

class YoutubeLivePresenter extends BaseLivePresenter {
    private final IYoutubeView youtubeView;
    private Disposable subscription;
    private final RxSchedulersProvider schedulersProvider = RxSchedulersProvider.getInstance();

    YoutubeLivePresenter(@NonNull final IYoutubeView youtubeView) {
        this.youtubeView = youtubeView;
        subscription = liveVideoUpdateEvent.subscribeOn(schedulersProvider.getComputationThread())
                .observeOn(schedulersProvider.getUIScheduler())
                .subscribe(this::showLiveStream, Crashlytics::logException);

    }

    void subscribe() {
        subscription = videoSelectedEvent.subscribeOn(schedulersProvider.getComputationThread())
                .observeOn(schedulersProvider.getUIScheduler())
                .subscribe(liveNews -> showLiveStream(liveNews.liveUrl.get()), Crashlytics::logException);
    }

    private void showLiveStream(@NonNull final String url) {
        youtubeView.displayVideo(url);
    }

    void clearState() { subscription.dispose(); }
}