package com.dbeginc.dbweather.ui.main.fragment;

import android.support.annotation.NonNull;

import com.dbeginc.dbweather.models.datatypes.weather.WeatherInfo;

import javax.annotation.Nonnull;

/**
 * Created by Darel Bitsy on 24/04/17.
 */

public interface IWeatherFragmentView<DATA_TYPE> {

    void showData(@NonNull final DATA_TYPE dataType);

    void requestUpdate();
}
