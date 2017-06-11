package com.dbeginc.dbweather.ui.main.news.fragments;

import android.support.annotation.NonNull;

import com.dbeginc.dbweather.models.datatypes.news.LiveNews;

import java.util.List;

/**
 * Created by darel on 07.06.17.
 */

interface INewsLive {
    void setupLiveView();

    void showLiveView(@NonNull final LiveNews liveNews);

    void updateLiveData(@NonNull final List<LiveNews> liveNewsList);
}
