package com.darelbitsy.dbweather.presenters.activities;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.util.Pair;
import android.util.Log;
import android.view.View;

import com.darelbitsy.dbweather.R;
import com.darelbitsy.dbweather.extensions.holder.ConstantHolder;
import com.darelbitsy.dbweather.extensions.utility.AppUtil;
import com.darelbitsy.dbweather.extensions.utility.weather.WeatherUtil;
import com.darelbitsy.dbweather.models.datatypes.geonames.GeoName;
import com.darelbitsy.dbweather.models.datatypes.news.Article;
import com.darelbitsy.dbweather.models.datatypes.weather.Weather;
import com.darelbitsy.dbweather.provider.news.DatabaseNewsProvider;
import com.darelbitsy.dbweather.provider.news.NetworkNewsProvider;
import com.darelbitsy.dbweather.provider.repository.IUserCitiesRepository;
import com.darelbitsy.dbweather.provider.schedulers.RxSchedulersProvider;
import com.darelbitsy.dbweather.provider.weather.DatabaseWeatherProvider;
import com.darelbitsy.dbweather.provider.weather.NetworkWeatherProvider;
import com.darelbitsy.dbweather.views.activities.IWeatherActivityView;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import io.reactivex.Scheduler;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;

/**
 * Created by Darel Bitsy on 22/04/17.
 * MainView Presenter with Rx
 */

public class RxWeatherActivityPresenter implements IWeatherActivityPresenter {

    private final DatabaseWeatherProvider mDatabaseWeatherProvider;
    private final NetworkWeatherProvider mNetworkWeatherProvider;
    private final DatabaseNewsProvider mDatabaseNewsProvider;
    private final NetworkNewsProvider mNetworkNewsProvider;
    private final RxSchedulersProvider mSchedulersProvider;
    private final IUserCitiesRepository mUserCitiesRepository;
    private final IWeatherActivityView mMainView;
    private final CompositeDisposable rxSubscriptions = new CompositeDisposable();
    private final Scheduler mObserverOnScheduler;
    private final Context mApplicationContext;


    public RxWeatherActivityPresenter(
            final Context context,
            final IUserCitiesRepository userCitiesRepository,
            final IWeatherActivityView mainView,
            final Scheduler scheduler) {

        mApplicationContext = context;

        mUserCitiesRepository = userCitiesRepository;

        mDatabaseWeatherProvider = new DatabaseWeatherProvider(mApplicationContext);
        mNetworkWeatherProvider = new NetworkWeatherProvider(mApplicationContext);

        mDatabaseNewsProvider = new DatabaseNewsProvider(mApplicationContext);
        mNetworkNewsProvider = new NetworkNewsProvider(mApplicationContext);

        mSchedulersProvider = RxSchedulersProvider.newInstance();

        mMainView = mainView;

        mObserverOnScheduler = scheduler;

    }

    public void configureView() {
        loadUserCitiesMenu();
        loadWeather();
        loadNews();
    }

    @Override
    public void loadWeather() {
        rxSubscriptions.add(mDatabaseWeatherProvider.getWeather()
                .subscribeOn(mSchedulersProvider.getWeatherScheduler())
                .observeOn(mObserverOnScheduler)
                .subscribeWith(new WeatherObserver()));
    }

    @Override
    public void loadNews() {
        rxSubscriptions.add(mDatabaseNewsProvider.getNews()
                .subscribeOn(mSchedulersProvider.getNewsScheduler())
                .observeOn(mObserverOnScheduler)
                .subscribeWith(new NewsObserver()));
    }

    @Override
    public void saveState(final Bundle save) {
    }

    @Override
    public void clearState() {
        rxSubscriptions.clear();
        cleanCache();
    }

    @Override
    public void getWeather() {
        rxSubscriptions.add(mNetworkWeatherProvider.getWeather()
                .subscribeOn(mSchedulersProvider.getWeatherScheduler())
                .observeOn(mObserverOnScheduler)
                .subscribeWith(new WeatherObserver()));
    }

    @Override
    public void getWeatherForCity(@NonNull final String cityName,
                                  final double latitude,
                                  final double longitude) {

        if (AppUtil.isNetworkAvailable(mApplicationContext)) {
            rxSubscriptions.add(mNetworkWeatherProvider.getWeatherForCity(cityName, latitude, longitude)
                    .subscribeOn(mSchedulersProvider.getWeatherScheduler())
                    .observeOn(mObserverOnScheduler)
                    .subscribeWith(new WeatherObserver()));

        } else {
            rxSubscriptions.add(mDatabaseWeatherProvider.getWeatherForCity(cityName, latitude, longitude)
                    .subscribeOn(mSchedulersProvider.getDatabaseWorkScheduler())
                    .observeOn(mObserverOnScheduler)
                    .subscribeWith(new WeatherObserver()));
        }
    }

    @Override
    public void loadWeatherForCity(@NonNull final String cityName,
                                   final double latitude,
                                   final double longitude) {

        rxSubscriptions.add(mDatabaseWeatherProvider
                .getWeatherForCity(cityName, latitude, longitude)
                .subscribeOn(mSchedulersProvider.getWeatherScheduler())
                .observeOn(mObserverOnScheduler)
                .subscribeWith(new WeatherObserver()));
    }

    @Override
    public void loadUserCitiesMenu() {

        rxSubscriptions.add(mUserCitiesRepository.getUserCities()
                .subscribeOn(mSchedulersProvider.getDatabaseWorkScheduler())
                .observeOn(mObserverOnScheduler)
                .subscribeWith(new DisposableSingleObserver<List<GeoName>>() {
                    @Override
                    public void onSuccess(@NonNull final List<GeoName> userCities) {
                        if (userCities.isEmpty()) {
                            mMainView.setupNavigationDrawerWithNoCities();

                        } else {
                            mMainView.setupNavigationDrawerWithCities(userCities);
                        }
                    }
                    @Override
                    public void onError(final Throwable throwable) {
                        mMainView.setupNavigationDrawerWithNoCities();
                    }
                }));
    }

    @Override
    public void removeCityFromUserCities(@NonNull final GeoName location) {
        mUserCitiesRepository.removeCity(location);
    }

    @Override
    public void getNews() {
        rxSubscriptions.add(mNetworkNewsProvider.getNews()
                .subscribeOn(mSchedulersProvider.getNewsScheduler())
                .observeOn(mObserverOnScheduler)
                .subscribeWith(new NewsObserver()));
    }

    private class NewsObserver extends DisposableSingleObserver<List<Article>> {
        @Override
        public void onSuccess(@NonNull final List<Article> articles) {
            mMainView.showNews(articles);
        }

        @Override
        public void onError(final Throwable throwable) {
            mMainView.showNetworkNewsErrorMessage();
        }
    }

    private void cleanCache() {
        final File dir = AppUtil.getFileCache(mApplicationContext);
        if (dir.isDirectory()) {
            for (final File file : dir.listFiles()) {
                Log.i(ConstantHolder.TAG, "Is File Cache Cleared on exit: "
                        + file.delete());
            }
        }
    }

    public void shareScreenShot(@NonNull final Activity activity) throws IOException {
        final Intent shareIntent = new Intent(Intent.ACTION_SEND);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
        } else {
            shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
            shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        }

        shareIntent.setType("image/jpeg");
        shareIntent.putExtra(Intent.EXTRA_STREAM, takeScreenShot(activity));

        activity.startActivity(Intent.createChooser(shareIntent,
                mApplicationContext.getString(R.string.send_to)));
    }

    private Uri takeScreenShot(@NonNull final Activity activity) throws IOException {
        OutputStream outputStream = null;

        if (Environment.getExternalStorageState().equalsIgnoreCase(Environment.MEDIA_MOUNTED)) {
            try {
                final View viewToShot = activity.getWindow().getDecorView().getRootView();
                final boolean defaultDrawing = viewToShot.isDrawingCacheEnabled();
                viewToShot.setDrawingCacheEnabled(true);
                final Bitmap screenShot = Bitmap.createBitmap(viewToShot.getDrawingCache());
                viewToShot.setDrawingCacheEnabled(defaultDrawing);

                final ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.TITLE, "db_weather");
                values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
                final Uri uri = activity.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        values);

                if (uri != null) {
                    outputStream = activity.getContentResolver().openOutputStream(uri);
                    screenShot.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                    return uri;

                }

            } catch (final IOException e) {
                Log.i(ConstantHolder.TAG, "Error while Creating screenshot File: " + e.getMessage());

            } finally {
                if (outputStream != null) {
                    outputStream.close();
                }
            }
        }
        return null;
    }

    private class WeatherObserver extends DisposableSingleObserver<Weather> {

        @Override
        public void onSuccess(@NonNull final Weather weather) {
            mMainView.showWeather(new Pair<>(WeatherUtil.parseWeather(weather, mApplicationContext),
                    weather.getHourly().getData()));
        }

        @Override
        public void onError(final Throwable throwable) {
            mMainView.showNetworkWeatherErrorMessage();
        }
    }
}
