package com.darelbitsy.dbweather.controller.api.services;

import com.darelbitsy.dbweather.model.news.MyMemoryJson;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by Darel Bitsy on 19/02/17.
 */

public interface TranslateService {

    @GET("/get")
    Call<MyMemoryJson> getTranslatedText (
            @Query("q")
            final String textToTranslate,
            @Query("langpair")
            final String langPair,
            @Query("de")
            final String email
    );

    @GET("/get")
    Call<MyMemoryJson> getTranslatedText (
            @Query("q")
            final String textToTranslate,
            @Query("langpair")
            final String langPair
    );

}
