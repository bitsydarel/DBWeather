package com.darelbitsy.dbweather.ui.main.fragment;

import android.support.annotation.NonNull;

/**
 * Created by Darel Bitsy on 24/04/17.
 */

public interface IWeatherFragmentView<DATA_TYPE> {

    void showData(@NonNull final DATA_TYPE dataType);

    void requestUpdate();
}
