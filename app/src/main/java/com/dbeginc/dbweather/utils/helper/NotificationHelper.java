package com.dbeginc.dbweather.utils.helper;

import android.content.Context;

import com.dbeginc.dbweather.R;

import java.util.Locale;

import javax.annotation.Nonnull;

public class NotificationHelper {
    private final String mIcon;
    private final int mTemperature;
    private final Context mContext;

    public NotificationHelper(final Context context, @Nonnull final String icon, final int temperature) {
        mIcon = icon;
        mTemperature = temperature;
        mContext = context;
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
                        mTemperature);
                break;
            case "snow":
                description = String.format(Locale.getDefault(),
                        mContext.getString(R.string.snow_desc),
                        mTemperature);
                break;
            case "sleet":
                description = String.format(Locale.getDefault(),
                        mContext.getString(R.string.sleet_desc),
                        mTemperature);
                break;
            case "wind":
                description = String.format(Locale.getDefault(),
                        mContext.getString(R.string.wind_desc),
                        mTemperature);
                break;
            case "fog":
                description = String.format(Locale.getDefault(),
                        mContext.getString(R.string.fog_desc),
                        mTemperature);
                break;
            case "cloudy":
                description = String.format(Locale.getDefault(),
                        mContext.getString(R.string.cloudy),
                        mTemperature);
                break;
            case "partly-cloudy-day":
                description = String.format(Locale.getDefault(),
                        mContext.getString(R.string.partly_cloudy_day_desc),
                        mTemperature);
                break;
            case "partly-cloudy-night":
                description = String.format(Locale.getDefault(),
                        mContext.getString(R.string.partly_cloudy_night_desc),
                        mTemperature);
                break;
            case "rain":
                description = String.format(Locale.getDefault(),
                        mContext.getString(R.string.rain_desc), mTemperature);
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
