package com.darelbitsy.dbweather.ui.main.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.widget.RelativeLayout;

import com.darelbitsy.dbweather.R;
import com.darelbitsy.dbweather.models.datatypes.weather.WeatherInfo;
import com.darelbitsy.dbweather.ui.animation.widgets.RainFallView;
import com.darelbitsy.dbweather.ui.animation.widgets.SnowFallView;

import static com.darelbitsy.dbweather.utils.holder.ConstantHolder.WEATHER_INFO_KEY;

/**
 * Created by Darel Bitsy on 24/04/17.
 * Weather Fragment Presenter
 */

public class WeatherFragmentPresenter implements IWeatherFragmentPresenter<WeatherInfo> {
    private final IWeatherFragmentView mView;
    private final WeatherInfo mWeatherInfo = new WeatherInfo();

    public WeatherFragmentPresenter(@NonNull final IWeatherFragmentView view) {
        mView = view;
    }

    @Override
    public void saveState(@NonNull final Bundle bundle) {
        bundle.putParcelable(WEATHER_INFO_KEY, mWeatherInfo);
    }

    @Override
    public void restoreState(@NonNull final Bundle bundle) {
        updateWeather(bundle.getParcelable(WEATHER_INFO_KEY));
    }

    @Override
    public void showData(@NonNull final WeatherInfo weatherInfo) {
        updateWeather(weatherInfo);
    }

    @Override
    public void updateData() {
        mView.requestUpdate();
    }

    public WeatherInfo getWeatherInfo() {
        return mWeatherInfo;
    }

    private void updateWeather(@NonNull final WeatherInfo weatherInfo) {
        mWeatherInfo.isCurrentWeather.set(weatherInfo.isCurrentWeather.get());

        mWeatherInfo.locationName.set(weatherInfo.locationName.get());
        mWeatherInfo.icon.set(weatherInfo.icon.get());
        mWeatherInfo.summary.set(weatherInfo.summary.get());
        mWeatherInfo.time.set(weatherInfo.time.get());

        mWeatherInfo.temperature.set(weatherInfo.temperature.get());
        mWeatherInfo.apparentTemperature.set(weatherInfo.apparentTemperature.get());

        mWeatherInfo.windSpeed.set(weatherInfo.windSpeed.get());
        mWeatherInfo.humidity.set(weatherInfo.humidity.get());

        mWeatherInfo.cloudCover.set(weatherInfo.cloudCover.get());

        mWeatherInfo.precipitationType.set(weatherInfo.precipitationType.get());
        mWeatherInfo.precipitationProbability.set(weatherInfo.precipitationProbability.get());

        mWeatherInfo.sunrise.set(weatherInfo.sunrise.get());
        mWeatherInfo.sunset.set(weatherInfo.sunset.get());
    }

    void showFallingSnowOrRain(@NonNull final ConstraintLayout currentWeatherLayout,
                               @NonNull final Context applicationContext,
                               @NonNull final RelativeLayout.LayoutParams layoutParams) {

        if(R.drawable.snow == mWeatherInfo.icon.get()) {

            if(currentWeatherLayout.findViewById(SnowFallView.VIEW_ID) == null) {
                currentWeatherLayout.addView(new SnowFallView(applicationContext), layoutParams);
            }

            if(currentWeatherLayout.findViewById(RainFallView.VIEW_ID) != null) {
               currentWeatherLayout.removeView(currentWeatherLayout.findViewById(RainFallView.VIEW_ID));
            }

        } else if(R.drawable.rain == mWeatherInfo.icon.get()) {

            if(currentWeatherLayout.findViewById(RainFallView.VIEW_ID) == null) {
               currentWeatherLayout.addView(new RainFallView(applicationContext),
                        layoutParams);
            }

            if (currentWeatherLayout.findViewById(SnowFallView.VIEW_ID) != null) {
                currentWeatherLayout.removeView(currentWeatherLayout.findViewById(SnowFallView.VIEW_ID));
            }

        } else if (R.drawable.sleet == mWeatherInfo.icon.get()) {

            if (currentWeatherLayout.findViewById(RainFallView.VIEW_ID) == null) {
                currentWeatherLayout.addView(new RainFallView(applicationContext),
                        layoutParams);
            }

            if (currentWeatherLayout.findViewById(SnowFallView.VIEW_ID) == null) {
                currentWeatherLayout.addView(new SnowFallView(applicationContext),
                        layoutParams);
            }

        } else {

            if (currentWeatherLayout.findViewById(RainFallView.VIEW_ID) != null) {
                currentWeatherLayout.removeView(currentWeatherLayout.findViewById(RainFallView.VIEW_ID));
            }

            if (currentWeatherLayout.findViewById(SnowFallView.VIEW_ID) != null) {
                currentWeatherLayout.removeView(currentWeatherLayout.findViewById(SnowFallView.VIEW_ID));
            }
        }
    }
}
