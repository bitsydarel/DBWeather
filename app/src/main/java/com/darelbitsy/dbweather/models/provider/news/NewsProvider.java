package com.darelbitsy.dbweather.models.provider.news;

import io.reactivex.Single;

/**
 * Created by Darel Bitsy on 22/04/17.
 */

public interface NewsProvider<TYPE> {

    Single<TYPE> getNews();
}
