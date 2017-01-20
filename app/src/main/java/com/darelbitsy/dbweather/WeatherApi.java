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

    public Hour[] getHour() { return mDay == null ? new Hour[0] : Arrays.copyOf(mHour, mHour.length); }

    public void setHour(final Hour[] hour) { mHour = Arrays.copyOf(hour, hour.length) ; }

    public Day[] getDay() { return mDay == null ? new Day[0] : Arrays.copyOf(mDay, mDay.length); }

    public void setDay(final Day[] day) { mDay = Arrays.copyOf(day, day.length); }
}
