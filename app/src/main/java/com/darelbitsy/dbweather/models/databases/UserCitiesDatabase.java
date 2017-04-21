package com.darelbitsy.dbweather.models.databases;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static com.darelbitsy.dbweather.models.datatypes.weather.DatabaseConstant.CITIES_TABLE;
import static com.darelbitsy.dbweather.models.datatypes.weather.DatabaseConstant.COLUMN_ID;
import static com.darelbitsy.dbweather.models.datatypes.weather.DatabaseConstant.COLUMN_INTEGER_TYPE;
import static com.darelbitsy.dbweather.models.datatypes.weather.DatabaseConstant.COLUMN_PRIMARY_KEY;
import static com.darelbitsy.dbweather.models.datatypes.weather.DatabaseConstant.COLUMN_REAL_TYPE;
import static com.darelbitsy.dbweather.models.datatypes.weather.DatabaseConstant.COLUMN_TEXT_TYPE;
import static com.darelbitsy.dbweather.models.datatypes.weather.DatabaseConstant.COLUMN_UNIQUE;
import static com.darelbitsy.dbweather.models.datatypes.weather.DatabaseConstant.COMMA;
import static com.darelbitsy.dbweather.models.datatypes.weather.DatabaseConstant.CREATE_TABLE_QUERY;
import static com.darelbitsy.dbweather.models.datatypes.weather.DatabaseConstant.THE_CITY_COUNTRY;
import static com.darelbitsy.dbweather.models.datatypes.weather.DatabaseConstant.THE_CITY_LATITUDE;
import static com.darelbitsy.dbweather.models.datatypes.weather.DatabaseConstant.THE_CITY_LONGITUDE;
import static com.darelbitsy.dbweather.models.datatypes.weather.DatabaseConstant.THE_CITY_NAME;

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
    }

    @Override
    public void onUpgrade(final SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    private void createCityTable(final SQLiteDatabase database) {
        database.execSQL(CREATE_TABLE_QUERY + CITIES_TABLE + " (" +
                COLUMN_ID + COLUMN_INTEGER_TYPE + COLUMN_PRIMARY_KEY + COMMA +
                THE_CITY_NAME + COLUMN_TEXT_TYPE + COLUMN_UNIQUE + COMMA +
                THE_CITY_COUNTRY + COLUMN_TEXT_TYPE + COMMA +
                THE_CITY_LATITUDE + COLUMN_REAL_TYPE + COLUMN_UNIQUE + COMMA +
                THE_CITY_LONGITUDE + COLUMN_REAL_TYPE + COLUMN_UNIQUE +
                ");"
        );
    }
}
