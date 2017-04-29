package com.darelbitsy.dbweather.dagger.modules;

import android.content.Context;

import com.darelbitsy.dbweather.utils.holder.ConstantHolder;
import com.darelbitsy.dbweather.models.provider.image.IImageProvider;
import com.darelbitsy.dbweather.models.provider.image.PicassoImageProvider;
import com.jakewharton.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

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
    public Picasso providesPicasso(final Context context, final OkHttpClient okHttpClient) {
        return new Picasso.Builder(context)
                .downloader(new OkHttp3Downloader(okHttpClient))
                .build();
    }

    @Provides
    @Singleton
    public Cache providesNetworkCache(final Context context) {
        return new Cache(new File(context.getCacheDir(), "dbweather_cache_dir"),
                ConstantHolder.CACHE_SIZE);
    }

    @Provides
    public OkHttpClient providesOkHttpClient(final Cache cache) {
        return new OkHttpClient.Builder()
                .connectTimeout(25, TimeUnit.SECONDS)
                .writeTimeout(25, TimeUnit.SECONDS)
                .readTimeout(45, TimeUnit.SECONDS)
                .retryOnConnectionFailure(true)
                .cache(cache)
                .build();
    }

    @Provides
    @Singleton
    public IImageProvider providesImageProvider(final Picasso picasso) {
        return new PicassoImageProvider(picasso);
    }
}