package com.darelbitsy.dbweather.provider.news;

import android.content.Context;

import com.darelbitsy.dbweather.extensions.helper.DatabaseOperation;
import com.darelbitsy.dbweather.models.datatypes.news.Article;

import java.util.List;

import io.reactivex.Single;

/**
 * Created by Darel Bitsy on 22/04/17.
 */

public class DatabaseNewsProvider implements INewsProvider<List<Article>> {

    private final DatabaseOperation mDatabaseOperation;

    public DatabaseNewsProvider(final Context context) {
        mDatabaseOperation = DatabaseOperation.newInstance(context.getApplicationContext());
    }

    @Override
    public Single<List<Article>> getNews() {
        return Single.fromCallable(mDatabaseOperation::getNewFromDatabase);
    }
}
