package com.darelbitsy.dbweather.presenters.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;

import com.darelbitsy.dbweather.models.datatypes.weather.WeatherInfo;
import com.darelbitsy.dbweather.views.fragments.IWeatherFragmentView;

/**
 * Created by Darel Bitsy on 24/04/17.
 * Weather Fragment Presenter
 */

public class WeatherFragmentPresent implements IWeatherFragmentPresenter<WeatherInfo> {
    private final IWeatherFragmentView mView;
    private WeatherInfo mWeatherInfo = new WeatherInfo();

    public WeatherFragmentPresent(@NonNull final IWeatherFragmentView view) {
        mView = view;
    }

    @Override
    public void saveState(final Bundle bundle) {

    }

    @Override
    public void showData(@NonNull final WeatherInfo weatherInfo) {
    }

    @Override
    public void updateData(@NonNull final WeatherInfo weatherInfo) {

    }


}
