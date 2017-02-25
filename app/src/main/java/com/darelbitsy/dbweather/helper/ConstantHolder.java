package com.darelbitsy.dbweather.helper;

import java.util.Locale;

/**
 * Created by Darel Bitsy on 18/02/17.
 */

public class ConstantHolder {
    public static final String TAG = "dbweather";
    public static final String IS_ALARM_ON = "is_alarm_set";
    public static boolean isGpsPermissionOn = false;
    public static boolean isAccountPermissionOn;

    public static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 7125;
    public static final int MY_PERMiSSIONS_REQUEST_GET_ACCOUNT = 242;
    public static final int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;

    public static final String MYMEMORY_APIURL = "http://api.mymemory.translated.net";
    public static final String USER_LANGUAGE = Locale.getDefault().getLanguage();

    public static final String WEATHER_DATA_KEY = "weather_data_key";
    public static final String CURRENT_WEATHER_KEY = "current_weather_key";
    public static final String DAILY_WEATHER_KEY = "daily_weather_key";
    public static final String HOURLY_WEATHER_KEY = "hourly_weather_key";
    public static final String MINUTELY_WEATHER_KEY = "minutely_weather_key";

    public static final String NEWS_DATA_KEY = "news_data_key";
    public static final String CITY_NAME_KEY = "city_name_key";

    public static final String NOTIF_ICON = "notif_hour";
    public static final String NOTIF_SUMMARY = "notif_hour";
    public static final String NOTIF_TEMPERATURE = "notif_temperature";

    public static final String[] LIST_OF_SOURCES = {
            "bbc-news",
            "cnn",
            "bbc-sport",
            "google-news",
            "hacker-news",
            "sky-sports-news",
            "the-new-york-times",
            "mtv-news"
    };
}
