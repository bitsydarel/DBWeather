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
    private Context mContext;
    private static OkHttpClient newsImageOkHttpClient;
    private static Picasso picasso;

    public GetImageDownloader(Context context) {
        mContext = context;
        if (newsImageOkHttpClient == null) {
            newsImageOkHttpClient = new OkHttpClient.Builder()
                    .cache(AppUtil.getCacheDirectory(context))
                    .connectTimeout(25, TimeUnit.SECONDS)
                    .writeTimeout(25, TimeUnit.SECONDS)
                    .readTimeout(45, TimeUnit.SECONDS)
                    .retryOnConnectionFailure(true)
                    .build();
        }
        if (picasso == null) {
            picasso = new Picasso.Builder(mContext)
                    .downloader(new OkHttp3Downloader(newsImageOkHttpClient))
                    .build();
        }
    }

    public Single<Bitmap> getObservableImageDownloader(String url) {
        return Single.create(emitter -> {
            try {
                if (AppUtil.isNetworkAvailable(mContext)) {
                    emitter.onSuccess(picasso
                            .load(url)
                            .get());

                } else { throw new IllegalStateException("No internet available to download the image"); }

            } catch (Exception e) {
                emitter.onError(e);
            }
        });
    }
}
