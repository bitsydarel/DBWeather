package com.dbeginc.dbweather.models.datatypes.weather;

/**
 * Created by Darel Bitsy on 19/02/17.
 * This class hold all the constant i need
 * to work with my database
 */

public class DatabaseConstant {

    private DatabaseConstant(){}

    public static final String CREATE_TABLE_QUERY = "create table if not exists ";

    public static final String COLUMN_ID = "id";

    public static final String COLUMN_PRIMARY_KEY = " primary key";
    public static final String COLUMN_UNIQUE = " unique";
    public static final String COLUMN_INTEGER_TYPE = " integer";
    public static final String COLUMN_TEXT_TYPE = " text";
    public static final String COLUMN_REAL_TYPE = " real";
    public static final String COMMA = ",";
    public static final String SELECT_EVERYTHING_FROM = "SELECT * FROM ";

    public static final String SELECT = "SELECT ";
    public static final String FROM = " FROM ";
    public static final String ORDER_BY = " ORDER BY ";
    public static final String NOT_NULL = " not null";
    public static final String FOREIGN_KEY = "FOREIGN KEY(%s) REFERENCES %s(%s)";
    private static final String ID = "id";

    private static final String TIME = "time";
    private static final String SUMMARY = "summary";
    private static final String ICON = "icon";
    private static final String HUMIDITY = "humidity";
    private static final String PRECIPCHANCE = "precipice_chance";
    private static final String PRECIPTYPE = "precipice_type";
    private static final String CLOUD_COVER = "cloud_cover";
    private static final String WIND_SPEED = "wind_speed";
    public static final String CITIES_TABLE = "cities";

    public static final String THE_CITY_NAME = "name";
    public static final String THE_CITY_COUNTRY = "country";
    public static final String THE_CITY_LATITUDE = "latitude";
    public static final String THE_CITY_LONGITUDE = "longitude";
    public static final String APPLICATION_TABLE = "application_table";
    public static final String TEMPERATURE_UNIT = "temperature_unit";

    public static final String LAST_WEATHER_SERVER_SYNC = "last_weather_server_sync";
    public static final String LAST_NEWS_SERVER_SYNC = "last_news_server_sync";

    public static final String WEATHER_TABLE = "weather_info";
    public static final String WEEK_SUMMARY = "week_summary";
    public static final String FULL_DAY_SUMMARY = "entire_day_summary";
    public static final String FULL_HOUR_SUMMARY = "entire_hour_summary";
    public static final String LAST_KNOW_LATITUDE = "last_know_latitude";
    public static final String LAST_KNOW_LONGITUDE = "last_know_longitude";
    public static final String CITY_NAME = "city_name";
    public static final String TIMEZONE = "timezone";

    public static final String ALERT_TABLE = "alerts";
    public static final String ALERT_ID = ID;
    public static final String ALERT_TITLE = "title";
    public static final String ALERT_TIME = TIME;
    public static final String ALERT_EXPIRES = "expires";
    public static final String ALERT_DESCRIPTION = "description";
    public static final String ALERT_URI = "uri";

    public static final String CURRENT_TABLE_NAME = "current";
    public static final String CURRENT_ID = ID;
    public static final String CURRENT_TIME = TIME;
    public static final String CURRENT_SUMMARY = SUMMARY;
    public static final String CURRENT_ICON = ICON;
    public static final String CURRENT_TEMPERATURE = "temperature";
    public static final String CURRENT_APPARENT_TEMPERATURE = "apparentTemperature";
    public static final String CURRENT_HUMIDITY = HUMIDITY;
    public static final String CURRENT_PRECIPCHANCE = PRECIPCHANCE;
    public static final String CURRENT_PRECIPTYPE = PRECIPTYPE;
    public static final String CURRENT_CLOUD_COVER = CLOUD_COVER;
    public static final String CURRENT_WIND_SPEED = WIND_SPEED;
    public static final String CURRENT_WIND_BEARING = "windBearing";


    public static final String HOURS_TABLE_NAME = "hours";
    public static final String HOUR_ID = ID;
    public static final String HOUR_TIME = TIME;
    public static final String HOUR_ICON = ICON;
    public static final String HOUR_SUMMARY = SUMMARY;
    public static final String HOUR_TEMPERATURE = "hour_temperature";
    public static final String HOUR_APPARENT_TEMPERATURE = "apparentTemperature";
    public static final String HOUR_HUMIDITY = HUMIDITY;
    public static final String HOUR_PRECIPCHANCE = PRECIPCHANCE;
    public static final String HOUR_PRECIPTYPE = PRECIPTYPE;
    public static final String HOUR_DEW_POINT = "dewPoint";
    public static final String HOUR_WIND_SPEED = WIND_SPEED;
    public static final String HOUR_WIND_BEARING = "windBearing";
    public static final String HOUR_CLOUD_COVER = CLOUD_COVER;
    public static final String HOUR_VISIBILITY = "visibility";
    public static final String HOUR_PRESSURE = "pressure";
    public static final String HOUR_OZONE = "ozone";

    public static final String DAYS_TABLE_NAME = "days";
    public static final String DAY_ID = ID;
    public static final String DAY_TIME = TIME;
    public static final String DAY_SUMMARY = SUMMARY;
    public static final String DAY_ICON = ICON;
    public static final String DAY_SUNRISE_TIME = "sunriseTime";
    public static final String DAY_SUNSET_TIME = "sunsetTime";
    public static final String DAY_MOON_PHASE = "moonPhase";
    public static final String DAY_PRECIPCHANCE = PRECIPCHANCE;
    public static final String DAY_PRECIPTYPE = PRECIPTYPE;
    public static final String DAY_TEMPERATURE_MAX = "temperature";
    public static final String DAY_APPARENT_TEMPERATURE_MAX = "apparentTemperatureMax";
    public static final String DAY_DEW_POINT = "dewPoint";
    public static final String DAY_HUMIDITY = HUMIDITY;
    public static final String DAY_WIND_SPEED = WIND_SPEED;
    public static final String DAY_WIND_BEARING = "wind_bearing";
    public static final String DAY_VISIBILITY = "visibility";
    public static final String DAY_CLOUD_COVER = CLOUD_COVER;
    public static final String DAY_PRESSURE = "pressure";
    public static final String DAY_OZONE = "ozone";

    public static final String MINUTELY_TABLE = "minutely";
    public static final String MINUTELY_ID = ID;
    public static final String MINUTELY_TIME = TIME;
    public static final String MINUTELY_PRECIPCHANCE = PRECIPCHANCE;
    public static final String MINUTELY_PRECIPTYPE = PRECIPTYPE;

    public static final String NEWS_TABLE_NAME = "news";
    public static final String NEWS_ID = ID;
    public static final String NEWS_SOURCE = "news_source";
    public static final String NEWS_TITLE = "news_title";
    public static final String NEWS_URL = "news_url";
    public static final String NEWS_PUBLISHED_AT = "published_at";
    public static final String NEWS_DESCRIPTION = "news_description";
    public static final String NEWS_IMAGE_URL = "news_image_url";

    public static final String NEWS_SOURCES_TABLE = "news_sources";
    public static final String NEWS_SOURCE_ID = "id";
    public static final String NEWS_SOURCE_NAME = "name";
    public static final String NEWS_SOURCE_COUNT = "count";
    public static final String NEWS_SOURCE_STATUS = "status";

    public static final String WEATHER_INSERTED = "weather_inserted";
    public static final String CURRENT_INSERTED = "current_inserted";
    public static final String ALERTS_INSERTED = "alerts_inserted";
    public static final String MINUTELY_INSERTED = "minutely_inserted";
    public static final String DAYS_INSERTED = "days_inserted";
    public static final String HOURLY_INSERTED = "hourly_inserted";
    public static final String NEWS_INSERTED = "news_inserted";
}
