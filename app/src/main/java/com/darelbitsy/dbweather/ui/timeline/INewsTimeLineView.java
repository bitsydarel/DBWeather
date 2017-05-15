package com.darelbitsy.dbweather.ui.timeline;

import com.darelbitsy.dbweather.models.datatypes.news.Article;

import java.util.List;

import javax.annotation.Nonnull;

/**
 * Created by Bitsy Darel on 15.05.17.
 */

interface INewsTimeLineView {
    void showNewsFeed(@Nonnull final List<Article> articles);

    void showDetails(@Nonnull final String url);

    void showError(final Throwable throwable);
}
