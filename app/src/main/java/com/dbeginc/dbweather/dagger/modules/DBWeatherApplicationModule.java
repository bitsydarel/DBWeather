package com.dbeginc.dbweather.dagger.modules;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.support.v4.util.Pair;
import android.util.Log;

import com.dbeginc.dbweather.DBWeatherApplication;
import com.dbeginc.dbweather.models.datatypes.geonames.GeoName;
import com.dbeginc.dbweather.models.datatypes.news.Article;
import com.dbeginc.dbweather.models.datatypes.news.LiveNews;
import com.dbeginc.dbweather.models.datatypes.weather.WeatherData;
import com.dbeginc.dbweather.models.provider.firebase.FirebaseAnalyticProvider;
import com.dbeginc.dbweather.models.provider.firebase.IAnalyticProvider;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import io.reactivex.subjects.PublishSubject;

import static com.dbeginc.dbweather.utils.holder.ConstantHolder.PREFS_NAME;
import static com.dbeginc.dbweather.utils.holder.ConstantHolder.TAG;

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
    DBWeatherApplication providesApplication() {
        return mDbWeatherApplication;
    }

    @Provides
    @Singleton
    Context providesApplicationContext() { return mDbWeatherApplication.getApplicationContext(); }

    @Provides
    @Singleton
    Resources providesAppResource(final Context context) { return context.getResources(); }

    @Provides
    @Singleton
    SharedPreferences providesSharedPreferences(final Context context) {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    @Provides
    @Singleton
    PublishSubject<String> providesLocationUpdateEvent() { return PublishSubject.create(); }

    @Provides
    @Singleton
    PublishSubject<WeatherData> provideWeatherUpdateEvent() { return PublishSubject.create(); }

    @Provides
    @Singleton
    PublishSubject<List<Article>> provideNewsUpdateEvent() { return PublishSubject.create(); }

    @Singleton
    @Provides
    PublishSubject<LiveNews> providesLiveSelectedEvent() { return PublishSubject.create(); }

    @Singleton
    @Provides
    PublishSubject<Pair<DataSnapshot, String>> providesLiveSourceDatabase() { return PublishSubject.create(); }

    @Provides
    IAnalyticProvider providesFirebaseAnalytics(final Context context) {
        final int googlePlayServicesAvailable = GoogleApiAvailability
                .getInstance()
                .isGooglePlayServicesAvailable(context);

        if (googlePlayServicesAvailable == ConnectionResult.SUCCESS) {
            return new FirebaseAnalyticProvider(context);
        } else {
            return (eventType, messageData) -> Log.i(TAG, "GOOGLE API NOT AVAILABLE, FIREBASE NOT AVAILABLE");
        }
    }

    @Provides
    @Singleton
    List<GeoName> providesLocationsList() { return new ArrayList<>(); }
}
