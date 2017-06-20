package com.dbeginc.dbweather.ui.main.news.live;

import com.dbeginc.dbweather.DBWeatherApplication;
import com.dbeginc.dbweather.models.datatypes.news.LiveNews;

import javax.inject.Inject;

import io.reactivex.subjects.PublishSubject;

/**
 * Created by darel on 08.06.17.
 */

public class BaseLivePresenter {

    @Inject
    protected PublishSubject<LiveNews> videoSelectedEvent = PublishSubject.create();
    protected final PublishSubject<String> liveVideoUpdateEvent = PublishSubject.create();

    protected BaseLivePresenter() {
        DBWeatherApplication.getComponent().inject(this);
    }
}
