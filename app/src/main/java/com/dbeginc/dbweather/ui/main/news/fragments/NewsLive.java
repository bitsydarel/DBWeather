package com.dbeginc.dbweather.ui.main.news.fragments;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dbeginc.dbweather.models.datatypes.news.Article;
import com.dbeginc.dbweather.ui.BaseFragment;

import java.util.ArrayList;
import java.util.List;

import static com.dbeginc.dbweather.utils.holder.ConstantHolder.NEWS_DATA_KEY;

/**
 * Created by darel on 07.06.17.
 */

public class NewsLive extends BaseFragment {

    public static synchronized NewsLive newInstance(@NonNull final List<Article> articles) {
        final NewsLive newsLiveFragment = new NewsLive();
        final Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(NEWS_DATA_KEY, (ArrayList<? extends Parcelable>) articles);
        newsLiveFragment.setArguments(bundle);
        return newsLiveFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }
}
