package com.darelbitsy.dbweather.helper.api;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.darelbitsy.dbweather.adapters.DatabaseOperation;
import com.darelbitsy.dbweather.controller.api.adapters.NewsRestAdapter;
import com.darelbitsy.dbweather.controller.api.adapters.TranslateRestAdapter;
import com.darelbitsy.dbweather.helper.ConstantHolder;
import com.darelbitsy.dbweather.helper.utility.AppUtil;
import com.darelbitsy.dbweather.model.news.News;
import com.darelbitsy.dbweather.model.news.NewsResponse;
import com.darelbitsy.dbweather.services.NewsDatabaseService;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Single;

/**
 * Created by Darel Bitsy on 18/02/17.
 * This class help get Newses in background
 * Observer will only choose how to display it
 * to choose of to display the data
 */

public class GetNewsesHelper {
    private static NewsRestAdapter newsRestAdapter;
    private static TranslateRestAdapter mTranslateRestAdapter;
    private final TranslateHelper mTranslateHelper;
    private static DatabaseOperation mDatabase;
    private final Context mContext;

    public GetNewsesHelper(Context context) {
        if (newsRestAdapter == null) {
            newsRestAdapter = new NewsRestAdapter();
        }

        if (mTranslateRestAdapter == null) {
            mTranslateRestAdapter = new TranslateRestAdapter();
        }
        mTranslateHelper = new TranslateHelper(context);

        if (mDatabase == null) {
            mDatabase = new DatabaseOperation(context);
        }

        mContext = context;
    }

    public Single<ArrayList<News>> getNewsesFromApi() {
        return Single.create(emitter -> {
                try {
                    final List<NewsResponse> newsResponseList = new ArrayList<>();

                    for (String source : ConstantHolder.LIST_OF_SOURCES) {
                        newsResponseList.add(newsRestAdapter
                                .getNews(source, "top")
                                .execute()
                                .body());

                    }

                    ArrayList<News> newses = parseNewses(newsResponseList);

                    Intent intent = new Intent(mContext, NewsDatabaseService.class);
                    intent.putParcelableArrayListExtra(ConstantHolder.NEWS_DATA_KEY, newses);
                    mContext.startService(intent);

                    if (!emitter.isDisposed()) { emitter.onSuccess(newses); }

                } catch (Exception e) { if (!emitter.isDisposed()) { emitter.onError(e); }  }

            });
    }

    public Single<ArrayList<News>> getNewsesFromDatabase(DatabaseOperation database) {
        return Single.create(emitter -> {
            try {
                ArrayList<News> newses = database.getNewFromDatabase();
                if (!emitter.isDisposed()) { emitter.onSuccess(newses); }

            } catch (Exception e) { if (!emitter.isDisposed()) { emitter.onError(e); } }

        });
    }

    private ArrayList<News> parseNewses(List<NewsResponse> newsResponses) {
        ArrayList<News> newses = new ArrayList<>();
        Account[] accounts = null;

        if (AppUtil.isAccountPermissionOn(mContext)) {
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

                    if (!"en".equals(ConstantHolder.USER_LANGUAGE)) {
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

        return newses;
    }
}
