package com.darelbitsy.dbweather.dagger.modules;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;

import com.darelbitsy.dbweather.DBWeatherApplication;
import com.darelbitsy.dbweather.models.provider.firebase.FirebaseAnalyticProvider;
import com.darelbitsy.dbweather.models.provider.firebase.IAnalyticProvider;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

import static com.darelbitsy.dbweather.utils.holder.ConstantHolder.PREFS_NAME;
import static com.darelbitsy.dbweather.utils.holder.ConstantHolder.TAG;

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

    @Provides
    public IAnalyticProvider providesFirebaseAnalytics(final Context context) {
        final int googlePlayServicesAvailable = GoogleApiAvailability
                .getInstance()
                .isGooglePlayServicesAvailable(context);

        if (googlePlayServicesAvailable == ConnectionResult.SUCCESS) {
            return new FirebaseAnalyticProvider(context);
        } else {
            return (eventType, messageData) -> Log.i(TAG, "GOOGLE API NOT AVAILABLE, FIREBASE NOT AVAILABLE");
        }
    }
}
