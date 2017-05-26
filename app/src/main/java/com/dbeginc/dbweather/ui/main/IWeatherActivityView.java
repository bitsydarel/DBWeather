package com.dbeginc.dbweather.ui.main;

import android.content.Context;

import com.dbeginc.dbweather.models.datatypes.geonames.GeoName;
import com.dbeginc.dbweather.models.datatypes.news.Article;
import com.dbeginc.dbweather.models.datatypes.weather.Alert;
import com.dbeginc.dbweather.models.datatypes.weather.WeatherData;

import java.util.List;

import javax.annotation.Nonnull;

/**
 * Created by Darel Bitsy on 22/04/17.
 * Interface representing the mainScreen of the
 */

public interface IWeatherActivityView {

    /**
     * This method display the Weather
     * on the main screen
     * @param weather the weather to be displayed
     */
    void showWeather(final WeatherData weather);

    /**
     * This method display
     * the scrollable news feed
     * @param news to be displayed
     */
    void showNews(final List<Article> news);

    /**
     * This method display the error message
     * when failed to fetch weather data
     */
    void showNetworkWeatherErrorMessage();

    /**
     * This method display the error message
     * when failed to fetch news data
     */
    void showNetworkNewsErrorMessage();

    /**
     * this method populate the navigation drawer
     * with the user is collection of cities
     * @param listOfLocation to populate the navigation drawer
     */
    void setupNavigationDrawerWithCities(final List<GeoName> listOfLocation);

    /**
     * this method setup the navigation drawer if no cities found
     */
    void setupNavigationDrawerWithNoCities();

    /**
     * This method request weather update
     */
    void requestUpdate();

    /**
     * This method provide the Context
     * @return an Context
     */
    Context getContext();

    /**
     * Show ScreenShot attempt error
     */
    void showScreenshotAttemptError();

    /**
     * Display network not available message
     * to the user
     */
    void showNetworkNotAvailableMessage();

    /**
     * Display weather alert to the user
     * from the city he subscribed or his current location
     * @param weatherAlert from the location
     */
    void showAlerts(@Nonnull final Alert weatherAlert);
}
