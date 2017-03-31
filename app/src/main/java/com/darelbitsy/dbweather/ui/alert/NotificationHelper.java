package com.darelbitsy.dbweather.ui.alert;

import android.content.Context;

import com.darelbitsy.dbweather.R;
import com.darelbitsy.dbweather.helper.utility.weather.WeatherUtil;
import com.jakewharton.threetenabp.AndroidThreeTen;

import java.util.Locale;

public class NotificationHelper {
    private String mIcon;
    private double mTemperature;
    private Context mContext;
    public static final String NOTIFICATION_DESC = "notification_desc";


    public NotificationHelper(Context context, String icon, double temperature) {
        mIcon = icon;
        mTemperature = temperature;
        mContext = context;
        AndroidThreeTen.init(context);
    }

    public String getDescription() {
        String description = "";
        switch (mIcon) {
            case "clear-night":
                description = "";
                break;
            case "clear-day":
                description = String.format(Locale.getDefault(),
                        mContext.getString(R.string.clear_day_desc),
                        WeatherUtil.getTemperatureInInt(mTemperature));
                break;
            case "snow":
                description = String.format(Locale.getDefault(),
                        mContext.getString(R.string.snow_desc),
                        WeatherUtil.getTemperatureInInt(mTemperature));
                break;
            case "sleet":
                description = String.format(Locale.getDefault(),
                        mContext.getString(R.string.sleet_desc),
                        WeatherUtil.getTemperatureInInt(mTemperature));
                break;
            case "wind":
                description = String.format(Locale.getDefault(),
                        mContext.getString(R.string.wind_desc),
                        WeatherUtil.getTemperatureInInt(mTemperature));
                break;
            case "fog":
                description = String.format(Locale.getDefault(),
                        mContext.getString(R.string.fog_desc),
                        WeatherUtil.getTemperatureInInt(mTemperature));
                break;
            case "cloudy":
                description = String.format(Locale.getDefault(),
                        mContext.getString(R.string.cloudy),
                        WeatherUtil.getTemperatureInInt(mTemperature));
                break;
            case "partly-cloudy-day":
                description = String.format(Locale.getDefault(),
                        mContext.getString(R.string.partly_cloudy_day_desc),
                        WeatherUtil.getTemperatureInInt(mTemperature));
                break;
            case "partly-cloudy-night":
                description = String.format(Locale.getDefault(),
                        mContext.getString(R.string.partly_cloudy_night_desc),
                        WeatherUtil.getTemperatureInInt(mTemperature));
                break;
            default:
                break;
        }
        return description;
    }

    public String getTitleFromIcon() {
        String title = mContext.getResources().getString(R.string.rain_title);
        switch (mIcon) {
            case "clear-night":
                title = mContext.getString(R.string.clear_night_title);
                break;
            case "clear-day":
                title = mContext.getString(R.string.clear_day_title);
                break;
            case "snow":
                title = mContext.getString(R.string.snow_title);
                break;
            case "sleet":
                title = mContext.getString(R.string.sleet_title);
                break;
            case "wind":
                title = mContext.getString(R.string.wind_title);
                break;
            case "fog":
                title = mContext.getString(R.string.fog_title);
                break;
            case "cloudy":
                title = mContext.getString(R.string.cloudy_title);
                break;
            case "partly-cloudy-day":
                title = mContext.getString(R.string.partly_cloudy_day_title);
                break;
            case "partly-cloudy-night":
                title = mContext.getString(R.string.partly_cloudy_night_title);
                break;
            default:
                break;
        }
        return title;
    }
}
