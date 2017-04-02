package com.darelbitsy.dbweather.adapters.database;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.darelbitsy.dbweather.helper.holder.ConstantHolder;
import com.darelbitsy.dbweather.helper.utility.weather.WeatherUtil;
import com.darelbitsy.dbweather.model.ApplicationDatabase;
import com.darelbitsy.dbweather.model.news.Article;
import com.darelbitsy.dbweather.model.weather.Alert;
import com.darelbitsy.dbweather.model.weather.Currently;
import com.darelbitsy.dbweather.model.weather.Daily;
import com.darelbitsy.dbweather.model.weather.DailyData;
import com.darelbitsy.dbweather.model.weather.Hourly;
import com.darelbitsy.dbweather.model.weather.HourlyData;
import com.darelbitsy.dbweather.model.weather.MinutelyData;
import com.darelbitsy.dbweather.model.weather.Weather;
import com.jakewharton.threetenabp.AndroidThreeTen;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static com.darelbitsy.dbweather.helper.holder.ConstantHolder.PREFS_NAME;
import static com.darelbitsy.dbweather.model.weather.DatabaseConstant.ALERTS_INSERTED;
import static com.darelbitsy.dbweather.model.weather.DatabaseConstant.ALERT_DESCRIPTION;
import static com.darelbitsy.dbweather.model.weather.DatabaseConstant.ALERT_EXPIRES;
import static com.darelbitsy.dbweather.model.weather.DatabaseConstant.ALERT_ID;
import static com.darelbitsy.dbweather.model.weather.DatabaseConstant.ALERT_TABLE;
import static com.darelbitsy.dbweather.model.weather.DatabaseConstant.ALERT_TIME;
import static com.darelbitsy.dbweather.model.weather.DatabaseConstant.ALERT_TITLE;
import static com.darelbitsy.dbweather.model.weather.DatabaseConstant.ALERT_URI;
import static com.darelbitsy.dbweather.model.weather.DatabaseConstant.APPLICATION_TABLE;
import static com.darelbitsy.dbweather.model.weather.DatabaseConstant.CITY_NAME;
import static com.darelbitsy.dbweather.model.weather.DatabaseConstant.CURRENT_APPARENT_TEMPERATURE;
import static com.darelbitsy.dbweather.model.weather.DatabaseConstant.CURRENT_CLOUD_COVER;
import static com.darelbitsy.dbweather.model.weather.DatabaseConstant.CURRENT_HUMIDITY;
import static com.darelbitsy.dbweather.model.weather.DatabaseConstant.CURRENT_ICON;
import static com.darelbitsy.dbweather.model.weather.DatabaseConstant.CURRENT_INSERTED;
import static com.darelbitsy.dbweather.model.weather.DatabaseConstant.CURRENT_PRECIPCHANCE;
import static com.darelbitsy.dbweather.model.weather.DatabaseConstant.CURRENT_PRECIPTYPE;
import static com.darelbitsy.dbweather.model.weather.DatabaseConstant.CURRENT_SUMMARY;
import static com.darelbitsy.dbweather.model.weather.DatabaseConstant.CURRENT_TABLE_NAME;
import static com.darelbitsy.dbweather.model.weather.DatabaseConstant.CURRENT_TEMPERATURE;
import static com.darelbitsy.dbweather.model.weather.DatabaseConstant.CURRENT_TIME;
import static com.darelbitsy.dbweather.model.weather.DatabaseConstant.CURRENT_WIND_BEARING;
import static com.darelbitsy.dbweather.model.weather.DatabaseConstant.CURRENT_WIND_SPEED;
import static com.darelbitsy.dbweather.model.weather.DatabaseConstant.DAYS_INSERTED;
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
import static com.darelbitsy.dbweather.model.weather.DatabaseConstant.FROM;
import static com.darelbitsy.dbweather.model.weather.DatabaseConstant.FULL_DAY_SUMMARY;
import static com.darelbitsy.dbweather.model.weather.DatabaseConstant.FULL_HOUR_SUMMARY;
import static com.darelbitsy.dbweather.model.weather.DatabaseConstant.HOURLY_INSERTED;
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
import static com.darelbitsy.dbweather.model.weather.DatabaseConstant.MINUTELY_INSERTED;
import static com.darelbitsy.dbweather.model.weather.DatabaseConstant.MINUTELY_PRECIPCHANCE;
import static com.darelbitsy.dbweather.model.weather.DatabaseConstant.MINUTELY_PRECIPTYPE;
import static com.darelbitsy.dbweather.model.weather.DatabaseConstant.MINUTELY_TABLE;
import static com.darelbitsy.dbweather.model.weather.DatabaseConstant.MINUTELY_TIME;
import static com.darelbitsy.dbweather.model.weather.DatabaseConstant.NEWS_DESCRIPTION;
import static com.darelbitsy.dbweather.model.weather.DatabaseConstant.NEWS_ID;
import static com.darelbitsy.dbweather.model.weather.DatabaseConstant.NEWS_IMAGE_URL;
import static com.darelbitsy.dbweather.model.weather.DatabaseConstant.NEWS_INSERTED;
import static com.darelbitsy.dbweather.model.weather.DatabaseConstant.NEWS_PUBLISHED_AT;
import static com.darelbitsy.dbweather.model.weather.DatabaseConstant.NEWS_SOURCE;
import static com.darelbitsy.dbweather.model.weather.DatabaseConstant.NEWS_TABLE_NAME;
import static com.darelbitsy.dbweather.model.weather.DatabaseConstant.NEWS_TITLE;
import static com.darelbitsy.dbweather.model.weather.DatabaseConstant.NEWS_URL;
import static com.darelbitsy.dbweather.model.weather.DatabaseConstant.ORDER_BY;
import static com.darelbitsy.dbweather.model.weather.DatabaseConstant.SELECT;
import static com.darelbitsy.dbweather.model.weather.DatabaseConstant.SELECT_EVERYTHING_FROM;
import static com.darelbitsy.dbweather.model.weather.DatabaseConstant.TIMEZONE;
import static com.darelbitsy.dbweather.model.weather.DatabaseConstant.WEATHER_INSERTED;
import static com.darelbitsy.dbweather.model.weather.DatabaseConstant.WEATHER_TABLE;
import static com.darelbitsy.dbweather.model.weather.DatabaseConstant.WEEK_SUMMARY;

/**
 * Created by Darel Bitsy on 26/01/17.
 * This class provide database operation
 * needed for the application to work
 */

public class DatabaseOperation {
    private static ApplicationDatabase mDatabase;
    private SharedPreferences.Editor mEditor;

    private boolean isDaysInserted;
    private boolean isHourInserted;
    private boolean isNewsInserted;
    private boolean isAlertInserted;
    private boolean isMinutelyInserted;
    private boolean iSCurrentInserted;
    private boolean isWeatherInserted;

    public DatabaseOperation(Context context) {
        if (mDatabase == null) {
            mDatabase = new ApplicationDatabase(context);
        }
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_NAME, context.MODE_PRIVATE);
        mEditor = sharedPreferences.edit();
        isWeatherInserted = sharedPreferences.getBoolean(WEATHER_INSERTED, false);
        iSCurrentInserted = sharedPreferences.getBoolean(CURRENT_INSERTED, false);
        isDaysInserted = sharedPreferences.getBoolean(DAYS_INSERTED, false);
        isHourInserted = sharedPreferences.getBoolean(HOURLY_INSERTED, false);
        isNewsInserted = sharedPreferences.getBoolean(NEWS_INSERTED, false);
        isAlertInserted = sharedPreferences.getBoolean(ALERTS_INSERTED, false);
        isMinutelyInserted = sharedPreferences.getBoolean(ALERTS_INSERTED, false);

        AndroidThreeTen.init(context);
    }

    /**
     * This method save the last time the weather
     * has been updated
     * I use it for debugging
     */
    public void saveLastWeatherServerSync() {
        SQLiteDatabase database = mDatabase.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(LAST_WEATHER_SERVER_SYNC, new Date().toString());
        int result = database.update(APPLICATION_TABLE, contentValues, null, null);
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
        SQLiteDatabase database = mDatabase.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(LAST_NEWS_SERVER_SYNC, new Date().toString());
        database.update(APPLICATION_TABLE, contentValues, null, null);
        contentValues.clear();
        database.close();
    }

    /**
     * This method save useful information for the weather
     * to be used when needed
     * @param weather instance to get all information needed
     */
    public void saveWeatherData(Weather weather) {
        SQLiteDatabase database = mDatabase.getWritableDatabase();
        ContentValues contentValues =  new ContentValues();
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

        if (isWeatherInserted) {
            int result = database.update(WEATHER_TABLE, contentValues, null, null);
            Log.i(ConstantHolder.TAG, "Saving weather data, result : "+ result);
            contentValues.clear();


        } else {
            long result = database.insert(WEATHER_TABLE,
                    null,
                    contentValues);

            contentValues.clear();

            if ( result == -1) {
                Log.i(ConstantHolder.TAG,
                        "Weather info On Weather Table Not Inserted");

            } else {
                Log.i(ConstantHolder.TAG,
                        "Weather info On Weather Table Inserted");
            }
            isWeatherInserted = true;
            mEditor.putBoolean(WEATHER_INSERTED,
                    isWeatherInserted).apply();
        }
        database.close();
    }

    /**
     * This method save weather alerts
     * @param alerts list of alerts to be saved in the database
     */
    public void saveAlerts(List<Alert> alerts) {
        SQLiteDatabase database = mDatabase.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        AtomicInteger id = new AtomicInteger(1);

        for (Alert alert : alerts) {
            contentValues.put(ALERT_TITLE, alert.getTitle());
            contentValues.put(ALERT_TIME, alert.getTime());
            contentValues.put(ALERT_EXPIRES, alert.getExpires());
            contentValues.put(ALERT_DESCRIPTION, alert.getDescription());
            contentValues.put(ALERT_URI, alert.getUri());

            if (isAlertInserted) {
                int result = database.update(ALERT_TABLE, contentValues,
                        ALERT_ID + " = ?",
                        new String[] {Integer.toString(id.getAndIncrement())});
                contentValues.clear();
                Log.i(ConstantHolder.TAG, "Row " + result + " With " + alert + " On Alerts table");

            } else {
                long result = database.insert(ALERT_TABLE, null, contentValues);
                contentValues.clear();

                if ( result == -1) {
                    Log.i(ConstantHolder.TAG, id.get() + " With " + alert+ " On Alerts Table Inserted");

                } else {
                    Log.i(ConstantHolder.TAG, id.get() + " With "+ alert + " On Alerts Table Inserted");
                    id.getAndIncrement();
                }
            }
        }
        if (!isAlertInserted) {
            isAlertInserted = true;
            mEditor.putBoolean(ALERTS_INSERTED, isAlertInserted);
            mEditor.apply();

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
        ContentValues databaseInsert = new ContentValues();
        SQLiteDatabase sqLiteDatabase = mDatabase.getWritableDatabase();

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
        if(iSCurrentInserted) {
            int result = sqLiteDatabase.update(CURRENT_TABLE_NAME,
                    databaseInsert,
                    null,
                    null);

            databaseInsert.clear();
            Log.i(ConstantHolder.TAG, "CURRENT TABLE UPDATED");
            Log.i(ConstantHolder.TAG, "Saving Current Weather, result : "+ result);

        } else {
            long result = sqLiteDatabase.insert(CURRENT_TABLE_NAME, null, databaseInsert);
            databaseInsert.clear();
            if (result == -1) {
                Log.i("DATABASE_DB", "CURRENT TABLE NOT INSERTED");
            } else {
                Log.i("DATABASE_DB", "CURRENT TABLE INSERTED");
            }
            iSCurrentInserted = true;
            mEditor.putBoolean(CURRENT_INSERTED,
                    iSCurrentInserted).apply();
        }
        sqLiteDatabase.close();
    }

    /**
     * This method save the week weather info
     * @param dailyData list of daily weather
     */
    public void saveDailyWeather(final List<DailyData> dailyData) {
        ContentValues databaseInsert = new ContentValues();
        SQLiteDatabase sqLiteDatabase = mDatabase.getWritableDatabase();
        AtomicInteger index = new AtomicInteger(1);
        for (DailyData day : dailyData) {
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

            if (isDaysInserted) {
                int result = sqLiteDatabase.update(DAYS_TABLE_NAME,
                        databaseInsert,
                        DAY_ID+ " = ?",
                        new String[] {Integer.toString(index.getAndIncrement())});
                databaseInsert.clear();
                Log.i("DATABASE_DB", "Row "+result+ " With " + day.toString() + " ON DAYS TABLE UPDATED");

            } else {
                long result = sqLiteDatabase.insert(DAYS_TABLE_NAME, null, databaseInsert);
                databaseInsert.clear();
                if ( result == -1) {
                    Log.i("DATABASE_DB", index.get() + " With " + day.toString() + " ON DAYS TABLE NOT INSERTED");

                } else { Log.i("DATABASE_DB", index.get() +" With " + day.toString() +  " ON DAYS TABLE INSERTED"); }
                index.getAndIncrement();
            }
        }
        if(!isDaysInserted) {
            isDaysInserted = true;
            mEditor.putBoolean(DAYS_INSERTED, isDaysInserted);
            mEditor.apply();
        }
        sqLiteDatabase.close();
    }

    /**
     * This method save hourly weather data
     * @param data weather in hourly format
     */
    public void saveHourlyWeather(final List<HourlyData> data) {
        ContentValues databaseInsert = new ContentValues();
        SQLiteDatabase sqLiteDatabase = mDatabase.getWritableDatabase();
        AtomicInteger index = new AtomicInteger(1);
        for (HourlyData hourlyData : data) {
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
            if (isHourInserted) {
                int row = sqLiteDatabase.update(HOURS_TABLE_NAME,
                        databaseInsert,
                        HOUR_ID + " = ?",
                        new String[] {Integer.toString(index.getAndIncrement())});
                databaseInsert.clear();
                Log.i(ConstantHolder.TAG, row + "ROW With " + hourlyData.toString() + " ON HOUR TABLE UPDATED");
            } else {
                long result = sqLiteDatabase.insert(HOURS_TABLE_NAME, null, databaseInsert);
                databaseInsert.clear();
                if (result == -1) { Log.i("DATABASE_DB", index + " With " + hourlyData.toString() + " ON HOUR TABLE NOT INSERTED"); }
                else { Log.i(ConstantHolder.TAG, index.get() + " With " + hourlyData.toString() + " ON HOUR TABLE INSERTED"); }
                index.getAndIncrement();
            }
        }
        if (!isHourInserted) {
            isHourInserted = true;
            mEditor.putBoolean(HOURLY_INSERTED, isHourInserted);
            mEditor.commit();
        }
        sqLiteDatabase.close();
    }

    public void saveMinutelyWeather(List<MinutelyData> minutelyWeatherData) {
        ContentValues contentValues = new ContentValues();
        SQLiteDatabase sqLiteDatabase = mDatabase.getWritableDatabase();
        AtomicInteger id = new AtomicInteger(1);
        for (MinutelyData minutelyWeather : minutelyWeatherData) {
            contentValues.put(MINUTELY_TIME, minutelyWeather.getTime());
            contentValues.put(MINUTELY_PRECIPCHANCE, minutelyWeather.getPrecipProbability());
            contentValues.put(MINUTELY_PRECIPTYPE, minutelyWeather.getPrecipType());
            if (isMinutelyInserted) {
                int row = sqLiteDatabase.update(MINUTELY_TABLE,
                        contentValues,
                        MINUTELY_ID + " = ?",
                        new String[] {Integer.toString(id.getAndIncrement())});
                contentValues.clear();
                Log.i(ConstantHolder.TAG, row + " ROW With " + minutelyWeather + " On Minutely Table UPDATED");

            } else {
                long result = sqLiteDatabase.insert(MINUTELY_TABLE, null, contentValues);
                contentValues.clear();
                if (result == -1) { Log.i(ConstantHolder.TAG, id + " With " + minutelyWeather + " On Minutely Table Not Inserted"); }
                else { Log.i(ConstantHolder.TAG, id + " With " + minutelyWeather + " On Minutely Table Not Inserted"); }
                id.getAndIncrement();
            }
        }
        if (!isMinutelyInserted) {
            isMinutelyInserted = true;
            mEditor.putBoolean(MINUTELY_INSERTED, isMinutelyInserted)
                    .apply();
        }
        sqLiteDatabase.close();
    }

    /**
     * This method save latest news received by the server
     * to be displayed ofline
     * @param newses array of newses
     */
    public void saveNewses(final ArrayList<Article> newses) {
        ContentValues databaseInsert = new ContentValues();
        SQLiteDatabase sqLiteDatabase = mDatabase.getWritableDatabase();
        AtomicInteger index = new AtomicInteger(1);
        for (Article news : newses) {
            databaseInsert.put(NEWS_SOURCE, news.getAuthor());
            databaseInsert.put(NEWS_TITLE, news.getTitle());

            databaseInsert.put(NEWS_URL, news.getArticleUrl());
            Log.i("URL_LOG", " : " + news.getArticleUrl());
            databaseInsert.put(NEWS_IMAGE_URL, news.getUrlToImage());
            Log.i("URL_LOG", "Got Image_Url: " + news.getUrlToImage() +
                    " Got News_Url: " + news.getArticleUrl() );

            databaseInsert.put(NEWS_PUBLISHED_AT, news.getPublishedAt());
            databaseInsert.put(NEWS_DESCRIPTION, news.getDescription());

            if (isNewsInserted) {
                int result = sqLiteDatabase.update(NEWS_TABLE_NAME,
                        databaseInsert,
                        NEWS_ID + " = ?",
                        new  String[] {Integer.toString(index.getAndIncrement())} );
                databaseInsert.clear();
                Log.i(ConstantHolder.TAG, "ROW "+ result+ " with " + news.toString() + " ON NEWS TABLE UPDATED");

            } else {
                long result = sqLiteDatabase.insert(NEWS_TABLE_NAME, null, databaseInsert);
                databaseInsert.clear();
                if (result == -1) {
                    Log.i(ConstantHolder.TAG, index.get() + " with " + news.toString() + " ON NEWS TABLE NOT INSERTED");

                } else { Log.i(ConstantHolder.TAG, index.get() + " with " + news.toString() + " ON NEWS TABLE INSERTED"); }
                index.getAndIncrement();
            }
        }
        if (!isNewsInserted) {
            isNewsInserted = true;
            mEditor.putBoolean(NEWS_INSERTED, isNewsInserted);
            mEditor.commit();
        }
        sqLiteDatabase.close();
    }

    /**
     * Get global weather info
     * from database
     * @return Weather object
     */
    public Weather getWeatherData() {
        Weather weather = new Weather();
        Cursor databaseCursor = mDatabase.getReadableDatabase().rawQuery(SELECT_EVERYTHING_FROM
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
        List<Alert> alerts = new ArrayList<>();
        Cursor dabaseCursor = mDatabase.getReadableDatabase().rawQuery(SELECT_EVERYTHING_FROM +
                ALERT_TABLE +
                ORDER_BY +
                ALERT_ID + " ASC", null);
        if (dabaseCursor != null) {
            AtomicInteger id = new AtomicInteger(0);
            for (dabaseCursor.moveToFirst(); !dabaseCursor.isAfterLast(); dabaseCursor.moveToNext()) {
                Alert alert = new Alert();
                alert.setTitle(dabaseCursor.getString(dabaseCursor.getColumnIndex(ALERT_TITLE)));
                alert.setTime(dabaseCursor.getLong(dabaseCursor.getColumnIndex(ALERT_TIME)));
                alert.setExpires(dabaseCursor.getLong(dabaseCursor.getColumnIndex(ALERT_EXPIRES)));
                alert.setDescription(dabaseCursor.getString(dabaseCursor.getColumnIndex(ALERT_DESCRIPTION)));
                alert.setUri(dabaseCursor.getString(dabaseCursor.getColumnIndex(ALERT_URI)));
                alerts.add(alert);
            }
            dabaseCursor.close();
        }
        return alerts;
    }

    /**
     * Get last saved weather from database
     * @return currently weather object
     */
    public Currently getCurrentWeatherFromDatabase() {
        Currently current = new Currently();
        Cursor databaseCursor = mDatabase.getReadableDatabase().rawQuery("SELECT * FROM " +
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
            Log.i(ConstantHolder.TAG, "From DB PROBABILITY: " + current.getPrecipProbability());
        }
        databaseCursor.close();
        return current;
    }

    //TODO: need to create an getMinutely method


    public void saveCoordinates(double latitude, double longitude) {
        ContentValues contentValues = new ContentValues();
        SQLiteDatabase database = mDatabase.getWritableDatabase();

        contentValues.put(LAST_KNOW_LATITUDE, latitude);
        contentValues.put(LAST_KNOW_LONGITUDE, longitude);


        int result = database.update(WEATHER_TABLE,
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
        Double[] coordinates = new Double[2];

        Cursor databaseCursor = mDatabase.getReadableDatabase().rawQuery(SELECT +
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
        Cursor databaseCursor  = mDatabase.getReadableDatabase()
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
        List<DailyData> days = new ArrayList<>();

        Cursor databaseCursor = mDatabase.getReadableDatabase().rawQuery("SELECT * FROM " +
                DAYS_TABLE_NAME +
                ORDER_BY +
                DAY_ID+" ASC", null);

        if(databaseCursor != null) {
            AtomicInteger index = new AtomicInteger(0);

            for (databaseCursor.moveToFirst(); !databaseCursor.isAfterLast(); databaseCursor.moveToNext()) {
                DailyData day = new DailyData();
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

                Log.i("DATABASE_DB", "getDailyWeatherFromDatabase: On " + index.get());
                Log.i("DATABASE_DB", day.toString() + ": On " + index.get());
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
        List<HourlyData> hourlyData = new ArrayList<>();

        Cursor databaseCursor = mDatabase.getReadableDatabase().rawQuery(SELECT_EVERYTHING_FROM
                + HOURS_TABLE_NAME
                + ORDER_BY
                + HOUR_ID +" ASC", null);

        if(databaseCursor != null) {
            AtomicInteger index = new AtomicInteger(0);

            for (databaseCursor.moveToFirst(); !databaseCursor.isAfterLast(); databaseCursor.moveToNext()) {
                HourlyData hour = new HourlyData();
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

                Log.i("DATABASE_DB", "getHourlyWeatherFromDatabase: On " + index.get());
                Log.i("DATABASE_DB", hour.toString() + ": On " + index.get());
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
        ArrayList<Article> newses = new ArrayList<>();

        Cursor databaseCursor = mDatabase.getReadableDatabase().rawQuery(SELECT_EVERYTHING_FROM +
                NEWS_TABLE_NAME +
                ORDER_BY +
                NEWS_ID + " ASC", null);

        if (databaseCursor != null) {
            AtomicInteger index = new AtomicInteger(0);
            for (databaseCursor.moveToFirst(); !databaseCursor.isAfterLast(); databaseCursor.moveToNext()) {
                Article news = new Article();
                news.setAuthor(databaseCursor.getString(databaseCursor.getColumnIndex(NEWS_SOURCE)));
                news.setTitle(databaseCursor.getString(databaseCursor.getColumnIndex(NEWS_TITLE)));
                news.setPublishedAt(databaseCursor.getString(databaseCursor.getColumnIndex(NEWS_PUBLISHED_AT)));
                news.setDescription(databaseCursor.getString(databaseCursor.getColumnIndex(NEWS_DESCRIPTION)));
                news.setUrlToImage(databaseCursor.getString(databaseCursor.getColumnIndex(NEWS_IMAGE_URL)));

                try {
                    news.setArticleUrl(databaseCursor.getString(databaseCursor.getColumnIndex(NEWS_URL)));

                } catch (MalformedURLException e) { Log.i(ConstantHolder.TAG, "Error: "+ e.getMessage()); }

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
    public HourlyData getNotificationHour(final long timeInMilliseconds, String timeZone) {
        HourlyData hour = new HourlyData();
        Cursor cursor = mDatabase.getReadableDatabase().rawQuery(SELECT +
                HOUR_TIME + ", " +
                HOUR_TEMPERATURE + ", " +
                HOUR_ICON + ", " +
                HOUR_SUMMARY+
                FROM +
                HOURS_TABLE_NAME, null);

        if(cursor != null) {

            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                long hourTime = cursor.getLong(cursor.getColumnIndex(HOUR_TIME));

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
}
