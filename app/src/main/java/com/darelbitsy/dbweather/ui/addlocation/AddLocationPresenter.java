package com.darelbitsy.dbweather.ui.addlocation;

import com.darelbitsy.dbweather.models.datatypes.geonames.GeoName;
import com.darelbitsy.dbweather.models.datatypes.news.Article;
import com.darelbitsy.dbweather.models.datatypes.weather.WeatherData;
import com.darelbitsy.dbweather.models.provider.AppDataProvider;
import com.darelbitsy.dbweather.models.provider.geoname.GeoNameLocationInfoProvider;
import com.darelbitsy.dbweather.models.provider.schedulers.RxSchedulersProvider;
import com.darelbitsy.dbweather.utils.utility.weather.WeatherUtil;

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
    private final RxSchedulersProvider schedulersProvider = RxSchedulersProvider.newInstance();
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
                .map(weather -> WeatherUtil.parseWeather(weather, locationView.getContext()))
                .observeOn(schedulersProvider.getUIScheduler())
                .subscribeWith(new DisposableSingleObserver<WeatherData>() {
                    @Override
                    public void onSuccess(final WeatherData weatherData) { locationView.saveWeatherForHomeButton(weatherData); }
                    @Override
                    public void onError(Throwable throwable) {}
                }));
    }

    void getNews() {
        mCompositeDisposable.add(dataProvider.getNewsFromDatabase()
                .subscribeOn(schedulersProvider.getNewsScheduler())
                .observeOn(schedulersProvider.getUIScheduler())
                .subscribeWith(new DisposableSingleObserver<List<Article>>() {
                    @Override
                    public void onSuccess(final List<Article> articles) { locationView.saveNewsForHomeButton(articles); }
                    @Override
                    public void onError(final Throwable throwable) {}
                }));
    }

    void addLocationToDatabase(@Nonnull final GeoName location) {
        mCompositeDisposable.add(dataProvider.addLocationToDatabase(location)
                .subscribeOn(schedulersProvider.getDatabaseWorkScheduler())
                .observeOn(schedulersProvider.getUIScheduler())
                .subscribeWith(new DisposableCompletableObserver() {
                    @Override
                    public void onComplete() { locationView.onLocationAdded(); }
                    @Override
                    public void onError(final Throwable throwable) { locationView.onLocationNotAdded(); }
                }));
    }

    void getLocations(@Nonnull final String query) {
        mCompositeDisposable.add(locationInfoProvider
                .getLocation(query)
                .subscribeOn(schedulersProvider.getWeatherScheduler())
                .observeOn(schedulersProvider.getUIScheduler())
                .subscribeWith(new DisposableSingleObserver<List<GeoName>>() {
                    @Override
                    public void onSuccess(final List<GeoName> geoNames) { locationView.displayFoundedLocation(geoNames); }
                    @Override
                    public void onError(final Throwable throwable) {}
                }));
    }

    void clearData() { mCompositeDisposable.clear(); }
}
