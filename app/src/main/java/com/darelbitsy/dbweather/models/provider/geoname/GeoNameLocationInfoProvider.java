package com.darelbitsy.dbweather.models.provider.geoname;

import android.content.Context;

import com.darelbitsy.dbweather.models.api.adapters.network.GeoNamesAdapter;
import com.darelbitsy.dbweather.models.datatypes.geonames.GeoName;

import java.util.List;

import io.reactivex.Single;

/**
 * Created by Darel Bitsy on 22/04/17.
 */

public class GeoNameLocationInfoProvider implements LocationInfoProvider<List<GeoName>> {
    private final GeoNamesAdapter geoNamesAdapter;

    public GeoNameLocationInfoProvider(final Context context) {
        geoNamesAdapter = GeoNamesAdapter.newInstance(context.getApplicationContext());
    }

    @Override
    public Single<List<GeoName>> getLocation(final String locationName) {
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
