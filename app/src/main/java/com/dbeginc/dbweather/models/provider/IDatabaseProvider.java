package com.dbeginc.dbweather.models.provider;

import android.support.v4.util.Pair;

import com.dbeginc.dbweather.models.datatypes.geonames.GeoName;

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
}
