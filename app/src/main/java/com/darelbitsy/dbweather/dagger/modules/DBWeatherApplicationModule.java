package com.darelbitsy.dbweather.dagger.modules;

import android.content.Context;
import android.content.SharedPreferences;

import com.darelbitsy.dbweather.DBWeatherApplication;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

import static com.darelbitsy.dbweather.utils.holder.ConstantHolder.PREFS_NAME;

/**
 * Created by Darel Bitsy on 24/04/17.
 * Application Module
 */

@Module
public class DBWeatherApplicationModule {

    private final DBWeatherApplication mDbWeatherApplication;

    public DBWeatherApplicationModule(final DBWeatherApplication dbWeatherApplication) {
        mDbWeatherApplication = dbWeatherApplication;
    }

    @Provides
    @Singleton
    public DBWeatherApplication providesApplication() {
        return mDbWeatherApplication;
    }

    @Provides
    @Singleton
    public Context providesApplicationContext() { return mDbWeatherApplication.getApplicationContext(); }

    @Provides
    @Singleton
    public SharedPreferences providesSharedPreferences(final Context context) {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }
}
