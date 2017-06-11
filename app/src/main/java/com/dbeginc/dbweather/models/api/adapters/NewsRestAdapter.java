package com.dbeginc.dbweather.models.api.adapters;

import com.dbeginc.dbweather.BuildConfig;
import com.dbeginc.dbweather.models.api.services.NewsService;
import com.dbeginc.dbweather.models.datatypes.news.NewsResponse;

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
    private static final String NEWS_API_URL = "https://newsapi.org/";
    private final NewsService mNewsApi;
    private final Set<String> LAST_NEWS_ONLY = new HashSet<>(Arrays.asList(
            "der-tagesspiegel",
            "die-zeit",
            "the-next-web",
            "wirtschafts-woche",
            "handelsblatt"
    ));

    @Inject
    public NewsRestAdapter(final OkHttpClient newsOkHttpClient) {
        final Retrofit restAdapter = new Retrofit.Builder()
                .baseUrl(NEWS_API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(newsOkHttpClient)
                .build();

        mNewsApi = restAdapter.create(NewsService.class);
    }

    public Call<NewsResponse> getNews(final String source) {
        if (LAST_NEWS_ONLY.contains(source)) {

            return mNewsApi.getNewsFromApiSync(source, "latest", BuildConfig.NEWS_API_KEY);
        }
        return mNewsApi.getNewsFromApiSync(source, "top", BuildConfig.NEWS_API_KEY);
    }
}