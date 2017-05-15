package com.darelbitsy.dbweather.ui.timeline;

import com.darelbitsy.dbweather.models.datatypes.news.Article;
import com.darelbitsy.dbweather.models.provider.AppDataProvider;
import com.darelbitsy.dbweather.models.provider.schedulers.RxSchedulersProvider;

import java.util.List;

import javax.annotation.Nonnull;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;

/**
 * Created by Bitsy Darel on 14.05.17.
 */

class NewsTimeLinePresenter {

    private final AppDataProvider dataProvider;
    private final CompositeDisposable rxSubscribtion;
    private INewsTimeLineView view;
    private final RxSchedulersProvider schedulersProvider = RxSchedulersProvider.newInstance();

    NewsTimeLinePresenter(@Nonnull final INewsTimeLineView view, @Nonnull final AppDataProvider dataProvider,
                          @Nonnull final CompositeDisposable compositeDisposable) {
        this.view = view;
        this.dataProvider = dataProvider;
        rxSubscribtion = compositeDisposable;
    }

    void loadNews() {
        rxSubscribtion.add(dataProvider.getNewsFromDatabase()
                .subscribeOn(schedulersProvider.getNewsScheduler())
                .observeOn(schedulersProvider.getUIScheduler())
                .subscribeWith(new DisposableSingleObserver<List<Article>>() {
                    @Override
                    public void onSuccess(final List<Article> articles) { view.showNewsFeed(articles); }

                    @Override
                    public void onError(final Throwable throwable) { view.showError(throwable); }
                }));
    }

    void getNews() {
        rxSubscribtion.add(dataProvider.getNewsFromDatabase()
                .subscribeOn(schedulersProvider.getNewsScheduler())
                .observeOn(schedulersProvider.getUIScheduler())
                .subscribeWith(new DisposableSingleObserver<List<Article>>() {
                    @Override
                    public void onSuccess(final List<Article> articles) { view.showNewsFeed(articles); }

                    @Override
                    public void onError(final Throwable throwable) { view.showError(throwable); }
                }));
    }

    void showDetails(@Nonnull final String url) { view.showDetails(url); }
}
