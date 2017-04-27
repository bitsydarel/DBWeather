package com.darelbitsy.dbweather.provider.news;

import com.darelbitsy.dbweather.models.datatypes.news.Article;

import java.util.List;

import io.reactivex.Single;

/**
 * Created by Darel Bitsy on 22/04/17.
 * This interface represent an news provider
 */

public interface INewsProvider {

    Single<List<Article>> getNews();
}
