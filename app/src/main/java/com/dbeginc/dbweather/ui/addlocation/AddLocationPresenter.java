package com.dbeginc.dbweather.ui.addlocation;

import com.crashlytics.android.Crashlytics;
import com.dbeginc.dbweather.models.datatypes.geonames.GeoName;
import com.dbeginc.dbweather.models.datatypes.news.Article;
import com.dbeginc.dbweather.models.datatypes.weather.WeatherData;
import com.dbeginc.dbweather.models.provider.AppDataProvider;
import com.dbeginc.dbweather.models.provider.geoname.GeoNameLocationInfoProvider;
import com.dbeginc.dbweather.models.provider.schedulers.RxSchedulersProvider;
import com.dbeginc.dbweather.utils.utility.weather.WeatherUtil;

import java.util.List;

import javax.annotation.Nonnull;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableCompletableObserver;
import io.reactivex.observers.DisposableSingleObserver;

/**
 * Created by Bitsy Darel on 13.05.17.
 */

class AddLocationPresenter {

    private final AppDataProvider dataProvider;
    private final IAddLocationView locationView;
    private final RxSchedulersProvider schedulersProvider = RxSchedulersProvider.getInstance();
    private final GeoNameLocationInfoProvider locationInfoProvider;
    private final CompositeDisposable mCompositeDisposable;

    AddLocationPresenter(final AppDataProvider appDataProvider, final GeoNameLocationInfoProvider locationInfoProvider, final CompositeDisposable compositeDisposable, final IAddLocationView view) {
        dataProvider = appDataProvider;
        this.locationInfoProvider = locationInfoProvider;
        mCompositeDisposable = compositeDisposable;
        locationView = view;
    }

    void getWeather() {
        mCompositeDisposable.add(dataProvider.getWeatherFromDatabase()
                .subscribeOn(schedulersProvider.getWeatherScheduler())
                .observeOn(schedulersProvider.getComputationThread())
                .unsubscribeOn(schedulersProvider.getWeatherScheduler())
                .map(weather -> WeatherUtil.parseWeather(weather, locationView.getContext()))
                .observeOn(schedulersProvider.getUIScheduler())
                .unsubscribeOn(schedulersProvider.getWeatherScheduler())
                .subscribeWith(new DisposableSingleObserver<WeatherData>() {
                    @Override
                    public void onSuccess(final WeatherData weatherData) { locationView.saveWeatherForHomeButton(weatherData); }
                    @Override
                    public void onError(final Throwable throwable) { Crashlytics.logException(throwable); }
                }));
    }

    void getNews() {
        mCompositeDisposable.add(dataProvider.getNewsFromDatabase()
                .subscribeOn(schedulersProvider.getNewsScheduler())
                .observeOn(schedulersProvider.getUIScheduler())
                .unsubscribeOn(schedulersProvider.getNewsScheduler())
                .subscribeWith(new DisposableSingleObserver<List<Article>>() {
                    @Override
                    public void onSuccess(final List<Article> articles) { locationView.saveNewsForHomeButton(articles); }
                    @Override
                    public void onError(final Throwable throwable) { Crashlytics.logException(throwable); }
                }));
    }

    void addLocationToDatabase(@Nonnull final GeoName location) {
        mCompositeDisposable.add(dataProvider.addLocationToDatabase(location)
                .subscribeOn(schedulersProvider.getDatabaseWorkScheduler())
                .observeOn(schedulersProvider.getUIScheduler())
                .unsubscribeOn(schedulersProvider.getDatabaseWorkScheduler())
                .subscribeWith(new DisposableCompletableObserver() {
                    @Override
                    public void onComplete() { locationView.onLocationAdded(); }
                    @Override
                    public void onError(final Throwable throwable) {
                        locationView.onLocationNotAdded();
                        Crashlytics.logException(throwable);
                    }
                }));
    }

    void getLocations(@Nonnull final String query) {
        mCompositeDisposable.add(locationInfoProvider
                .getLocation(query)
                .subscribeOn(schedulersProvider.getWeatherScheduler())
                .observeOn(schedulersProvider.getUIScheduler())
                .unsubscribeOn(schedulersProvider.getWeatherScheduler())
                .subscribeWith(new DisposableSingleObserver<List<GeoName>>() {
                    @Override
                    public void onSuccess(final List<GeoName> geoNames) { locationView.displayFoundedLocation(geoNames); }
                    @Override
                    public void onError(final Throwable throwable) { Crashlytics.logException(throwable); }
                }));
    }

    void clearData() { mCompositeDisposable.clear(); }
}
