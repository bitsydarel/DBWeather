package com.dbeginc.dbweather.ui.main.weather;

import android.content.Context;
import android.support.annotation.NonNull;

import com.dbeginc.dbweather.models.datatypes.geonames.GeoName;
import com.dbeginc.dbweather.models.datatypes.weather.Alert;
import com.dbeginc.dbweather.models.datatypes.weather.WeatherData;

import java.util.List;

import io.reactivex.functions.Action;
import io.reactivex.subjects.PublishSubject;

/**
 * Created by darel on 28.05.17.
 * Weather View Interface
 */

interface IWeatherView {

    void setupLocationLookupFeature(@NonNull final Context context);

    void showWeather(@NonNull final WeatherData weatherData);

    void showWeatherAlerts(@NonNull final List<Alert> listWeatherAlert);

    Context getAppContext();

    PublishSubject<String> getLocationUpdateEvent();

    PublishSubject<String> getVoiceSearchEvent();

    /**
     * This method handle location update notification
     * @param action name of the broadcast request action
     */
    void onLocationUpdate(@NonNull final String action);


    /**
     * This method handle voice query event
     * @param query user voice query
     */
    void onVoiceQueryReceived(@NonNull final String query);

    boolean isNetworkAvailable();

    void showNetworkNotAvailableMessage();

    void showWeatherError();

    void loadUserCities(@NonNull final List<GeoName> userCities);

    void notifySuccessfullAddedLocation();

    void addLocationToMenu(@NonNull final GeoName location);
}
