package com.dbeginc.dbweather.utils.holder;

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
    public static final String INTEGER_PRIMARY_KEY = " integer primary key autoincrement not null,";
    public static final String UPDATE_REQUEST = "update_request";
    public static final String WEATHER_INFO_KEY = "weather_info_key";
    public static final String LOCATION_UPDATE = "dbweather_location_update";

    public static final String PERMISSION_ID = "PERMISSION_ID";
    public static final String NEWS_PERMISSION_GRANTED = "NEWS_PERMISSION_GRANTED";
    public static final String NEWS_PERMISSION_DECLINED = "NEWS_PERMISSION_DECLINED";
    public static final String LOCATION_PERMISSION_GRANTED = "LOCATION_PERMISSION_GRANTED";
    public static final String LOCATION_PERMISSION_DECLINED = "LOCATION_PERMISSION_DECLINED";
    public static final String WRITE_PERMISSION_GRANTED = "WRITE_PERMISSION_GRANTED";
    public static final String WRITE_PERMISSION_DECLINED = "WRITE_PERMISSION_DECLINED";
    public static final String INDEX = "INDEX";
    public static final int WEATHER_ALERT_ID = 7125205;
    public static final String IS_CHECKED = "IS_CHECKED";
    public static final String CACHE_NAME = "dbweather_cache_dir";
    public static final String GlIDE_CACHE_NAME = "dbweather_glide_cache";
    public static final int NEWS_SYNC_JOB_ID = 5232017;
    public static final int WEATHER_SYNC_JOB_ID = 5222017;
    public static final String NEWS_SYNC_SERVICES_SCHEDULED = "NEWS_SYNC_SERVICES_SCHEDULED";
    public static final String WEATHER_SYNC_SERVICE_SCHEDULED = "WEATHER_SYNC_SERVICE_SCHEDULED";
    public static final String IS_COORDINATE_INSERTED = "IS_COORDINATE_INSERTED";
    public static final String CUSTOM_TAB_PACKAGE_NAME = "CUSTOM_TAB_PACKAGE_NAME";
    public static final String CUSTOM_TAB_PACKAGE_NOT_FOUND = "CUSTOM_TAB_PACKAGE_NOT_FOUND";
    public static final String SHOULD_WEATHER_BE_SAVED = "SHOULD_WEATHER_BE_SAVED";
    public static final String YOUTUBE_URL = "YOUTUBE_URL";
    public static final String LOCATION_KEY = "location_key";
    public static final String CONTENT_URL = "content://com.dbeginc.dbweather.models.provider.geoname.LocationSuggestionProvider/%s/%s?limit=50";
    public static final String VOICE_QUERY = "voice_query";
    public static final String PERMISSION_EVENT = "permission_event";
    public static final String OLD_INDEX = "OLD_INDEX";
    public static final String MYMEMORY = "MYMEMORY";
    public static final String QUERY_LENGTH_LIMIT = "QUERY LENGTH LIMIT";

    private ConstantHolder() {
        //To disable default constructor
    }

    public static final String TAG = "dbweather_application";
    public static final String IS_GPS_PERMISSION_GRANTED = "is_gps_permission_granted";
    public static final String IS_ACCOUNT_PERMISSION_GRANTED = "is_account_granted";
    public static final String IS_WRITE_PERMISSION_GRANTED = "is_write_granted";
    public static final String PREFS_NAME = "db_weather_prefs";


    public static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 7125;
    public static final int MY_PERMISSIONS_REQUEST_GET_ACCOUNT = 242;
    public static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 380;

    public static final String MYMEMORY_APIURL = "http://api.mymemory.translated.net";
    public static final String USER_LANGUAGE = Locale.getDefault().getLanguage();

    public static final String WEATHER_DATA_KEY = "weather_data_key";

    public static final String NEWS_DATA_KEY = "news_data_key";
    public static final String CITY_NAME_KEY = "city_name_key";

    public static final String NOTIF_ICON = "notif_hour";
    public static final String NOTIF_SUMMARY = "notif_summary";
    public static final String NOTIF_TEMPERATURE = "notif_temperature";

    public static final String IS_FROM_CITY_KEY = "is_from_city_key";
    public static final String SELECTED_CITY_LATITUDE = "selected_city_latitude";
    public static final String SELECTED_CITY_LONGITUDE = "selected_city_longitude";


    public static final List<String> supportedLang = Collections.unmodifiableList(Arrays.asList("ar","az","be","bs","ca","cs","de","el","en","es",
            "et","fr","hr","hu","id","it","is","kw","nb","nl","pl","pt","ru",
            "sk","sl","sr","sv","tet","tr","uk","x-pig-latin","zh","zh-tw"));

    public static final Map<List<String>, String> LIST_OF_TYPEFACES = getTypesFaces();
    public static final int CACHE_SIZE = 50 * 1024 * 1024;
    public static final String FIRST_RUN = "is_first_run";

    private static Map<List<String>, String> getTypesFaces() {
        final Map<List<String>, String> dictOfTypeFaces = new HashMap<>();

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
