package com.darelbitsy.dbweather.provider.preferences;

import android.support.annotation.NonNull;
import android.support.v4.util.Pair;

/**
 * Created by Darel Bitsy on 27/04/17.
 * Preference Provider
 */

public interface IPreferencesProvider {

    boolean isFirstRun();

    void setFirstRun(final boolean isFirstRun);

    boolean didUserSelectedCityFromDrawer();

    void userSelectedCityFromDrawer(final boolean isFromCity);

    Pair<String, double[]> getSelectedUserCity(@NonNull final String locationToFind);

    void setSelectedUserCity(@NonNull final String locationToSelected,
                             final double latitude,
                             final double longitude);

    void saveRecyclerBottomLimit(final float limit);

    float getRecyclerBottomLimit();

    boolean getWeatherNotificationStatus();

    void setWeatherNotificationStatus(final boolean isOn);

    boolean getNewsTranslationStatus();

    void setNewsTranslationStatus(final boolean isOn);

    void setAccountPermissionStatus(final boolean isPermissionAccorded);

    boolean getAccountPermissionStatus();

    void setGpsPermissionStatus(final boolean isPermissionAccorded);

    boolean getGpsPermissionStatus();

    void setWritePermissionStatus(final boolean isPermissionAccorded);

    boolean getWritePermissionStatus();
}
