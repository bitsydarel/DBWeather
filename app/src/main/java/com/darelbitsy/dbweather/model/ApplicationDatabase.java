package com.darelbitsy.dbweather.model;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static com.darelbitsy.dbweather.model.weather.DatabaseConstant.ALERT_DESCRIPTION;
import static com.darelbitsy.dbweather.model.weather.DatabaseConstant.ALERT_EXPIRES;
import static com.darelbitsy.dbweather.model.weather.DatabaseConstant.ALERT_ID;
import static com.darelbitsy.dbweather.model.weather.DatabaseConstant.ALERT_TABLE;
import static com.darelbitsy.dbweather.model.weather.DatabaseConstant.ALERT_TIME;
import static com.darelbitsy.dbweather.model.weather.DatabaseConstant.ALERT_TITLE;
import static com.darelbitsy.dbweather.model.weather.DatabaseConstant.ALERT_URI;
import static com.darelbitsy.dbweather.model.weather.DatabaseConstant.APPLICATION_TABLE;
import static com.darelbitsy.dbweather.model.weather.DatabaseConstant.CITY_NAME;
import static com.darelbitsy.dbweather.model.weather.DatabaseConstant.COLUMN_INTEGER_TYPE;
import static com.darelbitsy.dbweather.model.weather.DatabaseConstant.COLUMN_REAL_TYPE;
import static com.darelbitsy.dbweather.model.weather.DatabaseConstant.COLUMN_TEXT_TYPE;
import static com.darelbitsy.dbweather.model.weather.DatabaseConstant.COMMA;
import static com.darelbitsy.dbweather.model.weather.DatabaseConstant.CURRENT_APPARENT_TEMPERATURE;
import static com.darelbitsy.dbweather.model.weather.DatabaseConstant.CURRENT_CLOUD_COVER;
import static com.darelbitsy.dbweather.model.weather.DatabaseConstant.CURRENT_HUMIDITY;
import static com.darelbitsy.dbweather.model.weather.DatabaseConstant.CURRENT_ICON;
import static com.darelbitsy.dbweather.model.weather.DatabaseConstant.CURRENT_PRECIPCHANCE;
import static com.darelbitsy.dbweather.model.weather.DatabaseConstant.CURRENT_PRECIPTYPE;
import static com.darelbitsy.dbweather.model.weather.DatabaseConstant.CURRENT_SUMMARY;
import static com.darelbitsy.dbweather.model.weather.DatabaseConstant.CURRENT_TABLE_NAME;
import static com.darelbitsy.dbweather.model.weather.DatabaseConstant.CURRENT_TEMPERATURE;
import static com.darelbitsy.dbweather.model.weather.DatabaseConstant.CURRENT_TIME;
import static com.darelbitsy.dbweather.model.weather.DatabaseConstant.CURRENT_WIND_BEARING;
import static com.darelbitsy.dbweather.model.weather.DatabaseConstant.CURRENT_WIND_SPEED;
import static com.darelbitsy.dbweather.model.weather.DatabaseConstant.DAYS_TABLE_NAME;
import static com.darelbitsy.dbweather.model.weather.DatabaseConstant.DAY_APPARENT_TEMPERATURE_MAX;
import static com.darelbitsy.dbweather.model.weather.DatabaseConstant.DAY_CLOUD_COVER;
import static com.darelbitsy.dbweather.model.weather.DatabaseConstant.DAY_DEW_POINT;
import static com.darelbitsy.dbweather.model.weather.DatabaseConstant.DAY_HUMIDITY;
import static com.darelbitsy.dbweather.model.weather.DatabaseConstant.DAY_ICON;
import static com.darelbitsy.dbweather.model.weather.DatabaseConstant.DAY_ID;
import static com.darelbitsy.dbweather.model.weather.DatabaseConstant.DAY_MOON_PHASE;
import static com.darelbitsy.dbweather.model.weather.DatabaseConstant.DAY_OZONE;
import static com.darelbitsy.dbweather.model.weather.DatabaseConstant.DAY_PRECIPCHANCE;
import static com.darelbitsy.dbweather.model.weather.DatabaseConstant.DAY_PRECIPTYPE;
import static com.darelbitsy.dbweather.model.weather.DatabaseConstant.DAY_PRESSURE;
import static com.darelbitsy.dbweather.model.weather.DatabaseConstant.DAY_SUMMARY;
import static com.darelbitsy.dbweather.model.weather.DatabaseConstant.DAY_SUNRISE_TIME;
import static com.darelbitsy.dbweather.model.weather.DatabaseConstant.DAY_SUNSET_TIME;
import static com.darelbitsy.dbweather.model.weather.DatabaseConstant.DAY_TEMPERATURE_MAX;
import static com.darelbitsy.dbweather.model.weather.DatabaseConstant.DAY_TIME;
import static com.darelbitsy.dbweather.model.weather.DatabaseConstant.DAY_VISIBILITY;
import static com.darelbitsy.dbweather.model.weather.DatabaseConstant.DAY_WIND_BEARING;
import static com.darelbitsy.dbweather.model.weather.DatabaseConstant.DAY_WIND_SPEED;
import static com.darelbitsy.dbweather.model.weather.DatabaseConstant.FULL_DAY_SUMMARY;
import static com.darelbitsy.dbweather.model.weather.DatabaseConstant.FULL_HOUR_SUMMARY;
import static com.darelbitsy.dbweather.model.weather.DatabaseConstant.HOURS_TABLE_NAME;
import static com.darelbitsy.dbweather.model.weather.DatabaseConstant.HOUR_APPARENT_TEMPERATURE;
import static com.darelbitsy.dbweather.model.weather.DatabaseConstant.HOUR_CLOUD_COVER;
import static com.darelbitsy.dbweather.model.weather.DatabaseConstant.HOUR_DEW_POINT;
import static com.darelbitsy.dbweather.model.weather.DatabaseConstant.HOUR_HUMIDITY;
import static com.darelbitsy.dbweather.model.weather.DatabaseConstant.HOUR_ICON;
import static com.darelbitsy.dbweather.model.weather.DatabaseConstant.HOUR_ID;
import static com.darelbitsy.dbweather.model.weather.DatabaseConstant.HOUR_OZONE;
import static com.darelbitsy.dbweather.model.weather.DatabaseConstant.HOUR_PRECIPCHANCE;
import static com.darelbitsy.dbweather.model.weather.DatabaseConstant.HOUR_PRECIPTYPE;
import static com.darelbitsy.dbweather.model.weather.DatabaseConstant.HOUR_PRESSURE;
import static com.darelbitsy.dbweather.model.weather.DatabaseConstant.HOUR_SUMMARY;
import static com.darelbitsy.dbweather.model.weather.DatabaseConstant.HOUR_TEMPERATURE;
import static com.darelbitsy.dbweather.model.weather.DatabaseConstant.HOUR_TIME;
import static com.darelbitsy.dbweather.model.weather.DatabaseConstant.HOUR_VISIBILITY;
import static com.darelbitsy.dbweather.model.weather.DatabaseConstant.HOUR_WIND_BEARING;
import static com.darelbitsy.dbweather.model.weather.DatabaseConstant.HOUR_WIND_SPEED;
import static com.darelbitsy.dbweather.model.weather.DatabaseConstant.LAST_KNOW_LATITUDE;
import static com.darelbitsy.dbweather.model.weather.DatabaseConstant.LAST_KNOW_LONGITUDE;
import static com.darelbitsy.dbweather.model.weather.DatabaseConstant.LAST_NEWS_SERVER_SYNC;
import static com.darelbitsy.dbweather.model.weather.DatabaseConstant.LAST_WEATHER_SERVER_SYNC;
import static com.darelbitsy.dbweather.model.weather.DatabaseConstant.MINUTELY_ID;
import static com.darelbitsy.dbweather.model.weather.DatabaseConstant.MINUTELY_PRECIPCHANCE;
import static com.darelbitsy.dbweather.model.weather.DatabaseConstant.MINUTELY_PRECIPTYPE;
import static com.darelbitsy.dbweather.model.weather.DatabaseConstant.MINUTELY_TABLE;
import static com.darelbitsy.dbweather.model.weather.DatabaseConstant.MINUTELY_TIME;
import static com.darelbitsy.dbweather.model.weather.DatabaseConstant.NEWS_ID;
import static com.darelbitsy.dbweather.model.weather.DatabaseConstant.NEWS_PUBLISHED_AT;
import static com.darelbitsy.dbweather.model.weather.DatabaseConstant.NEWS_SOURCE;
import static com.darelbitsy.dbweather.model.weather.DatabaseConstant.NEWS_TABLE_NAME;
import static com.darelbitsy.dbweather.model.weather.DatabaseConstant.NEWS_TITLE;
import static com.darelbitsy.dbweather.model.weather.DatabaseConstant.NEWS_URL;
import static com.darelbitsy.dbweather.model.weather.DatabaseConstant.TIMEZONE;
import static com.darelbitsy.dbweather.model.weather.DatabaseConstant.WEATHER_TABLE;
import static com.darelbitsy.dbweather.model.weather.DatabaseConstant.WEEK_SUMMARY;

/**
 * Created by Darel Bitsy on 26/01/17.
 * This class create my application database
 * or upgrade it if needed
 */

public class ApplicationDatabase extends SQLiteOpenHelper {
    private static final int VERSION = 1;
    private static final String DB_NAME = "db_weather.sqlite";
    private static final String CREATE_TABLE_QUERY = "create table ";



    public ApplicationDatabase(Context context) {
        super(context, DB_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createApplicationTable(db);
        createWeatherTable(db);
        createAlertTable(db);
        createCurrentWeatherTable(db);
        createHoursWeatherTable(db);
        createDaysWeatherTable(db);
        createMinutelyTable(db);
        createNewsTable(db);
    }

    private void createApplicationTable(SQLiteDatabase database) {
        database.execSQL(CREATE_TABLE_QUERY + APPLICATION_TABLE + " (" +
                LAST_WEATHER_SERVER_SYNC + COLUMN_TEXT_TYPE + COMMA +
                LAST_NEWS_SERVER_SYNC + COLUMN_TEXT_TYPE +
                ");"
        );
    }

    private void createWeatherTable(SQLiteDatabase database) {
        database.execSQL(CREATE_TABLE_QUERY + WEATHER_TABLE + " (" +
                CITY_NAME + COLUMN_TEXT_TYPE + COMMA +
                TIMEZONE + COLUMN_TEXT_TYPE + COMMA +
                WEEK_SUMMARY + COLUMN_TEXT_TYPE + COMMA +
                FULL_DAY_SUMMARY + COLUMN_TEXT_TYPE + COMMA +
                FULL_HOUR_SUMMARY + COLUMN_TEXT_TYPE + COMMA +
                LAST_KNOW_LATITUDE  + COLUMN_REAL_TYPE + COMMA +
                LAST_KNOW_LONGITUDE + COLUMN_REAL_TYPE +
                ");"
        );
    }

    private void createAlertTable(SQLiteDatabase database) {
        database.execSQL(CREATE_TABLE_QUERY + ALERT_TABLE + " (" +
                ALERT_ID + " integer primary key autoincrement not null," +
                ALERT_TITLE + COLUMN_TEXT_TYPE + COMMA +
                ALERT_TIME + COLUMN_INTEGER_TYPE + COMMA +
                ALERT_EXPIRES + COLUMN_INTEGER_TYPE + COMMA +
                ALERT_DESCRIPTION + COLUMN_TEXT_TYPE + COMMA +
                ALERT_URI + COLUMN_TEXT_TYPE +
                ");");
    }

    private void createCurrentWeatherTable(SQLiteDatabase database) {
        database.execSQL(CREATE_TABLE_QUERY + CURRENT_TABLE_NAME + " (" +
                CURRENT_TIME + COLUMN_INTEGER_TYPE + COMMA +
                CURRENT_SUMMARY + COLUMN_TEXT_TYPE + COMMA +
                CURRENT_ICON + COLUMN_TEXT_TYPE + COMMA +
                CURRENT_TEMPERATURE + COLUMN_REAL_TYPE + COMMA +
                CURRENT_APPARENT_TEMPERATURE + COLUMN_REAL_TYPE + COMMA +
                CURRENT_HUMIDITY + COLUMN_REAL_TYPE + COMMA +
                CURRENT_PRECIPCHANCE + COLUMN_REAL_TYPE + COMMA +
                CURRENT_PRECIPTYPE+ COLUMN_TEXT_TYPE + COMMA +
                CURRENT_CLOUD_COVER + COLUMN_REAL_TYPE + COMMA +
                CURRENT_WIND_SPEED+ COLUMN_REAL_TYPE + COMMA +
                CURRENT_WIND_BEARING + COLUMN_INTEGER_TYPE +
                ");"
        );
    }

    private void createDaysWeatherTable(SQLiteDatabase database) {
        database.execSQL(CREATE_TABLE_QUERY + DAYS_TABLE_NAME + " (" +
                DAY_ID + " integer primary key autoincrement not null," +
                DAY_TIME + COLUMN_INTEGER_TYPE + COMMA +
                DAY_SUMMARY + COLUMN_TEXT_TYPE + COMMA +
                DAY_ICON + COLUMN_TEXT_TYPE + COMMA +
                DAY_SUNRISE_TIME + COLUMN_INTEGER_TYPE + COMMA +
                DAY_SUNSET_TIME + COLUMN_INTEGER_TYPE + COMMA +
                DAY_MOON_PHASE + COLUMN_REAL_TYPE + COMMA +
                DAY_PRECIPCHANCE + COLUMN_REAL_TYPE + COMMA +
                DAY_PRECIPTYPE + COLUMN_TEXT_TYPE + COMMA +
                DAY_TEMPERATURE_MAX + COLUMN_REAL_TYPE + COMMA +
                DAY_APPARENT_TEMPERATURE_MAX + COLUMN_REAL_TYPE + COMMA +
                DAY_DEW_POINT + COLUMN_REAL_TYPE + COMMA +
                DAY_HUMIDITY + COLUMN_REAL_TYPE + COMMA +
                DAY_WIND_SPEED + COLUMN_REAL_TYPE + COMMA +
                DAY_WIND_BEARING + COLUMN_INTEGER_TYPE + COMMA +
                DAY_VISIBILITY + COLUMN_INTEGER_TYPE + COMMA +
                DAY_CLOUD_COVER + COLUMN_REAL_TYPE + COMMA +
                DAY_PRESSURE + COLUMN_REAL_TYPE + COMMA +
                DAY_OZONE + COLUMN_REAL_TYPE +
                ");"
        );
    }

    private void createHoursWeatherTable(SQLiteDatabase database) {
        database.execSQL(CREATE_TABLE_QUERY + HOURS_TABLE_NAME +" (" +
                HOUR_ID + " integer primary key autoincrement not null," +
                HOUR_TIME + COLUMN_INTEGER_TYPE + COMMA +
                HOUR_SUMMARY + COLUMN_TEXT_TYPE + COMMA +
                HOUR_ICON + COLUMN_TEXT_TYPE + COMMA +
                HOUR_TEMPERATURE + COLUMN_REAL_TYPE + COMMA +
                HOUR_APPARENT_TEMPERATURE + COLUMN_REAL_TYPE + COMMA +
                HOUR_HUMIDITY + COLUMN_REAL_TYPE + COMMA +
                HOUR_PRECIPCHANCE + COLUMN_REAL_TYPE + COMMA +
                HOUR_PRECIPTYPE + COLUMN_TEXT_TYPE + COMMA +
                HOUR_DEW_POINT + COLUMN_REAL_TYPE + COMMA +
                HOUR_WIND_SPEED + COLUMN_REAL_TYPE + COMMA +
                HOUR_WIND_BEARING + COLUMN_INTEGER_TYPE + COMMA +
                HOUR_CLOUD_COVER + COLUMN_REAL_TYPE + COMMA +
                HOUR_VISIBILITY + COLUMN_REAL_TYPE + COMMA +
                HOUR_PRESSURE + COLUMN_REAL_TYPE + COMMA +
                HOUR_OZONE + COLUMN_REAL_TYPE +
                ");"
        );
    }

    private void createMinutelyTable(SQLiteDatabase database) {
        database.execSQL(CREATE_TABLE_QUERY + MINUTELY_TABLE +  " (" +
                MINUTELY_ID + " integer primary key autoincrement not null," +
                MINUTELY_TIME + COLUMN_INTEGER_TYPE + COMMA +
                MINUTELY_PRECIPCHANCE + COLUMN_REAL_TYPE + COMMA +
                MINUTELY_PRECIPTYPE + COLUMN_TEXT_TYPE + ");"
        );
    }

    private void createNewsTable(SQLiteDatabase database) {
        database.execSQL(CREATE_TABLE_QUERY + NEWS_TABLE_NAME + " (" +
                NEWS_ID + " integer primary key autoincrement not null," +
                NEWS_SOURCE + COLUMN_TEXT_TYPE + COMMA +
                NEWS_TITLE + COLUMN_TEXT_TYPE + COMMA +
                NEWS_URL + COLUMN_TEXT_TYPE + COMMA +
                NEWS_PUBLISHED_AT + COLUMN_TEXT_TYPE +
                ");"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
