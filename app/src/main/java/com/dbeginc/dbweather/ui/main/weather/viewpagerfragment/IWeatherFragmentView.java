package com.dbeginc.dbweather.ui.main.weather.viewpagerfragment;

import android.support.annotation.NonNull;

/**
 * Created by Darel Bitsy on 24/04/17.
 */

public interface IWeatherFragmentView<T> {

    void showData(@NonNull final T dataType);
}
