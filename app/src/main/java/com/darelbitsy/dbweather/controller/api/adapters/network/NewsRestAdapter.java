package com.darelbitsy.dbweather.controller.api.adapters.network;

import android.content.Context;

import com.darelbitsy.dbweather.controller.api.services.NewsService;
import com.darelbitsy.dbweather.helper.utility.AppUtil;
import com.darelbitsy.dbweather.model.news.NewsResponse;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Darel Bitsy on 18/02/17.
 */

public class NewsRestAdapter {
    private static final String NEWSAPI_URL= "https://newsapi.org/";
    private static final String NEWS_APIKEY = "e6e9d4a3f7f24a7a8d16f496df95126f";
    private final Retrofit mRestAdapter;
    private NewsService mNewsApi;

    public NewsRestAdapter(Context context) {
        mRestAdapter = new Retrofit.Builder()
                .baseUrl(NEWSAPI_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(AppUtil
                        .newsOkHttpClient
                        .cache(AppUtil.getCacheDirectory(context))
                        .build())
                .build();

        mNewsApi = mRestAdapter.create(NewsService.class);
    }

    public Call<NewsResponse> getNews(String source) {
        return mNewsApi.getNewsFromApiSync(source, "top", NEWS_APIKEY);
    }
}