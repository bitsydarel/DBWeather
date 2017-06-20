package com.dbeginc.dbweather.utils.services;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.dbeginc.dbweather.DBWeatherApplication;
import com.dbeginc.dbweather.models.api.adapters.NewsRestAdapter;
import com.dbeginc.dbweather.models.datatypes.news.Article;
import com.dbeginc.dbweather.models.datatypes.news.NewsResponse;
import com.dbeginc.dbweather.models.provider.schedulers.RxSchedulersProvider;
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

import io.reactivex.Completable;
import io.reactivex.observers.DisposableCompletableObserver;

import static com.dbeginc.dbweather.utils.holder.ConstantHolder.TAG;

/**
 * Created by Bitsy Darel on 22.05.17.
 * News Sync Job Scheduler
 */

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class NewsSyncJobScheduler extends JobService {
    @Inject
    NewsRestAdapter newsProvider;

    @Inject
    GoogleTranslateProvider mGoogleTranslateProvider;

    @Inject
    MyMemoryTranslateProvider mMyMemoryTranslateProvider;

    @Inject
    SharedPreferences mSharedPreferences;
    private DisposableCompletableObserver disposableCompletableObserver;

    public NewsSyncJobScheduler() {
        super();
        DBWeatherApplication.getComponent()
                .inject(this);
    }

    @Override
    public boolean onStartJob(final JobParameters params) {
        final RxSchedulersProvider schedulersProvider = RxSchedulersProvider.getInstance();

        disposableCompletableObserver = Completable.create(completableEmitter -> {
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
                completableEmitter.onComplete();

            } catch (final Exception exception) {
                if (!completableEmitter.isDisposed()) { completableEmitter.onError(exception); }
            }

        }).subscribeOn(schedulersProvider.getNewsScheduler())
                .observeOn(schedulersProvider.getNewsScheduler())
                .unsubscribeOn(schedulersProvider.getNewsScheduler())
                .subscribeWith(new DisposableCompletableObserver() {
                    @Override
                    public void onComplete() {
                        jobFinished(params, true);
                        Log.i(TAG, "NEWS SYNC FROM JOBSCHEDULER " + "IS DONE");
                    }

                    @Override
                    public void onError(final Throwable throwable) {
                        jobFinished(params, true);
                        Crashlytics.logException(throwable);
                    }
                });

        return true;
    }

    @Override
    public boolean onStopJob(final JobParameters params) {
        if (disposableCompletableObserver != null) { disposableCompletableObserver.dispose(); }
        return true;
    }

    private ArrayList<Article> parseNewses(final List<NewsResponse> newsResponses,
                                           final Map<String, Integer> listOfSource) {

        final ArrayList<Article> newses = new ArrayList<>();

        for (final NewsResponse response : newsResponses) {
            for (int i = 0; i < listOfSource.get(response.getSource()); i++) {
                final Article news = new Article();
                news.setAuthor(response.getSource());
                news.setPublishedAt(response.getArticles().get(i).getPublishedAt());
                String newsTitle = response.getArticles().get(i).getTitle();
                String newsDescription = response.getArticles().get(i).getDescription();

                newsTitle = newsTitle == null ? "" : newsTitle;
                newsDescription = newsDescription == null ? "" : newsDescription;

                try {
                    if (!"en".equals(ConstantHolder.USER_LANGUAGE) &&
                            mSharedPreferences.getBoolean(ConstantHolder.NEWS_TRANSLATION_KEY, true)) {

                        news.setTitle(StringEscapeUtils.unescapeHtml4(mMyMemoryTranslateProvider
                                .translateText(newsTitle)));

                        if (!newsDescription.isEmpty()) {
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