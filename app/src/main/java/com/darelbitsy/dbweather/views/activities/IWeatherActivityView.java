package com.darelbitsy.dbweather.views.activities;

import android.os.Bundle;

/**
 * Created by Darel Bitsy on 22/04/17.
 * Interface representing the mainScreen of the
 */

public interface IMainView<WEATHER, NEWS> {

    void requestWeatherUpdate();

    void requestNewsUpdate();

    void showWeather(final WEATHER weather);

    void showNews(final NEWS news);

    void showNetworkWeatherErrorMessage();

    void showNetworkNewsErrorMessage();

    void saveState(final Bundle bundle);
}
