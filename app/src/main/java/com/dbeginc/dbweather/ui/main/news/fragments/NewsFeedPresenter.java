package com.dbeginc.dbweather.ui.main.news.fragments;

import android.support.annotation.NonNull;

import com.crashlytics.android.Crashlytics;
import com.dbeginc.dbweather.models.provider.AppDataProvider;
import com.dbeginc.dbweather.models.provider.schedulers.RxSchedulersProvider;
import com.dbeginc.dbweather.ui.main.news.INewsView;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.subjects.PublishSubject;

/**
 * Created by darel on 29.05.17.
 */

public class NewsFeedPresenter {

    private final AppDataProvider dataProvider;
    private final PublishSubject<String> detailsEvent = PublishSubject.create();
    private final RxSchedulersProvider schedulersProvider = RxSchedulersProvider.getInstance();
    private final CompositeDisposable rxSubscription;
    private final INewsFeed view;

    NewsFeedPresenter(@NonNull final INewsFeed view, @NonNull final AppDataProvider dataProvider, @NonNull final CompositeDisposable rxSubscription) {
        this.view = view;
        this.dataProvider = dataProvider;
        this.rxSubscription = rxSubscription;
        detailsEvent.subscribe(view::showDetails, this::handleError);
    }

    PublishSubject<String> getDetailsEvent() { return detailsEvent; }

    public void getNews() {
        rxSubscription.add(
                dataProvider.getNewsFromApi()
                        .subscribeOn(schedulersProvider.getNewsScheduler())
                        .observeOn(schedulersProvider.getUIScheduler())
                        .subscribe(view::showNews,this::handleError)
        );
    }

    void loadNews() {
        rxSubscription.add(
                dataProvider.getNewsFromDatabase()
                        .subscribeOn(schedulersProvider.getNewsScheduler())
                        .observeOn(schedulersProvider.getUIScheduler())
                        .unsubscribeOn(schedulersProvider.getNewsScheduler())
                        .subscribe(view::showNews, this::handleError)
        );
    }

    private void handleError(@NonNull final Throwable throwable) {
        Crashlytics.logException(throwable);
        view.showError(throwable);
    }
}
