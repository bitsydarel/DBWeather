package com.darelbitsy.dbweather.models.databases;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static com.darelbitsy.dbweather.models.datatypes.weather.DatabaseConstant.CITIES_TABLE;
import static com.darelbitsy.dbweather.models.datatypes.weather.DatabaseConstant.CITY_NAME;
import static com.darelbitsy.dbweather.models.datatypes.weather.DatabaseConstant.COLUMN_ID;
import static com.darelbitsy.dbweather.models.datatypes.weather.DatabaseConstant.COLUMN_INTEGER_TYPE;
import static com.darelbitsy.dbweather.models.datatypes.weather.DatabaseConstant.COLUMN_REAL_TYPE;
import static com.darelbitsy.dbweather.models.datatypes.weather.DatabaseConstant.COLUMN_TEXT_TYPE;
import static com.darelbitsy.dbweather.models.datatypes.weather.DatabaseConstant.COLUMN_UNIQUE;
import static com.darelbitsy.dbweather.models.datatypes.weather.DatabaseConstant.COMMA;
import static com.darelbitsy.dbweather.models.datatypes.weather.DatabaseConstant.CREATE_TABLE_QUERY;
import static com.darelbitsy.dbweather.models.datatypes.weather.DatabaseConstant.CURRENT_APPARENT_TEMPERATURE;
import static com.darelbitsy.dbweather.models.datatypes.weather.DatabaseConstant.CURRENT_CLOUD_COVER;
import static com.darelbitsy.dbweather.models.datatypes.weather.DatabaseConstant.CURRENT_HUMIDITY;
import static com.darelbitsy.dbweather.models.datatypes.weather.DatabaseConstant.CURRENT_ICON;
import static com.darelbitsy.dbweather.models.datatypes.weather.DatabaseConstant.CURRENT_ID;
import static com.darelbitsy.dbweather.models.datatypes.weather.DatabaseConstant.CURRENT_PRECIPCHANCE;
import static com.darelbitsy.dbweather.models.datatypes.weather.DatabaseConstant.CURRENT_PRECIPTYPE;
import static com.darelbitsy.dbweather.models.datatypes.weather.DatabaseConstant.CURRENT_SUMMARY;
import static com.darelbitsy.dbweather.models.datatypes.weather.DatabaseConstant.CURRENT_TABLE_NAME;
import static com.darelbitsy.dbweather.models.datatypes.weather.DatabaseConstant.CURRENT_TEMPERATURE;
import static com.darelbitsy.dbweather.models.datatypes.weather.DatabaseConstant.CURRENT_TIME;
import static com.darelbitsy.dbweather.models.datatypes.weather.DatabaseConstant.CURRENT_WIND_BEARING;
import static com.darelbitsy.dbweather.models.datatypes.weather.DatabaseConstant.CURRENT_WIND_SPEED;
import static com.darelbitsy.dbweather.models.datatypes.weather.DatabaseConstant.DAYS_TABLE_NAME;
import static com.darelbitsy.dbweather.models.datatypes.weather.DatabaseConstant.DAY_APPARENT_TEMPERATURE_MAX;
import static com.darelbitsy.dbweather.models.datatypes.weather.DatabaseConstant.DAY_CLOUD_COVER;
import static com.darelbitsy.dbweather.models.datatypes.weather.DatabaseConstant.DAY_DEW_POINT;
import static com.darelbitsy.dbweather.models.datatypes.weather.DatabaseConstant.DAY_HUMIDITY;
import static com.darelbitsy.dbweather.models.datatypes.weather.DatabaseConstant.DAY_ICON;
import static com.darelbitsy.dbweather.models.datatypes.weather.DatabaseConstant.DAY_ID;
import static com.darelbitsy.dbweather.models.datatypes.weather.DatabaseConstant.DAY_MOON_PHASE;
import static com.darelbitsy.dbweather.models.datatypes.weather.DatabaseConstant.DAY_OZONE;
import static com.darelbitsy.dbweather.models.datatypes.weather.DatabaseConstant.DAY_PRECIPCHANCE;
import static com.darelbitsy.dbweather.models.datatypes.weather.DatabaseConstant.DAY_PRECIPTYPE;
import static com.darelbitsy.dbweather.models.datatypes.weather.DatabaseConstant.DAY_PRESSURE;
import static com.darelbitsy.dbweather.models.datatypes.weather.DatabaseConstant.DAY_SUMMARY;
import static com.darelbitsy.dbweather.models.datatypes.weather.DatabaseConstant.DAY_SUNRISE_TIME;
import static com.darelbitsy.dbweather.models.datatypes.weather.DatabaseConstant.DAY_SUNSET_TIME;
import static com.darelbitsy.dbweather.models.datatypes.weather.DatabaseConstant.DAY_TEMPERATURE_MAX;
import static com.darelbitsy.dbweather.models.datatypes.weather.DatabaseConstant.DAY_TIME;
import static com.darelbitsy.dbweather.models.datatypes.weather.DatabaseConstant.DAY_VISIBILITY;
import static com.darelbitsy.dbweather.models.datatypes.weather.DatabaseConstant.DAY_WIND_BEARING;
import static com.darelbitsy.dbweather.models.datatypes.weather.DatabaseConstant.DAY_WIND_SPEED;
import static com.darelbitsy.dbweather.models.datatypes.weather.DatabaseConstant.FOREIGN_KEY;
import static com.darelbitsy.dbweather.models.datatypes.weather.DatabaseConstant.HOURS_TABLE_NAME;
import static com.darelbitsy.dbweather.models.datatypes.weather.DatabaseConstant.HOUR_APPARENT_TEMPERATURE;
import static com.darelbitsy.dbweather.models.datatypes.weather.DatabaseConstant.HOUR_CLOUD_COVER;
import static com.darelbitsy.dbweather.models.datatypes.weather.DatabaseConstant.HOUR_DEW_POINT;
import static com.darelbitsy.dbweather.models.datatypes.weather.DatabaseConstant.HOUR_HUMIDITY;
import static com.darelbitsy.dbweather.models.datatypes.weather.DatabaseConstant.HOUR_ICON;
import static com.darelbitsy.dbweather.models.datatypes.weather.DatabaseConstant.HOUR_ID;
import static com.darelbitsy.dbweather.models.datatypes.weather.DatabaseConstant.HOUR_OZONE;
import static com.darelbitsy.dbweather.models.datatypes.weather.DatabaseConstant.HOUR_PRECIPCHANCE;
import static com.darelbitsy.dbweather.models.datatypes.weather.DatabaseConstant.HOUR_PRECIPTYPE;
import static com.darelbitsy.dbweather.models.datatypes.weather.DatabaseConstant.HOUR_PRESSURE;
import static com.darelbitsy.dbweather.models.datatypes.weather.DatabaseConstant.HOUR_SUMMARY;
import static com.darelbitsy.dbweather.models.datatypes.weather.DatabaseConstant.HOUR_TEMPERATURE;
import static com.darelbitsy.dbweather.models.datatypes.weather.DatabaseConstant.HOUR_TIME;
import static com.darelbitsy.dbweather.models.datatypes.weather.DatabaseConstant.HOUR_VISIBILITY;
import static com.darelbitsy.dbweather.models.datatypes.weather.DatabaseConstant.HOUR_WIND_BEARING;
import static com.darelbitsy.dbweather.models.datatypes.weather.DatabaseConstant.HOUR_WIND_SPEED;
import static com.darelbitsy.dbweather.models.datatypes.weather.DatabaseConstant.NOT_NULL;
import static com.darelbitsy.dbweather.models.datatypes.weather.DatabaseConstant.THE_CITY_COUNTRY;
import static com.darelbitsy.dbweather.models.datatypes.weather.DatabaseConstant.THE_CITY_LATITUDE;
import static com.darelbitsy.dbweather.models.datatypes.weather.DatabaseConstant.THE_CITY_LONGITUDE;
import static com.darelbitsy.dbweather.models.datatypes.weather.DatabaseConstant.THE_CITY_NAME;
import static com.darelbitsy.dbweather.extensions.holder.ConstantHolder.INTEGER_PRIMARY_KEY;

/**
 * Created by Darel Bitsy on 04/04/17.
 * User sqlite Helper to create and configure the user cities database
 */

public class UserCitiesDatabase extends SQLiteOpenHelper {
    private static final String DB_NAME = "user_city_list";
    private static final int VERSION = 1;

    public UserCitiesDatabase(final Context context) {
        super(context, DB_NAME, null, VERSION);
    }

    @Override
    public void onConfigure(final SQLiteDatabase db) {
        super.onConfigure(db);
        db.enableWriteAheadLogging();
    }

    @Override
    public void onCreate(final SQLiteDatabase db) {
        createCityTable(db);
        createCurrentWeatherTable(db);
        createDaysWeatherTable(db);
        createHoursWeatherTable(db);
    }

    @Override
    public void onUpgrade(final SQLiteDatabase db, final int oldVersion, final int newVersion) {

    }

    private void createCityTable(final SQLiteDatabase database) {
        database.execSQL(CREATE_TABLE_QUERY + CITIES_TABLE + " (" +
                COLUMN_ID + INTEGER_PRIMARY_KEY +
                THE_CITY_NAME + COLUMN_TEXT_TYPE + COLUMN_UNIQUE + NOT_NULL + COMMA +
                THE_CITY_COUNTRY + COLUMN_TEXT_TYPE + COMMA +
                THE_CITY_LATITUDE + COLUMN_REAL_TYPE + COLUMN_UNIQUE + COMMA +
                THE_CITY_LONGITUDE + COLUMN_REAL_TYPE + COLUMN_UNIQUE +
                ");"
        );
    }

    private void createCurrentWeatherTable(final SQLiteDatabase database) {
        database.execSQL(CREATE_TABLE_QUERY + CURRENT_TABLE_NAME + " (" +
                CURRENT_ID + INTEGER_PRIMARY_KEY +
                CITY_NAME + COLUMN_TEXT_TYPE + NOT_NULL  + COMMA +
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
                CURRENT_WIND_BEARING + COLUMN_INTEGER_TYPE + COMMA +
                String.format(FOREIGN_KEY,
                        CITY_NAME, CITIES_TABLE, THE_CITY_NAME) +
                ");"
        );
    }

    private void createDaysWeatherTable(final SQLiteDatabase database) {
        database.execSQL(CREATE_TABLE_QUERY + DAYS_TABLE_NAME + " (" +
                DAY_ID + INTEGER_PRIMARY_KEY +
                CITY_NAME + COLUMN_TEXT_TYPE + NOT_NULL + COMMA +
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
                DAY_OZONE + COLUMN_REAL_TYPE + COMMA +
                String.format(FOREIGN_KEY,
                        CITY_NAME, CITIES_TABLE, THE_CITY_NAME) +
                ");"
        );
    }

    private void createHoursWeatherTable(final SQLiteDatabase database) {
        database.execSQL(CREATE_TABLE_QUERY + HOURS_TABLE_NAME +" (" +
                HOUR_ID + INTEGER_PRIMARY_KEY +
                CITY_NAME + COLUMN_TEXT_TYPE + NOT_NULL + COMMA +
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
                HOUR_OZONE + COLUMN_REAL_TYPE + COMMA +
                String.format(FOREIGN_KEY,
                        CITY_NAME, CITIES_TABLE, THE_CITY_NAME) +
                ");"
        );
    }
}
