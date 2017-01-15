package com.darelbitsy.dbweather;

import com.darelbitsy.dbweather.weather.Current;
import com.darelbitsy.dbweather.weather.Day;
import com.darelbitsy.dbweather.weather.Hour;

import java.util.Arrays;

/**
 * Created by Darel Bitsy on 07/01/17.
 */

public class WeatherApi {
    private Current mCurrent;
    private Hour[] mHour;
    private Day[] mDay;

    public Current getCurrent() {
        return mCurrent;
    }

    public void setCurrent(final Current current) {
        mCurrent = current;
    }

    public Hour[] getHour() {
        return mHour;
    }

    public void setHour(final Hour[] hour) { mHour = (hour != null) ? Arrays.copyOf(hour, hour.length) : null; }

    public Day[] getDay() { return mDay; }

    public void setDay(final Day[] day) { mDay = Arrays.copyOf(day, day.length); }
}
