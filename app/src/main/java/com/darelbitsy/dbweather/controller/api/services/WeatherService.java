package com.darelbitsy.dbweather.controller.api.services;

import com.darelbitsy.dbweather.model.weather.Weather;

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
            @Path("apiKey") String apiKey,
            @Path("coordinates") String coordinates,
            @Query("units") String unitsType
    );

    @GET("forecast/{apiKey}/{coordinates}")
    Call<Weather> getWeatherWithLanguage (
            @Path("apiKey") String apiKey,
            @Path("coordinates") String coordinates,
            @Query("lang") String language,
            @Query("units") String unitsType
    );
}
