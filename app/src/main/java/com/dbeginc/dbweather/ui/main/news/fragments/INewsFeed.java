package com.dbeginc.dbweather.ui.main.news.fragments;

import android.content.Context;
import android.support.annotation.NonNull;

import com.dbeginc.dbweather.models.datatypes.news.Article;

import java.util.List;

import javax.annotation.Nonnull;

/**
 * Created by darel on 07.06.17.
 * News Feed View
 */

interface INewsFeed {

    void showNews(@NonNull final List<Article> articles);

    void showDetails(@Nonnull final String url);

    void showError(final Throwable throwable);

    Context getContext();
}
