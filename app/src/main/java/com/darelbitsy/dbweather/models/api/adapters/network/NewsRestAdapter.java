package com.darelbitsy.dbweather.models.api.adapters.network;

import android.content.Context;

import com.darelbitsy.dbweather.models.api.services.NewsService;
import com.darelbitsy.dbweather.models.utility.AppUtil;
import com.darelbitsy.dbweather.models.datatypes.news.NewsResponse;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Darel Bitsy on 18/02/17.
 * News api adapter
 */

public class NewsRestAdapter {
    private static final String NEWSAPI_URL= "https://newsapi.org/";
    private static final String NEWS_APIKEY = "e6e9d4a3f7f24a7a8d16f496df95126f";
    private final NewsService mNewsApi;
    private static NewsRestAdapter singletonNewsRestAdapter;

    public static NewsRestAdapter newInstance(final Context context) {
        if (singletonNewsRestAdapter == null) {
            singletonNewsRestAdapter =
                    new NewsRestAdapter(context.getApplicationContext());
        }
        return singletonNewsRestAdapter;
    }

    private NewsRestAdapter(final Context context) {
        final Retrofit restAdapter = new Retrofit.Builder()
                .baseUrl(NEWSAPI_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(AppUtil
                        .newsOkHttpClient
                        .cache(AppUtil.getCacheDirectory(context))
                        .build())
                .build();

        mNewsApi = restAdapter.create(NewsService.class);
    }

    public Call<NewsResponse> getNews(final String source) {
        if ("der-tagesspiegel".equalsIgnoreCase(source) ||
                "die-zeit".equalsIgnoreCase(source) ||
                "the-next-web".equalsIgnoreCase(source) ||
                "wirtschafts-woche".equalsIgnoreCase(source) ||
                "handelsblatt".equalsIgnoreCase(source)) {

            return mNewsApi.getNewsFromApiSync(source, "latest", NEWS_APIKEY);
        }
        return mNewsApi.getNewsFromApiSync(source, "top", NEWS_APIKEY);
    }
}