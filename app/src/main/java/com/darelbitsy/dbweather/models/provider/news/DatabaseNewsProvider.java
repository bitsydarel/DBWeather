package com.darelbitsy.dbweather.models.provider.news;

import android.content.Context;

import com.darelbitsy.dbweather.models.datatypes.news.Article;
import com.darelbitsy.dbweather.models.helper.DatabaseOperation;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Single;

/**
 * Created by Darel Bitsy on 22/04/17.
 */

public class DatabaseNewsProvider implements NewsProvider<List<Article>> {

    private final DatabaseOperation mDatabaseOperation;

    public DatabaseNewsProvider(final Context context) {
        mDatabaseOperation = DatabaseOperation.newInstance(context.getApplicationContext());
    }

    @Override
    public Single<List<Article>> getNews() {
        return Single.create(emitter -> {
            try {
                final ArrayList<Article> newFromDatabase = mDatabaseOperation.getNewFromDatabase();

                if (!emitter.isDisposed()) { emitter.onSuccess(newFromDatabase); }

            } catch (final Exception e) {
                if (!emitter.isDisposed()) { emitter.onError(e); }
            }
        });
    }
}
