package com.darelbitsy.dbweather.dagger.modules;

import android.content.Context;

import com.darelbitsy.dbweather.models.api.adapters.network.GeoNamesAdapter;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;

/**
 * Created by Darel Bitsy on 26/04/17.
 * This Provides all the retrofit instance needed
 */

@Module
public class RetrofitModule {

    @Provides
    @Singleton
    public GeoNamesAdapter providesGeoNameAdapter(final Context context, final OkHttpClient okHttpClient) {
        return new GeoNamesAdapter(context, okHttpClient);
    }
}
