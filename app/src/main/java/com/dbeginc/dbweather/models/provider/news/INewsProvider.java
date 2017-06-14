package com.dbeginc.dbweather.models.provider.news;

import com.dbeginc.dbweather.models.datatypes.news.Article;
import com.dbeginc.dbweather.models.datatypes.news.Sources;

import java.util.List;

import io.reactivex.Single;

/**
 * Created by Darel Bitsy on 22/04/17.
 * This interface represent an news provider
 */

public interface INewsProvider {

    Single<Sources> getSourcesList();

    Single<List<Article>> getNews();
}
