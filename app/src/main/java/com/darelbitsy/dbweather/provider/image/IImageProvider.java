package com.darelbitsy.dbweather.provider.image;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;

import io.reactivex.Single;

/**
 * Created by Darel Bitsy on 26/04/17.
 * This interface represent an image downloader
 */

public interface IImageProvider {

    Single<Bitmap> getBitmapImage(@NonNull final String url);
}
