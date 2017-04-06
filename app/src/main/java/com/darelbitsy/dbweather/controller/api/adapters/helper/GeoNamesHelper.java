package com.darelbitsy.dbweather.controller.api.adapters.helper;

import android.content.Context;

import com.darelbitsy.dbweather.controller.api.adapters.network.GeoNamesAdapter;
import com.darelbitsy.dbweather.model.geonames.GeoName;

import java.util.List;

import io.reactivex.Single;

/**
 * Created by Darel Bitsy on 03/04/17.
 * Helper class to query the api and get the result
 */

public class GeoNamesHelper {
    private final GeoNamesAdapter geoNamesAdapter;

    public GeoNamesHelper(Context context) {
        geoNamesAdapter = new GeoNamesAdapter(context);
    }

    public Single<List<GeoName>> getLocationFromApi(String query) {
        return Single.create(emitter -> {
           try {
               if (!emitter.isDisposed()) {
                   emitter.onSuccess(geoNamesAdapter
                           .getLocations(query)
                           .execute()
                           .body().getGeoName());
               }

           } catch (Exception e) { if (!emitter.isDisposed()) {emitter.onError(e);} }
        });
    }
}
