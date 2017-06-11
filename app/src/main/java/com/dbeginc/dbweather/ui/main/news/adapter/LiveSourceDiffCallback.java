package com.dbeginc.dbweather.ui.main.news.adapter;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.util.DiffUtil;

import com.dbeginc.dbweather.models.datatypes.news.LiveNews;

import java.util.List;

/**
 * Created by darel on 10.06.17.
 * Live Source
 */

public class LiveSourceDiffCallback extends DiffUtil.Callback {

    static final String OLD_INDEX = "OLD_INDEX";
    private final List<LiveNews> oldListOfLiveSource;
    private final List<LiveNews> newListOfLiveSource;

    public LiveSourceDiffCallback(@NonNull final List<LiveNews> oldListOfLiveSource, final List<LiveNews> newListOfLiveSource) {
        this.oldListOfLiveSource = oldListOfLiveSource;
        this.newListOfLiveSource = newListOfLiveSource;
    }

    @Override
    public int getOldListSize() {
        return oldListOfLiveSource.size();
    }

    @Override
    public int getNewListSize() {
        return newListOfLiveSource.size();
    }

    @Override
    public boolean areItemsTheSame(final int oldItemPosition, final int newItemPosition) {
        return oldListOfLiveSource.get(oldItemPosition).liveSource.get()
                .equalsIgnoreCase(newListOfLiveSource.get(newItemPosition).liveSource.get());
    }

    @Override
    public boolean areContentsTheSame(final int oldItemPosition, final int newItemPosition) {
        return oldListOfLiveSource.get(oldItemPosition).liveUrl.get()
                .equalsIgnoreCase(newListOfLiveSource.get(newItemPosition).liveUrl.get());
    }

    @Nullable
    @Override
    public Object getChangePayload(final int oldItemPosition, final int newItemPosition) {
        final Bundle bundle = new Bundle();
        bundle.putInt(OLD_INDEX, oldItemPosition);
        return bundle;
    }
}
