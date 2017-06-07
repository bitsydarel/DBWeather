package com.dbeginc.dbweather.ui.main.news.adapter;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.util.DiffUtil;

import com.dbeginc.dbweather.models.datatypes.news.Article;

import java.util.List;

import javax.annotation.Nonnull;

/**
 * Created by Bitsy Darel on 15.05.17.
 */

public class ArticleDiffCallback extends DiffUtil.Callback {

    static final String OLD_INDEX = "OLD_INDEX";
    private final List<Article> oldListOfArticle;
    private final List<Article> newListOfArticle;

    ArticleDiffCallback(@Nonnull final List<Article> oldList, @Nonnull final List<Article> newList) {
        this.oldListOfArticle = oldList;
        this.newListOfArticle = newList;
    }

    @Override
    public int getOldListSize() { return oldListOfArticle.size(); }

    @Override
    public int getNewListSize() { return newListOfArticle.size(); }

    @Override
    public boolean areItemsTheSame(final int oldItemPosition, final int newItemPosition) {
        return oldListOfArticle.get(oldItemPosition).getAuthor()
                .equalsIgnoreCase(newListOfArticle.get(newItemPosition).getAuthor());
    }

    @Override
    public boolean areContentsTheSame(final int oldItemPosition, final int newItemPosition) {
        return oldListOfArticle.get(oldItemPosition).equals(newListOfArticle.get(newItemPosition));
    }

    @Nullable
    @Override
    public Object getChangePayload(final int oldItemPosition, final int newItemPosition) {
        final Bundle bundle = new Bundle();
        bundle.putInt(OLD_INDEX, oldItemPosition);
        return bundle;
    }
}
