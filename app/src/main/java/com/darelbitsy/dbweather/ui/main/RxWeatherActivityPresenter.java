package com.darelbitsy.dbweather.ui.main;

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
import com.darelbitsy.dbweather.utils.holder.ConstantHolder;
import com.darelbitsy.dbweather.utils.utility.AppUtil;
import com.darelbitsy.dbweather.utils.utility.weather.WeatherUtil;
import com.darelbitsy.dbweather.models.datatypes.geonames.GeoName;
import com.darelbitsy.dbweather.models.datatypes.news.Article;
import com.darelbitsy.dbweather.models.datatypes.weather.Weather;
import com.darelbitsy.dbweather.models.provider.AppDataProvider;
import com.darelbitsy.dbweather.models.provider.repository.IUserCitiesRepository;
import com.darelbitsy.dbweather.models.provider.schedulers.RxSchedulersProvider;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;

/**
 * Created by Darel Bitsy on 22/04/17.
 * MainView Presenter with Rx
 */

public class RxWeatherActivityPresenter {

    private final RxSchedulersProvider mSchedulersProvider;
    private final IUserCitiesRepository mUserCitiesRepository;
    private final IWeatherActivityView mMainView;
    private final AppDataProvider mDataProvider;
    private final CompositeDisposable rxSubscriptions = new CompositeDisposable();


    public RxWeatherActivityPresenter(
            final IUserCitiesRepository userCitiesRepository,
            final IWeatherActivityView mainView,
            final AppDataProvider dataProvider) {

        mUserCitiesRepository = userCitiesRepository;

        mMainView = mainView;

        mDataProvider = dataProvider;

        mSchedulersProvider = RxSchedulersProvider.newInstance();
    }

    public void configureView() {
        loadUserCitiesMenu();
        loadWeather();
        loadNews();
    }

    public void loadWeather() {
        rxSubscriptions.add(mDataProvider.getWeatherFromDatabase()
                .subscribeOn(mSchedulersProvider.getWeatherScheduler())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new WeatherObserver()));
    }

    public void loadNews() {
        rxSubscriptions.add(mDataProvider.getNewsFromDatabase()
                .subscribeOn(mSchedulersProvider.getNewsScheduler())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new NewsObserver()));
    }

    public void saveState(final Bundle save) {

    }

    public void clearState(@NonNull final Context context) {
        rxSubscriptions.clear();
        cleanCache(context);
    }

    public void getWeather() {
        rxSubscriptions.add(mDataProvider.getWeatherFromApi()
                .subscribeOn(mSchedulersProvider.getWeatherScheduler())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new WeatherObserver()));
    }

    public void getWeatherForCity(@NonNull final String cityName,
                                  final double latitude,
                                  final double longitude) {

        rxSubscriptions.add(mDataProvider.getWeatherForCityFromApi(cityName, latitude, longitude)
                .subscribeOn(mSchedulersProvider.getWeatherScheduler())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new WeatherObserver()));
    }

    public void loadWeatherForCity(@NonNull final String cityName,
                                   final double latitude,
                                   final double longitude) {

        rxSubscriptions.add(mDataProvider.getWeatherForCityFromDatabase(cityName, latitude, longitude)
                .subscribeOn(mSchedulersProvider.getWeatherScheduler())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new WeatherObserver()));
    }

    public void loadUserCitiesMenu() {

        rxSubscriptions.add(mUserCitiesRepository.getUserCities()
                .subscribeOn(mSchedulersProvider.getDatabaseWorkScheduler())
                .observeOn(AndroidSchedulers.mainThread())
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

    public void removeCityFromUserCities(@NonNull final GeoName location) {
        mUserCitiesRepository.removeCity(location);
    }

    public void getNews() {
        rxSubscriptions.add(mDataProvider.getNewsFromApi()
                .subscribeOn(mSchedulersProvider.getNewsScheduler())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new NewsObserver()));
    }

    public boolean isFirstRun() {
        return mDataProvider.isFirstRun();
    }

    public void setFirstRun(final boolean isFirstRun) {
        mDataProvider.setFirstRun(isFirstRun);
    }

    public boolean didUserSelectedCityFromDrawer() {
        return mDataProvider.didUserSelectedCityFromDrawer();
    }

    public void userSelectedCityFromDrawer(final boolean isFromCity) {
        mDataProvider.userSelectedCityFromDrawer(isFromCity);
    }

    public Pair<String, double[]> getSelectedUserCity(@NonNull final String locationToFind) {
        return mDataProvider.getSelectedUserCity(locationToFind);
    }

    public void saveRecyclerBottomLimit(final float limit) {
        mDataProvider.saveRecyclerBottomLimit(limit);
    }

    public float getRecyclerBottomLimit() {
        return mDataProvider.getRecyclerBottomLimit();
    }

    public boolean getWeatherNotificationStatus() {
        return mDataProvider.getWeatherNotificationStatus();
    }

    public void setWeatherNotificationStatus(final boolean isOn) {
        mDataProvider.setWeatherNotificationStatus(isOn);
    }

    public boolean getNewsTranslationStatus() {
        return mDataProvider.getNewsTranslationStatus();
    }

    public void setNewsTranslationStatus(final boolean isOn) {
        mDataProvider.setNewsTranslationStatus(isOn);
    }

    public void setAccountPermissionStatus(final boolean isPermissionAccorded) {
        mDataProvider.setAccountPermissionStatus(isPermissionAccorded);
    }

    public boolean getAccountPermissionStatus() {
        return mDataProvider.getAccountPermissionStatus();
    }

    public void setGpsPermissionStatus(final boolean isPermissionAccorded) {
        mDataProvider.setGpsPermissionStatus(isPermissionAccorded);
    }

    public boolean getGpsPermissionStatus() {
        return mDataProvider.getGpsPermissionStatus();
    }

    public void setWritePermissionStatus(final boolean isPermissionAccorded) {
        mDataProvider.setWritePermissionStatus(isPermissionAccorded);
    }

    public boolean getWritePermissionStatus() {
        return mDataProvider.getWritePermissionStatus();
    }

    public void setSelectedUserCity(@NonNull final String locationSelected,
                             final double latitude,
                             final double longitude) {

        mDataProvider.setSelectedUserCity(locationSelected, latitude, longitude);
    }

    private void cleanCache(@NonNull final Context context) {
        final File dir = AppUtil.getFileCache(context);
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

        mMainView.launchActivity(Intent.createChooser(shareIntent,
                activity.getString(R.string.send_to)));
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

    private class NewsObserver extends DisposableSingleObserver<List<Article>> {
        @Override
        public void onSuccess(@NonNull final List<Article> articles) {
            if (!articles.isEmpty()) {
                mMainView.showNews(articles);

            } else {
                mMainView.showNetworkNewsErrorMessage();
            }
        }

        @Override
        public void onError(final Throwable throwable) {
            mMainView.showNetworkNewsErrorMessage();
        }
    }

    private class WeatherObserver extends DisposableSingleObserver<Weather> {

        @Override
        public void onSuccess(@NonNull final Weather weather) {
            if (weather.getCurrently() != null && !weather.getDaily().getData().isEmpty()
                    && !weather.getHourly().getData().isEmpty()) {

                mMainView.showWeather(new Pair<>(WeatherUtil.parseWeather(weather, mMainView.getAppContext()),
                        weather.getHourly().getData()));

            } else {
                mMainView.showNetworkWeatherErrorMessage();
            }
        }

        @Override
        public void onError(final Throwable throwable) {
            mMainView.showNetworkWeatherErrorMessage();
        }
    }
}
