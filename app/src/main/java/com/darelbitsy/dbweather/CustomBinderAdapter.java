package com.darelbitsy.dbweather;

import android.databinding.BindingAdapter;
import android.graphics.Typeface;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;

import com.darelbitsy.dbweather.extensions.utility.AppUtil;
import com.darelbitsy.dbweather.models.datatypes.weather.WeatherInfo;

/**
 * Created by Darel Bitsy on 26/04/17.
 * Custom Binder for my layout
 */

public class CustomBinderAdapter {

    @BindingAdapter("setFont")
    public static void setTypeFace(@NonNull final TextView textView, final boolean shouldSet) {
        if (shouldSet) {
            final Typeface typeFace = AppUtil.getAppGlobalTypeFace(textView.getContext());
            textView.setTypeface(typeFace);
        }
    }

    @BindingAdapter("setFont")
    public static void setTypeFace(@NonNull final Button button, final boolean shouldSet) {
        if (shouldSet) {
            final Typeface typeFace = AppUtil.getAppGlobalTypeFace(button.getContext());
            button.setTypeface(typeFace);
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
}
