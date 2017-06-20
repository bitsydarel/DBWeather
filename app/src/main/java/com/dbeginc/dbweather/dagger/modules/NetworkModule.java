package com.dbeginc.dbweather.dagger.modules;

import android.content.Context;

import com.bumptech.glide.load.engine.cache.InternalCacheDiskCacheFactory;
import com.dbeginc.dbweather.models.provider.image.GlideImageProvider;
import com.dbeginc.dbweather.models.provider.image.IImageProvider;
import com.dbeginc.dbweather.utils.holder.ConstantHolder;

import java.io.File;
import java.util.concurrent.TimeUnit;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.Cache;
import okhttp3.OkHttpClient;

import static com.dbeginc.dbweather.utils.holder.ConstantHolder.CACHE_SIZE;

/**
 * Created by Darel Bitsy on 24/04/17.
 */

@Module
public class NetworkModule {

    @Provides
    @Singleton
    Cache providesNetworkCache(final Context context) {
        return new Cache(new File(context.getCacheDir(), ConstantHolder.CACHE_NAME),
                CACHE_SIZE);
    }

    @Singleton
    @Provides
    InternalCacheDiskCacheFactory providesGlideCache(final Context context) {
        return new InternalCacheDiskCacheFactory(context, ConstantHolder.GlIDE_CACHE_NAME, CACHE_SIZE);
    }

    @Provides
    OkHttpClient providesOkHttpClient(final Cache cache) {
        return new OkHttpClient.Builder()
                .connectTimeout(35, TimeUnit.SECONDS)
                .writeTimeout(35, TimeUnit.SECONDS)
                .readTimeout(55, TimeUnit.SECONDS)
                .retryOnConnectionFailure(true)
                .cache(cache)
                .build();
    }

    @Provides
    @Singleton
    IImageProvider providesImageProvider() { return new GlideImageProvider(); }
}