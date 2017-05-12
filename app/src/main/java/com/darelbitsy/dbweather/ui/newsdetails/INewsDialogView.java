package com.darelbitsy.dbweather.ui.newsdetails;

import android.support.annotation.NonNull;

/**
 * Created by Darel Bitsy on 26/04/17.
 */

public interface INewsDialogView {
    void showImage(@NonNull final String url);

    void openArticleInBrowser(@NonNull final String articleUrl);

    void closeView();
}
