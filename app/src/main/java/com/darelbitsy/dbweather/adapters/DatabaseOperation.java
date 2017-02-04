package com.darelbitsy.dbweather.adapters;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.darelbitsy.dbweather.helper.WeatherCallHelper;
import com.darelbitsy.dbweather.weather.WeatherDatabase;
import com.darelbitsy.dbweather.weather.Current;
import com.darelbitsy.dbweather.weather.Day;
import com.darelbitsy.dbweather.weather.Hour;

import java.util.concurrent.atomic.AtomicInteger;

import static com.darelbitsy.dbweather.weather.WeatherDatabase.CURRENT_CITYNAME;
import static com.darelbitsy.dbweather.weather.WeatherDatabase.CURRENT_HUMIDITY;
import static com.darelbitsy.dbweather.weather.WeatherDatabase.CURRENT_ICON;
import static com.darelbitsy.dbweather.weather.WeatherDatabase.CURRENT_PRECIPCHANCE;
import static com.darelbitsy.dbweather.weather.WeatherDatabase.CURRENT_SUMMARY;
import static com.darelbitsy.dbweather.weather.WeatherDatabase.CURRENT_TABLE_NAME;
import static com.darelbitsy.dbweather.weather.WeatherDatabase.CURRENT_TEMPERATURE;
import static com.darelbitsy.dbweather.weather.WeatherDatabase.CURRENT_TIME;
import static com.darelbitsy.dbweather.weather.WeatherDatabase.CURRENT_TIMEZONE;
import static com.darelbitsy.dbweather.weather.WeatherDatabase.DAYS_TABLE_NAME;
import static com.darelbitsy.dbweather.weather.WeatherDatabase.DAY_HUMIDITY;
import static com.darelbitsy.dbweather.weather.WeatherDatabase.DAY_ICON;
import static com.darelbitsy.dbweather.weather.WeatherDatabase.DAY_ID;
import static com.darelbitsy.dbweather.weather.WeatherDatabase.DAY_PRECIPCHANCE;
import static com.darelbitsy.dbweather.weather.WeatherDatabase.DAY_SUMMARY;
import static com.darelbitsy.dbweather.weather.WeatherDatabase.DAY_TEMPERATURE_MAX;
import static com.darelbitsy.dbweather.weather.WeatherDatabase.DAY_TIME;
import static com.darelbitsy.dbweather.weather.WeatherDatabase.DAY_TIMEZONE;
import static com.darelbitsy.dbweather.weather.WeatherDatabase.HOURS_TABLE_NAME;
import static com.darelbitsy.dbweather.weather.WeatherDatabase.HOUR_ICON;
import static com.darelbitsy.dbweather.weather.WeatherDatabase.HOUR_ID;
import static com.darelbitsy.dbweather.weather.WeatherDatabase.HOUR_SUMMARY;
import static com.darelbitsy.dbweather.weather.WeatherDatabase.HOUR_TEMPERATURE;
import static com.darelbitsy.dbweather.weather.WeatherDatabase.HOUR_TIME;
import static com.darelbitsy.dbweather.weather.WeatherDatabase.HOUR_TIMEZONE;
import static com.darelbitsy.dbweather.weather.WeatherDatabase.LAST_JSON_DATA;
import static com.darelbitsy.dbweather.weather.WeatherDatabase.LAST_KNOW_LATITUDE;
import static com.darelbitsy.dbweather.weather.WeatherDatabase.LAST_KNOW_LONGITUDE;
import static com.darelbitsy.dbweather.weather.WeatherDatabase.WEEK_SUMMARY;

/**
 * Created by Darel Bitsy on 26/01/17.
 */

public class DatabaseOperation {
    private static WeatherDatabase mDatabase;
    public static final String PREFS_NAME = "db_weather_prefs";
    private SharedPreferences.Editor mEditor;
    private static final String CURRENT_INSERTED = "current_inserted";
    private static final String DAYS_INSERTED = "days_inserted";
    private static final String HOURLY_INSERTED = "hourly_inserted";
    private boolean isDaysInserted;
    private boolean isHourInserted;
    private boolean iSCurrentInserted;

    public DatabaseOperation(Context context) {
        if (mDatabase == null) {
            mDatabase = new WeatherDatabase(context);
        }
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_NAME, context.MODE_PRIVATE);
        mEditor = sharedPreferences.edit();
        iSCurrentInserted = sharedPreferences.getBoolean(CURRENT_INSERTED, false);
        isDaysInserted = sharedPreferences.getBoolean(DAYS_INSERTED, false);
        isHourInserted = sharedPreferences.getBoolean(HOURLY_INSERTED, false);
    }

    public void saveCurrentWeather(final Current current, final WeatherCallHelper weatherApi) {
        ContentValues databaseInsert = new ContentValues();
        SQLiteDatabase sqLiteDatabase = mDatabase.getWritableDatabase();

        databaseInsert.put(CURRENT_CITYNAME, current.getCityName());
        databaseInsert.put(CURRENT_TIMEZONE, current.getTimeZone());
        databaseInsert.put(CURRENT_SUMMARY, current.getSummary());
        databaseInsert.put(CURRENT_ICON, current.getIcon());
        databaseInsert.put(CURRENT_TIME, current.getTime());
        databaseInsert.put(CURRENT_TEMPERATURE, current.getTemperature());
        databaseInsert.put(CURRENT_HUMIDITY, current.getHumidity());
        databaseInsert.put(CURRENT_PRECIPCHANCE, current.getPrecipChance());
        databaseInsert.put(WEEK_SUMMARY, current.getWeekSummary());
        databaseInsert.put(LAST_KNOW_LATITUDE, weatherApi.getLatitude());
        databaseInsert.put(LAST_KNOW_LONGITUDE, weatherApi.getLongitude());
        if(iSCurrentInserted) {
            sqLiteDatabase.update(CURRENT_TABLE_NAME,
                    databaseInsert,
                    null,
                    null);
            databaseInsert.clear();
            Log.i("DATABASE_DB", "CURRENT TABLE UPDATED");
        } else {
            long result = sqLiteDatabase.insert(CURRENT_TABLE_NAME, null, databaseInsert);
            databaseInsert.clear();
            if (result == -1) {
                Log.i("DATABASE_DB", "CURRENT TABLE NOT INSERTED");
            } else { Log.i("DATABASE_DB", "CURRENT TABLE INSERTED"); }
            iSCurrentInserted = true;
            mEditor.putBoolean(CURRENT_INSERTED, iSCurrentInserted);
            mEditor.commit();
        }
    }

    public void saveDailyWeather(final Day[] days) {
        ContentValues databaseInsert = new ContentValues();
        SQLiteDatabase sqLiteDatabase = mDatabase.getWritableDatabase();
        AtomicInteger index = new AtomicInteger(1);
        for (Day day : days) {
            databaseInsert.put(DAY_SUMMARY, day.getSummary());
            databaseInsert.put(DAY_TIMEZONE, day.getTimeZone());
            databaseInsert.put(DAY_TIME, day.getTime());
            databaseInsert.put(DAY_TEMPERATURE_MAX, day.getTemperatureMax());
            databaseInsert.put(DAY_ICON, day.getIcon());
            databaseInsert.put(DAY_HUMIDITY, day.getHumidity());
            databaseInsert.put(DAY_PRECIPCHANCE, day.getPrecipChance());
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
                }
                else { Log.i("DATABASE_DB", index.get() +" With " + day.toString() +  " ON DAYS TABLE INSERTED"); }
                index.getAndIncrement();
            }
        }
        if(!isDaysInserted) {
            isDaysInserted = true;
            mEditor.putBoolean(DAYS_INSERTED, isDaysInserted);
            mEditor.commit();
        }
    }

    public void saveHourlyWeather(final Hour[] hours) {
        ContentValues databaseInsert = new ContentValues();
        SQLiteDatabase sqLiteDatabase = mDatabase.getWritableDatabase();
        AtomicInteger index = new AtomicInteger(1);
        for (Hour hour : hours) {
            databaseInsert.put(HOUR_SUMMARY, hour.getSummary());
            databaseInsert.put(HOUR_ICON, hour.getIcon());
            databaseInsert.put(HOUR_TIME, hour.getTime());
            databaseInsert.put(HOUR_TIMEZONE, hour.getTimeZone());
            databaseInsert.put(HOUR_TEMPERATURE, hour.getTemperature());
            if (isHourInserted) {
                int row = sqLiteDatabase.update(HOURS_TABLE_NAME,
                        databaseInsert,
                        HOUR_ID + " = ?",
                        new String[] {Integer.toString(index.getAndIncrement())});
                databaseInsert.clear();
                Log.i("DATABASE_DB", row + "ROW With " + hour.toString() + " ON HOUR TABLE UPDATED");
            } else {
                long result = sqLiteDatabase.insert(HOURS_TABLE_NAME, null, databaseInsert);
                databaseInsert.clear();
                if (result == -1) { Log.i("DATABASE_DB", index + " With " + hour.toString() + " ON HOUR TABLE NOT INSERTED"); }
                else { Log.i("DATABASE_DB", index.get() + " With " + hour.toString() + " ON HOUR TABLE INSERTED"); }
                index.getAndIncrement();
            }
        }
        if (!isHourInserted) {
            isHourInserted = true;
            mEditor.putBoolean(HOURLY_INSERTED, isHourInserted);
            mEditor.commit();
        }
    }

    public Current getCurrentWeatherFromDatabase() {
        Current current = new Current();
        SQLiteDatabase sqLiteDatabase = mDatabase.getReadableDatabase();
        Cursor databaseCursor = sqLiteDatabase.rawQuery("SELECT * FROM " +
                CURRENT_TABLE_NAME, null);
        databaseCursor.moveToFirst();
        if(!databaseCursor.isAfterLast()) {
            current.setCityName(databaseCursor.getString(databaseCursor.getColumnIndex(CURRENT_CITYNAME)));
            current.setTimeZone(databaseCursor.getString(databaseCursor.getColumnIndex(CURRENT_TIMEZONE)));
            current.setSummary(databaseCursor.getString(databaseCursor.getColumnIndex(CURRENT_SUMMARY)));
            current.setIcon(databaseCursor.getString(databaseCursor.getColumnIndex(CURRENT_ICON)));
            current.setTime(databaseCursor.getLong(databaseCursor.getColumnIndex(CURRENT_TIME)));
            current.setTemperature(databaseCursor.getInt(databaseCursor.getColumnIndex(CURRENT_TEMPERATURE)));
            current.setHumidity(databaseCursor.getInt(databaseCursor.getColumnIndex(CURRENT_HUMIDITY)));
            current.setPrecipChance(databaseCursor.getInt(databaseCursor.getColumnIndex(CURRENT_PRECIPCHANCE)));
            current.setWeekSummary(databaseCursor.getString(databaseCursor.getColumnIndex(WEEK_SUMMARY)));
        }
        databaseCursor.close();
        return current;
    }

    public Double[] getCoordinates() {
        Double[] coordinates = new Double[2];
        SQLiteDatabase sqLiteDatabase = mDatabase.getReadableDatabase();

        Cursor databaseCursor = sqLiteDatabase.rawQuery("SELECT " +
                LAST_KNOW_LATITUDE +
                ", " +LAST_KNOW_LONGITUDE +
                " FROM "+ CURRENT_TABLE_NAME , null);
        databaseCursor.moveToFirst();
        if(!databaseCursor.isAfterLast()) {
            coordinates[0] = databaseCursor.getDouble(databaseCursor.getColumnIndex(LAST_KNOW_LATITUDE));
            coordinates[1] = databaseCursor.getDouble(databaseCursor.getColumnIndex(LAST_KNOW_LONGITUDE));
        }
        databaseCursor.close();
        return coordinates;
    }

    public String getJsonData() {
        String jsonData = "";
        SQLiteDatabase sqLiteDatabase = mDatabase.getReadableDatabase();
        Cursor databaseCursor = sqLiteDatabase.rawQuery("SELECT " +
                LAST_JSON_DATA +
                " FROM " +
                CURRENT_TABLE_NAME, null);
        if(databaseCursor != null) {
            databaseCursor.moveToFirst();
            if(!databaseCursor.isAfterLast()) {
                jsonData = databaseCursor.getString(databaseCursor.getColumnIndex(LAST_JSON_DATA));
            }
            databaseCursor.close();
        }
        return jsonData;
    }

    public void setJsonData(String jsonData) {
        ContentValues databaseInsert = new ContentValues();
        databaseInsert.put(LAST_JSON_DATA, jsonData);
        SQLiteDatabase sqLiteDatabase = mDatabase.getWritableDatabase();
        sqLiteDatabase.update(CURRENT_TABLE_NAME, databaseInsert, null, null);
        sqLiteDatabase.close();
        Log.i("DATABASE_DB", "JSONDATA " + jsonData + " UPDATED");
    }

    public Day[] getDailyWeatherFromDatabase() {
        Day[] days = new Day[8];
        SQLiteDatabase sqLiteDatabase = mDatabase.getReadableDatabase();
        Cursor databaseCursor = sqLiteDatabase.rawQuery("SELECT * FROM " +
                DAYS_TABLE_NAME +
                " ORDER BY " +
                DAY_ID+" ASC", null);
        if(databaseCursor != null) {
            AtomicInteger index = new AtomicInteger(0);
            for (databaseCursor.moveToFirst(); !databaseCursor.isAfterLast(); databaseCursor.moveToNext()) {
                Day day = new Day();
                day.setSummary(databaseCursor.getString(databaseCursor.getColumnIndex(DAY_SUMMARY)));
                day.setTimeZone(databaseCursor.getString(databaseCursor.getColumnIndex(DAY_TIMEZONE)));
                day.setTime(databaseCursor.getLong(databaseCursor.getColumnIndex(DAY_TIME)));
                day.setTemperatureMax(databaseCursor.getInt(databaseCursor.getColumnIndex(DAY_TEMPERATURE_MAX)));
                day.setIcon(databaseCursor.getString(databaseCursor.getColumnIndex(DAY_ICON)));
                day.setHumidity(databaseCursor.getInt(databaseCursor.getColumnIndex(DAY_HUMIDITY)));
                day.setPrecipChance(databaseCursor.getInt(databaseCursor.getColumnIndex(DAY_PRECIPCHANCE)));
                Log.i("DATABASE_DB", "getDailyWeatherFromDatabase: On " + index.get());
                Log.i("DATABASE_DB", day.toString() + ": On " + index.get());
                days[index.getAndIncrement()] = day;
            }
            databaseCursor.close();
        }
        return days;
    }

    public Hour[] getHourlyWeatherFromDatabase() {
        Hour[] hours = new Hour[48];
        SQLiteDatabase sqLiteDatabase = mDatabase.getReadableDatabase();
        Cursor databaseCursor = sqLiteDatabase.rawQuery("SELECT * FROM "
                + HOURS_TABLE_NAME + " ORDER BY "+ HOUR_ID +" ASC", null);
        if(databaseCursor != null) {
            AtomicInteger index = new AtomicInteger(0);
            for (databaseCursor.moveToFirst(); !databaseCursor.isAfterLast(); databaseCursor.moveToNext()) {
                Hour hour = new Hour();
                hour.setSummary(databaseCursor.getString(databaseCursor.getColumnIndex(HOUR_SUMMARY)));
                hour.setIcon(databaseCursor.getString(databaseCursor.getColumnIndex(HOUR_ICON)));
                hour.setTime(databaseCursor.getLong(databaseCursor.getColumnIndex(HOUR_TIME)));
                hour.setTimeZone(databaseCursor.getString(databaseCursor.getColumnIndex(HOUR_TIMEZONE)));
                hour.setTemperature(databaseCursor.getInt(databaseCursor.getColumnIndex(HOUR_TEMPERATURE)));
                Log.i("DATABASE_DB", "getHourlyWeatherFromDatabase: On " + index.get());
                Log.i("DATABASE_DB", hour.toString() + ": On " + index.get());
                hours[index.getAndIncrement()] = hour;
            }
            databaseCursor.close();
        }
        return hours;
    }

    public Hour getNotificationHour(final long systemTime) {
        Hour hour = new Hour();
        SQLiteDatabase sqLiteDatabase = mDatabase.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM " +
                HOURS_TABLE_NAME, null);

        if(cursor != null) {
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                if(hour.getHour(systemTime).equalsIgnoreCase(hour.getHour(cursor.getLong(cursor.getColumnIndex(HOUR_TIME)))))
                {
                    hour.setTime(cursor.getLong(cursor.getColumnIndex(HOUR_TIME)));
                    hour.setTemperature(cursor.getInt(cursor.getColumnIndex(HOUR_TEMPERATURE)));
                    hour.setTimeZone(cursor.getString(cursor.getColumnIndex(HOUR_TIMEZONE)));
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
