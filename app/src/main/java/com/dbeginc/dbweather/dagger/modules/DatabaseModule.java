package com.dbeginc.dbweather.dagger.modules;

import android.content.Context;

import com.dbeginc.dbweather.models.provider.news.DatabaseNewsProvider;
import com.dbeginc.dbweather.models.provider.weather.DatabaseWeatherProvider;

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
    DatabaseWeatherProvider providesDatabaseWeatherProvider(final Context context) {
        return new DatabaseWeatherProvider(context);
    }

    @Provides
    @Singleton
    DatabaseNewsProvider providesDatabaseNewProvider(final Context context) {
        return new DatabaseNewsProvider(context);
    }
}
