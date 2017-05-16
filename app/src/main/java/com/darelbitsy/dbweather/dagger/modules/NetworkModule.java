package com.darelbitsy.dbweather.dagger.modules;

import android.content.Context;

import com.bumptech.glide.load.engine.cache.InternalCacheDiskCacheFactory;
import com.darelbitsy.dbweather.models.provider.image.GlideImageProvider;
import com.darelbitsy.dbweather.models.provider.image.IImageProvider;

import java.io.File;
import java.util.concurrent.TimeUnit;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.Cache;
import okhttp3.OkHttpClient;

import static com.darelbitsy.dbweather.utils.holder.ConstantHolder.CACHE_SIZE;

/**
 * Created by Darel Bitsy on 24/04/17.
 */

@Module
public class NetworkModule {

    @Provides
    @Singleton
    Cache providesNetworkCache(final Context context) {
        return new Cache(new File(context.getCacheDir(), "dbweather_cache_dir"),
                CACHE_SIZE);
    }

    @Singleton
    @Provides
    InternalCacheDiskCacheFactory providesGlideCache(final Context context) {
        return new InternalCacheDiskCacheFactory(context, "dbweather_glide_cache", CACHE_SIZE);
    }

    @Provides
    OkHttpClient providesOkHttpClient(final Cache cache) {
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
    IImageProvider providesImageProvider() {
        return new GlideImageProvider();
    }
}