package com.dbeginc.dbweather.models.provider.image;

import android.app.Activity;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.widget.ImageView;
import android.widget.ProgressBar;

/**
 * Created by Darel Bitsy on 26/04/17.
 * This interface represent an image downloader
 */

public interface IImageProvider {
    void loadImageToView(@NonNull final Activity activity, @NonNull final ImageView imageView, @DrawableRes final int errorImage, final ProgressBar progressBar, @NonNull final String url);
}
