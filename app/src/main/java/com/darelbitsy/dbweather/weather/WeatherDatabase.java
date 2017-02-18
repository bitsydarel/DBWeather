package com.darelbitsy.dbweather.weather;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Darel Bitsy on 26/01/17.
 */

public class WeatherDatabase extends SQLiteOpenHelper {
    private static final int VERSION = 1;
    private static final String DB_NAME = "db_weather.sqlite";
    private static final String CREATE_TABLE_QUERY = "create table ";

    public static final String DAYS_TABLE_NAME = "days";
    public static final String DAY_ID = "id";
    public static final String DAY_TIME = "time";
    public static final String DAY_TEMPERATURE_MAX = "temperature";
    public static final String DAY_ICON = "icon";
    public static final String DAY_HUMIDITY = "humidity";
    public static final String DAY_PRECIPCHANCE = "precipice_chance";
    public static final String DAY_PRECIPTYPE = "precipice_type";
    public static final String DAY_SUMMARY = "summary";
    public static final String DAY_WIND_SPEED = "wind_speed_percentage";
    public static final String DAY_CLOUD_COVER = "cloud_cover_percentage";
    public static final String DAY_TIMEZONE = "timezone";

    public static final String HOURS_TABLE_NAME = "hours";
    public static final String HOUR_ID = "id";
    public static final String HOUR_TIMEZONE = "timezone";
    public static final String HOUR_TIME = "time";
    public static final String HOUR_SUMMARY = "summary";
    public static final String HOUR_ICON = "icon";
    public static final String HOUR_TEMPERATURE = "hour_temperature";

    public static final String CURRENT_TABLE_NAME = "current";
    public static final String CURRENT_CITYNAME = "city_name";
    public static final String CURRENT_TIMEZONE = "timezone";
    public static final String CURRENT_SUMMARY = "summary";
    public static final String CURRENT_ICON = "icon";
    public static final String CURRENT_TIME = "time";
    public static final String CURRENT_TEMPERATURE = "temperature";
    public static final String CURRENT_HUMIDITY = "humidity";
    public static final String CURRENT_PRECIPCHANCE = "precipice_chance";
    public static final String CURRENT_PRECIPTYPE = "precipice_type";
    public static final String CURRENT_CLOUD_COVER = "cloud_cover";
    public static final String CURRENT_WIND_SPEED = "wind_speed";

    public static final String WEEK_SUMMARY = "week_summary";

    public static final String LAST_KNOW_LATITUDE = "last_know_latitude";
    public static final String LAST_KNOW_LONGITUDE = "last_know_longitude";
    public static final String LAST_JSON_DATA = "last_json_data";
    public static final String LAST_SERVER_SYNC = "last_server_sync";

    public static final String NEWS_TABLE_NAME = "news";
    public static final String NEWS_ID = "id";
    public static final String NEWS_SOURCE = "news_source";
    public static final String NEWS_TITLE = "news_title";
    public static final String NEWS_URL = "news_url";

    public WeatherDatabase(Context context) {
        super(context, DB_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createCurrentWeatherTable(db);
        createHoursWeatherTable(db);
        createDaysWeatherTable(db);
    }

    private void createDaysWeatherTable(SQLiteDatabase database) {
        database.execSQL(CREATE_TABLE_QUERY + DAYS_TABLE_NAME +" ("+
                DAY_ID + " integer primary key autoincrement not null," +
                DAY_SUMMARY + " text," +
                DAY_TIMEZONE + " text," +
                DAY_TIME + " integer," +
                DAY_TEMPERATURE_MAX + " integer," +
                DAY_ICON + " text," +
                DAY_HUMIDITY + " integer," +
                DAY_PRECIPCHANCE + " integer," +
                DAY_PRECIPTYPE+ " text," +
                DAY_CLOUD_COVER+ " integer," +
                DAY_WIND_SPEED+ " integer" +
                ");"
        );
    }

    private void createHoursWeatherTable(SQLiteDatabase database) {
        database.execSQL(CREATE_TABLE_QUERY + HOURS_TABLE_NAME +" (" +
                HOUR_ID + " integer primary key autoincrement not null," +
                HOUR_SUMMARY + " text," +
                HOUR_ICON + " integer," +
                HOUR_TIME + " integer," +
                HOUR_TEMPERATURE + " integer," +
                HOUR_TIMEZONE + " text" +
                ");"
        );
    }

    private void createNewsTable(SQLiteDatabase database) {
        database.execSQL(CREATE_TABLE_QUERY + NEWS_TABLE_NAME + " (" +
                NEWS_ID + " integer primary key autoincrement not null," +
                NEWS_SOURCE + " text," +
                NEWS_TITLE + " text," +
                NEWS_URL + " text" +
                ");"
        );
    }

    private void createCurrentWeatherTable(SQLiteDatabase database) {
        database.execSQL(CREATE_TABLE_QUERY + CURRENT_TABLE_NAME + " (" +
                CURRENT_CITYNAME + " text ," +
                CURRENT_TIMEZONE + " text," +
                CURRENT_SUMMARY + " text," +
                CURRENT_ICON + " integer," +
                CURRENT_TIME + " integer," +
                CURRENT_TEMPERATURE + " integer," +
                CURRENT_HUMIDITY + " integer," +
                CURRENT_PRECIPCHANCE + " integer," +
                CURRENT_PRECIPTYPE+ " text," +
                CURRENT_CLOUD_COVER+ " integer," +
                CURRENT_WIND_SPEED+ " integer," +
                WEEK_SUMMARY + " text," +
                LAST_KNOW_LATITUDE + " real," +
                LAST_KNOW_LONGITUDE + " real," +
                LAST_JSON_DATA + " text," +
                LAST_SERVER_SYNC + " text" +
                ");"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
