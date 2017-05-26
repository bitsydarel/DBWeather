package com.dbeginc.dbweather.ui.introduction;

import android.graphics.Color;
import android.support.v4.util.Pair;
import android.util.SparseArray;

import com.dbeginc.dbweather.R;

/**
 * Created by Bitsy Darel on 02.05.17.
 * Background color Manager
 */

public class PagePresenter {
    private final SparseArray<Pair<Integer, Integer>> pageWithColors = new SparseArray<>();

    PagePresenter() {
        pageWithColors.put(0, new Pair<>(R.layout.intro_welcome_page, Color.parseColor("#1976D2")));
        pageWithColors.put(1, new Pair<>(R.layout.intro_location_permission_page, Color.parseColor("#229ABC")));
        pageWithColors.put(2, new Pair<>(R.layout.intro_news_permission_page, Color.parseColor("#1E3F54")));
        pageWithColors.put(3, new Pair<>(R.layout.last_intro_fragment_page, Color.parseColor("#41D4D7")));
    }

    int getBackgroundColor(final int pageIndex) {
        return pageWithColors.get(pageIndex).second;
    }

    public int getPageLayout(final int pageIndex) {
        return pageWithColors.get(pageIndex).first;
    }

    public int getPageCount() {
        return pageWithColors.size();
    }
}
