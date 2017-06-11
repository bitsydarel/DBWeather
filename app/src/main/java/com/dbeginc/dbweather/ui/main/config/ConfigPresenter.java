package com.dbeginc.dbweather.ui.main.config;

import android.support.annotation.NonNull;
import android.support.v4.util.Pair;

import com.crashlytics.android.Crashlytics;
import com.dbeginc.dbweather.models.provider.AppDataProvider;
import com.dbeginc.dbweather.models.provider.schedulers.RxSchedulersProvider;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.subjects.PublishSubject;

/**
 * Created by darel on 11.06.17.
 */

class ConfigPresenter {

    private final AppDataProvider dataProvider;
    private final IConfigurationView configurationView;
    private final PublishSubject<Pair<Integer, Boolean>> clickEvent;
    private final CompositeDisposable subscriptions = new CompositeDisposable();
    private final RxSchedulersProvider schedulersProvider = RxSchedulersProvider.getInstance();

    ConfigPresenter(@NonNull final IConfigurationView configurationView, @NonNull final AppDataProvider mAppDataProvider, @NonNull final PublishSubject<Pair<Integer, Boolean>> configClickEvent) {
        this.configurationView = configurationView;
        dataProvider = mAppDataProvider;
        clickEvent = configClickEvent;
        subscribeToConfigClickEvent();
    }

    private void subscribeToConfigClickEvent() {
        subscriptions.add(
                clickEvent.subscribeOn(schedulersProvider.getComputationThread())
                        .observeOn(schedulersProvider.getUIScheduler())
                        .subscribe(configurationView::respondToClick, Crashlytics::logException)
        );
    }

    void loadUserCities() {
        subscriptions.add(
                dataProvider.getUserCitiesFromDatabase()
                        .subscribeOn(schedulersProvider.getDatabaseWorkScheduler())
                        .observeOn(schedulersProvider.getUIScheduler())
                        .subscribe(configurationView::loadCities, Crashlytics::logException)
        );
    }

    void clearState() { subscriptions.clear(); }
}
