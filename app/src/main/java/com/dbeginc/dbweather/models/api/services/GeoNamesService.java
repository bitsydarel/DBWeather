package com.dbeginc.dbweather.models.api.services;

import com.dbeginc.dbweather.models.datatypes.geonames.GeoNamesResult;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by Darel Bitsy on 03/04/17.
 * GeoName Api Retrofit service
 */

public interface GeoNamesService {
    @GET("/search")
    Call<GeoNamesResult> getLocation(
            @Query("q")
            final String query,
            @Query("username")
            final String username,
            @Query("style")
            final String style,
            @Query("maxRows")
            final int maxRows,
            @Query("isNameRequired")
            final boolean isNameRequired,
            @Query("lang")
            final String language
    );
}
