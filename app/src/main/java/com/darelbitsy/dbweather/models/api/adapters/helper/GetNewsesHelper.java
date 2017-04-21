package com.darelbitsy.dbweather.models.api.adapters.helper;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.darelbitsy.dbweather.models.helper.DatabaseOperation;
import com.darelbitsy.dbweather.models.api.adapters.network.NewsRestAdapter;
import com.darelbitsy.dbweather.models.api.adapters.network.TranslateRestAdapter;
import com.darelbitsy.dbweather.models.holder.ConstantHolder;
import com.darelbitsy.dbweather.models.utility.AppUtil;
import com.darelbitsy.dbweather.models.datatypes.news.Article;
import com.darelbitsy.dbweather.models.datatypes.news.NewsResponse;
import com.darelbitsy.dbweather.models.services.NewsDatabaseService;

import org.apache.commons.lang3.StringEscapeUtils;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import io.reactivex.Single;

import static com.darelbitsy.dbweather.models.holder.ConstantHolder.PREFS_NAME;

/**
 * Created by Darel Bitsy on 18/02/17.
 * This class help get Newses in background
 * Observer will only choose how to display it
 * to choose of to display the data
 */

public class GetNewsesHelper {
    private final NewsRestAdapter newsRestAdapter;
    private final TranslateRestAdapter mTranslateRestAdapter;
    private final TranslateHelper mTranslateHelper;
    private final Context mContext;
    private final DatabaseOperation mDatabaseOperation;
    private static GetNewsesHelper singletonGetNewsesHelper;

    public static GetNewsesHelper newInstance(final Context context) {
        if (singletonGetNewsesHelper == null) {
            singletonGetNewsesHelper = new GetNewsesHelper(context.getApplicationContext());
        }
        return singletonGetNewsesHelper;
    }

    private GetNewsesHelper(final Context context) {
        mContext = context;
        mDatabaseOperation = DatabaseOperation.newInstance(context);
        newsRestAdapter = NewsRestAdapter.newInstance(context);
        mTranslateRestAdapter = new TranslateRestAdapter();
        mTranslateHelper = TranslateHelper.newInstance(context);
    }

    public Single<ArrayList<Article>> getNewsesFromApi() {
        return Single.create(emitter -> {
                try {
                    final List<NewsResponse> newsResponseList = new ArrayList<>();
                    final Map<String, Integer> listOfSource = mDatabaseOperation.getActiveNewsSources();

                    for (final String source : listOfSource.keySet()) {
                        newsResponseList.add(newsRestAdapter
                                .getNews(source)
                                .execute()
                                .body());

                    }

                    final ArrayList<Article> newses = parseNewses(newsResponseList, mContext, listOfSource);

                    final Intent intent = new Intent(mContext, NewsDatabaseService.class);
                    intent.putParcelableArrayListExtra(ConstantHolder.NEWS_DATA_KEY, newses);
                    mContext.startService(intent);

                    if (!emitter.isDisposed()) { emitter.onSuccess(newses); }

                } catch (final Exception e) { if (!emitter.isDisposed()) { emitter.onError(e); }  }
            });
    }

    public Single<ArrayList<Article>> getNewsesFromDatabase(final DatabaseOperation database) {
        return Single.create(emitter -> {
            try {
                final ArrayList<Article> newses = database.getNewFromDatabase();
                if (!emitter.isDisposed()) { emitter.onSuccess(newses); }

            } catch (final Exception e) { if (!emitter.isDisposed()) { emitter.onError(e); } }
        });
    }

    private ArrayList<Article> parseNewses(final List<NewsResponse> newsResponses,
                                           final Context context,
                                           final Map<String, Integer> listOfSource) {

        final ArrayList<Article> newses = new ArrayList<>();
        Account[] accounts = null;

        if (AppUtil.isAccountPermissionOn(context)) {
            accounts = AccountManager.get(context)
                    .getAccountsByType("com.google");
        }

        for (final NewsResponse response : newsResponses) {
            for (int i = 0; i < listOfSource.get(response.getSource()); i++) {
                final Article news = new Article();
                news.setAuthor(response.getSource());
                news.setPublishedAt(response.getArticles().get(i).getPublishedAt());
                final String newsTitle = response.getArticles().get(i).getTitle();
                final String newsDescription = response.getArticles().get(i).getDescription();

                try {
                    if (!"en".equals(ConstantHolder.USER_LANGUAGE) &&
                            context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                                    .getBoolean(ConstantHolder.NEWS_TRANSLATION_KEY, true)) {

                        if (accounts != null) {
                            final String account = accounts[0].name;

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
