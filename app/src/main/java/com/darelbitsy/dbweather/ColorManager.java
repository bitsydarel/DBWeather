package com.darelbitsy.dbweather;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Darel Bitsy on 05/01/17.
 */

public class ColorManager {
    private Map<String, Integer> mColors;

    public ColorManager() {
        mColors = new HashMap<>();
        initialize();

    }

    private void initialize() {
        mColors.put("clear-day", R.drawable.clear_day_background);
        mColors.put("clear-night", R.drawable.clear_night_background);
        mColors.put("partly-cloudy-day", R.drawable.partly_cloudy_background);
        mColors.put("partly-cloudy-night", R.drawable.cloudy_night_background);
        mColors.put("cloudy", R.drawable.cloudy_background);
        mColors.put("fog", R.drawable.fog_background);
        mColors.put("sleet", R.drawable.sleet_background);
        mColors.put("snow", R.drawable.snow_background);
        mColors.put("wind", R.drawable.wind_background);
        mColors.put("rain", R.drawable.rain_background);
    }

    public int getBackgroundColor(final String icon) {
        return mColors.get(icon);
    }
}
