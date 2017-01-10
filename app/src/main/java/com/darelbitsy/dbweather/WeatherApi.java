package com.darelbitsy.dbweather;

import com.darelbitsy.dbweather.weather.Current;
import com.darelbitsy.dbweather.weather.Day;
import com.darelbitsy.dbweather.weather.Hour;

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

    public void setCurrent(Current current) {
        mCurrent = current;
    }

    public Hour[] getHour() {
        return mHour;
    }

    public void setHour(Hour[] hour) {
        mHour = hour;
    }

    public Day[] getDay() {
        return mDay;
    }

    public void setDay(Day[] day) {
        mDay = day;
    }

    public static int getIconId(String iconString) {
        int iconId = R.drawable.clear_day;

        switch (iconString) {
            case "clear-night":
                iconId = R.drawable.clear_night;
                break;
            case "rain":
                iconId = R.drawable.rain;
                break;
            case "snow":
                iconId = R.drawable.snow;
                break;
            case "sleet":
                iconId = R.drawable.sleet;
                break;
            case "wind":
                iconId = R.drawable.wind;
                break;
            case "fog":
                iconId = R.drawable.fog;
                break;
            case "cloudy":
                iconId = R.drawable.cloudy;
                break;
            case "partly-cloudy-day":
                iconId = R.drawable.partly_cloudy;
                break;
            case "partly-cloudy-night":
                iconId = R.drawable.cloudy_night;
                break;
            default:
                break;
        }
            return iconId;
    }
}
