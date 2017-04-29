package com.darelbitsy.dbweather.models.api.services;

import com.darelbitsy.dbweather.models.datatypes.weather.Weather;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by Darel Bitsy on 20/02/17.
 */

public interface WeatherService {
    @GET("forecast/{apiKey}/{coordinates}")
    Call<Weather> getWeather(
            @Path("apiKey")
            final String apiKey,
            @Path("coordinates")
            final String coordinates,
            @Query("units")
            final String unitsType
    );

    @GET("forecast/{apiKey}/{coordinates}")
    Call<Weather> getWeatherWithLanguage (
            @Path("apiKey")
            final String apiKey,
            @Path("coordinates")
            final String coordinates,
            @Query("lang")
            final String language,
            @Query("units")
            final String unitsType
    );
}
