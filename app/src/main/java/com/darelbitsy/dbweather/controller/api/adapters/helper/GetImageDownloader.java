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
 * Image Downloader Class
 * For downloading news image
 */

public class GetImageDownloader {
    private final Picasso mPicasso;
    private static GetImageDownloader singletonGetImageDownloader;
    private final Context mContext;

    public static GetImageDownloader newInstance(final Context context) {
        if (singletonGetImageDownloader == null) {
            singletonGetImageDownloader = new GetImageDownloader(context.getApplicationContext());
        }
        return singletonGetImageDownloader;
    }

    private GetImageDownloader(final Context context) {
        mContext = context;
        final OkHttpClient newsImageOkHttpClient = new OkHttpClient.Builder()
                .cache(AppUtil.getCacheDirectory(context))
                .connectTimeout(25, TimeUnit.SECONDS)
                .writeTimeout(25, TimeUnit.SECONDS)
                .readTimeout(45, TimeUnit.SECONDS)
                .retryOnConnectionFailure(true)
                .build();

        mPicasso = new Picasso.Builder(context)
                    .downloader(new OkHttp3Downloader(newsImageOkHttpClient))
                    .build();
    }

    public Single<Bitmap> getObservableImageDownloader(final String url) {
        return Single.create(emitter -> {
            try {
                if (AppUtil.isNetworkAvailable(mContext)
                        && !emitter.isDisposed()) {

                    emitter.onSuccess(mPicasso
                            .load(url)
                            .get());

                } else { throw new IllegalStateException("No internet available to download the image"); }

            } catch (final Exception e) {
                if (!emitter.isDisposed()) { emitter.onError(e); }
            }
        });
    }
}