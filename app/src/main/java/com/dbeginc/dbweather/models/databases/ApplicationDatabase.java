package com.dbeginc.dbweather.models.databases;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;

import static com.dbeginc.dbweather.models.datatypes.weather.DatabaseConstant.ALERT_DESCRIPTION;
import static com.dbeginc.dbweather.models.datatypes.weather.DatabaseConstant.ALERT_EXPIRES;
import static com.dbeginc.dbweather.models.datatypes.weather.DatabaseConstant.ALERT_ID;
import static com.dbeginc.dbweather.models.datatypes.weather.DatabaseConstant.ALERT_TABLE;
import static com.dbeginc.dbweather.models.datatypes.weather.DatabaseConstant.ALERT_TIME;
import static com.dbeginc.dbweather.models.datatypes.weather.DatabaseConstant.ALERT_TITLE;
import static com.dbeginc.dbweather.models.datatypes.weather.DatabaseConstant.ALERT_URI;
import static com.dbeginc.dbweather.models.datatypes.weather.DatabaseConstant.APPLICATION_TABLE;
import static com.dbeginc.dbweather.models.datatypes.weather.DatabaseConstant.CITY_NAME;
import static com.dbeginc.dbweather.models.datatypes.weather.DatabaseConstant.COLUMN_INTEGER_TYPE;
import static com.dbeginc.dbweather.models.datatypes.weather.DatabaseConstant.COLUMN_REAL_TYPE;
import static com.dbeginc.dbweather.models.datatypes.weather.DatabaseConstant.COLUMN_TEXT_TYPE;
import static com.dbeginc.dbweather.models.datatypes.weather.DatabaseConstant.COMMA;
import static com.dbeginc.dbweather.models.datatypes.weather.DatabaseConstant.CREATE_TABLE_QUERY;
import static com.dbeginc.dbweather.models.datatypes.weather.DatabaseConstant.CURRENT_APPARENT_TEMPERATURE;
import static com.dbeginc.dbweather.models.datatypes.weather.DatabaseConstant.CURRENT_CLOUD_COVER;
import static com.dbeginc.dbweather.models.datatypes.weather.DatabaseConstant.CURRENT_HUMIDITY;
import static com.dbeginc.dbweather.models.datatypes.weather.DatabaseConstant.CURRENT_ICON;
import static com.dbeginc.dbweather.models.datatypes.weather.DatabaseConstant.CURRENT_PRECIPCHANCE;
import static com.dbeginc.dbweather.models.datatypes.weather.DatabaseConstant.CURRENT_PRECIPTYPE;
import static com.dbeginc.dbweather.models.datatypes.weather.DatabaseConstant.CURRENT_SUMMARY;
import static com.dbeginc.dbweather.models.datatypes.weather.DatabaseConstant.CURRENT_TABLE_NAME;
import static com.dbeginc.dbweather.models.datatypes.weather.DatabaseConstant.CURRENT_TEMPERATURE;
import static com.dbeginc.dbweather.models.datatypes.weather.DatabaseConstant.CURRENT_TIME;
import static com.dbeginc.dbweather.models.datatypes.weather.DatabaseConstant.CURRENT_WIND_BEARING;
import static com.dbeginc.dbweather.models.datatypes.weather.DatabaseConstant.CURRENT_WIND_SPEED;
import static com.dbeginc.dbweather.models.datatypes.weather.DatabaseConstant.DAYS_TABLE_NAME;
import static com.dbeginc.dbweather.models.datatypes.weather.DatabaseConstant.DAY_APPARENT_TEMPERATURE_MAX;
import static com.dbeginc.dbweather.models.datatypes.weather.DatabaseConstant.DAY_CLOUD_COVER;
import static com.dbeginc.dbweather.models.datatypes.weather.DatabaseConstant.DAY_DEW_POINT;
import static com.dbeginc.dbweather.models.datatypes.weather.DatabaseConstant.DAY_HUMIDITY;
import static com.dbeginc.dbweather.models.datatypes.weather.DatabaseConstant.DAY_ICON;
import static com.dbeginc.dbweather.models.datatypes.weather.DatabaseConstant.DAY_ID;
import static com.dbeginc.dbweather.models.datatypes.weather.DatabaseConstant.DAY_MOON_PHASE;
import static com.dbeginc.dbweather.models.datatypes.weather.DatabaseConstant.DAY_OZONE;
import static com.dbeginc.dbweather.models.datatypes.weather.DatabaseConstant.DAY_PRECIPCHANCE;
import static com.dbeginc.dbweather.models.datatypes.weather.DatabaseConstant.DAY_PRECIPTYPE;
import static com.dbeginc.dbweather.models.datatypes.weather.DatabaseConstant.DAY_PRESSURE;
import static com.dbeginc.dbweather.models.datatypes.weather.DatabaseConstant.DAY_SUMMARY;
import static com.dbeginc.dbweather.models.datatypes.weather.DatabaseConstant.DAY_SUNRISE_TIME;
import static com.dbeginc.dbweather.models.datatypes.weather.DatabaseConstant.DAY_SUNSET_TIME;
import static com.dbeginc.dbweather.models.datatypes.weather.DatabaseConstant.DAY_TEMPERATURE_MAX;
import static com.dbeginc.dbweather.models.datatypes.weather.DatabaseConstant.DAY_TIME;
import static com.dbeginc.dbweather.models.datatypes.weather.DatabaseConstant.DAY_VISIBILITY;
import static com.dbeginc.dbweather.models.datatypes.weather.DatabaseConstant.DAY_WIND_BEARING;
import static com.dbeginc.dbweather.models.datatypes.weather.DatabaseConstant.DAY_WIND_SPEED;
import static com.dbeginc.dbweather.models.datatypes.weather.DatabaseConstant.FULL_DAY_SUMMARY;
import static com.dbeginc.dbweather.models.datatypes.weather.DatabaseConstant.FULL_HOUR_SUMMARY;
import static com.dbeginc.dbweather.models.datatypes.weather.DatabaseConstant.HOURS_TABLE_NAME;
import static com.dbeginc.dbweather.models.datatypes.weather.DatabaseConstant.HOUR_APPARENT_TEMPERATURE;
import static com.dbeginc.dbweather.models.datatypes.weather.DatabaseConstant.HOUR_CLOUD_COVER;
import static com.dbeginc.dbweather.models.datatypes.weather.DatabaseConstant.HOUR_DEW_POINT;
import static com.dbeginc.dbweather.models.datatypes.weather.DatabaseConstant.HOUR_HUMIDITY;
import static com.dbeginc.dbweather.models.datatypes.weather.DatabaseConstant.HOUR_ICON;
import static com.dbeginc.dbweather.models.datatypes.weather.DatabaseConstant.HOUR_ID;
import static com.dbeginc.dbweather.models.datatypes.weather.DatabaseConstant.HOUR_OZONE;
import static com.dbeginc.dbweather.models.datatypes.weather.DatabaseConstant.HOUR_PRECIPCHANCE;
import static com.dbeginc.dbweather.models.datatypes.weather.DatabaseConstant.HOUR_PRECIPTYPE;
import static com.dbeginc.dbweather.models.datatypes.weather.DatabaseConstant.HOUR_PRESSURE;
import static com.dbeginc.dbweather.models.datatypes.weather.DatabaseConstant.HOUR_SUMMARY;
import static com.dbeginc.dbweather.models.datatypes.weather.DatabaseConstant.HOUR_TEMPERATURE;
import static com.dbeginc.dbweather.models.datatypes.weather.DatabaseConstant.HOUR_TIME;
import static com.dbeginc.dbweather.models.datatypes.weather.DatabaseConstant.HOUR_VISIBILITY;
import static com.dbeginc.dbweather.models.datatypes.weather.DatabaseConstant.HOUR_WIND_BEARING;
import static com.dbeginc.dbweather.models.datatypes.weather.DatabaseConstant.HOUR_WIND_SPEED;
import static com.dbeginc.dbweather.models.datatypes.weather.DatabaseConstant.LAST_KNOW_LATITUDE;
import static com.dbeginc.dbweather.models.datatypes.weather.DatabaseConstant.LAST_KNOW_LONGITUDE;
import static com.dbeginc.dbweather.models.datatypes.weather.DatabaseConstant.LAST_NEWS_SERVER_SYNC;
import static com.dbeginc.dbweather.models.datatypes.weather.DatabaseConstant.LAST_WEATHER_SERVER_SYNC;
import static com.dbeginc.dbweather.models.datatypes.weather.DatabaseConstant.LIVE_SOURCE_ID;
import static com.dbeginc.dbweather.models.datatypes.weather.DatabaseConstant.LIVE_SOURCE_NAME;
import static com.dbeginc.dbweather.models.datatypes.weather.DatabaseConstant.LIVE_SOURCE_TABLE;
import static com.dbeginc.dbweather.models.datatypes.weather.DatabaseConstant.LIVE_SOURCE_URL;
import static com.dbeginc.dbweather.models.datatypes.weather.DatabaseConstant.MINUTELY_ID;
import static com.dbeginc.dbweather.models.datatypes.weather.DatabaseConstant.MINUTELY_PRECIPCHANCE;
import static com.dbeginc.dbweather.models.datatypes.weather.DatabaseConstant.MINUTELY_PRECIPTYPE;
import static com.dbeginc.dbweather.models.datatypes.weather.DatabaseConstant.MINUTELY_TABLE;
import static com.dbeginc.dbweather.models.datatypes.weather.DatabaseConstant.MINUTELY_TIME;
import static com.dbeginc.dbweather.models.datatypes.weather.DatabaseConstant.NEWS_DESCRIPTION;
import static com.dbeginc.dbweather.models.datatypes.weather.DatabaseConstant.NEWS_ID;
import static com.dbeginc.dbweather.models.datatypes.weather.DatabaseConstant.NEWS_IMAGE_URL;
import static com.dbeginc.dbweather.models.datatypes.weather.DatabaseConstant.NEWS_PUBLISHED_AT;
import static com.dbeginc.dbweather.models.datatypes.weather.DatabaseConstant.NEWS_SOURCE;
import static com.dbeginc.dbweather.models.datatypes.weather.DatabaseConstant.NEWS_SOURCES_TABLE;
import static com.dbeginc.dbweather.models.datatypes.weather.DatabaseConstant.NEWS_SOURCE_COUNT;
import static com.dbeginc.dbweather.models.datatypes.weather.DatabaseConstant.NEWS_SOURCE_ID;
import static com.dbeginc.dbweather.models.datatypes.weather.DatabaseConstant.NEWS_SOURCE_NAME;
import static com.dbeginc.dbweather.models.datatypes.weather.DatabaseConstant.NEWS_SOURCE_STATUS;
import static com.dbeginc.dbweather.models.datatypes.weather.DatabaseConstant.NEWS_TABLE_NAME;
import static com.dbeginc.dbweather.models.datatypes.weather.DatabaseConstant.NEWS_TITLE;
import static com.dbeginc.dbweather.models.datatypes.weather.DatabaseConstant.NEWS_URL;
import static com.dbeginc.dbweather.models.datatypes.weather.DatabaseConstant.TEMPERATURE_UNIT;
import static com.dbeginc.dbweather.models.datatypes.weather.DatabaseConstant.TIMEZONE;
import static com.dbeginc.dbweather.models.datatypes.weather.DatabaseConstant.WEATHER_TABLE;
import static com.dbeginc.dbweather.models.datatypes.weather.DatabaseConstant.WEEK_SUMMARY;
import static com.dbeginc.dbweather.utils.holder.ConstantHolder.INTEGER_PRIMARY_KEY;

/**
 * Created by Darel Bitsy on 26/01/17.
 * This class create my application database
 * or upgrade it if needed
 */

public class ApplicationDatabase extends SQLiteOpenHelper {
    private static final int VERSION = 1;
    private static final String DB_NAME = "db_weather.sqlite";

    public ApplicationDatabase(final Context context) {
        super(context, DB_NAME, null, VERSION);
    }

    /**
     * Called when the database connection is being configured, to enable features
     * such as write-ahead logging or foreign key support.
     * <p>
     * This method is called before {@link #onCreate}, {@link #onUpgrade},
     * {@link #onDowngrade}, or {@link #onOpen} are called.  It should not modify
     * the database except to configure the database connection as required.
     * </p><p>
     * This method should only call methods that configure the parameters of the
     * database connection, such as {@link SQLiteDatabase#enableWriteAheadLogging}
     * {@link SQLiteDatabase#setForeignKeyConstraintsEnabled},
     * {@link SQLiteDatabase#setLocale}, {@link SQLiteDatabase#setMaximumSize},
     * or executing PRAGMA statements.
     * </p>
     *
     * @param db The database.
     */
    @Override
    public void onConfigure(final SQLiteDatabase db) {
        super.onConfigure(db);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) { db.enableWriteAheadLogging(); }
    }

    /**
     * Called when the database is created for the first time. This is where the
     * creation of tables and the initial population of the tables should happen.
     *
     * @param db The database.
     */
    @Override
    public void onCreate(final SQLiteDatabase db) {
        createApplicationTable(db);
        createWeatherTable(db);
        createAlertTable(db);
        createCurrentWeatherTable(db);
        createHoursWeatherTable(db);
        createDaysWeatherTable(db);
        createMinutelyTable(db);
        createNewsTable(db);
        createNewsSourceTable(db);
        createLiveSourceTable(db);
    }

    private void createApplicationTable(final SQLiteDatabase database) {
        database.execSQL(CREATE_TABLE_QUERY + APPLICATION_TABLE + " (" +
                LAST_WEATHER_SERVER_SYNC + COLUMN_TEXT_TYPE + COMMA +
                LAST_NEWS_SERVER_SYNC + COLUMN_TEXT_TYPE +
                ");"
        );
    }

    private void createWeatherTable(final SQLiteDatabase database) {
        database.execSQL(CREATE_TABLE_QUERY + WEATHER_TABLE + " (" +
                CITY_NAME + COLUMN_TEXT_TYPE + COMMA +
                TIMEZONE + COLUMN_TEXT_TYPE + COMMA +
                WEEK_SUMMARY + COLUMN_TEXT_TYPE + COMMA +
                FULL_DAY_SUMMARY + COLUMN_TEXT_TYPE + COMMA +
                FULL_HOUR_SUMMARY + COLUMN_TEXT_TYPE + COMMA +
                LAST_KNOW_LATITUDE  + COLUMN_REAL_TYPE + COMMA +
                LAST_KNOW_LONGITUDE + COLUMN_REAL_TYPE + COMMA +
                TEMPERATURE_UNIT + COLUMN_TEXT_TYPE +
                ");"
        );
    }

    private void createAlertTable(final SQLiteDatabase database) {
        database.execSQL(CREATE_TABLE_QUERY + ALERT_TABLE + " (" +
                ALERT_ID + INTEGER_PRIMARY_KEY +
                ALERT_TITLE + COLUMN_TEXT_TYPE + COMMA +
                ALERT_TIME + COLUMN_INTEGER_TYPE + COMMA +
                ALERT_EXPIRES + COLUMN_INTEGER_TYPE + COMMA +
                ALERT_DESCRIPTION + COLUMN_TEXT_TYPE + COMMA +
                ALERT_URI + COLUMN_TEXT_TYPE +
                ");");
    }

    private void createCurrentWeatherTable(final SQLiteDatabase database) {
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

    private void createDaysWeatherTable(final SQLiteDatabase database) {
        database.execSQL(CREATE_TABLE_QUERY + DAYS_TABLE_NAME + " (" +
                DAY_ID + INTEGER_PRIMARY_KEY +
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

    private void createHoursWeatherTable(final SQLiteDatabase database) {
        database.execSQL(CREATE_TABLE_QUERY + HOURS_TABLE_NAME +" (" +
                HOUR_ID + INTEGER_PRIMARY_KEY +
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

    private void createMinutelyTable(final SQLiteDatabase database) {
        database.execSQL(CREATE_TABLE_QUERY + MINUTELY_TABLE +  " (" +
                MINUTELY_ID + INTEGER_PRIMARY_KEY +
                MINUTELY_TIME + COLUMN_INTEGER_TYPE + COMMA +
                MINUTELY_PRECIPCHANCE + COLUMN_REAL_TYPE + COMMA +
                MINUTELY_PRECIPTYPE + COLUMN_TEXT_TYPE + ");"
        );
    }

    private void createNewsTable(final SQLiteDatabase database) {
        database.execSQL(CREATE_TABLE_QUERY + NEWS_TABLE_NAME + " (" +
                NEWS_ID + INTEGER_PRIMARY_KEY +
                NEWS_SOURCE + COLUMN_TEXT_TYPE + COMMA +
                NEWS_TITLE + COLUMN_TEXT_TYPE + COMMA +
                NEWS_URL + COLUMN_TEXT_TYPE + COMMA +
                NEWS_PUBLISHED_AT + COLUMN_TEXT_TYPE + COMMA +
                NEWS_DESCRIPTION + COLUMN_TEXT_TYPE + COMMA +
                NEWS_IMAGE_URL + COLUMN_TEXT_TYPE +
                ");"
        );
    }

    private void createNewsSourceTable(final SQLiteDatabase database) {
        database.execSQL(CREATE_TABLE_QUERY + NEWS_SOURCES_TABLE + " (" +
                NEWS_SOURCE_ID + INTEGER_PRIMARY_KEY +
                NEWS_SOURCE_NAME + COLUMN_TEXT_TYPE + COMMA +
                NEWS_SOURCE_STATUS + COLUMN_INTEGER_TYPE + COMMA +
                NEWS_SOURCE_COUNT + COLUMN_INTEGER_TYPE +
                ");"
        );
    }

    private void createLiveSourceTable(final SQLiteDatabase database) {
        database.execSQL(CREATE_TABLE_QUERY + LIVE_SOURCE_TABLE + " (" +
                LIVE_SOURCE_ID + INTEGER_PRIMARY_KEY +
                LIVE_SOURCE_NAME + COLUMN_TEXT_TYPE + COMMA +
                LIVE_SOURCE_URL + COLUMN_TEXT_TYPE +
                ");"
        );
    }

    @Override
    public void onUpgrade(final SQLiteDatabase db,final int oldVersion,final int newVersion) {
    }
}
