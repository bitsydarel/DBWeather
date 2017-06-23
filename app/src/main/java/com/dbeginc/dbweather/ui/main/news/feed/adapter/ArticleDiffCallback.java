package com.dbeginc.dbweather.ui.main.news.feed.adapter;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.util.DiffUtil;

import com.dbeginc.dbweather.models.datatypes.news.Article;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import static com.dbeginc.dbweather.utils.holder.ConstantHolder.OLD_INDEX;

/**
 * Created by Bitsy Darel on 15.05.17.
 * Article Update calculator
 */

public class ArticleDiffCallback extends DiffUtil.Callback {

    private final List<Object> oldListOfArticle = new ArrayList<>();
    private final List<Object> newListOfArticle = new ArrayList<>();

    ArticleDiffCallback(@Nonnull final List<Object> oldList, @Nonnull final List<Article> newList) {
        this.oldListOfArticle.addAll(oldList);
        this.newListOfArticle.addAll(newList);
    }

    @Override
    public int getOldListSize() { return oldListOfArticle.size(); }

    @Override
    public int getNewListSize() { return newListOfArticle.size(); }

    @Override
    public boolean areItemsTheSame(final int oldItemPosition, final int newItemPosition) {
        if (oldItemPosition == 0 || oldItemPosition % 3 != 0 ) {
            final Article oldArticle = (Article) oldListOfArticle.get(oldItemPosition);
            final Article newArticle = (Article) newListOfArticle.get(newItemPosition);

            if (oldArticle.getArticleUrl() != null) {
                return oldArticle.getArticleUrl()
                        .equalsIgnoreCase(newArticle.getArticleUrl());
            } else {
                return oldArticle.getTitle().equalsIgnoreCase(newArticle.getTitle());
            }

        } else { return true; }
    }

    @Override
    public boolean areContentsTheSame(final int oldItemPosition, final int newItemPosition) {
        if (oldItemPosition == 0  || oldItemPosition % 3 != 0) {
            final Article oldArticle = (Article) oldListOfArticle.get(oldItemPosition);
            final Article newArticle = (Article) newListOfArticle.get(newItemPosition);

            final boolean isSame = oldArticle.getAuthor().equalsIgnoreCase(newArticle.getAuthor())
                    && oldArticle.getTitle().equalsIgnoreCase(newArticle.getTitle())
                    && oldArticle.getDescription().equalsIgnoreCase(newArticle.getDescription());

            if (oldArticle.getPublishedAt() != null && newArticle.getPublishedAt() != null) {
                return isSame && oldArticle.getPublishedAt().equalsIgnoreCase(newArticle.getPublishedAt());

            } else {
                return oldArticle.getPublishedAt() == null && newArticle.getPublishedAt() == null && isSame;
            }

        } else { return true; }
    }

    @Nullable
    @Override
    public Object getChangePayload(final int oldItemPosition, final int newItemPosition) {
        final Bundle bundle = new Bundle();
        bundle.putInt(OLD_INDEX, oldItemPosition);
        return bundle;
    }
}
