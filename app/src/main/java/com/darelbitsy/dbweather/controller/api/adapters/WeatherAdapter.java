package com.darelbitsy.dbweather.controller.api.adapters;

import com.darelbitsy.dbweather.controller.api.services.WeatherService;
import com.darelbitsy.dbweather.helper.ConstantHolder;
import com.darelbitsy.dbweather.model.weather.Weather;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Darel Bitsy on 20/02/17.
 */

public class WeatherAdapter {
    private static final String WEATHER_URL = "https://api.darksky.net/";
    private static final String WEATHER_APIKEY = "07aadf598548d8bb35d6621d5e3b3c7b";
    private final List<String> supportedLang = Arrays.asList("ar","az","be","bs","ca","cs","de","el","en","es",
            "et","fr","hr","hu","id","it","is","kw","nb","nl","pl","pt","ru",
            "sk","sl","sr","sv","tet","tr","uk","x-pig-latin","zh","zh-tw");

    private final OkHttpClient mHttpClient = new OkHttpClient();
    private final Retrofit mRestAdapter;
    private WeatherService mWeatherService;

    public WeatherAdapter() {
        mRestAdapter = new Retrofit.Builder()
                .baseUrl(WEATHER_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(mHttpClient)
                .build();

        mWeatherService = mRestAdapter.create(WeatherService.class);
    }

    public Weather getWeather(double latitude,
                              double longitude) throws IOException {

        Weather weatherData;
        String coordinates = String.format(Locale.ENGLISH, "%f,%f", latitude, longitude);

        if (supportedLang.contains(ConstantHolder.USER_LANGUAGE)) {
            weatherData = mWeatherService.getWeatherWithLanguage(WEATHER_APIKEY,
                    coordinates,
                    ConstantHolder.USER_LANGUAGE,
                    "auto").execute().body();

        } else {
            weatherData = mWeatherService.getWeather(WEATHER_APIKEY,
                    coordinates,
                    "auto").execute().body();

        }

        return weatherData;
    }
}
