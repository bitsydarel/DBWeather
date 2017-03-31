package com.darelbitsy.dbweather.controller.api.adapters.helper;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.darelbitsy.dbweather.adapters.database.DatabaseOperation;
import com.darelbitsy.dbweather.controller.api.adapters.network.NewsRestAdapter;
import com.darelbitsy.dbweather.controller.api.adapters.network.TranslateRestAdapter;
import com.darelbitsy.dbweather.helper.holder.ConstantHolder;
import com.darelbitsy.dbweather.helper.utility.AppUtil;
import com.darelbitsy.dbweather.model.news.Article;
import com.darelbitsy.dbweather.model.news.NewsResponse;
import com.darelbitsy.dbweather.helper.services.NewsDatabaseService;

import org.apache.commons.lang3.StringEscapeUtils;

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
            newsRestAdapter = new NewsRestAdapter(context);
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

    public Single<ArrayList<Article>> getNewsesFromApi() {
        return Single.create(emitter -> {
                try {
                    final List<NewsResponse> newsResponseList = new ArrayList<>();

                    for (String source : ConstantHolder.LIST_OF_SOURCES) {
                        newsResponseList.add(newsRestAdapter
                                .getNews(source, "top")
                                .execute()
                                .body());

                    }

                    ArrayList<Article> newses = parseNewses(newsResponseList);

                    Intent intent = new Intent(mContext, NewsDatabaseService.class);
                    intent.putParcelableArrayListExtra(ConstantHolder.NEWS_DATA_KEY, newses);
                    mContext.startService(intent);

                    if (!emitter.isDisposed()) { emitter.onSuccess(newses); }

                } catch (Exception e) { if (!emitter.isDisposed()) { emitter.onError(e); }  }

            });
    }

    public Single<ArrayList<Article>> getNewsesFromDatabase(DatabaseOperation database) {
        return Single.create(emitter -> {
            try {
                ArrayList<Article> newses = database.getNewFromDatabase();
                if (!emitter.isDisposed()) { emitter.onSuccess(newses); }

            } catch (Exception e) { if (!emitter.isDisposed()) { emitter.onError(e); } }

        });
    }

    private ArrayList<Article> parseNewses(List<NewsResponse> newsResponses) {
        ArrayList<Article> newses = new ArrayList<>();
        Account[] accounts = null;

        if (AppUtil.isAccountPermissionOn(mContext)) {
            accounts = AccountManager.get(mContext)
                    .getAccountsByType("com.google");
        }

        for (NewsResponse response : newsResponses) {
            for (int i = 0; i < 2; i++) {
                Article news = new Article();
                news.setAuthor(response.getSource());
                news.setPublishedAt(response.getArticles().get(i).getPublishedAt());
                String newsTitle = response.getArticles().get(i).getTitle();
                String newsDescription = response.getArticles().get(i).getDescription();

                try {
                    if (!"en".equals(ConstantHolder.USER_LANGUAGE)) {
                        if (accounts != null) {
                            String account = accounts[0].name;

                            news.setTitle(StringEscapeUtils.unescapeHtml4(mTranslateRestAdapter
                                    .translateText(newsTitle, account)));

                            news.setDescription(StringEscapeUtils.unescapeHtml4(mTranslateRestAdapter
                                    .translateText(newsDescription, account)));

                        } else {
                            news.setTitle(StringEscapeUtils.unescapeHtml4(mTranslateRestAdapter
                                    .translateText(newsTitle, "")));

                            news.setDescription(StringEscapeUtils.unescapeHtml4(mTranslateRestAdapter
                                    .translateText(newsDescription, "")));

                        }

                        if (news.getTitle().equalsIgnoreCase(newsTitle) ||
                                news.getTitle().contains("MYMEMORY WARNING")) {

                            news.setTitle(StringEscapeUtils.unescapeHtml4(mTranslateHelper
                                    .translateText(newsTitle)));
                        }
                        if (news.getDescription().equalsIgnoreCase(newsDescription) ||
                                news.getDescription().contains("MYMEMORY WARNING")) {

                            news.setDescription(StringEscapeUtils.unescapeHtml4(mTranslateHelper
                                    .translateText(newsDescription)));
                        }

                    } else {
                        news.setTitle(StringEscapeUtils.unescapeHtml4(newsTitle));
                        news.setDescription(StringEscapeUtils.unescapeHtml4(newsDescription));
                    }

                    news.setArticleUrl(response
                            .getArticles().get(i).getUrl());

                    news.setUrlToImage(response
                            .getArticles().get(i).getUrlToImage());

                } catch (GeneralSecurityException | IOException e) {
                    Log.i(ConstantHolder.TAG, "Error: " + e.getMessage());
                    news.setTitle(newsTitle);
                }
                newses.add(news);
            }
        }

        return newses;
    }
}
