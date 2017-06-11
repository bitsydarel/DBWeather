package com.dbeginc.dbweather.ui.main.news;

import android.content.Context;
import android.support.annotation.NonNull;

import com.dbeginc.dbweather.models.datatypes.news.Article;

import java.util.List;

import javax.annotation.Nonnull;

/**
 * Created by darel on 29.05.17.
 * News View
 */

public interface INewsView {

    void showError(final Throwable throwable);

    void handleRequestUpdate(@NonNull final String status);

    void refreshRequest();

    Context getContext();
}
