package com.darelbitsy.dbweather.extensions.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.util.Pair;

import com.darelbitsy.dbweather.extensions.holder.ConstantHolder;
import com.darelbitsy.dbweather.extensions.utility.weather.WeatherUtil;
import com.darelbitsy.dbweather.models.databases.ApplicationDatabase;
import com.darelbitsy.dbweather.models.databases.UserCitiesDatabase;
import com.darelbitsy.dbweather.models.datatypes.geonames.GeoName;
import com.darelbitsy.dbweather.models.datatypes.news.Article;
import com.darelbitsy.dbweather.models.datatypes.weather.Alert;
import com.darelbitsy.dbweather.models.datatypes.weather.Currently;
import com.darelbitsy.dbweather.models.datatypes.weather.Daily;
import com.darelbitsy.dbweather.models.datatypes.weather.DailyData;
import com.darelbitsy.dbweather.models.datatypes.weather.Hourly;
import com.darelbitsy.dbweather.models.datatypes.weather.HourlyData;
import com.darelbitsy.dbweather.models.datatypes.weather.MinutelyData;
import com.darelbitsy.dbweather.models.datatypes.weather.Weather;
import com.jakewharton.threetenabp.AndroidThreeTen;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import static com.darelbitsy.dbweather.models.datatypes.weather.DatabaseConstant.ALERT_DESCRIPTION;
import static com.darelbitsy.dbweather.models.datatypes.weather.DatabaseConstant.ALERT_EXPIRES;
import static com.darelbitsy.dbweather.models.datatypes.weather.DatabaseConstant.ALERT_ID;
import static com.darelbitsy.dbweather.models.datatypes.weather.DatabaseConstant.ALERT_TABLE;
import static com.darelbitsy.dbweather.models.datatypes.weather.DatabaseConstant.ALERT_TIME;
import static com.darelbitsy.dbweather.models.datatypes.weather.DatabaseConstant.ALERT_TITLE;
import static com.darelbitsy.dbweather.models.datatypes.weather.DatabaseConstant.ALERT_URI;
import static com.darelbitsy.dbweather.models.datatypes.weather.DatabaseConstant.APPLICATION_TABLE;
import static com.darelbitsy.dbweather.models.datatypes.weather.DatabaseConstant.CITIES_TABLE;
import static com.darelbitsy.dbweather.models.datatypes.weather.DatabaseConstant.CITY_NAME;
import static com.darelbitsy.dbweather.models.datatypes.weather.DatabaseConstant.CURRENT_APPARENT_TEMPERATURE;
import static com.darelbitsy.dbweather.models.datatypes.weather.DatabaseConstant.CURRENT_CLOUD_COVER;
import static com.darelbitsy.dbweather.models.datatypes.weather.DatabaseConstant.CURRENT_HUMIDITY;
import static com.darelbitsy.dbweather.models.datatypes.weather.DatabaseConstant.CURRENT_ICON;
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
import static com.darelbitsy.dbweather.models.datatypes.weather.DatabaseConstant.FROM;
import static com.darelbitsy.dbweather.models.datatypes.weather.DatabaseConstant.FULL_DAY_SUMMARY;
import static com.darelbitsy.dbweather.models.datatypes.weather.DatabaseConstant.FULL_HOUR_SUMMARY;
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
import static com.darelbitsy.dbweather.models.datatypes.weather.DatabaseConstant.LAST_KNOW_LATITUDE;
import static com.darelbitsy.dbweather.models.datatypes.weather.DatabaseConstant.LAST_KNOW_LONGITUDE;
import static com.darelbitsy.dbweather.models.datatypes.weather.DatabaseConstant.LAST_NEWS_SERVER_SYNC;
import static com.darelbitsy.dbweather.models.datatypes.weather.DatabaseConstant.LAST_WEATHER_SERVER_SYNC;
import static com.darelbitsy.dbweather.models.datatypes.weather.DatabaseConstant.MINUTELY_ID;
import static com.darelbitsy.dbweather.models.datatypes.weather.DatabaseConstant.MINUTELY_PRECIPCHANCE;
import static com.darelbitsy.dbweather.models.datatypes.weather.DatabaseConstant.MINUTELY_PRECIPTYPE;
import static com.darelbitsy.dbweather.models.datatypes.weather.DatabaseConstant.MINUTELY_TABLE;
import static com.darelbitsy.dbweather.models.datatypes.weather.DatabaseConstant.MINUTELY_TIME;
import static com.darelbitsy.dbweather.models.datatypes.weather.DatabaseConstant.NEWS_DESCRIPTION;
import static com.darelbitsy.dbweather.models.datatypes.weather.DatabaseConstant.NEWS_ID;
import static com.darelbitsy.dbweather.models.datatypes.weather.DatabaseConstant.NEWS_IMAGE_URL;
import static com.darelbitsy.dbweather.models.datatypes.weather.DatabaseConstant.NEWS_PUBLISHED_AT;
import static com.darelbitsy.dbweather.models.datatypes.weather.DatabaseConstant.NEWS_SOURCE;
import static com.darelbitsy.dbweather.models.datatypes.weather.DatabaseConstant.NEWS_SOURCES_TABLE;
import static com.darelbitsy.dbweather.models.datatypes.weather.DatabaseConstant.NEWS_SOURCE_COUNT;
import static com.darelbitsy.dbweather.models.datatypes.weather.DatabaseConstant.NEWS_SOURCE_NAME;
import static com.darelbitsy.dbweather.models.datatypes.weather.DatabaseConstant.NEWS_SOURCE_STATUS;
import static com.darelbitsy.dbweather.models.datatypes.weather.DatabaseConstant.NEWS_TABLE_NAME;
import static com.darelbitsy.dbweather.models.datatypes.weather.DatabaseConstant.NEWS_TITLE;
import static com.darelbitsy.dbweather.models.datatypes.weather.DatabaseConstant.NEWS_URL;
import static com.darelbitsy.dbweather.models.datatypes.weather.DatabaseConstant.ORDER_BY;
import static com.darelbitsy.dbweather.models.datatypes.weather.DatabaseConstant.SELECT;
import static com.darelbitsy.dbweather.models.datatypes.weather.DatabaseConstant.SELECT_EVERYTHING_FROM;
import static com.darelbitsy.dbweather.models.datatypes.weather.DatabaseConstant.THE_CITY_COUNTRY;
import static com.darelbitsy.dbweather.models.datatypes.weather.DatabaseConstant.THE_CITY_LATITUDE;
import static com.darelbitsy.dbweather.models.datatypes.weather.DatabaseConstant.THE_CITY_LONGITUDE;
import static com.darelbitsy.dbweather.models.datatypes.weather.DatabaseConstant.THE_CITY_NAME;
import static com.darelbitsy.dbweather.models.datatypes.weather.DatabaseConstant.TIMEZONE;
import static com.darelbitsy.dbweather.models.datatypes.weather.DatabaseConstant.WEATHER_TABLE;
import static com.darelbitsy.dbweather.models.datatypes.weather.DatabaseConstant.WEEK_SUMMARY;

/**
 * Created by Darel Bitsy on 26/01/17.
 * This class provide database operation
 * needed for the application to work
 */

public class DatabaseOperation {
    private final ApplicationDatabase applicationDatabase;
    private final UserCitiesDatabase userCitiesDatabase;

    private static DatabaseOperation singletonDatabaseOperation;

    public static DatabaseOperation newInstance(final Context context) {
        if (singletonDatabaseOperation == null) {
            singletonDatabaseOperation = new DatabaseOperation(context.getApplicationContext());
        }
        return singletonDatabaseOperation;
    }

    private DatabaseOperation(final Context context) {
        applicationDatabase = new ApplicationDatabase(context);
        userCitiesDatabase = new UserCitiesDatabase(context);

        //TODO:DB UNCOMMENT AFTER TESTING
        AndroidThreeTen.init(context);
    }

    /**
     * This method save the last time the weather
     * has been updated
     * I use it for debugging
     */
    void saveLastWeatherServerSync() {
        final SQLiteDatabase database = applicationDatabase.getWritableDatabase();
        final ContentValues contentValues = new ContentValues();
        contentValues.put(LAST_WEATHER_SERVER_SYNC, new Date().toString());
        final int result = database.update(APPLICATION_TABLE, contentValues, null, null);
        if (result == 0) {
            database.insert(APPLICATION_TABLE, null, contentValues);
        }
        contentValues.clear();
        database.close();
        Log.i(ConstantHolder.TAG, "Saving last weather server sync, result : "+ result);
    }

    /**
     *This method save the last time the newses
     * has been updated
     * I use it for debugging
     */
    public void saveLastNewsServerSync() {
        final SQLiteDatabase database = applicationDatabase.getWritableDatabase();
        final ContentValues contentValues = new ContentValues();
        contentValues.put(LAST_NEWS_SERVER_SYNC, new Date().toString());
        final int result = database.update(APPLICATION_TABLE, contentValues, null, null);
        if (result == 0) {
            database.insert(APPLICATION_TABLE, null, contentValues);
        }
        contentValues.clear();
        database.close();
    }

    /**
     * This method save useful information for the weather
     * to be used when needed
     * @param weather instance to get all information needed
     */
    public void saveWeatherData(final Weather weather) {
        final SQLiteDatabase database = applicationDatabase.getWritableDatabase();

        final ContentValues contentValues =  new ContentValues();
        contentValues.put(CITY_NAME, weather.getCityName());
        contentValues.put(TIMEZONE, weather.getTimezone());
        if (weather.getDaily() != null) {
            contentValues.put(WEEK_SUMMARY, weather.getDaily().getSummary());
        }
        contentValues.put(FULL_DAY_SUMMARY, weather.getHourly().getSummary());
        if (weather.getMinutely() != null) {
            contentValues.put(FULL_HOUR_SUMMARY, weather.getMinutely().getSummary());
        }
        contentValues.put(LAST_KNOW_LATITUDE, weather.getLatitude());
        contentValues.put(LAST_KNOW_LONGITUDE, weather.getLongitude());

        final int result = database.update(WEATHER_TABLE, contentValues, null, null);
        Log.i(ConstantHolder.TAG, "Saving weather data, result : "+ result);

        if (result == 0) {
            final long insert = database.insert(WEATHER_TABLE,
                    null,
                    contentValues);

            if ( insert == -1) {
                Log.i(ConstantHolder.TAG,
                        "Weather info On Weather Table Not Inserted");
            } else {
                Log.i(ConstantHolder.TAG,
                        "Weather info On Weather Table Inserted");
            }
        }

        contentValues.clear();
        database.close();
    }

    /**
     * This method save weather alerts
     * @param alerts list of alerts to be saved in the database
     */
    public void saveAlerts(final List<Alert> alerts) {
        final SQLiteDatabase database = applicationDatabase.getWritableDatabase();
        final ContentValues contentValues = new ContentValues();
        final AtomicInteger id = new AtomicInteger(1);

        for (Alert alert : alerts) {
            contentValues.put(ALERT_TITLE, alert.getTitle());
            contentValues.put(ALERT_TIME, alert.getTime());
            contentValues.put(ALERT_EXPIRES, alert.getExpires());
            contentValues.put(ALERT_DESCRIPTION, alert.getDescription());
            contentValues.put(ALERT_URI, alert.getUri());

            final int result = database.update(ALERT_TABLE, contentValues,
                    ALERT_ID + " = ?",
                    new String[] {Integer.toString(id.get())});

            Log.i(ConstantHolder.TAG, "Row " + result + " With " + alert + " On Alerts table");

            if (result == 0) {
                final long insert = database.insert(ALERT_TABLE, null, contentValues);

                if ( insert == -1) {
                    Log.i(ConstantHolder.TAG, id.get() + " With " + alert+ " On Alerts Table Not Inserted");

                } else {
                    Log.i(ConstantHolder.TAG, id.get() + " With "+ alert + " On Alerts Table Inserted");
                }
            }

            contentValues.clear();
            id.getAndIncrement();
        }
        database.close();
    }

    /**
     * Save the current weather data
     * to be displayed if the user open the app without
     * network
     * @param current it's the current weather info
     */
    public void saveCurrentWeather(final Currently current) {
        final ContentValues databaseInsert = new ContentValues();
        final SQLiteDatabase sqLiteDatabase = applicationDatabase.getWritableDatabase();

        databaseInsert.put(CURRENT_TIME, current.getTime());
        databaseInsert.put(CURRENT_SUMMARY, current.getSummary());
        databaseInsert.put(CURRENT_ICON, current.getIcon());
        databaseInsert.put(CURRENT_TEMPERATURE, current.getTemperature());
        databaseInsert.put(CURRENT_APPARENT_TEMPERATURE, current.getApparentTemperature());
        databaseInsert.put(CURRENT_HUMIDITY, current.getHumidity());
        databaseInsert.put(CURRENT_PRECIPCHANCE, current.getPrecipProbability());
        databaseInsert.put(CURRENT_PRECIPTYPE, current.getPrecipType());
        databaseInsert.put(CURRENT_CLOUD_COVER, current.getCloudCover());
        databaseInsert.put(CURRENT_WIND_SPEED, current.getWindSpeed());
        databaseInsert.put(CURRENT_WIND_BEARING, current.getWindBearing());
        Log.i(ConstantHolder.TAG, "To DB PROBABILITY: " + current.getPrecipProbability());

        final int result = sqLiteDatabase.update(CURRENT_TABLE_NAME,
                databaseInsert,
                null,
                null);

        if (result == 0) {
            final long insert = sqLiteDatabase.insert(CURRENT_TABLE_NAME, null, databaseInsert);
            if (insert == -1) {
                Log.i(ConstantHolder.TAG, "CURRENT TABLE NOT INSERTED");
            } else {
                Log.i(ConstantHolder.TAG, "CURRENT TABLE INSERTED");
            }

        } else {
            Log.i(ConstantHolder.TAG, "CURRENT TABLE UPDATED");
            Log.i(ConstantHolder.TAG, "Saving Current Weather, result : "+ result);
        }

        databaseInsert.clear();
        sqLiteDatabase.close();
    }

    /**
     * This method save the week weather info
     * @param dailyData list of daily weather
     */
    public void saveDailyWeather(final List<DailyData> dailyData) {
        final ContentValues databaseInsert = new ContentValues();
        final SQLiteDatabase sqLiteDatabase = applicationDatabase.getWritableDatabase();
        final AtomicInteger index = new AtomicInteger(1);

        for (final DailyData day : dailyData) {
            databaseInsert.put(DAY_TIME, day.getTime());
            databaseInsert.put(DAY_SUMMARY, day.getSummary());
            databaseInsert.put(DAY_ICON, day.getIcon());
            databaseInsert.put(DAY_SUNRISE_TIME, day.getSunriseTime());
            databaseInsert.put(DAY_SUNSET_TIME, day.getSunsetTime());
            databaseInsert.put(DAY_MOON_PHASE, day.getMoonPhase());
            databaseInsert.put(DAY_PRECIPCHANCE, day.getPrecipProbability());
            databaseInsert.put(DAY_PRECIPTYPE, day.getPrecipType());
            databaseInsert.put(DAY_TEMPERATURE_MAX, day.getTemperatureMax());
            databaseInsert.put(DAY_APPARENT_TEMPERATURE_MAX, day.getApparentTemperatureMax());
            databaseInsert.put(DAY_DEW_POINT, day.getDewPoint());
            databaseInsert.put(DAY_HUMIDITY, day.getHumidity());
            databaseInsert.put(DAY_WIND_SPEED, day.getWindSpeed());
            databaseInsert.put(DAY_WIND_BEARING, day.getWindBearing());
            databaseInsert.put(DAY_VISIBILITY, day.getVisibility());
            databaseInsert.put(DAY_CLOUD_COVER, day.getCloudCover());
            databaseInsert.put(DAY_PRESSURE, day.getPressure());
            databaseInsert.put(DAY_OZONE, day.getOzone());

            final int result = sqLiteDatabase.update(DAYS_TABLE_NAME,
                    databaseInsert,
                    DAY_ID+ " = ?",
                    new String[] {Integer.toString(index.get())});
            Log.i(ConstantHolder.TAG, "Row "+result+ " With " + day.toString() + " ON DAYS TABLE UPDATED");

            if (result == 0) {
                final long insert = sqLiteDatabase.insert(DAYS_TABLE_NAME, null, databaseInsert);
                databaseInsert.clear();

                if ( insert == -1) {
                    Log.i(ConstantHolder.TAG, index.get() + " With " + day.toString() + " ON DAYS TABLE NOT INSERTED");

                } else { Log.i(ConstantHolder.TAG, index.get() +" With " + day.toString() +  " ON DAYS TABLE INSERTED"); }
            }

            index.getAndIncrement();
            databaseInsert.clear();
        }
        sqLiteDatabase.close();
    }

    /**
     * This method save hourly weather data
     * @param data weather in hourly format
     */
    public void saveHourlyWeather(final List<HourlyData> data) {
        final ContentValues databaseInsert = new ContentValues();
        final SQLiteDatabase sqLiteDatabase = applicationDatabase.getWritableDatabase();
        final AtomicInteger index = new AtomicInteger(1);

        for (final HourlyData hourlyData : data) {
            databaseInsert.put(HOUR_TIME, hourlyData.getTime());
            databaseInsert.put(HOUR_SUMMARY, hourlyData.getSummary());
            databaseInsert.put(HOUR_ICON, hourlyData.getIcon());
            databaseInsert.put(HOUR_TEMPERATURE, hourlyData.getTemperature());
            databaseInsert.put(HOUR_APPARENT_TEMPERATURE, hourlyData.getApparentTemperature());
            databaseInsert.put(HOUR_HUMIDITY, hourlyData.getHumidity());
            databaseInsert.put(HOUR_PRECIPCHANCE, hourlyData.getPrecipProbability());
            databaseInsert.put(HOUR_PRECIPTYPE, hourlyData.getPrecipType());
            databaseInsert.put(HOUR_DEW_POINT, hourlyData.getDewPoint());
            databaseInsert.put(HOUR_WIND_SPEED, hourlyData.getWindSpeed());
            databaseInsert.put(HOUR_WIND_BEARING, hourlyData.getWindBearing());
            databaseInsert.put(HOUR_CLOUD_COVER, hourlyData.getCloudCover());
            databaseInsert.put(HOUR_VISIBILITY, hourlyData.getVisibility());
            databaseInsert.put(HOUR_PRESSURE, hourlyData.getPressure());
            databaseInsert.put(HOUR_OZONE, hourlyData.getOzone());

            final int row = sqLiteDatabase.update(HOURS_TABLE_NAME,
                    databaseInsert,
                    HOUR_ID + " = ?",
                    new String[] {Integer.toString(index.get())});

            Log.i(ConstantHolder.TAG, row + "ROW With " + hourlyData.toString() + " ON HOUR TABLE UPDATED");

            if (row == 0) {
                final long result = sqLiteDatabase.insert(HOURS_TABLE_NAME, null, databaseInsert);
                if (result == -1) { Log.i(ConstantHolder.TAG, index + " With " + hourlyData.toString() + " ON HOUR TABLE NOT INSERTED"); }
                else { Log.i(ConstantHolder.TAG, index.get() + " With " + hourlyData.toString() + " ON HOUR TABLE INSERTED"); }
            }

            databaseInsert.clear();
            index.getAndIncrement();
        }

        sqLiteDatabase.close();
    }

    public void saveMinutelyWeather(final List<MinutelyData> minutelyWeatherData) {
        final ContentValues contentValues = new ContentValues();
        final SQLiteDatabase sqLiteDatabase = applicationDatabase.getWritableDatabase();
        final AtomicInteger id = new AtomicInteger(1);

        for (final MinutelyData minutelyWeather : minutelyWeatherData) {
            contentValues.put(MINUTELY_TIME, minutelyWeather.getTime());
            contentValues.put(MINUTELY_PRECIPCHANCE, minutelyWeather.getPrecipProbability());
            contentValues.put(MINUTELY_PRECIPTYPE, minutelyWeather.getPrecipType());

            final int row = sqLiteDatabase.update(MINUTELY_TABLE,
                    contentValues,
                    MINUTELY_ID + " = ?",
                    new String[] {Integer.toString(id.get())});
            Log.i(ConstantHolder.TAG, row + " ROW With " + minutelyWeather + " On Minutely Table UPDATED");

            if (row == 0) {
                final long result = sqLiteDatabase.insert(MINUTELY_TABLE, null, contentValues);
                if (result == -1) { Log.i(ConstantHolder.TAG, id + " With " + minutelyWeather + " On Minutely Table Not Inserted"); }
                else { Log.i(ConstantHolder.TAG, id + " With " + minutelyWeather + " On Minutely Table Inserted"); }
            }

            contentValues.clear();
            id.getAndIncrement();
        }
        sqLiteDatabase.close();
    }

    /**
     * This method save latest news received by the server
     * to be displayed ofline
     * @param newses array of newses
     */
    public void saveNewses(final List<Article> newses) {
        final ContentValues databaseInsert = new ContentValues();
        final SQLiteDatabase sqLiteDatabase = applicationDatabase.getWritableDatabase();
        final AtomicInteger index = new AtomicInteger(1);

        for (final Article news : newses) {
            databaseInsert.put(NEWS_SOURCE, news.getAuthor());
            databaseInsert.put(NEWS_TITLE, news.getTitle());

            databaseInsert.put(NEWS_URL, news.getArticleUrl());
            databaseInsert.put(NEWS_IMAGE_URL, news.getUrlToImage());

            databaseInsert.put(NEWS_PUBLISHED_AT, news.getPublishedAt());
            databaseInsert.put(NEWS_DESCRIPTION, news.getDescription());

            final int result = sqLiteDatabase.update(NEWS_TABLE_NAME,
                    databaseInsert,
                    NEWS_ID + " = ?",
                    new  String[] {Integer.toString(index.get())} );
            Log.i(ConstantHolder.TAG, "ROW "+ result+ " with " + news.toString() + " ON NEWS TABLE UPDATED");

            if (result == 0) {
                final long insert = sqLiteDatabase.insert(NEWS_TABLE_NAME, null, databaseInsert);
                if (insert == -1) {
                    Log.i(ConstantHolder.TAG, index.get() + " with " + news.toString() + " ON NEWS TABLE NOT INSERTED");
                } else { Log.i(ConstantHolder.TAG, index.get() + " with " + news.toString() + " ON NEWS TABLE INSERTED"); }
            }

            databaseInsert.clear();
            index.getAndIncrement();
        }
        sqLiteDatabase.close();
    }

    /**
     * Get global weather info
     * from database
     * @return Weather object
     */
    public Weather getWeatherData() {
        final Weather weather = new Weather();
        final Cursor databaseCursor = applicationDatabase.getReadableDatabase().rawQuery(SELECT_EVERYTHING_FROM
                + WEATHER_TABLE, null);
        databaseCursor.moveToFirst();

        if (!databaseCursor.isAfterLast()) {
            weather.setCityName(databaseCursor.getString(databaseCursor.getColumnIndex(CITY_NAME)));
            weather.setTimezone(databaseCursor.getString(databaseCursor.getColumnIndex(TIMEZONE)));
            weather.setLatitude(databaseCursor.getDouble(databaseCursor.getColumnIndex(LAST_KNOW_LATITUDE)));
            weather.setLongitude(databaseCursor.getDouble(databaseCursor.getColumnIndex(LAST_KNOW_LONGITUDE)));
            weather.setHourly(new Hourly());
            weather.getHourly().setSummary(databaseCursor.getString(databaseCursor.getColumnIndex(FULL_DAY_SUMMARY)));

            weather.setDaily(new Daily());
            weather.getDaily().setSummary(databaseCursor.getString(databaseCursor.getColumnIndex(WEEK_SUMMARY)));

        }
        databaseCursor.close();
        return weather;
    }

    /**
     * Get alerts from the database
     * @return list of weather alerts
     */
    public List<Alert> getAlerts() {
        final List<Alert> alerts = new ArrayList<>();
        final Cursor databaseCursor = applicationDatabase.getReadableDatabase().rawQuery(SELECT_EVERYTHING_FROM +
                ALERT_TABLE +
                ORDER_BY +
                ALERT_ID + " ASC", null);

        if (databaseCursor != null) {
            for (databaseCursor.moveToFirst(); !databaseCursor.isAfterLast(); databaseCursor.moveToNext()) {
                final Alert alert = new Alert();
                alert.setTitle(databaseCursor.getString(databaseCursor.getColumnIndex(ALERT_TITLE)));
                alert.setTime(databaseCursor.getLong(databaseCursor.getColumnIndex(ALERT_TIME)));
                alert.setExpires(databaseCursor.getLong(databaseCursor.getColumnIndex(ALERT_EXPIRES)));
                alert.setDescription(databaseCursor.getString(databaseCursor.getColumnIndex(ALERT_DESCRIPTION)));
                alert.setUri(databaseCursor.getString(databaseCursor.getColumnIndex(ALERT_URI)));
                alerts.add(alert);
            }
            databaseCursor.close();
        }
        return alerts;
    }

    /**
     * Get last saved weather from database
     * @return currently weather object
     */
    public Currently getCurrentWeatherFromDatabase() {
        final Currently current = new Currently();
        final Cursor databaseCursor = applicationDatabase.getReadableDatabase().rawQuery("SELECT * FROM " +
                CURRENT_TABLE_NAME, null);
        databaseCursor.moveToFirst();

        if(!databaseCursor.isAfterLast()) {
            current.setTime(databaseCursor.getLong(databaseCursor.getColumnIndex(CURRENT_TIME)));
            current.setSummary(databaseCursor.getString(databaseCursor.getColumnIndex(CURRENT_SUMMARY)));
            current.setIcon(databaseCursor.getString(databaseCursor.getColumnIndex(CURRENT_ICON)));
            current.setTemperature(databaseCursor.getDouble(databaseCursor.getColumnIndex(CURRENT_TEMPERATURE)));
            current.setApparentTemperature(databaseCursor.getDouble(databaseCursor.getColumnIndex(CURRENT_APPARENT_TEMPERATURE)));
            current.setHumidity(databaseCursor.getDouble(databaseCursor.getColumnIndex(CURRENT_HUMIDITY)));
            current.setPrecipProbability(databaseCursor.getDouble(databaseCursor.getColumnIndex(CURRENT_PRECIPCHANCE)));
            current.setPrecipType(databaseCursor.getString(databaseCursor.getColumnIndex(CURRENT_PRECIPTYPE)));
            current.setCloudCover(databaseCursor.getDouble(databaseCursor.getColumnIndex(CURRENT_CLOUD_COVER)));
            current.setWindSpeed(databaseCursor.getDouble(databaseCursor.getColumnIndex(CURRENT_WIND_SPEED)));
            current.setWindBearing(databaseCursor.getLong(databaseCursor.getColumnIndex(CURRENT_WIND_BEARING)));
        }
        databaseCursor.close();
        return current;
    }

    public void saveCoordinates(final double latitude, final double longitude) {
        final ContentValues contentValues = new ContentValues();
        final SQLiteDatabase database = applicationDatabase.getWritableDatabase();

        contentValues.put(LAST_KNOW_LATITUDE, latitude);
        contentValues.put(LAST_KNOW_LONGITUDE, longitude);

        final int result = database.update(WEATHER_TABLE,
                        contentValues,
                        null,
                        null);
        Log.i(ConstantHolder.TAG, "Saving Coordinates, result : "+ result);
        database.close();
    }

    /**
     * Get last know latitude and longitude
     * from database
     * @return array doubles containing latitude and longitude
     */
    public Double[] getCoordinates() {
        final Double[] coordinates = new Double[2];

        final Cursor databaseCursor = applicationDatabase.getReadableDatabase().rawQuery(SELECT +
                LAST_KNOW_LATITUDE +
                ", " +LAST_KNOW_LONGITUDE +
                FROM + WEATHER_TABLE , null);

        if (databaseCursor != null) {
            databaseCursor.moveToFirst();
            if (!databaseCursor.isAfterLast()) {
                coordinates[0] = databaseCursor.getDouble(databaseCursor.getColumnIndex(LAST_KNOW_LATITUDE));
                coordinates[1] = databaseCursor.getDouble(databaseCursor.getColumnIndex(LAST_KNOW_LONGITUDE));
                databaseCursor.close();
                return coordinates;
            }
        }
        coordinates[0] = 34.0549;
        coordinates[1] = -118.2445;
        return coordinates;
    }

    /**
     * Get last know location where the user was
     * @return String in format of City, Country
     */
    public String getLastKnowLocation() {
        String cityName = "--";
        final Cursor databaseCursor  = applicationDatabase.getReadableDatabase()
                .rawQuery(SELECT +
                        CITY_NAME +
                        FROM +
                WEATHER_TABLE, null);

        if (databaseCursor != null) {
            databaseCursor.moveToFirst();

            if (!databaseCursor.isAfterLast()) {
                cityName = databaseCursor.getString(databaseCursor.getColumnIndex(CITY_NAME));

            }
            databaseCursor.close();
        }
        return cityName;
    }


    /**
     * Get week weather from the database
     * @return List of daily weather data
     */
    public List<DailyData> getDailyWeatherFromDatabase() {
        final List<DailyData> days = new ArrayList<>();

        final Cursor databaseCursor = applicationDatabase.getReadableDatabase().rawQuery("SELECT * FROM " +
                DAYS_TABLE_NAME +
                ORDER_BY +
                DAY_ID+" ASC", null);

        if(databaseCursor != null) {
            final AtomicInteger index = new AtomicInteger(0);

            for (databaseCursor.moveToFirst(); !databaseCursor.isAfterLast(); databaseCursor.moveToNext()) {
                final DailyData day = new DailyData();
                day.setTime(databaseCursor.getLong(databaseCursor.getColumnIndex(DAY_TIME)));
                day.setSummary(databaseCursor.getString(databaseCursor.getColumnIndex(DAY_SUMMARY)));
                day.setIcon(databaseCursor.getString(databaseCursor.getColumnIndex(DAY_ICON)));
                day.setSunriseTime(databaseCursor.getLong(databaseCursor.getColumnIndex(DAY_SUNRISE_TIME)));
                day.setSunsetTime(databaseCursor.getLong(databaseCursor.getColumnIndex(DAY_SUNSET_TIME)));
                day.setMoonPhase(databaseCursor.getDouble(databaseCursor.getColumnIndex(DAY_MOON_PHASE)));
                day.setPrecipProbability(databaseCursor.getDouble(databaseCursor.getColumnIndex(DAY_PRECIPCHANCE)));
                day.setPrecipType(databaseCursor.getString(databaseCursor.getColumnIndex(DAY_PRECIPTYPE)));
                day.setTemperatureMax(databaseCursor.getDouble(databaseCursor.getColumnIndex(DAY_TEMPERATURE_MAX)));
                day.setApparentTemperatureMax(databaseCursor.getDouble(databaseCursor.getColumnIndex(DAY_APPARENT_TEMPERATURE_MAX)));
                day.setDewPoint(databaseCursor.getDouble(databaseCursor.getColumnIndex(DAY_DEW_POINT)));
                day.setHumidity(databaseCursor.getDouble(databaseCursor.getColumnIndex(DAY_HUMIDITY)));
                day.setWindSpeed(databaseCursor.getDouble(databaseCursor.getColumnIndex(DAY_WIND_SPEED)));
                day.setWindBearing(databaseCursor.getLong(databaseCursor.getColumnIndex(DAY_WIND_BEARING)));
                day.setVisibility(databaseCursor.getLong(databaseCursor.getColumnIndex(DAY_VISIBILITY)));
                day.setCloudCover(databaseCursor.getDouble(databaseCursor.getColumnIndex(DAY_CLOUD_COVER)));
                day.setPressure(databaseCursor.getDouble(databaseCursor.getColumnIndex(DAY_PRESSURE)));
                day.setOzone(databaseCursor.getDouble(databaseCursor.getColumnIndex(DAY_OZONE)));

                Log.i(ConstantHolder.TAG, "getDailyWeatherFromDatabase: On " + index.get());
                Log.i(ConstantHolder.TAG, day.toString() + ": On " + index.get());
                days.add(day);
            }
            databaseCursor.close();
        }
        return days;
    }

    /**
     * Get hourly weather data from database
     * @return List of hourly weather
     */
    public List<HourlyData> getHourlyWeatherFromDatabase() {
        final List<HourlyData> hourlyData = new ArrayList<>();

        final Cursor databaseCursor = applicationDatabase.getReadableDatabase().rawQuery(SELECT_EVERYTHING_FROM
                + HOURS_TABLE_NAME
                + ORDER_BY
                + HOUR_ID +" ASC", null);

        if(databaseCursor != null) {
            final AtomicInteger index = new AtomicInteger(0);

            for (databaseCursor.moveToFirst(); !databaseCursor.isAfterLast(); databaseCursor.moveToNext()) {
                final HourlyData hour = new HourlyData();
                hour.setTime(databaseCursor.getLong(databaseCursor.getColumnIndex(HOUR_TIME)));
                hour.setIcon(databaseCursor.getString(databaseCursor.getColumnIndex(HOUR_ICON)));
                hour.setSummary(databaseCursor.getString(databaseCursor.getColumnIndex(HOUR_SUMMARY)));
                hour.setTemperature(databaseCursor.getDouble(databaseCursor.getColumnIndex(HOUR_TEMPERATURE)));
                hour.setApparentTemperature(databaseCursor.getDouble(databaseCursor.getColumnIndex(HOUR_APPARENT_TEMPERATURE)));
                hour.setHumidity(databaseCursor.getDouble(databaseCursor.getColumnIndex(HOUR_HUMIDITY)));
                hour.setPrecipProbability(databaseCursor.getDouble(databaseCursor.getColumnIndex(HOUR_PRECIPCHANCE)));
                hour.setPrecipType(databaseCursor.getString(databaseCursor.getColumnIndex(HOUR_PRECIPTYPE)));
                hour.setDewPoint(databaseCursor.getDouble(databaseCursor.getColumnIndex(HOUR_DEW_POINT)));
                hour.setWindSpeed(databaseCursor.getDouble(databaseCursor.getColumnIndex(HOUR_WIND_SPEED)));
                hour.setWindBearing(databaseCursor.getLong(databaseCursor.getColumnIndex(HOUR_WIND_BEARING)));
                hour.setCloudCover(databaseCursor.getDouble(databaseCursor.getColumnIndex(HOUR_CLOUD_COVER)));
                hour.setVisibility(databaseCursor.getDouble(databaseCursor.getColumnIndex(HOUR_VISIBILITY)));
                hour.setPressure(databaseCursor.getDouble(databaseCursor.getColumnIndex(HOUR_PRESSURE)));
                hour.setOzone(databaseCursor.getDouble(databaseCursor.getColumnIndex(HOUR_OZONE)));

                Log.i(ConstantHolder.TAG, "getHourlyWeatherFromDatabase: On " + index.get());
                Log.i(ConstantHolder.TAG, hour.toString() + ": On " + index.get());
                hourlyData.add(hour);
            }
            databaseCursor.close();
        }
        return hourlyData;
    }

    /**
     * This method get all the news
     * from the database and return it
     * @return list of newses
     */
    public ArrayList<Article> getNewFromDatabase() {
        final ArrayList<Article> newses = new ArrayList<>();

        final Cursor databaseCursor = applicationDatabase.getReadableDatabase().rawQuery(SELECT_EVERYTHING_FROM +
                NEWS_TABLE_NAME +
                ORDER_BY +
                NEWS_ID + " ASC", null);

        if (databaseCursor != null) {
            final AtomicInteger index = new AtomicInteger(0);
            for (databaseCursor.moveToFirst(); !databaseCursor.isAfterLast(); databaseCursor.moveToNext()) {
                final Article news = new Article();
                news.setAuthor(databaseCursor.getString(databaseCursor.getColumnIndex(NEWS_SOURCE)));
                news.setTitle(databaseCursor.getString(databaseCursor.getColumnIndex(NEWS_TITLE)));
                news.setPublishedAt(databaseCursor.getString(databaseCursor.getColumnIndex(NEWS_PUBLISHED_AT)));
                news.setDescription(databaseCursor.getString(databaseCursor.getColumnIndex(NEWS_DESCRIPTION)));
                news.setUrlToImage(databaseCursor.getString(databaseCursor.getColumnIndex(NEWS_IMAGE_URL)));

                try {
                    news.setArticleUrl(databaseCursor.getString(databaseCursor.getColumnIndex(NEWS_URL)));

                } catch (final MalformedURLException e) { Log.i(ConstantHolder.TAG, "Error: "+ e.getMessage()); }

                Log.i(ConstantHolder.TAG, "getNewsFromDatabase: On " + index.get());
                Log.i(ConstantHolder.TAG, news.toString() + ": On " + index.get());
                newses.add(news);
            }
            databaseCursor.close();
        }
        return newses;
    }


    /**
     *this function get weather for the alarm manager service
     * if network not available
     * @param timeInMilliseconds time when the alarm manager asked weather data
     * @param timeZone timezone of the user location
     * @return HourlyData object
     */
    public HourlyData getNotificationHour(final long timeInMilliseconds, final String timeZone) {
        final HourlyData hour = new HourlyData();
        final Cursor cursor = applicationDatabase.getReadableDatabase().rawQuery(SELECT +
                HOUR_TIME + ", " +
                HOUR_TEMPERATURE + ", " +
                HOUR_ICON + ", " +
                HOUR_SUMMARY+
                FROM +
                HOURS_TABLE_NAME, null);

        if(cursor != null) {

            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                final long hourTime = cursor.getLong(cursor.getColumnIndex(HOUR_TIME));

                if (WeatherUtil.getFormattedTime(timeInMilliseconds, timeZone)
                        .equals(WeatherUtil.getFormattedTime(hourTime, timeZone))) {

                    hour.setTime(hourTime);
                    hour.setTemperature(cursor.getDouble(cursor.getColumnIndex(HOUR_TEMPERATURE)));
                    hour.setIcon(cursor.getString(cursor.getColumnIndex(HOUR_ICON)));
                    hour.setSummary(cursor.getString(cursor.getColumnIndex(HOUR_SUMMARY)));
                    break;
                }
            }
            cursor.close();
        }
        return hour;
    }

    public void removeLocationFromDatabase(final GeoName location) {
        final SQLiteDatabase writableDatabase = userCitiesDatabase.getWritableDatabase();
        final int result = writableDatabase.delete(CITIES_TABLE,
                THE_CITY_NAME + "=?",
                new String[]{location.getName()});

        if (result == -1) {
            Log.i(ConstantHolder.TAG, "CITY " + location.getName() + " NOT REMOVED");
        } else {
            Log.i(ConstantHolder.TAG, "CITY " + location.getName() + " WAS REMOVED");
        }
        writableDatabase.close();
    }

    public void addLocationToDatabase(final GeoName location) {
        final SQLiteDatabase writableDatabase = userCitiesDatabase.getWritableDatabase();
        final ContentValues dataToInsert = new ContentValues();

        dataToInsert.put(THE_CITY_NAME, location.getName());
        dataToInsert.put(THE_CITY_COUNTRY, location.getCountryName());
        dataToInsert.put(THE_CITY_LATITUDE, location.getLatitude());
        dataToInsert.put(THE_CITY_LONGITUDE, location.getLongitude());

        final long insertResult = writableDatabase.insert(CITIES_TABLE, null, dataToInsert);
        dataToInsert.clear();
        if (insertResult == -1) {
            Log.i(ConstantHolder.TAG, "CITY " + location.getName() + " NOT INSERTED");
        } else {
            Log.i(ConstantHolder.TAG, "CITY " + location.getName() + " TABLE INSERTED");
        }
        writableDatabase.close();
    }

    public List<GeoName> getUserCitiesFromDatabase() {
        final List<GeoName> geoNameList = new ArrayList<>();

        final Cursor cursor = userCitiesDatabase.getReadableDatabase()
                .rawQuery(SELECT_EVERYTHING_FROM + CITIES_TABLE, null);

        if (cursor != null) {
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                final GeoName location = new GeoName();
                location.setName(cursor.getString(cursor.getColumnIndex(THE_CITY_NAME)));
                location.setCountryName(cursor.getString(cursor.getColumnIndex(THE_CITY_COUNTRY)));
                location.setLatitude(cursor.getDouble(cursor.getColumnIndex(THE_CITY_LATITUDE)));
                location.setLongitude(cursor.getDouble(cursor.getColumnIndex(THE_CITY_LONGITUDE)));
                geoNameList.add(location);
            }
            cursor.close();
        }
        return geoNameList;
    }

    public void initiateNewsSourcesTable() {
        final SQLiteDatabase writableDatabase = applicationDatabase.getWritableDatabase();
        final ContentValues dataToInsert = new ContentValues();
        final String[] default_list_of_sources = {
                "ars-technica",
                "bbc-news",
                "cnbc",
                "cnn",
                "daily-mail",
                "espn",
                "four-four-two",
                "google-news",
                "fox-sports",
                "mtv-news",
                "new-scientist",
        };

        final String[] list_of_sources = {
                "abc-news-au",
                "al-jazeera-english",
                "associated-press",
                "bbc-sport",
                "bild",
                "bloomberg",
                "breitbart-news",
                "business-insider",
                "business-insider-uk",
                "buzzfeed",
                "der-tagesspiegel",
                "die-zeit",
                "engadget",
                "entertainment-weekly",
                "espn-cric-info",
                "financial-times",
                "focus",
                "football-italia",
                "fortune",
                "gruenderszene",
                "hacker-news",
                "handelsblatt",
                "ign",
                "independent",
                "mashable",
                "metro",
                "mirror",
                "mtv-news-uk",
                "national-geographic",
                "the-new-york-times",
                "newsweek",
                "new-york-magazine",
                "nfl-news",
                "polygon",
                "recode",
                "reddit-r-all",
                "reuters",
                "sky-news",
                "sky-sports-news",
                "spiegel-online",
                "t3n",
                "talksport",
                "techcrunch",
                "techradar",
                "the-economist",
                "the-guardian-au",
                "the-guardian-uk",
                "the-hindu",
                "the-huffington-post",
                "the-lad-bible",
                "the-new-york-times",
                "the-next-web",
                "the-sport-bible",
                "the-telegraph",
                "the-times-of-india",
                "the-verge",
                "the-washington-post",
                "time",
                "the-wall-street-journal",
                "usa-today",
                "wired-de",
                "wirtschafts-woche"
        };

        for (final String source : default_list_of_sources) {
            dataToInsert.put(NEWS_SOURCE_NAME, source);
            dataToInsert.put(NEWS_SOURCE_COUNT, 2);

            dataToInsert.put(NEWS_SOURCE_STATUS, 1);

            final long result = writableDatabase.insert(NEWS_SOURCES_TABLE, null, dataToInsert);
            dataToInsert.clear();

            if (result == -1) {
                Log.i(ConstantHolder.TAG, source + " ON NEWS TABLE NOT INSERTED");
            } else {
                Log.i(ConstantHolder.TAG, source + " ON NEWS TABLE INSERTED");
            }
        }

        for (final String source : list_of_sources) {
            dataToInsert.put(NEWS_SOURCE_NAME, source);
            dataToInsert.put(NEWS_SOURCE_COUNT, 2);

            dataToInsert.put(NEWS_SOURCE_STATUS, 0);

            final long result = writableDatabase.insert(NEWS_SOURCES_TABLE, null, dataToInsert);
            dataToInsert.clear();

            if (result == -1) {
                Log.i(ConstantHolder.TAG, source + " ON NEWS TABLE NOT INSERTED");
            } else {
                Log.i(ConstantHolder.TAG, source + " ON NEWS TABLE INSERTED");
            }
        }
        writableDatabase.close();
    }

    public void saveNewsSourceConfiguration(final String nameOfTheSource,
                                            final int count,
                                            final int isOn) {

        final SQLiteDatabase writableDatabase = applicationDatabase.getWritableDatabase();
        final ContentValues dataToInsert = new ContentValues();
        dataToInsert.put(NEWS_SOURCE_NAME, nameOfTheSource);
        dataToInsert.put(NEWS_SOURCE_COUNT, count);
        dataToInsert.put(NEWS_SOURCE_STATUS, isOn);
        writableDatabase.update(NEWS_SOURCES_TABLE, dataToInsert,
                String.format(Locale.getDefault(), "%s=\"%s\"", NEWS_SOURCE_NAME, nameOfTheSource), null);

        writableDatabase.close();
    }

    public Map<String, Pair<Integer, Integer>> getNewsSources() {
        final Map<String, Pair<Integer, Integer>> listOfSources = new ConcurrentHashMap<>();
        final Cursor queryResult = applicationDatabase.getReadableDatabase()
                .rawQuery(SELECT_EVERYTHING_FROM + NEWS_SOURCES_TABLE, null);

        if (queryResult != null) {
            for (queryResult.moveToFirst(); !queryResult.isAfterLast(); queryResult.moveToNext()) {
                listOfSources.put(queryResult.getString(queryResult.getColumnIndex(NEWS_SOURCE_NAME)),
                        new Pair<>(queryResult.getInt(queryResult.getColumnIndex(NEWS_SOURCE_COUNT)),
                                queryResult.getInt(queryResult.getColumnIndex(NEWS_SOURCE_STATUS))));
            }
            queryResult.close();
        }
        return listOfSources;
    }

    public Map<String, Integer> getActiveNewsSources() {
        final Map<String, Integer> listOfNewses = new ConcurrentHashMap<>();
        final Cursor queryResult = applicationDatabase.getReadableDatabase()
                .rawQuery(SELECT_EVERYTHING_FROM + NEWS_SOURCES_TABLE
                        + " WHERE " + NEWS_SOURCE_STATUS + "=1", null);

        if (queryResult != null) {
            for(queryResult.moveToFirst(); !queryResult.isAfterLast(); queryResult.moveToNext()) {
                listOfNewses.put(queryResult.getString(queryResult.getColumnIndex(NEWS_SOURCE_NAME)),
                        queryResult.getInt(queryResult.getColumnIndex(NEWS_SOURCE_COUNT)));
            }

            queryResult.close();
        }

        return listOfNewses;
    }

    public void saveCurrentWeatherForCity(final String cityName, final Currently currently) {
        final SQLiteDatabase writableDatabase = userCitiesDatabase.getWritableDatabase();
        final Cursor query = userCitiesDatabase.getReadableDatabase().rawQuery(String.format("SELECT %s FROM %s WHERE %s=%s",
                CITY_NAME, CURRENT_TABLE_NAME, CITY_NAME, cityName), null);

        final ContentValues databaseInsert = new ContentValues();

        databaseInsert.put(CITY_NAME, cityName);
        databaseInsert.put(CURRENT_TIME, currently.getTime());
        databaseInsert.put(CURRENT_SUMMARY, currently.getSummary());
        databaseInsert.put(CURRENT_ICON, currently.getIcon());
        databaseInsert.put(CURRENT_TEMPERATURE, currently.getTemperature());
        databaseInsert.put(CURRENT_APPARENT_TEMPERATURE, currently.getApparentTemperature());
        databaseInsert.put(CURRENT_HUMIDITY, currently.getHumidity());
        databaseInsert.put(CURRENT_PRECIPCHANCE, currently.getPrecipProbability());
        databaseInsert.put(CURRENT_PRECIPTYPE, currently.getPrecipType());
        databaseInsert.put(CURRENT_CLOUD_COVER, currently.getCloudCover());
        databaseInsert.put(CURRENT_WIND_SPEED, currently.getWindSpeed());
        databaseInsert.put(CURRENT_WIND_BEARING, currently.getWindBearing());

        if (query != null && query.moveToFirst()) {
            if (query.getString(query.getColumnIndex(CITY_NAME)).equalsIgnoreCase(cityName)) {

                final int result = writableDatabase.update(CURRENT_TABLE_NAME,
                        databaseInsert,
                        null,
                        null);

                databaseInsert.clear();
                Log.i(ConstantHolder.TAG, String.format("Saving Current Weather For : %s, result : %d", cityName, result));

            } else {
                final long result = writableDatabase.insert(CURRENT_TABLE_NAME, null, databaseInsert);
                databaseInsert.clear();

                if (result == -1) {
                    Log.i(ConstantHolder.TAG, String.format("CURRENT TABLE NOT INSERTED FOR %s", cityName));
                } else {
                    Log.i(ConstantHolder.TAG, String.format("CURRENT TABLE INSERTED FOR %s", cityName));
                }
            }
            query.close();
        }

        writableDatabase.close();
    }

    public void saveDailyWeatherForCity(final String cityName, final List<DailyData> dailyData) {
        final ContentValues databaseInsert = new ContentValues();
        final SQLiteDatabase writableDatabase = userCitiesDatabase.getWritableDatabase();
        final AtomicInteger index = new AtomicInteger(1);

        for (final DailyData day : dailyData) {
            databaseInsert.put(CITY_NAME, cityName);
            databaseInsert.put(DAY_TIME, day.getTime());
            databaseInsert.put(DAY_SUMMARY, day.getSummary());
            databaseInsert.put(DAY_ICON, day.getIcon());
            databaseInsert.put(DAY_SUNRISE_TIME, day.getSunriseTime());
            databaseInsert.put(DAY_SUNSET_TIME, day.getSunsetTime());
            databaseInsert.put(DAY_MOON_PHASE, day.getMoonPhase());
            databaseInsert.put(DAY_PRECIPCHANCE, day.getPrecipProbability());
            databaseInsert.put(DAY_PRECIPTYPE, day.getPrecipType());
            databaseInsert.put(DAY_TEMPERATURE_MAX, day.getTemperatureMax());
            databaseInsert.put(DAY_APPARENT_TEMPERATURE_MAX, day.getApparentTemperatureMax());
            databaseInsert.put(DAY_DEW_POINT, day.getDewPoint());
            databaseInsert.put(DAY_HUMIDITY, day.getHumidity());
            databaseInsert.put(DAY_WIND_SPEED, day.getWindSpeed());
            databaseInsert.put(DAY_WIND_BEARING, day.getWindBearing());
            databaseInsert.put(DAY_VISIBILITY, day.getVisibility());
            databaseInsert.put(DAY_CLOUD_COVER, day.getCloudCover());
            databaseInsert.put(DAY_PRESSURE, day.getPressure());
            databaseInsert.put(DAY_OZONE, day.getOzone());

            final int result = writableDatabase.update(DAYS_TABLE_NAME,
                    databaseInsert,
                    DAY_ID + " = ?",
                    new String[] { Integer.toString(index.getAndIncrement())});

            if (result == 0) {
                writableDatabase.insert(DAYS_TABLE_NAME,
                        null,
                        databaseInsert);
            }

            databaseInsert.clear();
        }
        writableDatabase.close();
    }

    public void saveHourlyWeatherForCity(final String cityName, final List<HourlyData> data) {
        final ContentValues databaseInsert = new ContentValues();
        final SQLiteDatabase writableDatabase = userCitiesDatabase.getWritableDatabase();
        final AtomicInteger index = new AtomicInteger(1);

        for (final HourlyData hourlyData : data) {
            databaseInsert.put(CITY_NAME, cityName);
            databaseInsert.put(HOUR_TIME, hourlyData.getTime());
            databaseInsert.put(HOUR_SUMMARY, hourlyData.getSummary());
            databaseInsert.put(HOUR_ICON, hourlyData.getIcon());
            databaseInsert.put(HOUR_TEMPERATURE, hourlyData.getTemperature());
            databaseInsert.put(HOUR_APPARENT_TEMPERATURE, hourlyData.getApparentTemperature());
            databaseInsert.put(HOUR_HUMIDITY, hourlyData.getHumidity());
            databaseInsert.put(HOUR_PRECIPCHANCE, hourlyData.getPrecipProbability());
            databaseInsert.put(HOUR_PRECIPTYPE, hourlyData.getPrecipType());
            databaseInsert.put(HOUR_DEW_POINT, hourlyData.getDewPoint());
            databaseInsert.put(HOUR_WIND_SPEED, hourlyData.getWindSpeed());
            databaseInsert.put(HOUR_WIND_BEARING, hourlyData.getWindBearing());
            databaseInsert.put(HOUR_CLOUD_COVER, hourlyData.getCloudCover());
            databaseInsert.put(HOUR_VISIBILITY, hourlyData.getVisibility());
            databaseInsert.put(HOUR_PRESSURE, hourlyData.getPressure());
            databaseInsert.put(HOUR_OZONE, hourlyData.getOzone());

            final int result = writableDatabase.update(HOURS_TABLE_NAME,
                    databaseInsert,
                    HOUR_ID + " = ?",
                    new String[]{Integer.toString(index.getAndIncrement())});

            if (result == 0) {
                writableDatabase.insert(HOURS_TABLE_NAME, null, databaseInsert);
            }

            databaseInsert.clear();
        }
    }

    public Currently getCurrentlyWeatherForCity(final String cityName) {
        final Currently currently = new Currently();
        final Cursor query = userCitiesDatabase.getReadableDatabase().rawQuery(String.format(SELECT_EVERYTHING_FROM + CURRENT_TABLE_NAME + " WHERE %s=%s", CITY_NAME, cityName), null);
        query.moveToFirst();
        if (!query.isAfterLast()) {
            currently.setTime(query.getLong(query.getColumnIndex(CURRENT_TIME)));
            currently.setSummary(query.getString(query.getColumnIndex(CURRENT_SUMMARY)));
            currently.setIcon(query.getString(query.getColumnIndex(CURRENT_ICON)));
            currently.setTemperature(query.getDouble(query.getColumnIndex(CURRENT_TEMPERATURE)));
            currently.setApparentTemperature(query.getDouble(query.getColumnIndex(CURRENT_APPARENT_TEMPERATURE)));
            currently.setHumidity(query.getDouble(query.getColumnIndex(CURRENT_HUMIDITY)));
            currently.setPrecipProbability(query.getDouble(query.getColumnIndex(CURRENT_PRECIPCHANCE)));
            currently.setPrecipType(query.getString(query.getColumnIndex(CURRENT_PRECIPTYPE)));
            currently.setCloudCover(query.getDouble(query.getColumnIndex(CURRENT_CLOUD_COVER)));
            currently.setWindSpeed(query.getDouble(query.getColumnIndex(CURRENT_WIND_SPEED)));
            currently.setWindBearing(query.getLong(query.getColumnIndex(CURRENT_WIND_BEARING)));
        }

        query.close();
        return currently;
    }

    public List<DailyData> getDailyWeatherForCity(final String cityName) {
        final List<DailyData> days = new ArrayList<>();

        final Cursor query = userCitiesDatabase.getReadableDatabase().rawQuery(String.format(SELECT_EVERYTHING_FROM +
                        DAYS_TABLE_NAME +
                        " WHERE %s=%s" + ORDER_BY + DAY_ID + " ASC"
                , CITY_NAME, cityName), null);

        if (query != null) {
            for (query.moveToFirst(); !query.isAfterLast(); query.moveToNext()) {

                final DailyData day = new DailyData();
                day.setTime(query.getLong(query.getColumnIndex(DAY_TIME)));
                day.setSummary(query.getString(query.getColumnIndex(DAY_SUMMARY)));
                day.setIcon(query.getString(query.getColumnIndex(DAY_ICON)));
                day.setSunriseTime(query.getLong(query.getColumnIndex(DAY_SUNRISE_TIME)));
                day.setSunsetTime(query.getLong(query.getColumnIndex(DAY_SUNSET_TIME)));
                day.setMoonPhase(query.getDouble(query.getColumnIndex(DAY_MOON_PHASE)));
                day.setPrecipProbability(query.getDouble(query.getColumnIndex(DAY_PRECIPCHANCE)));
                day.setPrecipType(query.getString(query.getColumnIndex(DAY_PRECIPTYPE)));
                day.setTemperatureMax(query.getDouble(query.getColumnIndex(DAY_TEMPERATURE_MAX)));
                day.setApparentTemperatureMax(query.getDouble(query.getColumnIndex(DAY_APPARENT_TEMPERATURE_MAX)));
                day.setDewPoint(query.getDouble(query.getColumnIndex(DAY_DEW_POINT)));
                day.setHumidity(query.getDouble(query.getColumnIndex(DAY_HUMIDITY)));
                day.setWindSpeed(query.getDouble(query.getColumnIndex(DAY_WIND_SPEED)));
                day.setWindBearing(query.getLong(query.getColumnIndex(DAY_WIND_BEARING)));
                day.setVisibility(query.getLong(query.getColumnIndex(DAY_VISIBILITY)));
                day.setCloudCover(query.getDouble(query.getColumnIndex(DAY_CLOUD_COVER)));
                day.setPressure(query.getDouble(query.getColumnIndex(DAY_PRESSURE)));
                day.setOzone(query.getDouble(query.getColumnIndex(DAY_OZONE)));

                days.add(day);
            }
            query.close();
        }
        return days;
    }

    public List<HourlyData> getHourlyWeatherForCity(final String cityName) {
        final List<HourlyData> hourlyData = new ArrayList<>();

        final Cursor query = userCitiesDatabase.getReadableDatabase().rawQuery(String.format(SELECT_EVERYTHING_FROM
                        + HOURS_TABLE_NAME + " WHERE %s=%s" + ORDER_BY + HOUR_ID + " ASC"
                , CITY_NAME, cityName), null);

        if (query != null) {

            for (query.moveToFirst(); !query.isAfterLast(); query.moveToNext()) {
                final HourlyData hour = new HourlyData();
                hour.setTime(query.getLong(query.getColumnIndex(HOUR_TIME)));
                hour.setIcon(query.getString(query.getColumnIndex(HOUR_ICON)));
                hour.setSummary(query.getString(query.getColumnIndex(HOUR_SUMMARY)));
                hour.setTemperature(query.getDouble(query.getColumnIndex(HOUR_TEMPERATURE)));
                hour.setApparentTemperature(query.getDouble(query.getColumnIndex(HOUR_APPARENT_TEMPERATURE)));
                hour.setHumidity(query.getDouble(query.getColumnIndex(HOUR_HUMIDITY)));
                hour.setPrecipProbability(query.getDouble(query.getColumnIndex(HOUR_PRECIPCHANCE)));
                hour.setPrecipType(query.getString(query.getColumnIndex(HOUR_PRECIPTYPE)));
                hour.setDewPoint(query.getDouble(query.getColumnIndex(HOUR_DEW_POINT)));
                hour.setWindSpeed(query.getDouble(query.getColumnIndex(HOUR_WIND_SPEED)));
                hour.setWindBearing(query.getLong(query.getColumnIndex(HOUR_WIND_BEARING)));
                hour.setCloudCover(query.getDouble(query.getColumnIndex(HOUR_CLOUD_COVER)));
                hour.setVisibility(query.getDouble(query.getColumnIndex(HOUR_VISIBILITY)));
                hour.setPressure(query.getDouble(query.getColumnIndex(HOUR_PRESSURE)));
                hour.setOzone(query.getDouble(query.getColumnIndex(HOUR_OZONE)));

                hourlyData.add(hour);
            }
            query.close();
        }

        return hourlyData;
    }
}
