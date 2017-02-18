package com.darelbitsy.dbweather.helper;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.darelbitsy.dbweather.adapters.DatabaseOperation;
import com.darelbitsy.dbweather.news.News;
import com.darelbitsy.dbweather.ui.MainActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Darel Bitsy on 16/02/17.
 */

public class GetNewsData extends AsyncTask<Object, Boolean, List<String>>  {
    private final DatabaseOperation mDatabase;
    private News[] mNewses;
    private NewsCallHelper mNewsCallHelper;
    private final TranslateHelper mTranslateHelper;
    private final String userLang = Locale.getDefault().getLanguage();

    public GetNewsData(Context context) {
        mDatabase = new DatabaseOperation(context);
        mNewsCallHelper = new NewsCallHelper(context, mDatabase);
        mTranslateHelper = new TranslateHelper(context);
    }

    @Override
    protected List<String> doInBackground(Object... params) {
        mNewsCallHelper.call();
        return mNewsCallHelper.getNewsFromJson();
    }

    @Override
    protected void onPostExecute(List<String> newsList) {
        try {
            mNewses = getNewses(newsList);
        } catch (JSONException | MalformedURLException e) {
            mNewses = new News[0];
        }
    }

    private News[] getNewses(List<String> newsList) throws JSONException, MalformedURLException {
        News [] newses = new News[13];
        AtomicInteger index = new AtomicInteger(0);
        for (String someNews : newsList) {
            JSONObject newsData = new JSONObject(someNews);
            JSONArray articles = newsData.getJSONArray("articles");
            String newsSource = newsData.getString("source");
            for (int i = 0; i < 2; i++) {
                News news = new News();
                news.setNewsTitle(newsSource);
                news.setNewsTitle(articles.getJSONObject(i).getString("title"));
                news.setArticleUrl(articles.getJSONObject(i).getString("url"));
                newses[index.getAndIncrement()] = news;
            }
        }
        return newses;
    }

    public News[] getNewses() {
        if (new Locale("en").getLanguage().equals(userLang)) {
            return Arrays.copyOf(mNewses, mNewses.length);
        }

        if (mNewses.length > 2) {
            for (News news : mNewses) {
                try {
                    news.setNewsTitle(mTranslateHelper
                            .translateText(news.getNewsTitle()));

                } catch (GeneralSecurityException | IOException e) {
                    Log.i(MainActivity.TAG, "Error: " + e.getMessage());
                }
            }
        }
        return Arrays.copyOf(mNewses, mNewses.length);
    }
}
