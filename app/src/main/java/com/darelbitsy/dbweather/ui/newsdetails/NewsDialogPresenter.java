package com.darelbitsy.dbweather.ui.newsdetails;

import android.app.Activity;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.darelbitsy.dbweather.models.provider.image.IImageProvider;

/**
 * Created by Darel Bitsy on 26/04/17.
 * News Dialog View Presenter
 */

public class NewsDialogPresenter {

    private final IImageProvider mIImageProvider;

    NewsDialogPresenter(@NonNull final IImageProvider imageProvider) {
        mIImageProvider = imageProvider;
    }

    void getImage(@NonNull final Activity activity, @NonNull final ImageView imageView, @DrawableRes final int errorImage, @NonNull final ProgressBar progressBar, @NonNull final String imageUrl) {
        mIImageProvider.loadImageToView(activity, imageView, errorImage, progressBar, imageUrl);
    }
}
