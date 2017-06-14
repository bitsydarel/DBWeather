package com.dbeginc.dbweather.models.provider;

import android.support.annotation.NonNull;
import android.support.v4.util.Pair;

import com.dbeginc.dbweather.models.datatypes.geonames.GeoName;
import com.dbeginc.dbweather.models.datatypes.news.LiveNews;

import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;

import io.reactivex.Completable;
import io.reactivex.Single;

/**
 * Created by Bitsy Darel on 26.05.17.
 */

interface IDatabaseProvider {
    Completable addLocationToDatabase(@Nonnull final GeoName location);

    Completable saveNewsSourceConfiguration(@Nonnull final String sourceName, final int newsItemValue, final int isOn);

    Single<Map<String, Pair<Integer, Integer>>> getNewsSources();

    Single<Boolean> isLocationInDatabase(@NonNull final String cityName);

    Single<List<GeoName>> getUserCitiesFromDatabase();

    Single<Boolean> isLiveInDatabase(@NonNull final String liveSourceName);

    Completable refreshLiveData(@NonNull final LiveNews liveNews, final boolean isInTheDB);

    void initiateLiveSourcesTable();

    Single<List<LiveNews>> getLiveSources();

    Completable removeLiveSource(@NonNull final LiveNews liveNews);

    void removeLocationFromDatabase(final GeoName location);

    Single<Boolean> isNewsSourceInDatabase(@NonNull final String newsSource);

    Completable addNewsSourceToDatabase(@NonNull final String sourceName);
}
