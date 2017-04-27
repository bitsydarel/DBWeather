package com.darelbitsy.dbweather.models.api.adapters;

import com.darelbitsy.dbweather.models.api.services.NewsService;
import com.darelbitsy.dbweather.models.datatypes.news.NewsResponse;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Singleton;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Darel Bitsy on 18/02/17.
 * News api adapter
 */

@Singleton
public class NewsRestAdapter {
    private static final String NEWSAPI_URL= "https://newsapi.org/";
    private static final String NEWS_APIKEY = "e6e9d4a3f7f24a7a8d16f496df95126f";
    private final NewsService mNewsApi;
    private final Set<String> LASTEST_NEWS_ONLY = new HashSet<>(Arrays.asList(
            "der-tagesspiegel",
            "die-zeit",
            "the-next-web",
            "wirtschafts-woche",
            "handelsblatt"
    ));

    @Inject
    public NewsRestAdapter(final OkHttpClient newsOkHttpClient) {
        final Retrofit restAdapter = new Retrofit.Builder()
                .baseUrl(NEWSAPI_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(newsOkHttpClient)
                .build();

        mNewsApi = restAdapter.create(NewsService.class);
    }

    public Call<NewsResponse> getNews(final String source) {
        if (LASTEST_NEWS_ONLY.contains(source)) {

            return mNewsApi.getNewsFromApiSync(source, "latest", NEWS_APIKEY);
        }
        return mNewsApi.getNewsFromApiSync(source, "top", NEWS_APIKEY);
    }
}