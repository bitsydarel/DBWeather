package com.darelbitsy.dbweather.utils.helper;

import android.util.SparseIntArray;

import com.darelbitsy.dbweather.R;

/**
 * Created by Darel Bitsy on 05/01/17.
 * Color manager for dbweather app
 */

public class ColorManager {
    private final SparseIntArray mColors = new SparseIntArray();
    private static ColorManager singletonColorManager;

    public static ColorManager newInstance() {
        if (singletonColorManager == null) {
            singletonColorManager = new ColorManager();
        }
        return singletonColorManager;
    }

    private ColorManager() {
        initialize();

    }

    private void initialize() {
        mColors.put(R.drawable.clear_day, R.drawable.clear_day_background);
        mColors.put(R.drawable.clear_night, R.drawable.clear_night_background);
        mColors.put(R.drawable.partly_cloudy, R.drawable.partly_cloudy_background);
        mColors.put(R.drawable.cloudy_night, R.drawable.cloudy_night_background);
        mColors.put(R.drawable.cloudy, R.drawable.cloudy_background);
        mColors.put(R.drawable.fog, R.drawable.fog_background);
        mColors.put(R.drawable.sleet, R.drawable.sleet_background);
        mColors.put(R.drawable.snow, R.drawable.snow_background);
        mColors.put(R.drawable.wind, R.drawable.wind_background);
        mColors.put(R.drawable.rain, R.drawable.rain_background);
    }

    public int getBackgroundColor(final int icon) {
        return mColors.get(icon);
    }
}
