package com.darelbitsy.dbweather.presenters;

import android.support.annotation.NonNull;

/**
 * Created by Darel Bitsy on 24/04/17.
 * Weather Fragment
 */

public interface IWeatherFragmentPresenter<TYPE> {
    void showWeather(@NonNull final TYPE weatherInfo);

    void updateWeather(@NonNull final TYPE weatherInfo);
}
