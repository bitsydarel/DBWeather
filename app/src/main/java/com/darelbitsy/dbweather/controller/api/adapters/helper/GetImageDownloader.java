package com.darelbitsy.dbweather.controller.api.adapters.helper;

import android.content.Context;
import android.graphics.Bitmap;

import com.darelbitsy.dbweather.helper.utility.AppUtil;
import com.jakewharton.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

import java.util.concurrent.TimeUnit;

import io.reactivex.Single;
import okhttp3.OkHttpClient;

/**
 * Created by Darel Bitsy on 12/03/17.
 */

public class GetImageDownloader {
    private static OkHttpClient newsImageOkHttpClient;
    private final Picasso mPicasso;

    public GetImageDownloader(Context context) {
        if (newsImageOkHttpClient == null) {
            newsImageOkHttpClient = new OkHttpClient.Builder()
                    .cache(AppUtil.getCacheDirectory(context))
                    .connectTimeout(25, TimeUnit.SECONDS)
                    .writeTimeout(25, TimeUnit.SECONDS)
                    .readTimeout(45, TimeUnit.SECONDS)
                    .retryOnConnectionFailure(true)
                    .build();
        }
        mPicasso = new Picasso.Builder(context)
                    .downloader(new OkHttp3Downloader(newsImageOkHttpClient))
                    .build();
    }

    public Single<Bitmap> getObservableImageDownloader(String url, Context context) {
        return Single.create(emitter -> {
            try {
                if (AppUtil.isNetworkAvailable(context)) {
                    emitter.onSuccess(mPicasso
                            .load(url)
                            .get());

                } else { throw new IllegalStateException("No internet available to download the image"); }

            } catch (Exception e) {
                emitter.onError(e);
            }
        });
    }
}
