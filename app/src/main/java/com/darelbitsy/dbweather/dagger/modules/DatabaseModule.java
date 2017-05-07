package com.darelbitsy.dbweather.dagger.modules;

import android.content.Context;

import com.darelbitsy.dbweather.models.provider.news.DatabaseNewsProvider;
import com.darelbitsy.dbweather.models.provider.weather.DatabaseWeatherProvider;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Darel Bitsy on 24/04/17.
 * Database Module for
 * instance related to database call
 */

@Module
public class DatabaseModule {

    @Provides
    @Singleton
    public DatabaseWeatherProvider providesDatabaseWeatherProvider(final Context context) {
        return new DatabaseWeatherProvider(context);
    }

    @Provides
    @Singleton
    public DatabaseNewsProvider providesDatabaseNewProvider(final Context context) {
        return new DatabaseNewsProvider(context);
    }
}
