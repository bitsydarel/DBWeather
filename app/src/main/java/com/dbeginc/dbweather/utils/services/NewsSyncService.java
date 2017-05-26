package com.dbeginc.dbweather.utils.services;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.annotation.Nullable;

import com.crashlytics.android.Crashlytics;
import com.dbeginc.dbweather.DBWeatherApplication;
import com.dbeginc.dbweather.models.api.adapters.NewsRestAdapter;
import com.dbeginc.dbweather.models.datatypes.news.Article;
import com.dbeginc.dbweather.models.datatypes.news.NewsResponse;
import com.dbeginc.dbweather.models.provider.translators.GoogleTranslateProvider;
import com.dbeginc.dbweather.models.provider.translators.MyMemoryTranslateProvider;
import com.dbeginc.dbweather.utils.helper.DatabaseOperation;
import com.dbeginc.dbweather.utils.holder.ConstantHolder;

import org.apache.commons.lang3.StringEscapeUtils;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.inject.Inject;

import static com.dbeginc.dbweather.utils.holder.ConstantHolder.NEWS_SYNC_JOB_ID;

/**
 * Created by Bitsy Darel on 23.05.17.
 * News Feed sync service
 */

public class NewsSyncService extends IntentService {
    @Inject
    NewsRestAdapter newsProvider;

    @Inject
    GoogleTranslateProvider mGoogleTranslateProvider;

    @Inject
    MyMemoryTranslateProvider mMyMemoryTranslateProvider;

    @Inject
    SharedPreferences mSharedPreferences;

    public NewsSyncService() {
        super(NewsSyncService.class.getSimpleName());
        DBWeatherApplication.getComponent().inject(this);
    }

    @Override
    protected void onHandleIntent(@Nullable final Intent intent) {
        try {
            final DatabaseOperation databaseOperation = DatabaseOperation.getInstance(getApplicationContext());
            final List<NewsResponse> newsResponseList = new ArrayList<>();
            final Map<String, Integer> listOfSource = databaseOperation.getActiveNewsSources();

            for (final String source : listOfSource.keySet()) {
                newsResponseList.add(newsProvider
                        .getNews(source)
                        .execute()
                        .body());
            }

            databaseOperation.saveNewses(parseNewses(newsResponseList, listOfSource));

        } catch (final Exception error) { Crashlytics.logException(error); }

        final Intent newsSyncService = new Intent(this, NewsSyncService.class);
        newsSyncService.setFlags(START_FLAG_REDELIVERY);

        final PendingIntent servicePendingIntent = PendingIntent.getBroadcast(this,
                NEWS_SYNC_JOB_ID,
                newsSyncService,
                PendingIntent.FLAG_UPDATE_CURRENT);


        final AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        alarmManager.setRepeating(AlarmManager.RTC,
                System.currentTimeMillis() + 3600000,
                AlarmManager.INTERVAL_HOUR,
                servicePendingIntent);
    }

    private ArrayList<Article> parseNewses(final List<NewsResponse> newsResponses,
                                           final Map<String, Integer> listOfSource) {

        final ArrayList<Article> newses = new ArrayList<>();

        for (final NewsResponse response : newsResponses) {
            for (int i = 0; i < listOfSource.get(response.getSource()); i++) {
                final Article news = new Article();
                news.setAuthor(response.getSource());
                news.setPublishedAt(response.getArticles().get(i).getPublishedAt());
                final String newsTitle = response.getArticles().get(i).getTitle();
                final String newsDescription = response.getArticles().get(i).getDescription();

                try {
                    if (!"en".equals(ConstantHolder.USER_LANGUAGE) &&
                            mSharedPreferences.getBoolean(ConstantHolder.NEWS_TRANSLATION_KEY, true)) {

                        news.setTitle(StringEscapeUtils.unescapeHtml4(mMyMemoryTranslateProvider
                                .translateText(newsTitle)));

                        if (newsDescription != null && !newsDescription.isEmpty()) {
                            news.setDescription(StringEscapeUtils.unescapeHtml4(mMyMemoryTranslateProvider
                                    .translateText(newsDescription)));

                        } else {
                            news.setDescription("");
                        }

                        if (news.getTitle().equalsIgnoreCase(newsTitle)
                                || news.getTitle().toUpperCase(Locale.getDefault()).contains("MYMEMORY WARNING")
                                || news.getTitle().toUpperCase(Locale.getDefault()).contains("QUERY LENGTH LIMIT")) {

                            news.setTitle(StringEscapeUtils.unescapeHtml4(mGoogleTranslateProvider
                                    .translateText(newsTitle)));
                        }

                        if (!news.getDescription().isEmpty() &&
                                (news.getDescription().equalsIgnoreCase(newsDescription) || news.getDescription().contains("MYMEMORY WARNING") ||
                                        news.getDescription().toUpperCase(Locale.getDefault()).contains("QUERY LENGTH LIMIT"))) {

                            news.setDescription(StringEscapeUtils.unescapeHtml4(mGoogleTranslateProvider
                                    .translateText(news.getDescription())));
                        }

                    } else {
                        news.setTitle(StringEscapeUtils.unescapeHtml4(newsTitle));
                        news.setDescription(StringEscapeUtils.unescapeHtml4(newsDescription));
                    }

                    news.setArticleUrl(response.getArticles().get(i).getUrl());

                    news.setUrlToImage(response.getArticles().get(i).getUrlToImage());

                } catch (GeneralSecurityException | IOException e) {
                    news.setTitle(newsTitle);
                }
                newses.add(news);
            }
        }
        return newses;
    }
}
