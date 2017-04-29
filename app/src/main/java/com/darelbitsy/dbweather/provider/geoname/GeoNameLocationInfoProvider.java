package com.darelbitsy.dbweather.provider.geoname;

import android.support.annotation.NonNull;

import com.darelbitsy.dbweather.DBWeatherApplication;
import com.darelbitsy.dbweather.models.api.adapters.GeoNamesAdapter;
import com.darelbitsy.dbweather.models.datatypes.geonames.GeoName;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Single;

/**
 * Created by Darel Bitsy on 22/04/17.
 * Location provider
 * with help of GeoName Api
 */

@Singleton
public class GeoNameLocationInfoProvider implements ILocationInfoProvider {
    @Inject GeoNamesAdapter geoNamesAdapter;

    @Inject
    public GeoNameLocationInfoProvider() {
        DBWeatherApplication.getComponent().inject(this);
    }

    @Override
    public Single<List<GeoName>> getLocation(@NonNull final String locationName) {
        return Single.create(emitter -> {
            try {
                if (!emitter.isDisposed()) {
                    emitter.onSuccess(geoNamesAdapter
                            .getLocations(locationName)
                            .execute()
                            .body()
                            .getGeoName());
                }

            } catch (final Exception e) { if (!emitter.isDisposed()) {emitter.onError(e);} }
        });
    }
}
