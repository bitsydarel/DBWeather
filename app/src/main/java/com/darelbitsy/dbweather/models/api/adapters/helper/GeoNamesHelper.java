package com.darelbitsy.dbweather.models.api.adapters.helper;

import android.content.Context;

import com.darelbitsy.dbweather.models.api.adapters.network.GeoNamesAdapter;
import com.darelbitsy.dbweather.models.datatypes.geonames.GeoName;

import java.util.List;

import io.reactivex.Single;

/**
 * Created by Darel Bitsy on 03/04/17.
 * Helper class to query the api and get the result
 */

public class GeoNamesHelper {
    private final GeoNamesAdapter geoNamesAdapter;
    private static GeoNamesHelper singletonGeoNamesHelper;

    public static GeoNamesHelper newInstance(final Context context) {
        if (singletonGeoNamesHelper == null) {
            singletonGeoNamesHelper = new GeoNamesHelper(context.getApplicationContext());
        }
        return singletonGeoNamesHelper;
    }

    private GeoNamesHelper(final Context context) {
        geoNamesAdapter = GeoNamesAdapter.newInstance(context);
    }

    public Single<List<GeoName>> getLocationFromApi(final String query) {
        return Single.create(emitter -> {
           try {
               if (!emitter.isDisposed()) {
                   emitter.onSuccess(geoNamesAdapter
                           .getLocations(query)
                           .execute()
                           .body()
                           .getGeoName());
               }

           } catch (final Exception e) { if (!emitter.isDisposed()) {emitter.onError(e);} }
        });
    }
}
