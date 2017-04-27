package com.darelbitsy.dbweather.views.activities;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;

/**
 * Created by Darel Bitsy on 26/04/17.
 */

public interface INewsDialogView {
    void showImage(@NonNull final Bitmap image);

    void showDefaultImage();
}
