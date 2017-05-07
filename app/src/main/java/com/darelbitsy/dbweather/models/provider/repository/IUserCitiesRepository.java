package com.darelbitsy.dbweather.models.provider.repository;

import android.support.annotation.NonNull;

import com.darelbitsy.dbweather.models.datatypes.geonames.GeoName;

import java.util.List;

import io.reactivex.Single;

/**
 * Created by Darel Bitsy on 23/04/17.
 */

public interface IUserCitiesRepository {

    Single<List<GeoName>> getUserCities();

    void removeCity(@NonNull final GeoName location);
}
