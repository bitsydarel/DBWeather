package com.darelbitsy.dbweather.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.util.DiffUtil;

import com.darelbitsy.dbweather.models.datatypes.news.Article;

import java.util.List;

import javax.annotation.Nonnull;

import static com.darelbitsy.dbweather.utils.holder.ConstantHolder.INDEX;
import static com.darelbitsy.dbweather.utils.holder.ConstantHolder.NEWS_DATA_KEY;

/**
 * Created by Bitsy Darel on 15.05.17.
 */

public class ArticleDiffCallback extends DiffUtil.Callback {

    private final List<Article> oldListOfArticle;
    private final List<Article> newListOfArticle;

    public ArticleDiffCallback(@Nonnull final List<Article> oldList, @Nonnull final List<Article> newList) {
        this.oldListOfArticle = oldList;
        this.newListOfArticle = newList;
    }

    @Override
    public int getOldListSize() { return oldListOfArticle.size(); }

    @Override
    public int getNewListSize() { return newListOfArticle.size(); }

    @Override
    public boolean areItemsTheSame(final int oldItemPosition, final int newItemPosition) {
        return oldListOfArticle.get(oldItemPosition).getArticleUrl()
                .equalsIgnoreCase(newListOfArticle.get(newItemPosition).getArticleUrl());
    }

    @Override
    public boolean areContentsTheSame(final int oldItemPosition, final int newItemPosition) {
        final Article oldArticle = oldListOfArticle.get(oldItemPosition);
        final Article newArticle = newListOfArticle.get(newItemPosition);

        return oldArticle.getTitle().equalsIgnoreCase(newArticle.getTitle()) &&
                oldArticle.getArticleUrl().equalsIgnoreCase(newArticle.getArticleUrl());
    }

    @Nullable
    @Override
    public Object getChangePayload(final int oldItemPosition, final int newItemPosition) {
        final Bundle bundle = new Bundle();

        bundle.putInt(INDEX, oldItemPosition);
        bundle.putParcelable(NEWS_DATA_KEY, newListOfArticle.get(newItemPosition));

        return bundle;
    }
}
