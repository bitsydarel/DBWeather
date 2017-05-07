package com.darelbitsy.dbweather.models.provider.geoname;

import android.support.annotation.NonNull;

import com.darelbitsy.dbweather.models.datatypes.geonames.GeoName;

import java.util.List;

import io.reactivex.Single;

/**
 * Created by Darel Bitsy on 22/04/17.
 * Interface representing an location provider
 */

public interface ILocationInfoProvider {
    Single<List<GeoName>> getLocation(@NonNull final String locationName);
}
