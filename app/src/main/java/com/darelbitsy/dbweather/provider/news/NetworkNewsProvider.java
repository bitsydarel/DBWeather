package com.darelbitsy.dbweather.provider.news;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.util.Log;

import com.darelbitsy.dbweather.models.api.adapters.network.NewsRestAdapter;
import com.darelbitsy.dbweather.models.datatypes.news.Article;
import com.darelbitsy.dbweather.models.datatypes.news.NewsResponse;
import com.darelbitsy.dbweather.extensions.helper.DatabaseOperation;
import com.darelbitsy.dbweather.extensions.holder.ConstantHolder;
import com.darelbitsy.dbweather.provider.translators.GoogleTranslateProvider;
import com.darelbitsy.dbweather.provider.translators.MyMemoryTranslateProvider;
import com.darelbitsy.dbweather.extensions.services.NewsDatabaseService;
import com.darelbitsy.dbweather.extensions.utility.AppUtil;

import org.apache.commons.lang3.StringEscapeUtils;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import io.reactivex.Single;

import static com.darelbitsy.dbweather.extensions.holder.ConstantHolder.PREFS_NAME;

/**
 * Created by Darel Bitsy on 22/04/17.
 * News Provider by Network
 */

public class NetworkNewsProvider implements INewsProvider<List<Article>> {

    private final Context mApplicationContext;
    private final DatabaseOperation mDatabaseOperation;
    private final NewsRestAdapter mNewsRestAdapter;
    private final GoogleTranslateProvider mGoogleTranslateProvider;
    private MyMemoryTranslateProvider mMyMemoryTranslateProvider;

    public NetworkNewsProvider(final Context context) {
        mApplicationContext = context.getApplicationContext();
        mDatabaseOperation = DatabaseOperation.newInstance(mApplicationContext);
        mNewsRestAdapter = NewsRestAdapter.newInstance(mApplicationContext);
        mGoogleTranslateProvider = GoogleTranslateProvider.newInstance(mApplicationContext);
        mMyMemoryTranslateProvider = new MyMemoryTranslateProvider();
    }

    @Override
    public Single<List<Article>> getNews() {

        return Single.create(emitter -> {
            try {
                final List<NewsResponse> newsResponseList = new ArrayList<>();
                final Map<String, Integer> listOfSource = mDatabaseOperation.getActiveNewsSources();

                for (final String source : listOfSource.keySet()) {
                    newsResponseList.add(mNewsRestAdapter
                            .getNews(source)
                            .execute()
                            .body());

                }

                final List<Article> newses = parseNewses(newsResponseList, mApplicationContext, listOfSource);

                final Intent intent = new Intent(mApplicationContext, NewsDatabaseService.class);
                intent.putParcelableArrayListExtra(ConstantHolder.NEWS_DATA_KEY, (ArrayList<? extends Parcelable>) newses);
                mApplicationContext.startService(intent);

                if (!emitter.isDisposed()) { emitter.onSuccess(newses); }

            } catch (final Exception e) { if (!emitter.isDisposed()) { emitter.onError(e); }  }
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

                        if (accounts != null && !accounts[0].name.isEmpty()) {
                            final String account = accounts[0].name;

                            news.setTitle(StringEscapeUtils.unescapeHtml4(mMyMemoryTranslateProvider
                                    .translateText(newsTitle, account)));

                            news.setDescription(StringEscapeUtils.unescapeHtml4(mMyMemoryTranslateProvider
                                    .translateText(newsDescription, account)));

                        } else {
                            news.setTitle(StringEscapeUtils.unescapeHtml4(mMyMemoryTranslateProvider
                                    .translateText(newsTitle)));

                            news.setDescription(StringEscapeUtils.unescapeHtml4(mMyMemoryTranslateProvider
                                    .translateText(newsDescription)));

                        }

                        if (news.getTitle().equalsIgnoreCase(newsTitle) ||
                                news.getTitle().contains("MYMEMORY WARNING")) {

                            news.setTitle(StringEscapeUtils.unescapeHtml4(mGoogleTranslateProvider
                                    .translateText(newsTitle)));
                        }
                        if (news.getDescription().equalsIgnoreCase(newsDescription) ||
                                news.getDescription().contains("MYMEMORY WARNING")) {

                            news.setDescription(StringEscapeUtils.unescapeHtml4(mGoogleTranslateProvider
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
