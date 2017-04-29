package com.darelbitsy.dbweather.provider.image;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;

import com.squareup.picasso.Picasso;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Single;

/**
 * Created by Darel Bitsy on 12/03/17.
 * Image Downloader Class
 * For downloading news image
 */

@Singleton
public class PicassoImageProvider implements IImageProvider {
    private final Picasso mPicasso;

    @Inject
    public PicassoImageProvider(@NonNull final Picasso picasso) {
        mPicasso = picasso;
    }

    @Override
    public Single<Bitmap> getBitmapImage(@NonNull final String url) {
        return Single.fromCallable(() -> mPicasso
                .load(url)
                .get());
    }
}