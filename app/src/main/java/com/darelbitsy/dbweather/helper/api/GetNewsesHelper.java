package com.darelbitsy.dbweather.helper.api;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.darelbitsy.dbweather.adapters.DatabaseOperation;
import com.darelbitsy.dbweather.controller.api.adapters.NewsRestAdapter;
import com.darelbitsy.dbweather.controller.api.adapters.TranslateRestAdapter;
import com.darelbitsy.dbweather.helper.ConstantHolder;
import com.darelbitsy.dbweather.helper.utility.AppUtil;
import com.darelbitsy.dbweather.model.news.News;
import com.darelbitsy.dbweather.model.news.NewsResponse;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Darel Bitsy on 18/02/17.
 * This class help get Newses in background
 * Subclass will on override the onPostExecution
 * to choose of to display the data
 */

public class GetNewsesHelper extends AsyncTask<Object, Void, ArrayList<News>> {
    private static NewsRestAdapter mRestAdapter;
    private static TranslateRestAdapter mTranslateRestAdapter;
    private TranslateHelper mTranslateHelper;
    private static DatabaseOperation mDatabase;
    private Context mContext;

    public GetNewsesHelper(Context context) {
        if (mTranslateRestAdapter == null) {
            mTranslateRestAdapter = new TranslateRestAdapter();
        }
        mTranslateHelper = new TranslateHelper(context);
        if (mDatabase == null) {
            mDatabase = new DatabaseOperation(context);
        }
        mContext = context;

    }

    @Override
    protected ArrayList<News> doInBackground(Object... params) {
        final List<NewsResponse> newsResponseList = new ArrayList<>();
        if (mRestAdapter == null) { mRestAdapter = new NewsRestAdapter(); }

        ArrayList<News> newses = new ArrayList<>();

        if (AppUtil.isNetworkAvailable(mContext)) {
            for (String source : ConstantHolder.LIST_OF_SOURCES) {
                try {
                    if ("sky-sports-news".equalsIgnoreCase(source)) {
                        newsResponseList.add(mRestAdapter
                                .getNews(source, "latest")
                                .execute()
                                .body());
                    } else {
                        newsResponseList.add(mRestAdapter
                                .getNews(source, "top")
                                .execute().body());
                    }
                } catch (IOException e) {
                    Log.i(ConstantHolder.TAG, "Error : " + e.getMessage());
                }
            }
            parseNewses(newsResponseList, newses);
            mDatabase.saveNewses(newses);
        }
        return newses;
    }


    private void parseNewses(List<NewsResponse> newsResponses, ArrayList<News> newses) {
        Account[] accounts = null;
        if (ConstantHolder.isAccountPermissionOn) {
             accounts = AccountManager.get(mContext)
                    .getAccountsByType("com.google");
        }
        for (NewsResponse response : newsResponses) {
            for (int i = 0; i < 2; i++) {
                News news = new News();
                news.setNewsSource(response.getSource());
                news.setPublishedAt(response.getArticles().get(i).getPublishedAt());
                String newsTitle = response.getArticles().get(i).getTitle();
                try {

                    if (!ConstantHolder.USER_LANGUAGE.equals("en")) {
                        if (accounts != null) {
                            String account = accounts[0].name;
                            news.setNewsTitle(mTranslateRestAdapter
                                    .translateText(newsTitle, account));

                        } else {
                            news.setNewsTitle(mTranslateRestAdapter
                                    .translateText(newsTitle, ""));

                        }

                        if (news.getNewsTitle().equals(newsTitle) ||
                                news.getNewsTitle().contains("MYMEMORY WARNING")) {
                            news.setNewsTitle(mTranslateHelper
                                    .translateText(newsTitle));
                        }

                    } else {
                        news.setNewsTitle(newsTitle);
                    }

                    news.setArticleUrl(response
                            .getArticles().get(i).getUrl());

                } catch (GeneralSecurityException | IOException e) {
                    Log.i(ConstantHolder.TAG, "Error: "+e.getMessage());
                    news.setNewsTitle(newsTitle);
                }
                newses.add(news);
            }
        }
    }
}
