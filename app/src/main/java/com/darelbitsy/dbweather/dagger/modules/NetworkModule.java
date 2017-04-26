package com.darelbitsy.dbweather.dagger.modules;

import android.content.Context;

import com.darelbitsy.dbweather.extensions.holder.ConstantHolder;
import com.darelbitsy.dbweather.provider.news.NetworkNewsProvider;
import com.darelbitsy.dbweather.provider.weather.NetworkWeatherProvider;

import java.io.File;
import java.util.concurrent.TimeUnit;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.Cache;
import okhttp3.OkHttpClient;

/**
 * Created by Darel Bitsy on 24/04/17.
 */

@Module
public class NetworkModule {

    @Provides
    @Singleton
    public NetworkWeatherProvider providesNetworkWeatherProvider(final Context context) {
        return new NetworkWeatherProvider(context);
    }

    @Provides
    @Singleton
    public NetworkNewsProvider providesNetworkNewsProvider(final Context context) {
        return new NetworkNewsProvider(context);
    }

    @Provides
    @Singleton
    public Cache providesNetworkCache(final Context context) {
        return new Cache(new File(context.getCacheDir(), "dbweather_cache_dir"),
                ConstantHolder.CACHE_SIZE);
    }

    @Provides
    @Singleton
    public OkHttpClient providesOkHttpClient(final Cache cache) {
        return new OkHttpClient.Builder()
                .connectTimeout(25, TimeUnit.SECONDS)
                .writeTimeout(25, TimeUnit.SECONDS)
                .readTimeout(45, TimeUnit.SECONDS)
                .retryOnConnectionFailure(true)
                .cache(cache)
                .build();
    }
}