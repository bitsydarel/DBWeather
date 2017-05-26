package com.dbeginc.dbweather;

import android.content.Context;

import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.Registry;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.integration.okhttp3.OkHttpUrlLoader;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.cache.InternalCacheDiskCacheFactory;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.module.AppGlideModule;
import com.bumptech.glide.request.RequestOptions;

import java.io.InputStream;

import javax.inject.Inject;

import okhttp3.OkHttpClient;

/**
 * Created by Bitsy Darel on 08.05.17.
 * Configuration class for Glide
 */

@GlideModule
public class GlideConfiguration extends AppGlideModule {
    @Inject
    OkHttpClient okHttpClient;

    @Inject
    InternalCacheDiskCacheFactory cacheDiskCacheFactory;

    public GlideConfiguration() {
        super();
        DBWeatherApplication.getComponent()
                .inject(this);
    }

    /**
     * Apply Configuration to Glide Builder
     * @param context
     * @param builder Glide Builder
     */
    @Override
    public void applyOptions(final Context context, final GlideBuilder builder) {
        super.applyOptions(context, builder);
        builder.setDiskCache(cacheDiskCacheFactory)
                .setDefaultRequestOptions(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.ALL))
                .setDefaultRequestOptions(RequestOptions.formatOf(DecodeFormat.PREFER_ARGB_8888));
    }

    @Override
    public void registerComponents(final Context context, final Registry registry) {
        registry.replace(GlideUrl.class, InputStream.class, new OkHttpUrlLoader.Factory(okHttpClient));
    }

}
