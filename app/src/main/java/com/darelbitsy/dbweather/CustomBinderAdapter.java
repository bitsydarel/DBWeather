package com.darelbitsy.dbweather;

import android.content.Context;
import android.databinding.BindingAdapter;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;

import com.darelbitsy.dbweather.models.datatypes.weather.WeatherInfo;
import com.darelbitsy.dbweather.ui.introduction.IntroPresenter;
import com.darelbitsy.dbweather.ui.introduction.viewpager.IntroViewPager;
import com.darelbitsy.dbweather.utils.holder.ConstantHolder;

import java.util.List;
import java.util.Map;

import static com.darelbitsy.dbweather.utils.holder.ConstantHolder.LIST_OF_TYPEFACES;
import static com.darelbitsy.dbweather.utils.holder.ConstantHolder.USER_LANGUAGE;

/**
 * Created by Darel Bitsy on 26/04/17.
 * Custom Binder for my layout
 */

public class CustomBinderAdapter {

    private CustomBinderAdapter() {
        // to prevent this to be instantiate
    }

    @BindingAdapter("setFont")
    public static void setTypeFace(@NonNull final TextView textView, final boolean shouldSet) {
        if (shouldSet) {
            textView.setTypeface(getAppGlobalTypeFace(textView.getContext()));
        }
    }

    @BindingAdapter("tintMyBackground")
    public static void setBackgroundTint(@NonNull final ImageButton button, final boolean shouldTint) {
        if (shouldTint && Build.VERSION_CODES.M > Build.VERSION.SDK_INT) {
            button.setBackgroundColor(Color.TRANSPARENT);
        }
    }

    @BindingAdapter("setFont")
    public static void setTypeFace(@NonNull final Button button, final boolean shouldSet) {
        if (shouldSet) {
            button.setTypeface(getAppGlobalTypeFace(button.getContext()));
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

    private static Typeface getAppGlobalTypeFace(final Context context) {
        Typeface typeface = null;

        for (final Map.Entry<List<String>, String> languages : LIST_OF_TYPEFACES.entrySet()) {
            if (languages.getKey().contains(USER_LANGUAGE)) {
                typeface = Typeface.createFromAsset(context.getAssets(),
                        languages.getValue());
            }
        }

        return typeface;
    }
}
