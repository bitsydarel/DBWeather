package com.darelbitsy.dbweather.models.provider.geoname;

import io.reactivex.Single;

/**
 * Created by Darel Bitsy on 22/04/17.
 */

public interface LocationInfoProvider<TYPE> {
    Single<TYPE> getLocation(final String locationName);
}
