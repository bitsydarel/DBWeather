package com.darelbitsy.dbweather.presenters.fragments;

import android.content.Context;
import android.databinding.BindingAdapter;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.VideoView;

import com.darelbitsy.dbweather.R;
import com.darelbitsy.dbweather.extensions.utility.AppUtil;
import com.darelbitsy.dbweather.models.datatypes.weather.WeatherInfo;
import com.darelbitsy.dbweather.views.animation.widgets.RainFallView;
import com.darelbitsy.dbweather.views.animation.widgets.SnowFallView;
import com.darelbitsy.dbweather.views.fragments.IWeatherFragmentView;

import static com.darelbitsy.dbweather.extensions.holder.ConstantHolder.WEATHER_INFO_KEY;

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
        mWeatherInfo.isVideoPlaying.set(weatherInfo.isVideoPlaying.get());
        mWeatherInfo.videoBackgroundFile.set(weatherInfo.videoBackgroundFile.get());

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

        mWeatherInfo.setSleet(weatherInfo.isSleet());
    }

    @BindingAdapter("setFont")
    public static void setTypeFace(@NonNull final TextView textView, final boolean shouldSet) {
        if (shouldSet) {
            final Typeface typeFace = AppUtil.getAppGlobalTypeFace(textView.getContext());
            textView.setTypeface(typeFace);
        }
    }

    @BindingAdapter({"android:src"})
    public static void setImageViewResource(@NonNull final ImageView imageView, final int resource) {
        imageView.setImageResource(resource);
    }

    @BindingAdapter("showVideoBackground")
    public static void showVideoBackground(@NonNull final VideoView videoView, @NonNull final WeatherInfo weatherInfo) {
        if (weatherInfo.isCurrentWeather.get() && weatherInfo.videoBackgroundFile.get() != 0) {
            videoView.stopPlayback();

            videoView.setVideoURI(Uri.parse("android.resource://" +
                    videoView.getContext().getPackageName() +
                    "/" +
                    weatherInfo.videoBackgroundFile.get()));

            videoView.setOnPreparedListener(mediaPlayer -> mediaPlayer.setLooping(true));
            weatherInfo.isVideoPlaying.set(true);
            videoView.start();

        } else {
            weatherInfo.isVideoPlaying.set(false);
        }
    }

    public void showFallingSnowOrRain(@NonNull final ConstraintLayout currentWeatherLayout,
                                      @NonNull final Context applicationContext,
                                      @NonNull final RelativeLayout.LayoutParams layoutParams) {

        if(R.raw.snow_background == mWeatherInfo.videoBackgroundFile.get()) {

            if(currentWeatherLayout.findViewById(SnowFallView.VIEW_ID) == null) {
                currentWeatherLayout.addView(new SnowFallView(applicationContext), layoutParams);
            }

            if(currentWeatherLayout.findViewById(RainFallView.VIEW_ID) != null) {
               currentWeatherLayout.removeView(currentWeatherLayout.findViewById(RainFallView.VIEW_ID));
            }

        } else if(R.raw.rain_background == mWeatherInfo.videoBackgroundFile.get()) {

            if(currentWeatherLayout.findViewById(RainFallView.VIEW_ID) == null) {
               currentWeatherLayout.addView(new RainFallView(applicationContext),
                        layoutParams);
            }

            if (currentWeatherLayout.findViewById(SnowFallView.VIEW_ID) != null) {
                currentWeatherLayout.removeView(currentWeatherLayout.findViewById(SnowFallView.VIEW_ID));
            }

        } else if (mWeatherInfo.isSleet()) {

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
