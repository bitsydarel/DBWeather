package com.dbeginc.dbweather.utils.holder;

import org.jetbrains.annotations.Nullable;

/**
 * Created by Darel Bitsy on 18/02/17.
 * This class hold all constant variable needed for
 */

public class ConstantHolder {

    public static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 7125;
    public static final String COMPONENT_NAME_KEY = "COMPONENT_NAME_KEY";
    public static final String NOTIFICATION_KEY = "notification_key";
    public static final String LATITUDE = "locationLatitude";
    public static final String LONGITUDE = "locationLongitude";
    public static final String CURRENT_LOCATION = "current_location";
    public static final String LOCATIONS = "locations";

    /****************************** Article Constant ***********************************/
    public static final String ARTICLES_DATA = "article_data";
    public static final String NEWS_PAPERS = "news_papers";

    /****************************** DAY Constant ***********************************/
    public static final String DAY_DATA = "day_data";

    /****************************** Hour Constant ***********************************/

    public static final String WEATHER_INFO_KEY = "weather_info_key";
    public static final String LOCATION_UPDATE = "dbweather_location_update";

    public static final String WEATHER_ALERT_CHANNEL_ID = "forecast_alert_id";
    public static final String WEATHER_ALERT_CHANNEL_NAME = "DBWeather forecast alert";
    public static final int WEATHER_ALERT_ID = 7125205;
    public static final String DBWEATHER_NETWORK_CACHE = "dbweather_network_cache";
    public static final String DBWEATHER_IMAGE_CACHE = "dbweather_image_cache";
    public static final String CUSTOM_TAB_PACKAGE_NAME = "CUSTOM_TAB_PACKAGE_NAME";
    public static final String CUSTOM_TAB_PACKAGE_NOT_FOUND = "CUSTOM_TAB_PACKAGE_NOT_FOUND";
    public static final String PERMISSION_EVENT = "permission_event";
    public static final String SEARCH_QUERY_URI = "content://com.dbeginc.dbweather.utils.contentprovider.LocationSuggestionProvider/search_suggest_query/%s?limit=50";
    public static final String LIVES_DATA = "lives_data";
    public static final String YOUTUBE_THUMBNAIL_URL = "https://img.youtube.com/vi/%s/hqdefault.jpg";
    public static final String SOURCE_KEY = "source_key";

    private ConstantHolder() {
        //To disable default constructor
    }

    /********************** Weather **********************/
    public static final String USER_LOCATION = "user_location";
    public static final String USER_LATITUDE = "user_latitude";
    public static final String USER_LONGITUDE = "user_longitude";

    public static final String TAG = "dbweather_app";
    public static final String IS_GPS_PERMISSION_GRANTED = "is_gps_permission_granted";
    public static final String PREFS_NAME = "db_weather_prefs";

    public static final String IS_GPS_LOCATION = "is_current_location";

    public static final int CACHE_SIZE = 50 * 1024 * 1024;

    public static final String FIRST_RUN = "is_first_run";
}
