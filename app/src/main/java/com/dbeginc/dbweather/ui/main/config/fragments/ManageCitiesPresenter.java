package com.dbeginc.dbweather.ui.main.config.fragments;

import android.support.annotation.NonNull;

import com.crashlytics.android.Crashlytics;
import com.dbeginc.dbweather.models.datatypes.geonames.GeoName;
import com.dbeginc.dbweather.models.provider.AppDataProvider;
import com.dbeginc.dbweather.models.provider.schedulers.RxSchedulersProvider;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.subjects.PublishSubject;

/**
 * Created by darel on 10.06.17.
 */

class ManageCitiesPresenter {

    private final AppDataProvider dataProvider;
    private final RxSchedulersProvider schedulersProvider = RxSchedulersProvider.getInstance();
    private final CompositeDisposable subscriptions = new CompositeDisposable();
    private final PublishSubject<GeoName> removeLocationEvent;

    ManageCitiesPresenter(@NonNull final AppDataProvider mAppDataProvider, final PublishSubject<GeoName> removeLocationEvent) {
        dataProvider = mAppDataProvider;
        this.removeLocationEvent = removeLocationEvent;
    }

    void removeLocation(@NonNull final GeoName location) {
        dataProvider.removeLocationFromDatabase(location);
    }

    void subscribeToRemoveLocationEvent() {
        subscriptions.add(
                removeLocationEvent.subscribeOn(schedulersProvider.getWeatherScheduler())
                        .observeOn(schedulersProvider.getUIScheduler())
                        .subscribe(dataProvider::removeLocationFromDatabase, Crashlytics::logException)
        );
    }

    void clearState() { subscriptions.clear(); }
}
