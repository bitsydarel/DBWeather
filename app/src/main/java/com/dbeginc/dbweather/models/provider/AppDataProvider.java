package com.dbeginc.dbweather.models.provider;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v4.util.Pair;

import com.dbeginc.dbweather.DBWeatherApplication;
import com.dbeginc.dbweather.models.datatypes.geonames.GeoName;
import com.dbeginc.dbweather.models.datatypes.news.Article;
import com.dbeginc.dbweather.models.datatypes.news.LiveNews;
import com.dbeginc.dbweather.models.datatypes.news.Sources;
import com.dbeginc.dbweather.models.datatypes.weather.Weather;
import com.dbeginc.dbweather.models.provider.geoname.GeoNameLocationInfoProvider;
import com.dbeginc.dbweather.models.provider.news.DatabaseNewsProvider;
import com.dbeginc.dbweather.models.provider.news.NetworkNewsProvider;
import com.dbeginc.dbweather.models.provider.preferences.IPreferencesProvider;
import com.dbeginc.dbweather.models.provider.repository.DatabaseUserCitiesRepository;
import com.dbeginc.dbweather.models.provider.weather.DatabaseWeatherProvider;
import com.dbeginc.dbweather.models.provider.weather.NetworkWeatherProvider;
import com.dbeginc.dbweather.utils.helper.DatabaseOperation;

import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Completable;
import io.reactivex.Single;

import static com.dbeginc.dbweather.utils.holder.ConstantHolder.CUSTOM_TAB_PACKAGE_NAME;
import static com.dbeginc.dbweather.utils.holder.ConstantHolder.FIRST_RUN;
import static com.dbeginc.dbweather.utils.holder.ConstantHolder.IS_ACCOUNT_PERMISSION_GRANTED;
import static com.dbeginc.dbweather.utils.holder.ConstantHolder.IS_FROM_CITY_KEY;
import static com.dbeginc.dbweather.utils.holder.ConstantHolder.IS_GPS_PERMISSION_GRANTED;
import static com.dbeginc.dbweather.utils.holder.ConstantHolder.IS_WRITE_PERMISSION_GRANTED;
import static com.dbeginc.dbweather.utils.holder.ConstantHolder.NEWS_TRANSLATION_KEY;
import static com.dbeginc.dbweather.utils.holder.ConstantHolder.NOTIFICATION_KEY;
import static com.dbeginc.dbweather.utils.holder.ConstantHolder.SHOULD_WEATHER_BE_SAVED;

/**
 * Created by Darel Bitsy on 27/04/17.
 * Data Provider for the application
 */

@Singleton
public class AppDataProvider implements IDataProvider, IPreferencesProvider, IDatabaseProvider {
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
    DatabaseUserCitiesRepository mUserCitiesRepository;
    @Inject
    GeoNameLocationInfoProvider mGeoNameLocationInfoProvider;

    private DatabaseOperation mDatabaseOperation;

    @Inject
    public AppDataProvider(final Context context) {
        DBWeatherApplication.getComponent()
                .inject(this);
        mDatabaseOperation = DatabaseOperation.getInstance(context);
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
        mSharedPreferences.edit()
                .putBoolean(FIRST_RUN, isFirstRun)
                .apply();
    }

    @Override
    public boolean isCurrentWeatherFromGps() {
        return mSharedPreferences.getBoolean(IS_FROM_CITY_KEY, false);
    }

    @Override
    public void setCurrentWeatherFromGps(final boolean isFromUserCities) {
        mSharedPreferences.edit()
                .putBoolean(IS_FROM_CITY_KEY, isFromUserCities)
                .apply();
    }

    @Override
    public boolean getWeatherNotificationStatus() {
        return mSharedPreferences.getBoolean(NOTIFICATION_KEY, false);
    }

    @Override
    public void setWeatherNotificationStatus(final boolean isOn) {
        mSharedPreferences.edit()
                .putBoolean(NOTIFICATION_KEY, isOn)
                .apply();
    }

    @Override
    public boolean getNewsTranslationStatus() {
        return mSharedPreferences.getBoolean(NEWS_TRANSLATION_KEY, false);
    }

    @Override
    public void setNewsTranslationStatus(final boolean isOn) {
        mSharedPreferences.edit()
                .putBoolean(NEWS_TRANSLATION_KEY, isOn)
                .apply();
    }

    @Override
    public void setAccountPermissionStatus(final boolean isPermissionAccorded) {
        mSharedPreferences.edit()
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

    @Override
    public void setCustomTabPackage(@Nonnull final String packageName) {
        mSharedPreferences.edit()
                .putString(CUSTOM_TAB_PACKAGE_NAME, packageName)
                .apply();
    }

    @Override
    public String getCustomTabPackage() {
        return mSharedPreferences.getString(CUSTOM_TAB_PACKAGE_NAME, "");
    }

    @Override
    public boolean shouldWeatherBeSaved() {
        return mSharedPreferences.getBoolean(SHOULD_WEATHER_BE_SAVED, false);
    }

    @Override
    public void doWeSaveWeather(boolean value) {
        mSharedPreferences.edit().putBoolean(SHOULD_WEATHER_BE_SAVED, value)
                .apply();
    }

    @Override
    public Completable addLocationToDatabase(@NonNull final GeoName location) {
        return mDatabaseOperation.addLocationToDatabase(location);
    }

    @Override
    public Completable saveNewsSourceConfiguration(@Nonnull final String sourceName, final int newsItemValue, final int isOn) {
        return mDatabaseOperation.saveNewsSourceConfiguration(sourceName, newsItemValue, isOn);
    }

    @Override
    public Single<Map<String, Pair<Integer, Integer>>> getNewsSources() {
        return mDatabaseOperation.getNewsSources();
    }

    @Override
    public Single<Boolean> isLocationInDatabase(@NonNull String cityName) {
        return mDatabaseOperation.isLocationInDatabase(cityName);
    }

    @Override
    public Single<List<GeoName>> getUserCitiesFromDatabase() {
        return mUserCitiesRepository.getUserCities();
    }

    @Override
    public Single<Boolean> isLiveInDatabase(@NonNull String liveSourceName) {
        return mDatabaseOperation.isLiveInDatabase(liveSourceName);
    }

    @Override
    public Completable refreshLiveData(@NonNull LiveNews liveNews, boolean isInTheDB) {
        return mDatabaseOperation.refreshLiveData(liveNews, isInTheDB);
    }

    @Override
    public void initiateLiveSourcesTable() {
        mDatabaseOperation.initiateLiveSourcesTable();
    }

    @Override
    public Single<List<LiveNews>> getLiveSources() {
        return mDatabaseOperation.getLiveSources();
    }

    @Override
    public Completable removeLiveSource(@NonNull LiveNews liveNews) {
        return mDatabaseOperation.removeLiveSource(liveNews);
    }

    @Override
    public void removeLocationFromDatabase(@NonNull final GeoName location) {
        mDatabaseOperation.removeLocationFromDatabase(location);
    }

    @Override
    public Single<Boolean> isNewsSourceInDatabase(@NonNull final String newsSource) {
        return mDatabaseOperation.isNewsSourceInDatabase(newsSource);
    }

    @Override
    public Completable addNewsSourceToDatabase(@NonNull String sourceName) {
        return mDatabaseOperation.addNewsSourceToDatabase(sourceName);
    }

    @Override
    public Single<Sources> getNewsFeedSources() {
        return mNetworkNewsProvider.getSourcesList();
    }

    @Override
    public Single<List<GeoName>> getLocationsForQuery(@NonNull String query) {
        return mGeoNameLocationInfoProvider.getLocation(query);
    }
}
