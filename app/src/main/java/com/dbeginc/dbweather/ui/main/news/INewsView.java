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
    void showNews(@NonNull final List<Article> articles);

    void showDetails(@Nonnull final String url);

    void showError(final Throwable throwable);

    Context getContext();
}
