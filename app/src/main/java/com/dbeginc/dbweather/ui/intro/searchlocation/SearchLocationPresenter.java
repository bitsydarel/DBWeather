package com.dbeginc.dbweather.ui.intro.searchlocation;

import android.support.annotation.NonNull;

import com.crashlytics.android.Crashlytics;
import com.dbeginc.dbweather.models.datatypes.geonames.GeoName;
import com.dbeginc.dbweather.models.datatypes.weather.Flags;
import com.dbeginc.dbweather.models.datatypes.weather.Hourly;
import com.dbeginc.dbweather.models.datatypes.weather.Weather;
import com.dbeginc.dbweather.models.provider.AppDataProvider;
import com.dbeginc.dbweather.models.provider.schedulers.RxSchedulersProvider;
import com.dbeginc.dbweather.utils.helper.DatabaseOperation;

import java.util.Locale;
import java.util.TimeZone;

import io.reactivex.Completable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.subjects.PublishSubject;

/**
 * Created by darel on 15.06.17.
 * Search Location Presenter
 */

class SearchLocationPresenter {

    private final SearchLocationView view;
    private final AppDataProvider dataProvider;
    private final DatabaseOperation databaseOperation;
    private final PublishSubject<GeoName> loadLocationEvent;
    private final PublishSubject<String> voiceQuery;
    private final RxSchedulersProvider schedulersProvider = RxSchedulersProvider.getInstance();
    private final CompositeDisposable subscriptions = new CompositeDisposable();

    SearchLocationPresenter(@NonNull final SearchLocationView searchLocationView, @NonNull final AppDataProvider dataProvider, @NonNull final PublishSubject<GeoName> loadLocationEvent, PublishSubject<String> voiceQuery) {
        view = searchLocationView;
        this.dataProvider = dataProvider;
        this.loadLocationEvent = loadLocationEvent;
        this.voiceQuery = voiceQuery;
        databaseOperation = DatabaseOperation.getInstance(view.getContext());
    }

    void subscribeToVoiceQuery() {
        subscriptions.add(
                voiceQuery.subscribeOn(schedulersProvider.getComputationThread())
                        .observeOn(schedulersProvider.getUIScheduler())
                        .doOnNext(view::setQuery)
                        .subscribe(s -> subscriptions.add(
                                dataProvider.getLocationsForQuery(s)
                                        .subscribeOn(schedulersProvider.getWeatherScheduler())
                                        .observeOn(schedulersProvider.getUIScheduler())
                                        .subscribe(view::loadLocation, Crashlytics::logException)
                        ), Crashlytics::logException)
        );
    }

    void subscribeToLocationClickEvent() {
        subscriptions.add(
                loadLocationEvent.subscribeOn(schedulersProvider.getComputationThread())
                        .observeOn(schedulersProvider.getUIScheduler())
                        .subscribe(this::loadLocation, Crashlytics::logException)
        );
    }

    private void loadLocation(@NonNull final GeoName location) {
        final Weather weather = new Weather();
        weather.setCityName(String.format(Locale.getDefault(), "%s, %s", location.getName(), location.getCountryName()));
        weather.setTimezone(TimeZone.getDefault().getID());
        weather.setLatitude(location.getLatitude());
        weather.setLongitude(location.getLongitude());
        weather.setHourly(new Hourly());
        weather.getHourly().setSummary("UNKNOWN");
        weather.setFlags(new Flags());
        weather.getFlags().setUnits("C");

        subscriptions.add(
                Completable.create(completableEmitter -> {
                    databaseOperation.saveWeatherData(weather);
                    completableEmitter.onComplete();

                }).subscribeOn(schedulersProvider.getWeatherScheduler())
                        .observeOn(schedulersProvider.getUIScheduler())
                        .subscribe(view::onLocationSelected, Crashlytics::logException)
        );
    }

    void clearState() { subscriptions.clear(); }
}
