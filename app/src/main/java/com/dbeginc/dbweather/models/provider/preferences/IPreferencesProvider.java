package com.dbeginc.dbweather.models.provider.preferences;

import android.support.annotation.NonNull;
import android.support.v4.util.Pair;

import javax.annotation.Nonnull;

/**
 * Created by Darel Bitsy on 27/04/17.
 * Preference Provider
 */

public interface IPreferencesProvider {

    boolean isFirstRun();

    void setFirstRun(final boolean isFirstRun);

    boolean isCurrentWeatherFromGps();

    void setCurrentWeatherFromGps(final boolean isFromCity);

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

    void setCustomTabPackage(@Nonnull final String packageName);

    String getCustomTabPackage();

    boolean shouldWeatherBeSaved();

    void doWeSaveWeather(final boolean value);
}
