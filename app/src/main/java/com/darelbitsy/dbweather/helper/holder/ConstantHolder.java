package com.darelbitsy.dbweather.helper.holder;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Created by Darel Bitsy on 18/02/17.
 * This class hold all constant variable needed for
 */

public class ConstantHolder {

    public static final String NOTIFICATION_KEY = "notification_key";
    public static final String NEWS_TRANSLATION_KEY = "news_translation_key";

    public static final String RECYCLER_BOTTOM_LIMIT = "recycler_bottom_limit";

    private ConstantHolder() {
        //To disable default constructor
    }
    public static final String WEEK_SUMMARY = "week_summary";
    public static final String TAG = "dbweather";
    public static final String IS_ALARM_ON = "is_alarm_set";
    public static final String IS_GPS_PERMISSION_GRANTED = "is_gps_permission_granted";
    public static final String IS_ACCOUNT_PERMISSION_GRANTED = "is_account_granted";
    public static final String PREFS_NAME = "db_weather_prefs";


    public static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 7125;
    public static final int MY_PERMiSSIONS_REQUEST_GET_ACCOUNT = 242;

    public static final String MYMEMORY_APIURL = "http://api.mymemory.translated.net";
    public static final String USER_LANGUAGE = Locale.getDefault().getLanguage();

    public static final String WEATHER_DATA_KEY = "weather_data_key";
    public static final String CURRENT_WEATHER_KEY = "current_weather_key";
    public static final String DAILY_WEATHER_KEY = "daily_weather_key";
    public static final String DAY_WEATHER_KEY = "day_weather_key";
    public static final String HOURLY_WEATHER_KEY = "hourly_weather_key";
    public static final String MINUTELY_WEATHER_KEY = "minutely_weather_key";

    public static final String NEWS_DATA_KEY = "news_data_key";
    public static final String CITY_NAME_KEY = "city_name_key";

    public static final String NOTIF_ICON = "notif_hour";
    public static final String NOTIF_SUMMARY = "notif_summary";
    public static final String NOTIF_TEMPERATURE = "notif_temperature";

    public static final String DRAWER_WAS_OPEN = "drawer_was_open";

    public static final String[] LIST_OF_SOURCES = {
            "bbc-news",
            "cnbc",
            "cnn",
            "bbc-sport",
            "google-news",
            "the-new-york-times",
            "espn",
            "the-wall-street-journal",
            "hacker-news",
            "sky-sports-news",
            "mtv-news"
    };

    public static final List<String> supportedLang = Collections.unmodifiableList(Arrays.asList("ar","az","be","bs","ca","cs","de","el","en","es",
            "et","fr","hr","hu","id","it","is","kw","nb","nl","pl","pt","ru",
            "sk","sl","sr","sv","tet","tr","uk","x-pig-latin","zh","zh-tw"));

    public static final Map<List<String>, String> LIST_OF_TYPEFACES = getTypesFaces();
    public static final int CACHE_SIZE = 50 * 1024 * 1024;
    public static final String FIRST_RUN = "is_first_run";

    private static Map<List<String>, String> getTypesFaces() {
        Map<List<String>, String> dictOfTypeFaces = new HashMap<>();

        dictOfTypeFaces.put(Arrays.asList("en", "fr", "es", "de", "cs", "ca",
                "az", "bs", "et", "hr", "hu", "id", "it", "is", "nb", "nl",
                "pl"),
                "fonts/default.ttf");

        dictOfTypeFaces.put(Collections.singletonList("ar"),
                "fonts/arabic.ttf");

        dictOfTypeFaces.put(Arrays.asList("be", "bg", "pt", "ru", "sk", "sl",
                "sr", "sv", "tr", "uk"),
                "fonts/est_europe.ttf");

        return Collections.unmodifiableMap(dictOfTypeFaces);
    }
}