package com.darelbitsy.dbweather.models.provider;

import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v4.util.Pair;

import com.darelbitsy.dbweather.DBWeatherApplication;
import com.darelbitsy.dbweather.models.datatypes.news.Article;
import com.darelbitsy.dbweather.models.datatypes.weather.Weather;
import com.darelbitsy.dbweather.models.provider.news.DatabaseNewsProvider;
import com.darelbitsy.dbweather.models.provider.news.NetworkNewsProvider;
import com.darelbitsy.dbweather.models.provider.preferences.IPreferencesProvider;
import com.darelbitsy.dbweather.models.provider.weather.DatabaseWeatherProvider;
import com.darelbitsy.dbweather.models.provider.weather.NetworkWeatherProvider;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Single;

import static com.darelbitsy.dbweather.utils.holder.ConstantHolder.CITY_NAME_KEY;
import static com.darelbitsy.dbweather.utils.holder.ConstantHolder.FIRST_RUN;
import static com.darelbitsy.dbweather.utils.holder.ConstantHolder.IS_ACCOUNT_PERMISSION_GRANTED;
import static com.darelbitsy.dbweather.utils.holder.ConstantHolder.IS_FROM_CITY_KEY;
import static com.darelbitsy.dbweather.utils.holder.ConstantHolder.IS_GPS_PERMISSION_GRANTED;
import static com.darelbitsy.dbweather.utils.holder.ConstantHolder.IS_WRITE_PERMISSION_GRANTED;
import static com.darelbitsy.dbweather.utils.holder.ConstantHolder.NEWS_TRANSLATION_KEY;
import static com.darelbitsy.dbweather.utils.holder.ConstantHolder.NOTIFICATION_KEY;
import static com.darelbitsy.dbweather.utils.holder.ConstantHolder.RECYCLER_BOTTOM_LIMIT;
import static com.darelbitsy.dbweather.utils.holder.ConstantHolder.SELECTED_CITY_LATITUDE;
import static com.darelbitsy.dbweather.utils.holder.ConstantHolder.SELECTED_CITY_LONGITUDE;

/**
 * Created by Darel Bitsy on 27/04/17.
 * Data Provider for the application
 */

@Singleton
public class AppDataProvider implements IDataProvider, IPreferencesProvider {
    @Inject
    DatabaseWeatherProvider mDatabaseWeatherProvider;
    @Inject
    NetworkWeatherProvider mNetworkWeatherProvider;
    @Inject
    DatabaseNewsProvider mDatabaseNewsProvider;
    @Inject
    NetworkNewsProvider mNetworkNewsProvider;
    @Inject
    SharedPreferences mSharedPreferences;

    @Inject
    public AppDataProvider() {
        DBWeatherApplication.getComponent()
                .inject(this);
    }

    @Override
    public Single<List<Article>> getNewsFromApi() {
        return mNetworkNewsProvider.getNews();
    }

    @Override
    public Single<List<Article>> getNewsFromDatabase() {
        return mDatabaseNewsProvider.getNews();
    }

    @Override
    public Single<Weather> getWeatherFromApi() {
        return mNetworkWeatherProvider.getWeather();
    }

    @Override
    public Single<Weather> getWeatherFromDatabase() { return mDatabaseWeatherProvider.getWeather(); }

    @Override
    public Single<Weather> getWeatherForCityFromApi(@NonNull final String cityName, final double latitude, final double longitude) {
        return mNetworkWeatherProvider.getWeatherForCity(cityName, latitude, longitude);
    }

    @Override
    public Single<Weather> getWeatherForCityFromDatabase(@NonNull final String cityName, final double latitude, final double longitude) {
        return mDatabaseWeatherProvider.getWeatherForCity(cityName, latitude, longitude);
    }

    @Override
    public boolean isFirstRun() {
        return mSharedPreferences.getBoolean(FIRST_RUN, true);
    }

    @Override
    public void setFirstRun(final boolean isFirstRun) {
        mSharedPreferences
                .edit()
                .putBoolean(FIRST_RUN, isFirstRun)
                .apply();
    }

    @Override
    public boolean didUserSelectedCityFromDrawer() {
        return mSharedPreferences.getBoolean(IS_FROM_CITY_KEY, false);
    }

    @Override
    public void userSelectedCityFromDrawer(final boolean isFromCity) {
        mSharedPreferences
                .edit()
                .putBoolean(IS_FROM_CITY_KEY, isFromCity)
                .apply();
    }

    @Override
    public Pair<String, double[]> getSelectedUserCity(@NonNull final String messageIfNotFound) {
        final double[] coordinates = new double[2];

        coordinates[0] = Double.longBitsToDouble(mSharedPreferences.getLong(SELECTED_CITY_LATITUDE, 0));

        coordinates[1] = Double.longBitsToDouble(mSharedPreferences.getLong(SELECTED_CITY_LONGITUDE, 0));

        return new Pair<>(mSharedPreferences.getString(CITY_NAME_KEY, messageIfNotFound), coordinates);
    }

    @Override
    public void setSelectedUserCity(@NonNull final String locationToSelected,
                                    final double latitude,
                                    final double longitude) {

        mSharedPreferences
                .edit()
                .putString(CITY_NAME_KEY, locationToSelected)
                .apply();

        mSharedPreferences
                .edit()
                .putLong(SELECTED_CITY_LATITUDE, Double.doubleToRawLongBits(latitude))
                .apply();

        mSharedPreferences
                .edit()
                .putLong(SELECTED_CITY_LONGITUDE, Double.doubleToRawLongBits(longitude))
                .apply();
    }

    @Override
    public void saveRecyclerBottomLimit(final float heightLimit) {
        mSharedPreferences
                .edit()
                .putFloat(RECYCLER_BOTTOM_LIMIT, Math.round(heightLimit * 0.7f))
                .apply();
    }

    @Override
    public float getRecyclerBottomLimit() {
        return mSharedPreferences.getFloat(RECYCLER_BOTTOM_LIMIT, 0);
    }

    @Override
    public boolean getWeatherNotificationStatus() {
        return mSharedPreferences.getBoolean(NOTIFICATION_KEY, false);
    }

    @Override
    public void setWeatherNotificationStatus(final boolean isOn) {
        mSharedPreferences
                .edit()
                .putBoolean(NOTIFICATION_KEY, isOn)
                .apply();
    }

    @Override
    public boolean getNewsTranslationStatus() {
        return mSharedPreferences.getBoolean(NEWS_TRANSLATION_KEY, false);
    }

    @Override
    public void setNewsTranslationStatus(final boolean isOn) {
        mSharedPreferences
                .edit()
                .putBoolean(NEWS_TRANSLATION_KEY, isOn)
                .apply();
    }

    @Override
    public void setAccountPermissionStatus(final boolean isPermissionAccorded) {
        mSharedPreferences
                .edit()
                .putBoolean(IS_ACCOUNT_PERMISSION_GRANTED, isPermissionAccorded)
                .apply();
    }

    @Override
    public boolean getAccountPermissionStatus() {
        return mSharedPreferences.getBoolean(IS_ACCOUNT_PERMISSION_GRANTED, false);
    }

    @Override
    public void setGpsPermissionStatus(final boolean isPermissionAccorded) {
        mSharedPreferences
                .edit()
                .putBoolean(IS_GPS_PERMISSION_GRANTED, isPermissionAccorded)
                .apply();
    }

    @Override
    public boolean getGpsPermissionStatus() {
        return mSharedPreferences.getBoolean(IS_GPS_PERMISSION_GRANTED, false);
    }

    @Override
    public void setWritePermissionStatus(final boolean isPermissionAccorded) {
        mSharedPreferences
                .edit()
                .putBoolean(IS_WRITE_PERMISSION_GRANTED, isPermissionAccorded)
                .apply();
    }

    @Override
    public boolean getWritePermissionStatus() {
        return mSharedPreferences.getBoolean(IS_WRITE_PERMISSION_GRANTED, false);
    }
}
