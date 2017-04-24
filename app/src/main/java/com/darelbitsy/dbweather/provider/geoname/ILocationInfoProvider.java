package com.darelbitsy.dbweather.models.provider.geoname;

import android.support.annotation.NonNull;

import io.reactivex.Single;

/**
 * Created by Darel Bitsy on 22/04/17.
 */

public interface ILocationInfoProvider<TYPE> {
    Single<TYPE> getLocation(@NonNull final String locationName);
}
