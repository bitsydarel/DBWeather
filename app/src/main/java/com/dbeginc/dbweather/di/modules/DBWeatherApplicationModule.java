package com.dbeginc.dbweather.di.modules;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;

import com.dbeginc.dbweather.di.scopes.AppScope;
import com.dbeginc.dbweather.utils.helper.ApplicationPreferences;

import dagger.Module;
import dagger.Provides;

import static com.dbeginc.dbweather.utils.holder.ConstantHolder.PREFS_NAME;

/**
 * Created by Darel Bitsy on 24/04/17.
 * Application Module
 */

@Module
public class DBWeatherApplicationModule {
    private final Context context;

    public DBWeatherApplicationModule(final Context context) {
        this.context = context;
    }

    @Provides
    @AppScope
    Context providesApplicationContext() { return context.getApplicationContext(); }

    @Provides
    @AppScope
    Resources providesAppResource(final Context context) { return context.getResources(); }

    @Provides
    @AppScope
    SharedPreferences providesSharedPreferences(final Context context) {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    @Provides
    @AppScope
    ApplicationPreferences provideApplicationPreferences(final SharedPreferences sharedPreferences) {
        return new ApplicationPreferences(sharedPreferences);
    }
}
