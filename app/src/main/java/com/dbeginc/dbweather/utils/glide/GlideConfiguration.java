/*
 *  Copyright (C) 2017 Darel Bitsy
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License
 */

package com.dbeginc.dbweather.utils.glide;

import android.content.Context;

import com.bumptech.glide.Glide;
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
import com.dbeginc.dbweather.DBWeatherApplication;

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
    public void registerComponents(Context context, Glide glide, Registry registry) {
        super.registerComponents(context, glide, registry);
        registry.replace(GlideUrl.class, InputStream.class, new OkHttpUrlLoader.Factory(okHttpClient));
    }
}
