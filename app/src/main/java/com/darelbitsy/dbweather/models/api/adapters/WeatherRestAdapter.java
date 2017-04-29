package com.darelbitsy.dbweather.models.api.adapters;

import android.support.annotation.NonNull;

import com.darelbitsy.dbweather.utils.holder.ConstantHolder;
import com.darelbitsy.dbweather.models.api.services.WeatherService;
import com.darelbitsy.dbweather.models.datatypes.weather.Weather;

import java.io.IOException;
import java.util.Locale;

import javax.inject.Inject;
import javax.inject.Singleton;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.darelbitsy.dbweather.utils.holder.ConstantHolder.supportedLang;

/**
 * Created by Darel Bitsy on 20/02/17.
 * Weather adapter
 * this class manage the method to get the weather
 */

@Singleton
public class WeatherRestAdapter {
    private static final String WEATHER_URL = "https://api.darksky.net/";
    private static final String WEATHER_APIKEY = "07aadf598548d8bb35d6621d5e3b3c7b";

    private final WeatherService mWeatherService;

    @Inject
    public WeatherRestAdapter(@NonNull final OkHttpClient okHttpClient) {
        final Retrofit restAdapter = new Retrofit.Builder()
                .baseUrl(WEATHER_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build();

        mWeatherService =
                    restAdapter.create(WeatherService.class);
    }

    public Weather getWeather(final double latitude,
                              final double longitude) throws IOException {

        final Weather weatherData;
        final String coordinates = String.format(Locale.ENGLISH, "%f,%f", latitude, longitude);

        if (supportedLang.contains(ConstantHolder.USER_LANGUAGE)) {
            weatherData = mWeatherService.getWeatherWithLanguage(WEATHER_APIKEY,
                    coordinates,
                    ConstantHolder.USER_LANGUAGE,
                    "auto")
                    .execute()
                    .body();

        } else {
            weatherData = mWeatherService.getWeather(WEATHER_APIKEY,
                    coordinates,
                    "auto")
                    .execute()
                    .body();

        }

        return weatherData;
    }
}
