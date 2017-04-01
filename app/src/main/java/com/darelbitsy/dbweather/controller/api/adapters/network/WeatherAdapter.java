package com.darelbitsy.dbweather.controller.api.adapters.network;

import android.content.Context;

import com.darelbitsy.dbweather.controller.api.services.WeatherService;
import com.darelbitsy.dbweather.helper.holder.ConstantHolder;
import com.darelbitsy.dbweather.helper.utility.AppUtil;
import com.darelbitsy.dbweather.model.weather.Weather;

import java.io.IOException;
import java.util.Locale;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.darelbitsy.dbweather.helper.holder.ConstantHolder.supportedLang;

/**
 * Created by Darel Bitsy on 20/02/17.
 */

public class WeatherAdapter {
    private static final String WEATHER_URL = "https://api.darksky.net/";
    private static final String WEATHER_APIKEY = "07aadf598548d8bb35d6621d5e3b3c7b";

    private static Retrofit mRestAdapter;

    private static WeatherService mWeatherService;

    public WeatherAdapter(Context context) {
        if (mRestAdapter == null) {
            mRestAdapter = new Retrofit.Builder()
                    .baseUrl(WEATHER_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(AppUtil
                            .weatherOkHttpClient
                            .cache(AppUtil.getCacheDirectory(context))
                            .build())
                    .build();
        }

        if (mWeatherService == null) {
            mWeatherService =
                    mRestAdapter.create(WeatherService.class);
        }
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