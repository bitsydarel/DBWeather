package com.darelbitsy.dbweather.models.provider.image;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by Bitsy Darel on 08.05.17.
 */

@Singleton
public class GlideImageProvider implements IImageProvider {

    @Inject
    public GlideImageProvider() {}

    @Override
    public void loadImageToView(@NonNull final Activity activity, @NonNull final ImageView imageView, @NonNull final String url) {
        loadImageToView(activity, imageView, 0, null, url);
    }

    @Override
    public void loadImageToView(@NonNull final Activity activity, @NonNull final ImageView imageView, @NonNull final ProgressBar progressBar, @NonNull final String url) {
        loadImageToView(activity, imageView, 0, null, url);
    }

    @Override
    public void loadImageToView(@NonNull final Activity activity, @NonNull final ImageView imageView, @DrawableRes final int errorImage, final ProgressBar progressBar, @NonNull final String url) {
        Glide.with(activity)
                .load(url)
                .apply(RequestOptions.errorOf(errorImage))
                .apply(RequestOptions.centerCropTransform().centerCrop())
                .apply(RequestOptions.skipMemoryCacheOf(false))
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable final GlideException e, final Object o, final Target<Drawable> target, final boolean b) {
                        if (progressBar != null) { progressBar.setVisibility(View.GONE); }
                        return false;
                    }
                    @Override
                    public boolean onResourceReady(final Drawable drawable, final Object o, final Target<Drawable> target, final DataSource dataSource, final boolean b) {
                        if (progressBar != null) { progressBar.setVisibility(View.GONE); }
                        return false;
                    }
                })
                .into(imageView);
    }
}
