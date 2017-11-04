package com.dbeginc.dbweather.di.modules;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;

import com.dbeginc.dbweather.di.scopes.AppScope;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;
import io.reactivex.subjects.PublishSubject;

import static com.dbeginc.dbweather.utils.holder.ConstantHolder.LOCATION_UPDATE;
import static com.dbeginc.dbweather.utils.holder.ConstantHolder.PERMISSION_EVENT;
import static com.dbeginc.dbweather.utils.holder.ConstantHolder.PREFS_NAME;
import static com.dbeginc.dbweather.utils.holder.ConstantHolder.VOICE_QUERY;

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
    @Named(LOCATION_UPDATE)
    PublishSubject<String> providesLocationUpdateEvent() { return PublishSubject.create(); }

    @Provides
    @AppScope
    @Named(VOICE_QUERY)
    PublishSubject<String> providesVoiceQuery() { return PublishSubject.create(); }

    @Provides
    @AppScope
    @Named(PERMISSION_EVENT)
    PublishSubject<Boolean> providesPermissionEvent() {  return PublishSubject.create(); }

}
