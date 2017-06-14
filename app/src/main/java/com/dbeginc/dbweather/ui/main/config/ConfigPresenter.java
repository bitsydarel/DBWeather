package com.dbeginc.dbweather.ui.main.config;

import android.support.annotation.NonNull;

import com.crashlytics.android.Crashlytics;
import com.dbeginc.dbweather.models.provider.AppDataProvider;
import com.dbeginc.dbweather.models.provider.schedulers.RxSchedulersProvider;

import io.reactivex.disposables.CompositeDisposable;

/**
 * Created by darel on 11.06.17.
 */

class ConfigPresenter {

    private final AppDataProvider dataProvider;
    private final IConfigurationView configurationView;
    private final CompositeDisposable subscriptions = new CompositeDisposable();
    private final RxSchedulersProvider schedulersProvider = RxSchedulersProvider.getInstance();

    ConfigPresenter(@NonNull final IConfigurationView configurationView, @NonNull final AppDataProvider mAppDataProvider) {
        this.configurationView = configurationView;
        dataProvider = mAppDataProvider;
    }

    void subscribeToClickEvent() {
        subscriptions.add(
                configurationView.getConfigurationItemClickEvent()
                        .subscribeOn(schedulersProvider.getComputationThread())
                        .observeOn(schedulersProvider.getUIScheduler())
                        .subscribe(configurationView::respondToClick, Crashlytics::logException)
        );

        subscriptions.add(
                configurationView.getConfigurationBackEvent()
                .subscribeOn(schedulersProvider.getComputationThread())
                .observeOn(schedulersProvider.getUIScheduler())
                .subscribe(configurationView::onClickEvent, Crashlytics::logException)
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
