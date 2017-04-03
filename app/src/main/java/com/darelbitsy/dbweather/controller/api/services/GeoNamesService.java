package com.darelbitsy.dbweather.controller.api.services;

import com.darelbitsy.dbweather.model.geonames.GeoNamesResult;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by Darel Bitsy on 03/04/17.
 */

public interface GeoNamesService {
    @GET("/search")
    Call<GeoNamesResult> getLocation(
            @Query("q") String query,
            @Query("username") String username,
            @Query("style") String style,
            @Query("maxRows") int maxRows,
            @Query("isNameRequired") boolean isNameRequired,
            @Query("lang") String language
    );
}
