package com.darelbitsy.dbweather.presenters.fragments;

import android.content.Context;
import android.databinding.BindingAdapter;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.VideoView;

import com.darelbitsy.dbweather.R;
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
    private WeatherInfo mWeatherInfo = new WeatherInfo();

    public WeatherFragmentPresenter(@NonNull final IWeatherFragmentView view) {
        mView = view;
    }

    @Override
    public void saveState(@NonNull final Bundle bundle) {
        bundle.putParcelable(WEATHER_INFO_KEY, mWeatherInfo);
    }

    @Override
    public void restoreState(@NonNull final Bundle bundle) {
        mWeatherInfo = bundle.getParcelable(WEATHER_INFO_KEY);
    }

    @Override
    public void showData(@NonNull final WeatherInfo weatherInfo) {
        mWeatherInfo = weatherInfo;
    }

    @Override
    public void updateData() {
        mView.requestUpdate();
    }

    public WeatherInfo getWeatherInfo() {
        return mWeatherInfo;
    }


    @BindingAdapter({"android:src"})
    public static void setImageViewResource(ImageView imageView, int resource) {
        imageView.setImageResource(resource);
    }

    @BindingAdapter("bind:showVideoBackground")
    public static void showVideoBackgroun(@NonNull final VideoView videoView, @NonNull final WeatherInfo weatherInfo) {
        if (weatherInfo.isCurrentWeather.get() && weatherInfo.icon.get() != 0) {
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
