package com.darelbitsy.dbweather.ui.main.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;

/**
 * Created by Darel Bitsy on 24/04/17.
 * Weather Fragment
 */

public interface IWeatherFragmentPresenter<TYPE> {

    void saveState(@NonNull final Bundle bundle);

    void restoreState(@NonNull final Bundle bundle);

    void showData(@NonNull final TYPE weatherInfo);

    void updateData();
}
